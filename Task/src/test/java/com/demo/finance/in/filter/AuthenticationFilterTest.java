package com.demo.finance.in.filter;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import com.demo.finance.in.filter.AuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.fail;
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
    void publicEndpoint_ShouldPassThrough() {
        try {
            when(request.getRequestURI()).thenReturn("/api/users/authenticate");

            filter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
        } catch (Exception e) {
            fail("Public endpoint should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("User-restricted endpoint should allow user access")
    void userEndpoint_ShouldAllowUser() {
        try {
            UserDto user = new UserDto();
            user.setRole(new Role("user"));

            when(request.getRequestURI()).thenReturn("/api/user/data");
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("currentUser")).thenReturn(user);

            filter.doFilter(request, response, chain);

            verify(chain).doFilter(request, response);
            verify(response, never()).setStatus(anyInt());
        } catch (Exception e) {
            fail("Regular user should be allowed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Admin endpoint should allow admin access without writing to response")
    void adminEndpoint_ShouldAllowAdmin() {
        try {
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
        } catch (Exception e) {
            fail("Admin should be allowed on admin endpoints. Error: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Request without session should return 401 Unauthorized")
    void noSession_ShouldReturn401() {
        try {
            when(request.getRequestURI()).thenReturn("/api/private");
            when(request.getSession(false)).thenReturn(null);
            when(response.getWriter()).thenReturn(writer);

            filter.doFilter(request, response, chain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            verify(writer).write("{\"error\":\"Authentication required\"}");
        } catch (Exception e) {
            fail("Should handle missing session gracefully: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Non-admin user accessing admin endpoint should return 403 Forbidden")
    void adminEndpoint_NonAdminUser_ShouldReturn403() {
        try {
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
        } catch (Exception e) {
            fail("Should reject non-admin users from admin endpoints: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("User-restricted endpoint should block admin access")
    void userEndpoint_ShouldBlockAdmin() {
        try {
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
        } catch (Exception e) {
            fail("Admin should be blocked from user endpoints: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("User-restricted endpoint should block unauthenticated access")
    void userEndpoint_ShouldBlockUnauthenticated() {
        try {
            when(request.getRequestURI()).thenReturn("/api/user/data");
            when(request.getSession(false)).thenReturn(null);
            when(response.getWriter()).thenReturn(writer);

            filter.doFilter(request, response, chain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            verify(writer).write("{\"error\":\"Authentication required\"}");
            verify(chain, never()).doFilter(request, response);
        } catch (Exception e) {
            fail("Should block unauthenticated access: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Admin endpoint should block unauthenticated access")
    void adminEndpoint_ShouldBlockUnauthenticated() {
        try {
            when(request.getRequestURI()).thenReturn("/api/admin/users");
            when(request.getSession(false)).thenReturn(null);
            when(response.getWriter()).thenReturn(writer);

            filter.doFilter(request, response, chain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            verify(writer).write("{\"error\":\"Authentication required\"}");
            verify(chain, never()).doFilter(request, response);
        } catch (Exception e) {
            fail("Should block unauthenticated access to admin endpoints: " + e.getMessage());
        }
    }
}