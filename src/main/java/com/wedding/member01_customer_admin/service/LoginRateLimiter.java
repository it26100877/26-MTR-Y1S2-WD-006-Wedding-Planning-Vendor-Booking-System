package com.wedding.member01_customer_admin.service;

import com.wedding.common.exceptions.BusinessRuleException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight per-process login abuse limiter.
 * Production deployments with more than one application instance should use a
 * shared store (for example Redis) so limits are consistent across instances.
 */
@Service
public class LoginRateLimiter {
    private static final int MAX_LOGIN_ATTEMPTS = 8;
    private static final int MAX_OTP_RESENDS_PER_IP = 10;
    private static final long WINDOW_MILLIS = Duration.ofMinutes(15).toMillis();
    private static final long CLEANUP_INTERVAL_MILLIS = Duration.ofMinutes(5).toMillis();

    private static final class Bucket {
        private long windowStartedAt;
        private int count;
        private Bucket(long now) { windowStartedAt = now; count = 0; }
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private volatile long lastCleanup = System.currentTimeMillis();

    public void checkLoginAllowed(HttpServletRequest request, String email) {
        check("login:" + clientIp(request) + ":" + normalize(email), MAX_LOGIN_ATTEMPTS,
                "Too many login attempts. Please wait 15 minutes and try again.");
    }

    public void recordLoginAttempt(HttpServletRequest request, String email) {
        increment("login:" + clientIp(request) + ":" + normalize(email), MAX_LOGIN_ATTEMPTS,
                "Too many login attempts. Please wait 15 minutes and try again.");
    }

    public void checkOtpResendAllowed(HttpServletRequest request) {
        check("otp-resend:" + clientIp(request), MAX_OTP_RESENDS_PER_IP,
                "Too many OTP resend requests. Please wait 15 minutes.");
    }

    public void recordOtpResend(HttpServletRequest request) {
        increment("otp-resend:" + clientIp(request), MAX_OTP_RESENDS_PER_IP,
                "Too many OTP resend requests. Please wait 15 minutes.");
    }

    private void check(String key, int max, String message) {
        synchronized (buckets) {
            cleanupIfNeeded();
            Bucket b = current(key);
            if (b.count >= max) throw new BusinessRuleException(message);
        }
    }

    private void increment(String key, int max, String message) {
        synchronized (buckets) {
            cleanupIfNeeded();
            Bucket b = current(key);
            if (b.count >= max) throw new BusinessRuleException(message);
            b.count++;
        }
    }

    private Bucket current(String key) {
        long now = System.currentTimeMillis();
        Bucket bucket = buckets.get(key);
        if (bucket == null || now - bucket.windowStartedAt >= WINDOW_MILLIS) {
            bucket = new Bucket(now);
            buckets.put(key, bucket);
        }
        return bucket;
    }

    private void cleanupIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastCleanup < CLEANUP_INTERVAL_MILLIS) return;
        Iterator<Map.Entry<String, Bucket>> it = buckets.entrySet().iterator();
        while (it.hasNext()) {
            if (now - it.next().getValue().windowStartedAt >= WINDOW_MILLIS) it.remove();
        }
        // Bound memory if an attacker rotates identifiers/IPs.
        if (buckets.size() > 20_000) buckets.clear();
        lastCleanup = now;
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static String clientIp(HttpServletRequest request) {
        // Use the container-resolved address; do not trust arbitrary X-Forwarded-For headers.
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
}
