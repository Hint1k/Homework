package com.demo.finance.filter;

import com.demo.finance.in.filter.ExceptionHandlerFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private PrintWriter writer;
    @InjectMocks
    private ExceptionHandlerFilter filter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Normal request should pass through filter chain without modification")
    void normalRequest_ShouldPassThrough() {
        try {
            filter.doFilter(request, response, chain);
            verify(chain).doFilter(request, response);
        } catch (Exception e) {
            fail("Normal request should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should return 500 with error message when exception occurs")
    void thrownException_ShouldReturn500() {
        try {
            when(response.getWriter()).thenReturn(writer);
            doThrow(new RuntimeException("Test error")).when(chain).doFilter(request, response);

            filter.doFilter(request, response, chain);

            verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            verify(writer).write("{\"error\": \"Test error\"}");
        } catch (Exception e) {
            fail("Should handle exceptions and return 500: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should still set status code when error occurs during response writing")
    void errorDuringHandling_ShouldStillRespond() {
        try {
            when(response.getWriter()).thenThrow(new IOException("Writer failed"));
            doThrow(new RuntimeException("Original error")).when(chain).doFilter(request, response);

            filter.doFilter(request, response, chain);

            verify(response, times(2)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            fail("Should handle errors during error handling: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should preserve existing HTTP status code when exception occurs")
    void preservesExistingStatusCode() {
        try {
            when(response.getStatus()).thenReturn(404);
            when(response.getWriter()).thenReturn(writer);
            doThrow(new RuntimeException("Not found")).when(chain).doFilter(request, response);

            filter.doFilter(request, response, chain);

            verify(response).setStatus(404);
        } catch (Exception e) {
            fail("Should preserve existing status code: " + e.getMessage());
        }
    }
}