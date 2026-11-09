package com.wedding.member01_customer_admin.model;

import com.wedding.common.model.User;

/**
 * Represents a system administrator in the Wedding Planning System.
 * Extends User demonstrating Inheritance and Polymorphism.
 *
 * @author Member 01
 */
public class Admin extends User {

 /** Admin privilege level: "SUPER" or "STANDARD". */
 private String adminLevel;

 // ─── Constructors ────────────────────────────────────────────────────────────

 public Admin() {
 super();
 }

 /**
 * Full constructor.
 *
 * @param userId Unique admin ID (e.g. ADM-xxxxx-xxxx)
 * @param name Full name
 * @param email Email address
 * @param phone Phone number
 * @param passwordHash Hashed password
 * @param createdAt ISO date-time of account creation
 * @param active Whether account is active
 * @param adminLevel "SUPER" or "STANDARD"
 */
 public Admin(String userId, String name, String email, String phone,
 String passwordHash, String createdAt, boolean active,
 String adminLevel) {
 super(userId, name, email, phone, passwordHash, createdAt, active);
 this.adminLevel = adminLevel;
 }

 // ─── Polymorphism ────────────────────────────────────────────────────────────

 @Override
 public String getRole() {
 return "ADMIN";
 }

 // ─── Getters & Setters ───────────────────────────────────────────────────────

 public String getAdminLevel() { return adminLevel; }
 public void setAdminLevel(String v) { this.adminLevel = v; }

 @Override
 public String toString() {
 return "Admin[" + getUserId() + "] " + getName() + " <" + getEmail() + ">"
 + " level=" + adminLevel;
 }
}
