package com.wedding.member01_customer_admin.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotifyLkSmsServiceTest {

    @Test
    void normalizesSriLankanNumbersForNotifyGateway() {
        assertEquals("94771234567", NotifyLkSmsService.toInternational("0771234567"));
        assertEquals("94771234567", NotifyLkSmsService.toInternational("+94771234567"));
    }

    @Test
    void rejectsInvalidRecipientNumbers() {
        assertThrows(IllegalArgumentException.class,
                () -> NotifyLkSmsService.toInternational("1234567890"));
        assertThrows(IllegalArgumentException.class,
                () -> NotifyLkSmsService.toInternational(null));
    }

    @Test
    void refusesDemoSenderIdForOtpDelivery() {
        assertThrows(IllegalStateException.class, () ->
                new NotifyLkSmsService(
                        "https://app.notify.lk/api/v1/send",
                        "test-user",
                        "test-api-key",
                        "NotifyDEMO"));
    }
}
