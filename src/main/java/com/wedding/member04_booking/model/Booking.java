package com.wedding.member04_booking.model;

import java.math.BigDecimal;

/**
 * Domain model representing a Booking between a Customer and a Vendor
 * within a Wedding Plan.
 * Demonstrates Encapsulation: all fields are private with public getters/setters.
 *
 * <p>File format (bookings.csv) — 12 pipe-separated fields:
 * bookingId|customerId|vendorId|planId|bookingDate|requestedAt|status|
 * serviceDetails|agreedAmount|notes|confirmedAt|cancelledAt
 *
 * @author Member 04 – Booking Management
 */
public class Booking {

 // ── Private Fields ────────────────────────────────────────────────────────
 /** Unique booking identifier (BKG-xxxxx-xxxx). */
 private String bookingId;

 /** ID of the customer who made the booking. */
 private String customerId;

 /** ID of the vendor being booked. */
 private String vendorId;

 /** ID of the associated wedding plan. */
 private String planId;

 /** The wedding event date (yyyy-MM-dd). */
 private String bookingDate;

 /** Timestamp when the booking request was submitted (yyyy-MM-dd HH:mm). */
 private String requestedAt;

 /** Current lifecycle status of the booking. */
 private BookingStatus status;

 /** Description of the specific service(s) requested. */
 private String serviceDetails;

 /** The agreed price for the service. */
 private BigDecimal agreedAmount;

 /** Optional free-text notes from the customer. */
 private String notes;

 /** Timestamp when the booking was confirmed or rejected. */
 private String confirmedAt;

 /** Timestamp when the booking was cancelled. */
 private String cancelledAt;

 // ── Constructors ──────────────────────────────────────────────────────────
 public Booking() {}

 public Booking(String bookingId,
 String customerId,
 String vendorId,
 String planId,
 String bookingDate,
 String requestedAt,
 BookingStatus status,
 String serviceDetails,
 BigDecimal agreedAmount,
 String notes,
 String confirmedAt,
 String cancelledAt) {
 this.bookingId = bookingId;
 this.customerId = customerId;
 this.vendorId = vendorId;
 this.planId = planId;
 this.bookingDate = bookingDate;
 this.requestedAt = requestedAt;
 this.status = status;
 this.serviceDetails = serviceDetails;
 this.agreedAmount = agreedAmount;
 this.notes = notes;
 this.confirmedAt = confirmedAt;
 this.cancelledAt = cancelledAt;
 }

 // ── Getters ───────────────────────────────────────────────────────────────
 public String getBookingId() { return bookingId; }
 public String getCustomerId() { return customerId; }
 public String getVendorId() { return vendorId; }
 public String getPlanId() { return planId; }
 public String getBookingDate() { return bookingDate; }
 public String getRequestedAt() { return requestedAt; }
 public BookingStatus getStatus() { return status; }
 public String getServiceDetails() { return serviceDetails; }
 public BigDecimal getAgreedAmount() { return agreedAmount; }
 public String getNotes() { return notes; }
 public String getConfirmedAt() { return confirmedAt; }
 public String getCancelledAt() { return cancelledAt; }

 // ── Setters ───────────────────────────────────────────────────────────────
 public void setBookingId(String bookingId) { this.bookingId = bookingId; }
 public void setCustomerId(String customerId) { this.customerId = customerId; }
 public void setVendorId(String vendorId) { this.vendorId = vendorId; }
 public void setPlanId(String planId) { this.planId = planId; }
 public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
 public void setRequestedAt(String requestedAt) { this.requestedAt = requestedAt; }
 public void setStatus(BookingStatus status) { this.status = status; }
 public void setServiceDetails(String serviceDetails) { this.serviceDetails = serviceDetails; }
 public void setAgreedAmount(BigDecimal agreedAmount) { this.agreedAmount = agreedAmount; }
 public void setNotes(String notes) { this.notes = notes; }
 public void setConfirmedAt(String confirmedAt) { this.confirmedAt = confirmedAt; }
 public void setCancelledAt(String cancelledAt) { this.cancelledAt = cancelledAt; }

 // ── toString ──────────────────────────────────────────────────────────────
 @Override
 public String toString() {
 return "Booking{" +
 "bookingId='" + bookingId + '\'' +
 ", customerId='" + customerId + '\'' +
 ", vendorId='" + vendorId + '\'' +
 ", planId='" + planId + '\'' +
 ", bookingDate='" + bookingDate + '\'' +
 ", requestedAt='" + requestedAt + '\'' +
 ", status=" + status +
 ", serviceDetails='"+ serviceDetails + '\'' +
 ", agreedAmount=" + agreedAmount +
 ", notes='" + notes + '\'' +
 ", confirmedAt='" + confirmedAt + '\'' +
 ", cancelledAt='" + cancelledAt + '\'' +
 '}';
 }
}
