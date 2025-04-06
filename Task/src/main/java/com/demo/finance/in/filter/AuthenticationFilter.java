package com.demo.finance.in.filter;

import com.demo.finance.domain.dto.UserDto;
import jakarta.servlet.Filter;
import com.demo.finance.out.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter for authenticating user requests and managing access control based on roles.
 * <p>
 * This filter intercepts incoming requests, validates JWT tokens, and ensures that users have the appropriate role
 * to access certain endpoints. Public endpoints such as registration, authentication, and Swagger UI are exempt from
 * authentication. Users with the "admin" role are restricted to specific endpoints, while "user" role users are
 * allowed to access general user endpoints.
 * </p>
 * <p>
 * This filter is executed as part of the Spring filter chain and is ordered with a lower precedence (1) for early
 * execution.
 * </p>
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

    private final JwtService jwtService;

    /**
     * Filters incoming requests to validate authentication and enforce role-based access control.
     * <p>
     * This method checks if the request targets a public endpoint (no authentication required), extracts the JWT token
     * from the request, validates it, and ensures the user has the appropriate role to access the requested endpoint.
     * If any validation fails, an error response is returned. Otherwise, the request proceeds in the filter chain.
     * </p>
     *
     * @param request  the {@link ServletRequest} representing the incoming request
     * @param response the {@link ServletResponse} representing the outgoing response
     * @param chain    the {@link FilterChain} allowing the request to proceed if validated
     * @throws IOException      if an I/O error occurs during filtering
     * @throws ServletException if a general servlet exception occurs
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
        String token = extractToken(httpRequest);
        if (token == null) {
            sendErrorResponse(httpResponse, "Authentication required");
            return;
        }
        UserDto userDto;
        try {
            userDto = jwtService.validateToken(token);
        } catch (Exception e) {
            sendErrorResponse(httpResponse, e.getMessage());
            return;
        }
        String role = userDto.getRole().getName().toLowerCase();
        if (isAdminEndpoint(requestURI)) {
            if (!"admin".equals(role)) {
                sendErrorResponse(httpResponse, "Access denied. Admin role required");
                return;
            }
        } else {
            if (!"user".equals(role)) {
                sendErrorResponse(httpResponse, "Access denied. User role required");
                return;
            }
        }
        httpRequest.setAttribute("currentUser", userDto);
        chain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the "Authorization" header in the incoming request.
     * <p>
     * The token is expected to be in the form of "Bearer <token>". If this pattern is not followed,
     * or the token is not present, {@code null} is returned.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the "Authorization" header
     * @return the JWT token as a string, or {@code null} if no valid token is found
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Sends an error response with the specified message and a 401 Unauthorized status code.
     * <p>
     * The response is returned with a JSON body containing the error message.
     * </p>
     *
     * @param response the {@link HttpServletResponse} to send the error response
     * @param message  the error message to include in the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    /**
     * Determines if the given request URI corresponds to a public endpoint that does not require authentication.
     * <p>
     * Public endpoints include user registration, authentication, logout, and Swagger UI paths.
     * </p>
     *
     * @param requestURI the URI of the incoming request
     * @return {@code true} if the endpoint is public, {@code false} otherwise
     */
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.endsWith("/api/users/registration") || requestURI.endsWith("/api/users/authenticate")
                || requestURI.endsWith("/api/users/logout") || requestURI.startsWith("/swagger-ui/")
                || requestURI.startsWith("/swagger-ui") || requestURI.equals("/swagger-ui.html")
                || requestURI.equals("/") || requestURI.startsWith("/v3/api-docs");
    }

    /**
     * Determines if the given request URI corresponds to an admin-only endpoint.
     * <p>
     * Admin endpoints are restricted to users with the "admin" role. This method matches the URI pattern
     * for admin user management routes.
     * </p>
     *
     * @param requestURI the URI of the incoming request
     * @return {@code true} if the endpoint is an admin-only endpoint, {@code false} otherwise
     */
    private boolean isAdminEndpoint(String requestURI) {
        String path = requestURI.split("\\?")[0];
        return path.matches("^/api/admin/users(/.*)?$");
    }
}