package com.demo.finance.app;

import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskMainTest extends AbstractContainerBaseSetup {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }
}