package com.wedding.member04_booking.model;

/**
 * Enum representing all possible lifecycle states of a booking.
 * Demonstrates Enumeration with behaviour (displayName field + getter).
 *
 * @author Member 04 – Booking Management
 */
public enum BookingStatus {

 PENDING("Pending"),
 CONFIRMED("Confirmed"),
 REJECTED("Rejected"),
 CANCELLED("Cancelled"),
 COMPLETED("Completed");

 // ── Fields ────────────────────────────────────────────────────────────────
 private final String displayName;

 // ── Constructor ───────────────────────────────────────────────────────────
 BookingStatus(String displayName) {
 this.displayName = displayName;
 }

 // ── Getter ────────────────────────────────────────────────────────────────
 public String getDisplayName() {
 return displayName;
 }

 /**
 * Safe lookup by name; returns PENDING as default if unrecognised.
 *
 * @param name the enum constant name (case-insensitive)
 * @return matching BookingStatus, or PENDING if not found
 */
 public static BookingStatus fromString(String name) {
 if (name == null || name.trim().isEmpty()) return PENDING;
 try {
 return BookingStatus.valueOf(name.trim().toUpperCase());
 } catch (IllegalArgumentException e) {
 return PENDING;
 }
 }
}
