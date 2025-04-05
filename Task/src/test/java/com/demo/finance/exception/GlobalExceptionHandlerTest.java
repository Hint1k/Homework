package com.demo.finance.exception;

import com.demo.finance.app.TaskMain;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.app.config.LiquibaseManager;
import com.demo.finance.in.controller.AdminController;
import com.demo.finance.in.controller.BudgetController;
import com.demo.finance.in.controller.GoalController;
import com.demo.finance.in.controller.NotificationController;
import com.demo.finance.in.controller.ReportController;
import com.demo.finance.in.controller.TransactionController;
import com.demo.finance.in.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.sql.DataSource;

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

    @Test
    void testHandleInvalidJson() throws Exception {
        String malformedJson = "{key: 'value', extraComma: ,}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid JSON format"))
                .andExpect(jsonPath("$.details").value("Malformed request body"));
    }
}