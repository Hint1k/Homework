package com.demo.finance.in.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A global exception handling filter that intercepts and processes uncaught exceptions
 * occurring during request processing. This filter provides consistent error responses
 * in JSON format and proper HTTP status codes for all exceptions that propagate up
 * the filter chain.
 *
 * <p>The filter catches all exceptions, logs them, and returns a structured JSON response
 * with error details. It handles both the original application exceptions and any
 * exceptions that might occur during the error handling process itself.</p>
 *
 * <p>Mapped to process all requests ("/*") to ensure comprehensive exception handling
 * across the entire application.</p>
 */
@WebFilter("/*")
public class ExceptionHandlerFilter implements Filter {

    private static final Logger log = Logger.getLogger(ExceptionHandlerFilter.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes each request by invoking the next filter in the chain and catching any
     * exceptions that occur during request processing.
     *
     * @param request  the servlet request being processed
     * @param response the servlet response to be populated
     * @param chain    the filter chain for invoking the next filter or resource
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(response, ex);
        }
    }

    /**
     * Handles exceptions by generating an appropriate error response in JSON format.
     *
     * <p>This method:
     * <ul>
     *   <li>Sets the HTTP status code to 500 (Internal Server Error)</li>
     *   <li>Constructs a structured error response with message and status</li>
     *   <li>Logs the exception details at SEVERE level</li>
     *   <li>Writes the JSON response to the output</li>
     * </ul>
     * <p>
     * The method includes comprehensive error handling to ensure some response is always
     * sent to the client, even if the error handling process itself fails.
     *
     * @param response the servlet response to populate with error details
     * @param ex       the exception that was caught during request processing
     */
    private void handleException(ServletResponse response, Exception ex) {
        try {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            int statusCode = httpResponse.getStatus() != 0 ? httpResponse.getStatus()
                    : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            String message = ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage()
                    : "An unexpected error occurred.";
            log.log(Level.SEVERE, "Unhandled exception: " + ex.getMessage(), ex);
            httpResponse.setStatus(statusCode);
            httpResponse.setContentType("application/json");
            String jsonResponse = objectMapper.writeValueAsString(message);
            httpResponse.getWriter().write(jsonResponse);
            log.log(Level.INFO, "7: JSON response written to the output stream");
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