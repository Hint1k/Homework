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

import java.io.IOException;

public class AuthenticationFilter implements Filter {

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
        if (isAdminEndpoint(requestURI)) {
            Object currentUser = session.getAttribute("currentUser");
            if (currentUser instanceof UserDto user) {
                if (!"admin".equalsIgnoreCase(user.getRole().getName())) {
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    httpResponse.getWriter().write("Access denied. Admin role required.");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Checks if the given URI corresponds to a public endpoint.
     *
     * @param requestURI the request URI
     * @return true if the URI is for a public endpoint, false otherwise
     */
    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.endsWith("/api/users/registration") || requestURI.endsWith("/api/users/authenticate");
    }

    /**
     * Checks if the given URI corresponds to an admin-only endpoint.
     *
     * @param requestURI the request URI
     * @return true if the URI is for an admin-only endpoint, false otherwise
     */
    private boolean isAdminEndpoint(String requestURI) {
        return requestURI.startsWith("/api/admin/users/");
    }
}