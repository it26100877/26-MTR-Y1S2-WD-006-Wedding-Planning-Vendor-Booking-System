package com.wedding.common.model;

/**
 * Abstract base class for all system users.
 * Demonstrates Abstraction and Encapsulation.
 * Customer and Admin inherit from this class.
 *
 * @author Common Module
 */
public abstract class User {

 private String userId;
 private String name;
 private String email;
 private String phone;
 private String passwordHash;
 private String createdAt;
 private boolean active;

 protected User() {}

 protected User(String userId, String name, String email, String phone,
 String passwordHash, String createdAt, boolean active) {
 this.userId = userId;
 this.name = name;
 this.email = email;
 this.phone = phone;
 this.passwordHash = passwordHash;
 this.createdAt = createdAt;
 this.active = active;
 }

 /** Returns the role of this user (Polymorphism via method override). */
 public abstract String getRole();

 // ─── Getters & Setters ───────────────────────────────────────────────────────

 public String getUserId() { return userId; }
 public void setUserId(String v) { this.userId = v; }

 public String getName() { return name; }
 public void setName(String v) { this.name = v; }

 public String getEmail() { return email; }
 public void setEmail(String v) { this.email = v; }

 public String getPhone() { return phone; }
 public void setPhone(String v) { this.phone = v; }

 public String getPasswordHash() { return passwordHash; }
 public void setPasswordHash(String v) { this.passwordHash = v; }

 public String getCreatedAt() { return createdAt; }
 public void setCreatedAt(String v) { this.createdAt = v; }

 public boolean isActive() { return active; }
 public void setActive(boolean v){ this.active = v; }

 /**
  * Creates a salted PBKDF2-HMAC-SHA256 password hash.
  * Format: PBKDF2$iterations$saltBase64$hashBase64
  */
 public static String hashPassword(String plain) {
     if (plain == null) throw new IllegalArgumentException("Password cannot be null.");
     try {
         int iterations = 210_000;
         byte[] salt = new byte[16];
         new java.security.SecureRandom().nextBytes(salt);
         javax.crypto.SecretKeyFactory factory =
             javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
         javax.crypto.spec.PBEKeySpec spec =
             new javax.crypto.spec.PBEKeySpec(plain.toCharArray(), salt, iterations, 256);
         byte[] derived = factory.generateSecret(spec).getEncoded();
         spec.clearPassword();
         java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
         return "PBKDF2$" + iterations + "$" + encoder.encodeToString(salt)
             + "$" + encoder.encodeToString(derived);
     } catch (java.security.GeneralSecurityException ex) {
         throw new IllegalStateException("Unable to hash password securely.", ex);
     }
 }

 /** Verifies modern PBKDF2 hashes and supports legacy hashes for migration. */
 public boolean checkPassword(String plain) {
     if (plain == null || passwordHash == null) return false;
     if (passwordHash.startsWith("PBKDF2$")) {
         try {
             String[] parts = passwordHash.split("\\$");
             if (parts.length != 4) return false;
             int iterations = Integer.parseInt(parts[1]);
             byte[] salt = java.util.Base64.getDecoder().decode(parts[2]);
             byte[] expected = java.util.Base64.getDecoder().decode(parts[3]);
             javax.crypto.SecretKeyFactory factory =
                 javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
             javax.crypto.spec.PBEKeySpec spec =
                 new javax.crypto.spec.PBEKeySpec(plain.toCharArray(), salt, iterations, expected.length * 8);
             byte[] actual = factory.generateSecret(spec).getEncoded();
             spec.clearPassword();
             return java.security.MessageDigest.isEqual(expected, actual);
         } catch (Exception ex) {
             return false;
         }
     }
     // Backward compatibility for accounts created by the earlier demo hash.
     return legacyHashPassword(plain).equals(passwordHash);
 }

 /** Identifies old demo hashes so the service can upgrade them after successful login. */
 public boolean needsPasswordHashUpgrade() {
     return passwordHash == null || !passwordHash.startsWith("PBKDF2$");
 }

 private static String legacyHashPassword(String plain) {
     int hash = 31;
     for (char c : plain.toCharArray()) hash = hash * 31 + c;
     return "H" + Math.abs(hash);
 }

 @Override
 public String toString() {
 return getRole() + "[" + userId + "] " + name + " <" + email + ">";
 }
}

