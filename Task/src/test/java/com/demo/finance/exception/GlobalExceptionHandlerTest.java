package com.demo.finance.exception;

import com.demo.finance.app.TaskMain;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.app.config.LiquibaseManager;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.in.controller.AdminController;
import com.demo.finance.in.controller.BudgetController;
import com.demo.finance.in.controller.GoalController;
import com.demo.finance.in.controller.NotificationController;
import com.demo.finance.in.controller.ReportController;
import com.demo.finance.in.controller.TransactionController;
import com.demo.finance.in.controller.UserController;
import com.demo.finance.out.service.JwtService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {TaskMain.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminController adminController;
    @MockBean
    private UserController userController;
    @MockBean
    private GoalController goalController;
    @MockBean
    private TransactionController transactionController;
    @MockBean
    private BudgetController budgetController;
    @MockBean
    private NotificationController notificationController;
    @MockBean
    private ReportController reportController;
    @MockBean
    private DatabaseConfig databaseConfig;
    @MockBean
    private DataSource dataSource;
    @MockBean
    private LiquibaseManager liquibaseManager;
    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        UserDto userDto = Instancio.create(UserDto.class);
        userDto.setRole("USER");
        when(jwtService.validateToken(anyString())).thenReturn(userDto);
    }

    @Test
    @DisplayName("Test handling of malformed JSON with extra comma")
    void testHandleMalformedJson() throws Exception {
        String extraComma = "{\"name\": \"jay\", \"email\": \"jay@demo.com\", \"password\": \"123\",}";
        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(extraComma))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid JSON format"))
                .andExpect(jsonPath("$.details")
                        .value("Malformed JSON: Check for missing commas or syntax errors"));
    }

    @Test
    @DisplayName("Test handling of JSON with missing comma")
    void testHandleMissingComma() throws Exception {
        String missingComma = "{\"name\": \"jay\" \"email\": \"jay@demo.com\", \"password\": \"123\"}";
        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingComma))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details")
                        .value("Malformed JSON: Check for missing commas or syntax errors"));
    }

    @Test
    @DisplayName("Test handling of JSON with invalid structure (missing bracket)")
    void testHandleInvalidJsonStructure() throws Exception {
        String missingBracket = "{\"name\": \"jay\", \"email\": \"jay@demo.com\", \"password\": \"123\"";
        mockMvc.perform(post("/api/users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingBracket))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details")
                        .value("Malformed request body"));
    }

    @Test
    @DisplayName("Test handling of JSON with invalid date format")
    void testHandleInvalidDateFormat() throws Exception {
        String jsonWithInvalidDate = """
                {
                  "fromDate": "2025/01/01",
                  "toDate": "2025/06/30"
                }
                """;

        mockMvc.perform(post("/api/reports/by-date")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer fake-jwt-token")
                        .content(jsonWithInvalidDate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid JSON format"))
                .andExpect(jsonPath("$.details")
                        .value("Invalid date format. Expected: yyyy-MM-dd (e.g., 2025-01-01)"));
    }

    @Test
    @DisplayName("Test handling of JSON with invalid date value (e.g., month 13 or day 32)")
    void testHandleInvalidDateValue() throws Exception {
        String jsonWithInvalidDay = """
                {
                  "fromDate": "2025-01-01",
                  "toDate": "2025-12-33"
                }
                """;

        mockMvc.perform(post("/api/reports/by-date")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer fake-jwt-token")
                        .content(jsonWithInvalidDay))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid JSON format"))
                .andExpect(jsonPath("$.details")
                        .value("Invalid date value (e.g., month 13 or day 32)"));
    }
}