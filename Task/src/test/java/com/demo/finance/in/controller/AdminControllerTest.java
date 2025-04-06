package com.demo.finance.in.controller;

import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.AdminService;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.UserService;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;
    @Mock
    private AdminService adminService;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private TransactionService transactionService;
    @Mock
    private ValidationUtils validationUtils;
    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    private UserDto createUserDto(Long userId, String email, String name) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setEmail(email);
        userDto.setName(name);
        return userDto;
    }

    private TransactionDto createTransactionDto() {
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(1L);
        dto.setDescription("Test transaction");
        return dto;
    }

    private PaginationParams createPaginationParams() {
        return new PaginationParams(1, 10);
    }

    @Test
    @DisplayName("Get paginated users - Success scenario")
    void testGetPaginatedUsers_Success() throws Exception {
        PaginationParams params = createPaginationParams();
        PaginatedResponse<UserDto> response = new PaginatedResponse<>(
                List.of(createUserDto(1L, "user1@example.com", "User One")),
                10, 1, 1, 10);

        when(validationUtils.validateRequest(any(PaginationParams.class), eq(Mode.PAGE))).thenReturn(params);
        when(userService.getPaginatedUsers(1, 10)).thenReturn(response);

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.metadata.totalItems").value(10));

        verify(validationUtils, times(1))
                .validateRequest(any(PaginationParams.class), eq(Mode.PAGE));
        verify(userService, times(1)).getPaginatedUsers(1, 10);
    }

    @Test
    @DisplayName("Get user transactions - Success scenario")
    void testGetUserTransactions_Success() throws Exception {
        Long userId = 2L;
        PaginationParams params = createPaginationParams();
        PaginatedResponse<TransactionDto> response = new PaginatedResponse<>(
                List.of(createTransactionDto()),
                5, 1, 1, 10);

        when(validationUtils.parseUserId("2", Mode.GET)).thenReturn(userId);
        when(validationUtils.validateRequest(any(PaginationParams.class), eq(Mode.PAGE))).thenReturn(params);
        when(transactionService.getPaginatedTransactionsForUser(userId, 1, 10)).thenReturn(response);

        mockMvc.perform(get("/api/admin/users/transactions/2")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.metadata.user_id").value(2));

        verify(validationUtils, times(1)).parseUserId("2", Mode.GET);
        verify(validationUtils, times(1))
                .validateRequest(any(PaginationParams.class), eq(Mode.PAGE));
        verify(transactionService, times(1))
                .getPaginatedTransactionsForUser(userId, 1, 10);
    }

    @Test
    @DisplayName("Block/unblock user - Success scenario")
    void testBlockOrUnblockUser_Success() throws Exception {
        Long userId = 2L;
        UserDto userDto = createUserDto(userId, "user@example.com", null);
        userDto.setBlocked(true);

        when(validationUtils.parseUserId("2", Mode.BLOCK_UNBLOCK)).thenReturn(userId);
        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.BLOCK_UNBLOCK))).thenReturn(userDto);
        when(adminService.blockOrUnblockUser(userId, userDto)).thenReturn(true);

        mockMvc.perform(patch("/api/admin/users/block/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"blocked\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("User blocked/unblocked status changed successfully"));

        verify(validationUtils, times(1)).parseUserId("2", Mode.BLOCK_UNBLOCK);
        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.BLOCK_UNBLOCK));
        verify(adminService, times(1)).blockOrUnblockUser(userId, userDto);
    }

    @Test
    @DisplayName("Update user role - Success scenario")
    void testUpdateUserRole_Success() throws Exception {
        Long userId = 2L;
        UserDto userDto = createUserDto(userId, "user@example.com", null);

        when(validationUtils.parseUserId("2", Mode.UPDATE_ROLE)).thenReturn(userId);
        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_ROLE))).thenReturn(userDto);
        when(adminService.updateUserRole(userId, userDto)).thenReturn(true);

        mockMvc.perform(patch("/api/admin/users/role/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User role updated successfully"));

        verify(validationUtils, times(1)).parseUserId("2", Mode.UPDATE_ROLE);
        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.UPDATE_ROLE));
        verify(adminService, times(1)).updateUserRole(userId, userDto);
    }

    @Test
    @DisplayName("Delete user - Success scenario")
    void testDeleteUser_Success() throws Exception {
        Long userId = 2L;
        when(validationUtils.parseUserId("2", Mode.DELETE)).thenReturn(userId);
        when(adminService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"))
                .andExpect(jsonPath("$.data.userId").value(2));

        verify(validationUtils, times(1)).parseUserId("2", Mode.DELETE);
        verify(adminService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Invalid pagination - ValidationException")
    void testGetPaginatedUsers_ValidationException() throws Exception {
        when(validationUtils.validateRequest(any(PaginationParams.class), eq(Mode.PAGE)))
                .thenThrow(new ValidationException("Invalid page number"));

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid pagination parameters: Invalid page number"));

        verify(validationUtils, times(1))
                .validateRequest(any(PaginationParams.class), eq(Mode.PAGE));
    }

    @Test
    @DisplayName("User not found - GET request")
    void testGetUserDetails_UserNotFound() throws Exception {
        Long userId = 2L;
        when(validationUtils.parseUserId("2", Mode.GET)).thenReturn(userId);
        when(adminService.getUser(userId)).thenReturn(null);

        mockMvc.perform(get("/api/admin/users/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found."));

        verify(validationUtils, times(1)).parseUserId("2", Mode.GET);
        verify(adminService, times(1)).getUser(userId);
    }

    @Test
    @DisplayName("Get user details - Success scenario")
    void testGetUserDetails_Success() throws Exception {
        Long userId = 2L;
        User user = new User();
        user.setUserId(userId);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(new Role("user"));
        user.setVersion(1L);

        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setEmail("test@example.com");
        userDto.setRole(new Role("user"));

        when(validationUtils.parseUserId("2", Mode.GET)).thenReturn(userId);
        when(adminService.getUser(userId)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/admin/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User details"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(validationUtils, times(1)).parseUserId("2", Mode.GET);
        verify(adminService, times(1)).getUser(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("Block/unblock user - Service returns false")
    void testBlockOrUnblockUser_Failure() throws Exception {
        Long userId = 2L;
        UserDto userDto = createUserDto(userId, "user@example.com", null);
        userDto.setBlocked(true);

        when(validationUtils.parseUserId("2", Mode.BLOCK_UNBLOCK)).thenReturn(userId);
        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.BLOCK_UNBLOCK))).thenReturn(userDto);
        when(adminService.blockOrUnblockUser(userId, userDto)).thenReturn(false);

        mockMvc.perform(patch("/api/admin/users/block/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"blocked\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to block/unblock user."));

        verify(validationUtils, times(1)).parseUserId("2", Mode.BLOCK_UNBLOCK);
        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.BLOCK_UNBLOCK));
        verify(adminService, times(1)).blockOrUnblockUser(userId, userDto);
    }

    @Test
    @DisplayName("Update user role - Service returns false")
    void testUpdateUserRole_Failure() throws Exception {
        Long userId = 2L;
        UserDto userDto = createUserDto(userId, "user@example.com", null);

        when(validationUtils.parseUserId("2", Mode.UPDATE_ROLE)).thenReturn(userId);
        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.UPDATE_ROLE))).thenReturn(userDto);
        when(adminService.updateUserRole(userId, userDto)).thenReturn(false);

        mockMvc.perform(patch("/api/admin/users/role/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"ADMIN\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Failed to update role."));

        verify(validationUtils, times(1)).parseUserId("2", Mode.UPDATE_ROLE);
        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.UPDATE_ROLE));
        verify(adminService, times(1)).updateUserRole(userId, userDto);
    }

    @Test
    @DisplayName("Delete user - Service returns false")
    void testDeleteUser_Failure() throws Exception {
        Long userId = 2L;
        when(validationUtils.parseUserId("2", Mode.DELETE)).thenReturn(userId);
        when(adminService.deleteUser(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/users/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found."));

        verify(validationUtils, times(1)).parseUserId("2", Mode.DELETE);
        verify(adminService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Get user transactions - Invalid user ID format")
    void testGetUserTransactions_InvalidUserId() throws Exception {
        when(validationUtils.parseUserId("invalid", Mode.GET))
                .thenThrow(new ValidationException("Invalid user ID"));

        mockMvc.perform(get("/api/admin/users/transactions/invalid")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid user ID"));

        verify(validationUtils, times(1)).parseUserId("invalid", Mode.GET);
        verify(transactionService, never()).getPaginatedTransactionsForUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Block/unblock user - Invalid request body")
    void testBlockOrUnblockUser_InvalidRequest() throws Exception {
        when(validationUtils.validateRequest(any(UserDto.class), eq(Mode.BLOCK_UNBLOCK)))
                .thenThrow(new ValidationException("Blocked status required"));

        mockMvc.perform(patch("/api/admin/users/block/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Blocked status required"));

        verify(validationUtils, times(1))
                .validateRequest(any(UserDto.class), eq(Mode.BLOCK_UNBLOCK));
        verify(adminService, never()).blockOrUnblockUser(anyLong(), any(UserDto.class));
    }
}