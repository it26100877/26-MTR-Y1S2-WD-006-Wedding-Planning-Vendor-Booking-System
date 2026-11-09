package com.wedding.member06_review_feedback.model;

/**
 * Moderation statuses for customer reviews.
 *
 * @author Member 06 – Review & Feedback Management
 */
public enum ReviewStatus {
 PENDING_MODERATION("Pending Moderation"),
 APPROVED("Approved"),
 REJECTED("Rejected");

 private final String displayName;

 ReviewStatus(String displayName) {
 this.displayName = displayName;
 }

 public String getDisplayName() {
 return displayName;
 }

 public static ReviewStatus fromString(String name) {
 if (name == null || name.trim().isEmpty()) return PENDING_MODERATION;
 try {
 return ReviewStatus.valueOf(name.trim().toUpperCase());
 } catch (IllegalArgumentException e) {
 return PENDING_MODERATION;
 }
 }
}

