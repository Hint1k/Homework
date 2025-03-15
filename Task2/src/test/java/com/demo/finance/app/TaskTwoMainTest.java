package com.demo.finance.app;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskTwoMainTest {

    @BeforeAll
    static void setupEnvPath() {
        System.setProperty("ENV_PATH", "src/test/resources/.env");
    }

    @AfterAll
    static void resetEnvPath() {
        System.clearProperty("ENV_PATH");
    }

}