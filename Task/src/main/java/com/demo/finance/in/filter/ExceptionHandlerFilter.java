package com.demo.finance.in.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
@Component
@Order(Integer.MIN_VALUE)
@Slf4j
public class ExceptionHandlerFilter implements Filter {

    private static final String ERROR_RESPONSE_FORMAT = "{\"error\": \"%s\"}";

    /**
     * Creates and returns a new {@link ResponseWrapper} instance to wrap the given
     * {@link HttpServletResponse}. This wrapper is used to track whether the response
     * has been committed, ensuring proper handling of exceptions without modifying
     * already-committed responses.
     *
     * @param response the original HTTP response to be wrapped
     * @return a new {@link ResponseWrapper} instance wrapping the provided response
     */
    ResponseWrapper createResponseWrapper(HttpServletResponse response) {
        return new ResponseWrapper(response);
    }

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
        ResponseWrapper wrappedResponse = createResponseWrapper((HttpServletResponse) response);
        try {
            chain.doFilter(request, wrappedResponse);
        } catch (Exception ex) {
            if (wrappedResponse.isCommitted()) {
                return;
            }
            handleException(wrappedResponse, ex);
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
            log.error("Unhandled exception: {}", ex.getMessage(), ex);
            httpResponse.setStatus(statusCode);
            httpResponse.setContentType("application/json");
            String jsonResponse = String.format(ERROR_RESPONSE_FORMAT, message);
            httpResponse.getWriter().write(jsonResponse);
            log.info("7: JSON response written to the output stream");
        } catch (Exception handlerEx) {
            log.error("Failed to handle exception: {}", handlerEx.getMessage(), handlerEx);
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            try {
                response.getWriter().write(String.format(ERROR_RESPONSE_FORMAT, "An unexpected error occurred"));
            } catch (IOException e) {
                log.error("Completely failed to write error response", e);
            }
        }
    }

    /**
     * A wrapper for {@link HttpServletResponse} that tracks whether the response
     * has been committed. This ensures that exception handling does not attempt
     * to modify an already committed response.
     *
     * <p>This wrapper overrides status-setting and error-sending methods to update
     * an internal flag, allowing filters to check whether further modifications
     * to the response are possible.</p>
     */
    static class ResponseWrapper extends HttpServletResponseWrapper {
        private boolean committed = false;

        /**
         * Constructs a new {@code ResponseWrapper} for the given {@link HttpServletResponse}.
         *
         * @param response the original HTTP response to wrap
         */
        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        /**
         * Sets the HTTP status code for the response and marks the response as committed.
         *
         * @param sc the HTTP status code
         */
        @Override
        public void setStatus(int sc) {
            committed = true;
            super.setStatus(sc);
        }

        /**
         * Sends an HTTP error response with the specified status code and message.
         * This marks the response as committed.
         *
         * @param sc  the HTTP status code
         * @param msg the error message
         * @throws IOException if an input or output exception occurs
         */
        @Override
        public void sendError(int sc, String msg) throws IOException {
            committed = true;
            super.sendError(sc, msg);
        }

        /**
         * Sends an HTTP error response with the specified status code.
         * This marks the response as committed.
         *
         * @param sc the HTTP status code
         * @throws IOException if an input or output exception occurs
         */
        @Override
        public void sendError(int sc) throws IOException {
            committed = true;
            super.sendError(sc);
        }

        /**
         * Checks whether the response has been committed, either by this wrapper
         * or by the underlying response.
         *
         * @return {@code true} if the response has been committed, {@code false} otherwise
         */
        @Override
        public boolean isCommitted() {
            return committed || super.isCommitted();
        }
    }
}