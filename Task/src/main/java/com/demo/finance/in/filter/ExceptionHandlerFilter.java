package com.demo.finance.in.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.annotation.WebFilter;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {

    private static final Logger log = Logger.getLogger(ExceptionHandlerFilter.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(response, ex);
        }
    }

    private void handleException(ServletResponse response, Exception ex) {
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            String message = "An unexpected error occurred.)";
            log.log(Level.SEVERE, "Unhandled exception: " + ex.getMessage(), ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", message);
            errorResponse.put("status", statusCode);
            httpResponse.setStatus(statusCode);
            httpResponse.setContentType("application/json");
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            httpResponse.getWriter().write(jsonResponse);
        } catch (Exception handlerEx) {
            log.log(Level.SEVERE, "Failed to handle exception: " + handlerEx.getMessage(), handlerEx);
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            try {
                response.getWriter().write("{\"error\":\"An unexpected error occurred\"}");
            } catch (IOException e) {
                log.log(Level.SEVERE, "Completely failed to write error response", e);
            }
        }
    }
}