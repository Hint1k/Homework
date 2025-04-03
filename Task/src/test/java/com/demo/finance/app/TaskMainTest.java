package com.demo.finance.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TaskMainTest {

    @BeforeAll
    static void setup() {
        System.setProperty("ENV_PATH", "src/test/resources/.env");
        System.setProperty("YML_PATH", "src/test/resources/application.yml");
    }

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }
}