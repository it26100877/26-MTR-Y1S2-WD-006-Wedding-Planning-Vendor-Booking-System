package com.wedding.member01_customer_admin.service;

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
import java.util.Locale;

/**
 * Real SMS sender for the Notify.lk gateway (Sri Lanka). Enable with
 * {@code eclat.sms.provider=notifylk} and fill in the credentials in application.properties.
 * Check the request fields against your gateway account's API documentation.
 *
 * @author Member 01
 */
@Service
@ConditionalOnProperty(name = "eclat.sms.provider", havingValue = "notifylk")
public class NotifyLkSmsService implements SmsService {

    private static final Logger LOG = LoggerFactory.getLogger(NotifyLkSmsService.class);

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
    private final String url;
    private final String userId;
    private final String apiKey;
    private final String senderId;

    public NotifyLkSmsService(@Value("${eclat.sms.notifylk.url}") String url,
                              @Value("${eclat.sms.notifylk.user-id}") String userId,
                              @Value("${eclat.sms.notifylk.api-key}") String apiKey,
                              @Value("${eclat.sms.notifylk.sender-id}") String senderId) {
        this.url = url;
        this.userId = userId;
        this.apiKey = apiKey;
        this.senderId = senderId;
    }

    @Override
    public void send(String phone, String message) {
        String body = form("user_id", userId) + "&" + form("api_key", apiKey)
                + "&" + form("sender_id", senderId) + "&" + form("to", toInternational(phone))
                + "&" + form("message", message);
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() / 100 != 2 || !res.body().toLowerCase(Locale.ROOT).contains("success")) {
                LOG.error("SMS gateway rejected the message: HTTP {} {}", res.statusCode(), res.body());
                throw new BusinessRuleException("We could not send the verification code. Please try again.");
            }
        } catch (IOException e) {
            LOG.error("SMS gateway unreachable", e);
            throw new BusinessRuleException("We could not send the verification code. Please try again.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessRuleException("We could not send the verification code. Please try again.");
        }
    }

    /** 0771234567 or +94771234567 -> 94771234567 */
    static String toInternational(String phone) {
        String d = phone.replaceAll("[^0-9]", "");
        return d.startsWith("0") ? "94" + d.substring(1) : d;
    }

    private static String form(String k, String v) {
        return k + "=" + URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
    }
}
