package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServletTest {

    @Mock private RegistrationService registrationService;
    @Mock private UserService userService;
    @Mock private ValidationUtils validationUtils;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private PrintWriter printWriter;
    @Spy private UserMapper userMapper = UserMapper.INSTANCE;
    private UserServlet userServlet;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        userServlet = new UserServlet(registrationService, userService, validationUtils, objectMapper);
        when(response.getWriter()).thenReturn(printWriter);
    }


    @Test
    @DisplayName("Register user - Success scenario")
    void testRegisterUser_Success() throws Exception {
        String requestBody = "{\"email\": \"test@example.com\", \"password\": \"password123\"}";
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(request.getPathInfo()).thenReturn("/registration");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateUserJson(any(), eq(Mode.REGISTER_USER))).thenReturn(userDto);
        when(registrationService.registerUser(any())).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(new User());

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(printWriter).write(contains("User registered successfully"));
    }

    @Test
    @DisplayName("Authenticate user - Success scenario")
    void testAuthenticateUser_Success() throws Exception {
        String requestBody = "{\"email\": \"test@example.com\", \"password\": \"password123\"}";
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(request.getPathInfo()).thenReturn("/authenticate");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateUserJson(any(), eq(Mode.AUTHENTICATE))).thenReturn(userDto);
        when(registrationService.authenticate(any())).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(new User());
        when(request.getSession()).thenReturn(session);

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Authentication successful"));
    }

    @Test
    @DisplayName("Get authenticated user - Success scenario")
    void testGetAuthenticatedUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(request.getPathInfo()).thenReturn("/me");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Authenticated user details"));
    }

    @Test
    @DisplayName("Update user - Success scenario")
    void testUpdateUser_Success() throws Exception {
        String requestBody = "{\"email\": \"updated@example.com\"}";
        UserDto sessionUser = new UserDto();
        sessionUser.setUserId(1L);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setEmail("updated@example.com");

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(sessionUser);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateUserJson(any(), eq(Mode.UPDATE_USER))).thenReturn(updatedUserDto);
        when(userService.updateOwnAccount(any(), anyLong())).thenReturn(true);
        when(userService.getUserByEmail("updated@example.com")).thenReturn(new User());

        userServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("User updated successfully"));
    }

    @Test
    @DisplayName("Delete account - Success scenario")
    void testDeleteAccount_Success() throws Exception {
        UserDto sessionUser = new UserDto();
        sessionUser.setUserId(2L);
        sessionUser.setEmail("test@example.com");

        User mockUser = new User();
        mockUser.setUserId(2L);
        mockUser.setEmail("test@example.com");

        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(sessionUser);
        when(userService.deleteOwnAccount(2L)).thenReturn(true);

        userServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Account deleted successfully"));
        verify(session).invalidate();
    }

    @Test
    @DisplayName("Invalid registration request - ValidationException")
    void testRegisterUser_ValidationException() throws Exception {
        String requestBody = "{\"email\": \"invalid\"}";

        when(request.getPathInfo()).thenReturn("/registration");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateUserJson(any(), eq(Mode.REGISTER_USER)))
                .thenThrow(new ValidationException("Invalid email format"));

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Invalid email format"));
    }

    @Test
    @DisplayName("User not authenticated - Get /me")
    void testGetAuthenticatedUser_NotLoggedIn() throws Exception {
        when(request.getPathInfo()).thenReturn("/me");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(null);

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write(contains("No user is currently logged in"));
    }

    @Test
    @DisplayName("Endpoint not found - POST request")
    void testDoPost_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        userServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }
}