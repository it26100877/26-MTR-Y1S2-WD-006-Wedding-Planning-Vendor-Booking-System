package com.wedding.member01_customer_admin.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.member01_customer_admin.model.Customer;
import com.wedding.member01_customer_admin.model.OtpChallenge;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Creates, sends and checks the one-time passwords used to verify a customer's phone
 * at login. Codes are 6 digits, valid for 5 minutes, allow 5 wrong guesses in total
 * and 3 resends (30 seconds apart). Only a salted SHA-256 hash is kept in the session.
 *
 * @author Member 01
 */
@Service
public class OtpService {

    public enum Result { OK, WRONG, EXPIRED, LOCKED, NONE }

    public static final String SESSION_KEY = "otpChallenge";
    public static final int MAX_ATTEMPTS = 5;
    public static final int MAX_RESENDS = 3;
    private static final long TTL_MS = 5 * 60_000L;
    private static final long RESEND_COOLDOWN_MS = 30_000L;

    private final SecureRandom random = new SecureRandom();
    private final SmsService smsService;

    public OtpService(SmsService smsService) {
        this.smsService = smsService;
    }

    /** Sends a first code to the customer's phone and remembers it in the session. */
    public OtpChallenge start(HttpSession session, Customer customer) {
        return issue(session, customer, 0, 0);
    }

    public OtpChallenge current(HttpSession session) {
        Object o = session.getAttribute(SESSION_KEY);
        return o instanceof OtpChallenge c ? c : null;
    }

    public void clear(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    /** Sends a fresh code, respecting the resend limit and cooldown. */
    public void resend(HttpSession session, Customer customer) {
        OtpChallenge old = current(session);
        if (old == null) {
            throw new BusinessRuleException("Your login session has expired. Please log in again.");
        }
        if (old.getResendCount() >= MAX_RESENDS) {
            throw new BusinessRuleException("Too many codes requested. Please log in again.");
        }
        long wait = secondsUntilResend(old);
        if (wait > 0) {
            throw new BusinessRuleException("Please wait " + wait + " seconds before requesting another code.");
        }
        issue(session, customer, old.getResendCount() + 1, old.getAttempts());
    }

    public Result verify(HttpSession session, String code) {
        OtpChallenge ch = current(session);
        if (ch == null) return Result.NONE;
        if (System.currentTimeMillis() > ch.getExpiresAt()) {
            clear(session);
            return Result.EXPIRED;
        }

        String entered = code == null ? "" : code.trim();
        if (MessageDigest.isEqual(hash(ch.getSalt(), entered), ch.getCodeHash())) {
            return Result.OK;
        }
        ch.incrementAttempts();
        if (ch.getAttempts() >= MAX_ATTEMPTS) {
            clear(session);
            return Result.LOCKED;
        }
        session.setAttribute(SESSION_KEY, ch);
        return Result.WRONG;
    }

    public long secondsUntilResend(OtpChallenge ch) {
        long left = ch.getSentAt() + RESEND_COOLDOWN_MS - System.currentTimeMillis();
        return left <= 0 ? 0 : (left + 999) / 1000;
    }

    public int attemptsLeft(OtpChallenge ch) {
        return Math.max(0, MAX_ATTEMPTS - ch.getAttempts());
    }

    /** 0771234567 -> 07•••••567 */
    public static String maskPhone(String phone) {
        String d = phone == null ? "" : phone.replaceAll("[^0-9+]", "");
        if (d.length() <= 5) return "•".repeat(d.length());
        return d.substring(0, 2) + "•".repeat(d.length() - 5) + d.substring(d.length() - 3);
    }

    private OtpChallenge issue(HttpSession session, Customer customer, int resendCount, int attempts) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        long now = System.currentTimeMillis();
        OtpChallenge ch = new OtpChallenge(customer.getUserId(), salt, hash(salt, code),
                now + TTL_MS, now, resendCount, attempts);

        // Plain ASCII keeps the message in one SMS segment.
        smsService.send(customer.getPhone(),
                "Your ECLAT login code is " + code + ". It expires in 5 minutes. Do not share it with anyone.");
        session.setAttribute(SESSION_KEY, ch);
        return ch;
    }

    private static byte[] hash(byte[] salt, String code) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(code.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
