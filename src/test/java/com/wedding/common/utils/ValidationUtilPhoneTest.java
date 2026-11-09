package com.wedding.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilPhoneTest {

    @Test
    void acceptsSriLankanMobileNumbersInLocalAndInternationalFormats() {
        assertTrue(ValidationUtil.isValidPhone("0771234567"));
        assertTrue(ValidationUtil.isValidPhone("0712345678"));
        assertTrue(ValidationUtil.isValidPhone("+94771234567"));
    }

    @Test
    void rejectsInvalidOrNonSriLankanPhoneNumbers() {
        assertFalse(ValidationUtil.isValidPhone("07777775424242"));
        assertFalse(ValidationUtil.isValidPhone("1234567890"));
        assertFalse(ValidationUtil.isValidPhone("94771234567"));
        assertFalse(ValidationUtil.isValidPhone("+9477123456"));
        assertFalse(ValidationUtil.isValidPhone("07712 34567"));
        assertFalse(ValidationUtil.isValidPhone(null));
        assertFalse(ValidationUtil.isValidPhone(""));
    }
    @Test
    void enforcesMinimumEightCharacterPasswords() {
        assertFalse(ValidationUtil.isValidPassword("1234567"));
        assertTrue(ValidationUtil.isValidPassword("12345678"));
        assertTrue(ValidationUtil.isValidPassword("secret123"));
        assertFalse(ValidationUtil.isValidPassword(null));
    }

}
