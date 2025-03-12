package com.demo.finance.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @InjectMocks private ApplicationConfig applicationConfig;

    @Test
    @DisplayName("Load admin properties - verifies default admin configurations")
    void testLoadAdminProperties() {
        try {
            Properties adminProps = getPrivateAdminProperties(applicationConfig);

            assertThat(adminProps.getProperty("admin.id")).isEqualTo("1");
            assertThat(adminProps.getProperty("admin.email")).isEqualTo("admin@demo.com");
            assertThat(adminProps.getProperty("admin.name")).isEqualTo("Default Admin");
            assertThat(adminProps.getProperty("admin.password")).isEqualTo("123");
            assertThat(adminProps.getProperty("admin.role")).isEqualTo("admin");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail("testLoadAdminProperties() failed: " + e.getMessage());
        }
    }

    private Properties getPrivateAdminProperties(ApplicationConfig config) throws Exception {
        var field = ApplicationConfig.class.getDeclaredField("adminProperties");
        field.setAccessible(true);
        return (Properties) field.get(config);
    }
}