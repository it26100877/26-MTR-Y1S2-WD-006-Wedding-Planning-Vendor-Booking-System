package com.wedding.member01_customer_admin.model;

import java.io.Serializable;

/**
 * A pending one-time-password check, kept in the HTTP session between
 * "password accepted" and "phone verified". Only a salted hash of the code is stored.
 *
 * @author Member 01
 */
public class OtpChallenge implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String userId;
    private final byte[] salt;
    private final byte[] codeHash;
    private final long expiresAt;
    private final long sentAt;
    private final int resendCount;
    private int attempts;

    public OtpChallenge(String userId, byte[] salt, byte[] codeHash,
                        long expiresAt, long sentAt, int resendCount, int attempts) {
        this.userId = userId;
        this.salt = salt;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.sentAt = sentAt;
        this.resendCount = resendCount;
        this.attempts = attempts;
    }

    public String getUserId()   { return userId; }
    public byte[] getSalt()     { return salt; }
    public byte[] getCodeHash() { return codeHash; }
    public long getExpiresAt()  { return expiresAt; }
    public long getSentAt()     { return sentAt; }
    public int getResendCount() { return resendCount; }
    public int getAttempts()    { return attempts; }
    public void incrementAttempts() { attempts++; }
}
