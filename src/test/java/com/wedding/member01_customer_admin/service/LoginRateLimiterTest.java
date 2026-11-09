package com.wedding.member01_customer_admin.service;

import com.wedding.common.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimiterTest {

    @Test
    void limitsRepeatedLoginAttemptsForAccountAndClientAddress() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.10");

        for (int i = 0; i < 8; i++) {
            limiter.recordLoginAttempt(request, "person@example.com");
        }
        assertThrows(BusinessRuleException.class,
                () -> limiter.recordLoginAttempt(request, "PERSON@example.com"));
    }

    @Test
    void limitsOtpResendRequestsPerClientAddress() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.11");

        for (int i = 0; i < 10; i++) limiter.recordOtpResend(request);
        assertThrows(BusinessRuleException.class, () -> limiter.recordOtpResend(request));
    }
}
