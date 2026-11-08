package com.wedding.member01_customer_admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Development / demo sender: prints the SMS to the server console instead of sending it,
 * so the OTP flow can be tested without an SMS account. Never use this in production.
 *
 * @author Member 01
 */
@Service
@ConditionalOnProperty(name = "eclat.sms.provider", havingValue = "console", matchIfMissing = true)
public class ConsoleSmsService implements SmsService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleSmsService.class);

    @Override
    public void send(String phone, String message) {
        LOG.warn("[SMS console mode - nothing was sent] to {}: {}", phone, message);
    }
}
