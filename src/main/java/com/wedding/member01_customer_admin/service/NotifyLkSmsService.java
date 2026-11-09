package com.wedding.member01_customer_admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedding.common.exceptions.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Notify.lk SMS API v1 integration. Credentials and approved sender ID are
 * supplied only through environment-backed configuration; secrets are never logged.
 */
@Service
@ConditionalOnProperty(name = "eclat.sms.provider", havingValue = "notifylk")
public class NotifyLkSmsService implements SmsService {
    private static final Logger LOG = LoggerFactory.getLogger(NotifyLkSmsService.class);
    private static final String OFFICIAL_ENDPOINT = "https://app.notify.lk/api/v1/send";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String url;
    private final String userId;
    private final String apiKey;
    private final String senderId;

    public NotifyLkSmsService(
            @Value("${eclat.sms.notifylk.url:https://app.notify.lk/api/v1/send}") String url,
            @Value("${eclat.sms.notifylk.user-id:}") String userId,
            @Value("${eclat.sms.notifylk.api-key:}") String apiKey,
            @Value("${eclat.sms.notifylk.sender-id:}") String senderId) {
        this.url = url == null || url.isBlank() ? OFFICIAL_ENDPOINT : url.trim();
        this.userId = userId == null ? "" : userId.trim();
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.senderId = senderId == null ? "" : senderId.trim();
        if (!OFFICIAL_ENDPOINT.equals(this.url)) {
            throw new IllegalArgumentException("Notify.lk endpoint must be the official HTTPS API endpoint.");
        }
        if (this.userId.isBlank() || this.apiKey.isBlank() || this.senderId.isBlank()) {
            throw new IllegalStateException("Notify.lk requires NOTIFYLK_USER_ID, NOTIFYLK_API_KEY and NOTIFYLK_SENDER_ID.");
        }
        if ("NotifyDEMO".equalsIgnoreCase(this.senderId)) {
            throw new IllegalStateException("NotifyDEMO must not be used for OTP or production messages. Configure your approved Sender ID.");
        }
    }

    @Override
    public void send(String phone, String message) {
        if (message == null || message.isBlank() || message.length() > 621) {
            throw new BusinessRuleException("SMS message is empty or exceeds Notify.lk's 621-character limit.");
        }
        String recipient = toInternational(phone);
        String body = form("user_id", userId) + "&" + form("api_key", apiKey)
                + "&" + form("sender_id", senderId) + "&" + form("to", recipient)
                + "&" + form("message", message);
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(12))
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() / 100 != 2) {
                LOG.warn("Notify.lk returned HTTP status {}", response.statusCode());
                throw deliveryFailure();
            }
            JsonNode json = objectMapper.readTree(response.body());
            if (!"success".equalsIgnoreCase(json.path("status").asText())) {
                LOG.warn("Notify.lk rejected SMS request (status field was not success).");
                throw deliveryFailure();
            }
        } catch (IOException ex) {
            LOG.warn("Unable to communicate with Notify.lk SMS API.", ex);
            throw deliveryFailure();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw deliveryFailure();
        }
    }

    /** Converts 0771234567 or +94771234567 to Notify.lk's 94771234567 format. */
    static String toInternational(String phone) {
        if (phone == null) throw new IllegalArgumentException("Recipient phone number is required.");
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("0") && digits.length() == 10) return "94" + digits.substring(1);
        if (digits.startsWith("94") && digits.length() == 11) return digits;
        throw new IllegalArgumentException("Notify.lk requires a valid Sri Lankan mobile number.");
    }

    private static String form(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static BusinessRuleException deliveryFailure() {
        return new BusinessRuleException("We could not send the verification code. Please try again.");
    }
}
