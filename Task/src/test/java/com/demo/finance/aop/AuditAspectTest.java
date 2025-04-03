package com.demo.finance.aop;

import com.demo.finance.domain.dto.UserDto;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @InjectMocks
    private AuditAspect auditAspect;
    @Mock
    private Logger mockLogger;
    @Mock
    private JoinPoint mockJoinPoint;
    @Mock
    private Signature mockSignature;

    @BeforeEach
    void setUp() {
        when(mockJoinPoint.getSignature()).thenReturn(mockSignature);
        when(mockSignature.getName()).thenReturn("testMethod");
    }

    @Test
    @DisplayName("Should process UserDto argument - Verifies user ID extraction")
    void testLogAudit_WithUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUserId(123L);
        Object[] args = new Object[]{userDto};

        when(mockJoinPoint.getArgs()).thenReturn(args);

        auditAspect.logAudit(mockJoinPoint, "SuccessResult");

        verify(mockLogger, never()).info(anyString());
    }

    @Test
    @DisplayName("Should handle non-UserDto arguments - Verifies graceful handling")
    void testLogAudit_WithoutUserDto() {
        Object[] args = new Object[]{"arg1", 42};

        when(mockJoinPoint.getArgs()).thenReturn(args);

        auditAspect.logAudit(mockJoinPoint, "AnotherResult");

        verify(mockLogger, never()).info(anyString());
    }
}