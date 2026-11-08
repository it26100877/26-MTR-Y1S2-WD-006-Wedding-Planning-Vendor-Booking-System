package com.wedding.member06_review_feedback.model;

/**
 * Domain model representing a verified Customer Review and Star Rating for a Vendor.
 * Demonstrates Encapsulation: private fields with getters and setters.
 *
 * @author Member 06 – Review & Feedback Management
 */
public class Review {

 private String reviewId;
 private String customerId;
 private String vendorId;
 private String bookingId; // FK to verified completed booking
 private int rating; // 1 to 5 stars
 private String title;
 private String feedback;
 private String submittedAt;
 private String updatedAt;
 private ReviewStatus status;
 private String moderatedBy; // Admin ID
 private String moderationNote;

 public Review() {}

 public Review(String reviewId, String customerId, String vendorId, String bookingId,
 int rating, String title, String feedback, String submittedAt,
 String updatedAt, ReviewStatus status, String moderatedBy, String moderationNote) {
 this.reviewId = reviewId;
 this.customerId = customerId;
 this.vendorId = vendorId;
 this.bookingId = bookingId;
 this.rating = rating;
 this.title = title;
 this.feedback = feedback;
 this.submittedAt = submittedAt;
 this.updatedAt = updatedAt;
 this.status = status;
 this.moderatedBy = moderatedBy;
 this.moderationNote = moderationNote;
 }

 public String getReviewId() { return reviewId; }
 public void setReviewId(String v) { this.reviewId = v; }

 public String getCustomerId() { return customerId; }
 public void setCustomerId(String v) { this.customerId = v; }

 public String getVendorId() { return vendorId; }
 public void setVendorId(String v) { this.vendorId = v; }

 public String getBookingId() { return bookingId; }
 public void setBookingId(String v) { this.bookingId = v; }

 public int getRating() { return rating; }
 public void setRating(int v) { this.rating = v; }

 public String getTitle() { return title; }
 public void setTitle(String v) { this.title = v; }

 public String getFeedback() { return feedback; }
 public void setFeedback(String v) { this.feedback = v; }

 public String getSubmittedAt() { return submittedAt; }
 public void setSubmittedAt(String v) { this.submittedAt = v; }

 public String getUpdatedAt() { return updatedAt; }
 public void setUpdatedAt(String v) { this.updatedAt = v; }

 public ReviewStatus getStatus() { return status; }
 public void setStatus(ReviewStatus v) { this.status = v; }

 public String getModeratedBy() { return moderatedBy; }
 public void setModeratedBy(String v) { this.moderatedBy = v; }

 public String getModerationNote() { return moderationNote; }
 public void setModerationNote(String v) { this.moderationNote = v; }

 /** Returns formatted stars string for display (e.g. "" or ""). */
 public String getStarsDisplay() {
 StringBuilder sb = new StringBuilder();
 for (int i = 1; i <= 5; i++) {
 sb.append(i <= rating ? "" : "");
 }
 return sb.toString();
 }

 @Override
 public String toString() {
 return "Review [" + reviewId + "] Vendor:" + vendorId + " Rating:" + rating + "/5";
 }
}

