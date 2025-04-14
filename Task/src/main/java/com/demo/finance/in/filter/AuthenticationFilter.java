package com.demo.finance.in.filter;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.out.service.TokenService;
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
    private final TokenService tokenService;

    /**
     * Filters incoming HTTP requests to authenticate users based on their JWT token and manage access control.
     * <p>
     * This method intercepts the request, validates the JWT token from the "Authorization" header, checks the
     * user's role, and grants or denies access to the requested endpoint based on the role. It also handles public
     * endpoints that do not require authentication. If authentication fails or the user lacks the required role,
     * an error response is sent.
     * </p>
     *
     * @param request  the {@link ServletRequest} representing the incoming HTTP request
     * @param response the {@link ServletResponse} representing the outgoing HTTP response
     * @param chain    the {@link FilterChain} used to pass the request and response along the filter chain
     * @throws IOException      if an I/O error occurs during request processing
     * @throws ServletException if a servlet-related error occurs during request processing
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
        tokenService.setCurrentToken(token);
        UserDto userDto;
        try {
            userDto = jwtService.validateToken(token);
            String role = userDto.getRole().toUpperCase();
            if (isAdminEndpoint(requestURI)) {
                if (!"ADMIN".equals(role)) {
                    sendErrorResponse(httpResponse, "Access denied. Admin role required");
                    return;
                }
            } else {
                if (!"USER".equals(role)) {
                    sendErrorResponse(httpResponse, "Access denied. User role required");
                    return;
                }
            }
            httpRequest.setAttribute("currentUser", userDto);
            chain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(httpResponse, e.getMessage());
        } finally {
            tokenService.clearCurrentToken();
        }
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