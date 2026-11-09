package com.wedding.member01_customer_admin.service;

/**
 * Sends a text message. Implementations are chosen with the
 * {@code eclat.sms.provider} property (console | notifylk).
 *
 * @author Member 01
 */
public interface SmsService {

    /**
     * @param phone   the customer's phone number as stored (e.g. 0771234567 or +94771234567)
     * @param message plain-text message body
     * @throws com.wedding.common.exceptions.BusinessRuleException if the message could not be sent
     */
    void send(String phone, String message);
}
