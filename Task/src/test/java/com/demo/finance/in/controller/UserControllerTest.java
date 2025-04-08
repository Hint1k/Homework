package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.custom.DuplicateEmailException;
import com.demo.finance.exception.custom.ValidationException;
import com.demo.finance.out.service.JwtService;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    private UserDto createUserDto(Long userId, String email, String name) {
        UserDto userDto = Instancio.create(UserDto.class);
        userDto.setUserId(userId);
        userDto.setEmail(email);
        userDto.setName(name);
        return userDto;
    }

    private User createUser(Long userId, String email, String name) {
        User user = Instancio.create(User.class);
        user.setUserId(userId);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    @Test
    @DisplayName("Register user - Success scenario")
    void testRegisterUser_Success() throws Exception {
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
    }

    @Test
    @DisplayName("Authenticate user - Success scenario")
    void testAuthenticateUser_Success() throws Exception {
        UserDto validatedDto = createUserDto(null, "test@example.com", null);
        User user = createUser(1L, "test@example.com", null);
        UserDto responseDto = createUserDto(1L, "test@example.com", null);
        String expectedToken = "generated.jwt.token";

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE))).thenReturn(validatedDto);
        when(registrationService.authenticate(validatedDto)).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);
        when(jwtService.generateToken("test@example.com", List.of("user"), 1L))
                .thenReturn(expectedToken);

        mockMvc.perform(post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(header().string("Authorization", "Bearer " + expectedToken));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE));
        verify(registrationService, times(1)).authenticate(validatedDto);
        verify(userService, times(1)).getUserByEmail("test@example.com");
        verify(userMapper, times(1)).toDto(user);
        verify(jwtService, times(1))
                .generateToken("test@example.com", List.of("user"), 1L);
    }

    @Test
    @DisplayName("Get current user details - Success scenario")
    void testGetCurrentUser_Success() throws Exception {
        UserDto currentUser = createUserDto(1L, "test@example.com", "Test User");
        currentUser.setRole("USER");

        mockMvc.perform(get("/api/users/me")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Authenticated user details"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(jwtService, never()).validateToken(any());
    }

    @Test
    @DisplayName("Update user - Success scenario")
    void testUpdateUser_Success() throws Exception {
        UserDto currentUser = createUserDto(1L, "current@example.com", null);
        currentUser.setRole("USER");
        UserDto updateDto = createUserDto(null, "updated@example.com", "Updated Name");
        User updatedUser = createUser(1L, "updated@example.com", "Updated Name");
        UserDto responseDto = createUserDto(1L, "updated@example.com", "Updated Name");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER))).thenReturn(updateDto);
        when(userService.updateOwnAccount(any(UserDto.class), eq(1L))).thenReturn(true);
        when(userMapper.toDto(updatedUser)).thenReturn(responseDto);
        when(userService.getUserByEmail("updated@example.com")).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\",\"name\":\"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER));
        verify(userService, times(1)).updateOwnAccount(any(UserDto.class), eq(1L));
        verify(userService, times(1)).getUserByEmail("updated@example.com");
        verify(userMapper, times(1)).toDto(updatedUser);
    }

    @Test
    @DisplayName("Delete account - Success scenario")
    void testDeleteAccount_Success() throws Exception {
        UserDto currentUser = createUserDto(2L, "test@example.com", null);
        currentUser.setRole("USER");

        when(userService.deleteOwnAccount(2L)).thenReturn(true);

        mockMvc.perform(delete("/api/users")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService, times(1)).deleteOwnAccount(2L);
        verify(jwtService, never()).validateToken(any());
    }

    @Test
    @DisplayName("Invalid registration request - ValidationException")
    void testRegisterUser_ValidationException() throws Exception {
        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER)))
                .thenThrow(new ValidationException("Invalid email format"));

        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email format"));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER));
        verify(registrationService, never()).registerUser(any(UserDto.class));
        verify(userService, never()).getUserByEmail(anyString());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("User not authenticated - Get /me")
    void testGetCurrentUser_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("")));

        verify(jwtService, never()).validateToken(any());
    }

    @Test
    @DisplayName("Duplicate email - Registration")
    void testRegisterUser_DuplicateEmail() throws Exception {
        String content = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}";
        UserDto userDto = createUserDto(null, "test@example.com", "Test User");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER))).thenReturn(userDto);
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
        verify(userService, never()).getUserByEmail(anyString());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Register user - Failure to retrieve user after registration")
    void testRegisterUser_FailedToRetrieveUser() throws Exception {
        String content = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}";
        UserDto validatedDto = createUserDto(null, "test@example.com", "Test User");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER))).thenReturn(validatedDto);
        when(registrationService.registerUser(validatedDto)).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(null);

        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to retrieve user details."));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER));
        verify(registrationService, times(1)).registerUser(validatedDto);
        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("Register user - Registration service returns false")
    void testRegisterUser_RegistrationFailed() throws Exception {
        String content = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}";
        UserDto validatedDto = createUserDto(null, "test@example.com", "Test User");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER))).thenReturn(validatedDto);
        when(registrationService.registerUser(validatedDto)).thenReturn(false);

        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to register user."));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.REGISTER_USER));
        verify(registrationService, times(1)).registerUser(validatedDto);
        verify(userService, never()).getUserByEmail(anyString());
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

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE));
        verify(registrationService, times(1)).authenticate(validatedDto);
        verify(jwtService, never()).generateToken(any(), any(), any());
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

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.AUTHENTICATE));
        verify(registrationService, times(1)).authenticate(validatedDto);
        verify(userService, times(1)).getUserByEmail("test@example.com");
        verify(jwtService, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("Update user - Failed to retrieve updated user")
    void testUpdateUser_FailedToRetrieveUpdatedUser() throws Exception {
        UserDto currentUser = createUserDto(1L, "current@example.com", null);
        currentUser.setRole("USER");
        UserDto updateDto = createUserDto(null, "updated@example.com", "Updated Name");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER))).thenReturn(updateDto);
        when(userService.updateOwnAccount(any(UserDto.class), eq(1L))).thenReturn(true);
        when(userService.getUserByEmail("updated@example.com")).thenReturn(null);

        mockMvc.perform(put("/api/users")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\",\"name\":\"Updated Name\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error")
                        .value("Failed to retrieve updated user details."));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER));
        verify(userService, times(1)).updateOwnAccount(any(UserDto.class), eq(1L));
        verify(userService, times(1)).getUserByEmail("updated@example.com");
    }

    @Test
    @DisplayName("Update user - Update failed")
    void testUpdateUser_UpdateFailed() throws Exception {
        UserDto currentUser = createUserDto(1L, "current@example.com", null);
        currentUser.setRole("USER");
        UserDto updateDto = createUserDto(null, "updated@example.com", "Updated Name");

        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER))).thenReturn(updateDto);
        when(userService.updateOwnAccount(any(UserDto.class), eq(1L))).thenReturn(false);

        mockMvc.perform(put("/api/users")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\",\"name\":\"Updated Name\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to update account."));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.UPDATE_USER));
        verify(userService, times(1)).updateOwnAccount(any(UserDto.class), eq(1L));
        verify(userService, never()).getUserByEmail(anyString());
    }

    @Test
    @DisplayName("Delete account - Failed to delete")
    void testDeleteAccount_Failed() throws Exception {
        UserDto currentUser = createUserDto(2L, "test@example.com", null);
        currentUser.setRole("USER");

        when(userService.deleteOwnAccount(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/users")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to delete account."));

        verify(userService, times(1)).deleteOwnAccount(2L);
    }
}