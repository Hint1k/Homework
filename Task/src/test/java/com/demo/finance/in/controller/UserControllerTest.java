package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.DuplicateEmailException;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private UserService userService;
    @Mock
    private ValidationUtils validationUtils;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    private UserDto createUserDto(Long userId, String email, String name) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setEmail(email);
        userDto.setName(name);
        return userDto;
    }

    private User createUser(Long userId, String email, String name) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    @Test
    @DisplayName("Register user - Success scenario")
    void testRegisterUser_Success() {
        try {
            String content = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}";
            UserDto validatedDto = createUserDto(null, "test@example.com", "Test User");
            User user = createUser(null, "test@example.com", "Test User");
            UserDto responseDto = createUserDto(null, "test@example.com", "Test User");

            when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER))).thenReturn(validatedDto);
            when(registrationService.registerUser(validatedDto)).thenReturn(true);
            when(userService.getUserByEmail("test@example.com")).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(responseDto);

            mockMvc.perform(post("/api/users/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("User registered successfully"))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"));

            verify(validationUtils, times(1))
                    .validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER));
            verify(registrationService, times(1)).registerUser(validatedDto);
            verify(userService, times(1)).getUserByEmail("test@example.com");
            verify(userMapper, times(1)).toDto(user);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Authenticate user - Success scenario")
    void testAuthenticateUser_Success() {
        try {
            UserDto validatedDto = createUserDto(null, "test@example.com", null);
            User user = createUser(null, "test@example.com", null);
            UserDto responseDto = createUserDto(null, "test@example.com", null);

            when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE))).thenReturn(validatedDto);
            when(registrationService.authenticate(validatedDto)).thenReturn(true);
            when(userService.getUserByEmail("test@example.com")).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(responseDto);

            mockMvc.perform(post("/api/users/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Authentication successful"))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"));

            verify(validationUtils, times(1))
                    .validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE));
            verify(registrationService, times(1)).authenticate(validatedDto);
            verify(userService, times(1)).getUserByEmail("test@example.com");
            verify(userMapper, times(1)).toDto(user);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Get current user details - Success scenario")
    void testGetCurrentUser_Success() {
        try {
            UserDto currentUser = createUserDto(null, "test@example.com", null);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("currentUser", currentUser);

            mockMvc.perform(get("/api/users/me")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Authenticated user details"))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Update user - Success scenario")
    void testUpdateUser_Success() {
        try {
            UserDto currentUser = createUserDto(1L, "current@example.com", null);
            UserDto updateDto = createUserDto(null, "updated@example.com", "Updated Name");
            User updatedUser = createUser(1L, "updated@example.com", "Updated Name");
            UserDto responseDto = createUserDto(null, "updated@example.com", "Updated Name");

            MockHttpSession session = new MockHttpSession();
            session.setAttribute("currentUser", currentUser);

            when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER))).thenReturn(updateDto);
            when(userService.updateOwnAccount(any(UserDto.class), eq(1L))).thenReturn(true);
            when(userMapper.toDto(updatedUser)).thenReturn(responseDto);
            when(userService.getUserByEmail("updated@example.com")).thenReturn(updatedUser);

            mockMvc.perform(put("/api/users")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"updated@example.com\",\"name\":\"Updated Name\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User updated successfully"))
                    .andExpect(jsonPath("$.data.email").value("updated@example.com"));

            verify(validationUtils, times(1)).validateRequest(any(), eq(Mode.UPDATE_USER));
            verify(userService, times(1)).updateOwnAccount(any(), eq(1L));
            verify(userService, times(1)).getUserByEmail("updated@example.com");
            verify(userMapper, times(1)).toDto(updatedUser);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Delete account - Success scenario")
    void testDeleteAccount_Success() {
        try {
            UserDto currentUser = createUserDto(2L, "test@example.com", null);
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("currentUser", currentUser);

            when(userService.deleteOwnAccount(2L)).thenReturn(true);

            mockMvc.perform(delete("/api/users")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Account deleted successfully"))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"));

            verify(userService, times(1)).deleteOwnAccount(2L);
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Invalid registration request - ValidationException")
    void testRegisterUser_ValidationException() {
        try {
            when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER)))
                    .thenThrow(new ValidationException("Invalid email format"));

            mockMvc.perform(post("/api/users/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"invalid\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid email format"));

            verify(validationUtils, times(1))
                    .validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("User not authenticated - Get /me")
    void testGetCurrentUser_NotAuthenticated() {
        try {
            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("")));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Duplicate email - Registration")
    void testRegisterUser_DuplicateEmail() {
        try {
            String content = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}";
            UserDto userDto = createUserDto(null, "test@example.com", "Test User");

            when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER)))
                    .thenReturn(userDto);
            when(registrationService.registerUser(any(UserDto.class)))
                    .thenThrow(new DuplicateEmailException("Email already exists"));

            mockMvc.perform(post("/api/users/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Email already exists"));

            verify(validationUtils, times(1))
                    .validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER));
            verify(registrationService, times(1)).registerUser(any(UserDto.class));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Register user - Failure to retrieve user after registration")
    void testRegisterUser_FailedToRetrieveUser() throws Exception {
        UserDto validatedDto = createUserDto(null, "test@example.com", "Test User");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER))).thenReturn(validatedDto);
        when(registrationService.registerUser(validatedDto)).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to retrieve user details."));
    }

    @Test
    @DisplayName("Register user - Registration service returns false")
    void testRegisterUser_RegistrationFailed() throws Exception {
        UserDto validatedDto = createUserDto(null, "test@example.com", "Test User");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER))).thenReturn(validatedDto);
        when(registrationService.registerUser(validatedDto)).thenReturn(false);

        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to register user."));
    }

    @Test
    @DisplayName("Authenticate user - Invalid credentials")
    void testAuthenticateUser_InvalidCredentials() throws Exception {
        UserDto validatedDto = createUserDto(null, "test@example.com", null);

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE))).thenReturn(validatedDto);
        when(registrationService.authenticate(validatedDto)).thenReturn(false);

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials."));
    }

    @Test
    @DisplayName("Authenticate user - Failed to retrieve user")
    void testAuthenticateUser_FailedToRetrieveUser() throws Exception {
        UserDto validatedDto = createUserDto(null, "test@example.com", null);

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE))).thenReturn(validatedDto);
        when(registrationService.authenticate(validatedDto)).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to retrieve user details."));
    }

    @Test
    @DisplayName("Update user - Failed to retrieve updated user")
    void testUpdateUser_FailedToRetrieveUpdatedUser() throws Exception {
        UserDto currentUser = createUserDto(1L, "current@example.com", null);
        UserDto updateDto = createUserDto(null, "updated@example.com", "Updated Name");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", currentUser);

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER))).thenReturn(updateDto);
        when(userService.updateOwnAccount(any(UserDto.class), eq(1L))).thenReturn(true);
        when(userService.getUserByEmail("updated@example.com")).thenReturn(null);

        mockMvc.perform(put("/api/users")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\",\"name\":\"Updated Name\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to retrieve updated user details."));
    }

    @Test
    @DisplayName("Update user - Update failed")
    void testUpdateUser_UpdateFailed() throws Exception {
        UserDto currentUser = createUserDto(1L, "current@example.com", null);
        UserDto updateDto = createUserDto(null, "updated@example.com", "Updated Name");

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", currentUser);

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER))).thenReturn(updateDto);
        when(userService.updateOwnAccount(any(UserDto.class), eq(1L))).thenReturn(false);

        mockMvc.perform(put("/api/users")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\",\"name\":\"Updated Name\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to update account."));
    }

    @Test
    @DisplayName("Delete account - Failed to delete")
    void testDeleteAccount_Failed() throws Exception {
        UserDto currentUser = createUserDto(2L, "test@example.com", null);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", currentUser);

        when(userService.deleteOwnAccount(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/users")
                        .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to delete account."));
    }

    @Test
    @DisplayName("Logout - Success scenario")
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/api/users/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}