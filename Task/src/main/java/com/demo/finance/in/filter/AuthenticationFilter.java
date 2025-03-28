package com.demo.finance.in.filter;

import com.demo.finance.domain.dto.UserDto;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * The {@code AuthenticationFilter} class implements the {@link Filter} interface
 * and provides a mechanism to enforce authentication and authorization checks on incoming HTTP requests.
 * It ensures that:
 * - Public endpoints are accessible without authentication.
 * - Admin-specific endpoints are accessible only to users with the "admin" role.
 * - All other protected endpoints are accessible only to users with the "user" role.
 */
@Component
@Order(1)
public class AuthenticationFilter implements Filter {

    /**
     * Filters incoming HTTP requests to enforce authentication and authorization rules.
     * - Allows access to public endpoints without authentication.
     * - Requires authentication for all other endpoints.
     * - Restricts access to admin-specific endpoints to users with the "admin" role.
     * - Restricts access to all other protected endpoints to users with the "user" role.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @param chain    the filter chain used to pass the request and response to the next filter or target resource
     * @throws IOException      if an I/O error occurs during request processing
     * @throws ServletException if a servlet-specific error occurs during request processing
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();
        if (isPublicEndpoint(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Authentication required.");
            return;
        }
        Object currentUser = session.getAttribute("currentUser");
        if (!(currentUser instanceof UserDto user)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Invalid user session.");
            return;
        }
        String role = user.getRole().getName().toLowerCase();
        if (isAdminEndpoint(requestURI)) {
            if (!"admin".equalsIgnoreCase(role)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("Access denied. Admin role required.");
                return;
            }
        }
        else {
            if (!"user".equalsIgnoreCase(role)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("Access denied. User role required.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Determines whether the given request URI corresponds to a public endpoint.
     * Public endpoints do not require authentication.
     *
     * @param requestURI the URI of the incoming request
     * @return {@code true} if the request URI matches a public endpoint, {@code false} otherwise
     */
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.endsWith("/api/users/registration") || requestURI.endsWith("/api/users/authenticate")
                || requestURI.endsWith("/api/users/logout");
    }

    /**
     * Determines whether the given request URI corresponds to an admin-specific endpoint.
     * Admin-specific endpoints require the "admin" role for access.
     *
     * @param requestURI the URI of the incoming request
     * @return {@code true} if the request URI matches an admin-specific endpoint, {@code false} otherwise
     */
    private boolean isAdminEndpoint(String requestURI) {
        String path = requestURI.split("\\?")[0];
        return path.matches("^/api/admin/users(/.*)?$");
    }
}