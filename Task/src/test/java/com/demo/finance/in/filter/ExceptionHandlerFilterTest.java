package com.demo.finance.in.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    @DisplayName("Normal request should pass through filter chain without modification")
    void normalRequest_ShouldPassThrough() throws ServletException, IOException {
        ArgumentCaptor<ExceptionHandlerFilter.ResponseWrapper> responseCaptor =
                ArgumentCaptor.forClass(ExceptionHandlerFilter.ResponseWrapper.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(Mockito.eq(request), responseCaptor.capture());
        assertThat(responseCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("Should return 500 with error message when exception occurs")
    void thrownException_ShouldReturn500() throws ServletException, IOException {
        when(response.getWriter()).thenReturn(writer);
        ExceptionHandlerFilter.ResponseWrapper realResponseWrapper = new ExceptionHandlerFilter.ResponseWrapper(response);
        ExceptionHandlerFilter spyFilter = Mockito.spy(filter);
        when(spyFilter.createResponseWrapper(response)).thenReturn(realResponseWrapper);
        doThrow(new RuntimeException("Test error")).when(chain).doFilter(request, realResponseWrapper);

        spyFilter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(writer).write("{\"error\": \"Test error\"}");
    }

    @Test
    @DisplayName("Should still set status code when error occurs during response writing")
    void errorDuringHandling_ShouldStillRespond() throws ServletException, IOException {
        when(response.getWriter()).thenThrow(new IOException("Writer failed"));
        doThrow(new RuntimeException("Original error")).when(chain).doFilter(request, response);

        filter.doFilter(request, response, chain);

        verify(response, times(2)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should preserve existing HTTP status code when exception occurs")
    void preservesExistingStatusCode() throws ServletException, IOException {
        when(response.getStatus()).thenReturn(404);
        when(response.getWriter()).thenReturn(writer);
        doThrow(new RuntimeException("Not found")).when(chain).doFilter(request, response);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(404);
    }
}