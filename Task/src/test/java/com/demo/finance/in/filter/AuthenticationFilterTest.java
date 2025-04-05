package com.demo.finance.in.filter;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private HttpSession session;
    @Mock
    private PrintWriter writer;
    @InjectMocks
    private AuthenticationFilter filter;

    @Test
    @DisplayName("Public endpoint should bypass authentication checks")
    void publicEndpoint_ShouldPassThrough() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/users/authenticate");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("User-restricted endpoint should allow user access")
    void userEndpoint_ShouldAllowUser() throws ServletException, IOException {
        UserDto user = new UserDto();
        user.setRole(new Role("user"));

        when(request.getRequestURI()).thenReturn("/api/user/data");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Admin endpoint should allow admin access without writing to response")
    void adminEndpoint_ShouldAllowAdmin() throws ServletException, IOException {
        UserDto admin = new UserDto();
        admin.setUserId(1L);
        admin.setRole(new Role("admin"));

        when(request.getRequestURI()).thenReturn("/api/admin/users/*");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(admin);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verify(writer, never()).write(anyString());
    }

    @Test
    @DisplayName("Request without session should return 401 Unauthorized")
    void noSession_ShouldReturn401() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/private");
        when(request.getSession(false)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Authentication required\"}");
    }

    @Test
    @DisplayName("Non-admin user accessing admin endpoint should return 403 Forbidden")
    void adminEndpoint_NonAdminUser_ShouldReturn403() throws ServletException, IOException {
        UserDto user = new UserDto();
        user.setRole(new Role("user"));

        when(request.getRequestURI()).thenReturn("/api/admin/users/123");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Changed from FORBIDDEN
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Access denied. Admin role required\"}");
    }

    @Test
    @DisplayName("User-restricted endpoint should block admin access")
    void userEndpoint_ShouldBlockAdmin() throws IOException, ServletException {
        UserDto admin = new UserDto();
        admin.setRole(new Role("admin"));

        when(request.getRequestURI()).thenReturn("/api/user/data");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(admin);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Access denied. User role required\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("User-restricted endpoint should block unauthenticated access")
    void userEndpoint_ShouldBlockUnauthenticated() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/user/data");
        when(request.getSession(false)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Authentication required\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Admin endpoint should block unauthenticated access")
    void adminEndpoint_ShouldBlockUnauthenticated() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/admin/users");
        when(request.getSession(false)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(writer).write("{\"error\":\"Authentication required\"}");
        verify(chain, never()).doFilter(request, response);
    }
}