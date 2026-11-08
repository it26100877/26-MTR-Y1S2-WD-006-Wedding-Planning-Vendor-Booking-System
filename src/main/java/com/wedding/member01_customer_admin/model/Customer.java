package com.wedding.member01_customer_admin.model;

import com.wedding.common.model.User;

/**
 * Represents a customer (couple) using the Wedding Planning System.
 * Extends User demonstrating Inheritance and Polymorphism.
 *
 * @author Member 01
 */
public class Customer extends User {

 // ─── Additional Fields ───────────────────────────────────────────────────────
 private String address;
 private String weddingDate; // yyyy-MM-dd

 // ─── Constructors ────────────────────────────────────────────────────────────

 public Customer() {
 super();
 }

 /**
 * Full constructor.
 *
 * @param userId Unique customer ID (e.g. CST-xxxxx-xxxx)
 * @param name Full name
 * @param email Email address
 * @param phone Phone number
 * @param passwordHash Hashed password
 * @param createdAt ISO date-time of account creation
 * @param active Whether account is active
 * @param address Physical address
 * @param weddingDate Planned wedding date (yyyy-MM-dd)
 */
 public Customer(String userId, String name, String email, String phone,
 String passwordHash, String createdAt, boolean active,
 String address, String weddingDate) {
 super(userId, name, email, phone, passwordHash, createdAt, active);
 this.address = address;
 this.weddingDate = weddingDate;
 }

 // ─── Polymorphism ────────────────────────────────────────────────────────────

 @Override
 public String getRole() {
 return "CUSTOMER";
 }

 // ─── Getters & Setters ───────────────────────────────────────────────────────

 public String getAddress() { return address; }
 public void setAddress(String v) { this.address = v; }

 public String getWeddingDate() { return weddingDate; }
 public void setWeddingDate(String v){ this.weddingDate = v; }

 @Override
 public String toString() {
 return "Customer[" + getUserId() + "] " + getName() + " <" + getEmail() + ">"
 + " weddingDate=" + weddingDate;
 }
}
