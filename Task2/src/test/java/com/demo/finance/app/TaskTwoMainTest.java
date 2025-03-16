//package com.demo.finance.app;
//
//import com.demo.finance.app.config.ApplicationConfig;
//import com.demo.finance.app.config.DatabaseConfig;
//import com.demo.finance.app.config.LiquibaseManager;
//import com.demo.finance.in.cli.CliHandler;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import sun.misc.Unsafe;
//
//import java.lang.reflect.Field;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TaskTwoMainTest {
//
//    private DatabaseConfig databaseConfigMock;
//    private LiquibaseManager liquibaseManagerMock;
//
//    @Mock
//    private ApplicationConfig applicationConfig;
//    @Mock private CliHandler cliHandler;
//
//    @BeforeAll
//    static void setupEnvPath() {
//        System.setProperty("ENV_PATH", "src/test/resources/.env");
//    }
//
//    @AfterAll
//    static void resetEnvPath() {
//        System.clearProperty("ENV_PATH");
//    }
//
//    @BeforeEach
//    void setup() throws Exception {
//        // 1. Mock DatabaseConfig to prevent real .env loading
//        databaseConfigMock = mock(DatabaseConfig.class);
//        when(databaseConfigMock.getDbUrl()).thenReturn("jdbc:postgresql://test-db:5432/testdb");
//        when(databaseConfigMock.getDbUsername()).thenReturn("testuser");
//        when(databaseConfigMock.getDbPassword()).thenReturn("testpass");
//
//        // 2. Spy on LiquibaseManager but override runMigrations()
//        liquibaseManagerMock = spy(new LiquibaseManager(databaseConfigMock));
//        doNothing().when(liquibaseManagerMock).runMigrations(); // Prevent actual DB call
//
//        // 3. Inject the mocked instances into TaskTwoMain
//        replaceFinalDatabaseConfigMock();
//    }
//
//    @Test
//    @DisplayName("Verify that database migrations and CLI start correctly")
//    void testMainExecution() {
//        // Mock behavior for ApplicationConfig
//        when(applicationConfig.getCliHandler()).thenReturn(cliHandler);
//
//        // Run the application
//        TaskTwoMain.main(new String[]{});
//
//        // Verify that database migrations are executed (mocked, so no real DB call happens)
//        verify(liquibaseManagerMock, times(1)).runMigrations();
//
//        // Verify CLI is started
//        verify(cliHandler, times(1)).start();
//    }
//
//    /**
//     * Uses Unsafe to replace the final static field INSTANCE in DatabaseConfig.
//     */
//    private void replaceFinalDatabaseConfigMock() throws Exception {
//        Field instanceField = DatabaseConfig.class.getDeclaredField("INSTANCE");
//        instanceField.setAccessible(true);
//
//        // Get Unsafe instance
//        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
//        unsafeField.setAccessible(true);
//        Unsafe unsafe = (Unsafe) unsafeField.get(null);
//
//        // Modify the final static field
//        unsafe.putObjectVolatile(DatabaseConfig.class, unsafe.staticFieldOffset(instanceField), databaseConfigMock);
//    }
//}