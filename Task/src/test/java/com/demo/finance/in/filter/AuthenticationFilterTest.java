package com.demo.finance.in.filter;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.out.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.times;
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
    private JwtService jwtService;
    @Mock
    private PrintWriter writer;
    @InjectMocks
    private AuthenticationFilter filter;
    private UserDto user;

    @BeforeEach
    void setUp() {
        user = Instancio.create(UserDto.class);
    }

    @Test
    @DisplayName("Public endpoint should bypass authentication checks")
    void publicEndpoint_ShouldPassThrough() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/users/authenticate");

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(jwtService, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("Valid token with user role should allow access to user endpoint")
    void validTokenWithUserRole_ShouldAllowUserAccess() throws ServletException, IOException {
        user.setRole("USER");

        when(request.getRequestURI()).thenReturn("/api/user/data");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(jwtService.validateToken("valid.token.here")).thenReturn(user);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Valid token with admin role should allow access to admin endpoint")
    void validTokenWithAdminRole_ShouldAllowAdminAccess() throws ServletException, IOException {
        user.setRole("ADMIN");

        when(request.getRequestURI()).thenReturn("/api/admin/users/123");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(jwtService.validateToken("valid.token.here")).thenReturn(user);

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Request without token should return 401 Unauthorized")
    void noToken_ShouldReturn401() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/private");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(writer, times(1)).write("{\"error\":\"Authentication required\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Invalid token should return 401 Unauthorized")
    void invalidToken_ShouldReturn401() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/private");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
        when(jwtService.validateToken("invalid.token")).thenThrow(new IllegalArgumentException("Invalid token"));
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(writer, times(1)).write("{\"error\":\"Invalid token\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Non-admin user accessing admin endpoint should return 401 Unauthorized")
    void nonAdminAccessingAdminEndpoint_ShouldReturn401() throws ServletException, IOException {
        user.setRole("USER");

        when(request.getRequestURI()).thenReturn("/api/admin/users/123");
        when(request.getHeader("Authorization")).thenReturn("Bearer user.token");
        when(jwtService.validateToken("user.token")).thenReturn(user);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(writer, times(1)).write("{\"error\":\"Access denied. Admin role required\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Admin user accessing user endpoint should return 401 Unauthorized")
    void adminAccessingUserEndpoint_ShouldReturn401() throws IOException, ServletException {
        user.setRole("ADMIN");

        when(request.getRequestURI()).thenReturn("/api/user/data");
        when(request.getHeader("Authorization")).thenReturn("Bearer admin.token");
        when(jwtService.validateToken("admin.token")).thenReturn(user);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(writer, times(1)).write("{\"error\":\"Access denied. User role required\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Malformed Authorization header should return 401 Unauthorized")
    void malformedAuthHeader_ShouldReturn401() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/private");
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");
        when(response.getWriter()).thenReturn(writer);

        filter.doFilter(request, response, chain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).setContentType("application/json");
        verify(writer, times(1)).write("{\"error\":\"Authentication required\"}");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Swagger UI endpoint should bypass authentication")
    void swaggerEndpoint_ShouldPassThrough() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        filter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(jwtService, never()).validateToken(anyString());
    }
}