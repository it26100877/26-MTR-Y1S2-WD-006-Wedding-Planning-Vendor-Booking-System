package com.wedding.member05_contract_payment.model;

import java.math.BigDecimal;

/**
 * Represents a vendor service contract in the Wedding Planning System.
 * Links a booking to a vendor with financial terms and payment tracking.
 * Demonstrates Encapsulation with private fields and public getters/setters.
 *
 * @author Member 05
 */
public class VendorContract {

 // ─── Identity & References ─────────────────────────────────────────────────
 private String contractId;
 private String bookingId; // FK → Booking
 private String customerId;
 private String vendorId;

 // ─── Dates ────────────────────────────────────────────────────────────────
 private String contractDate; // ISO date when contract was created
 private String startDate;
 private String endDate;

 // ─── Financials ───────────────────────────────────────────────────────────
 private BigDecimal contractAmount;
 private String terms;
 private ContractStatus status;

 // ─── Audit ────────────────────────────────────────────────────────────────
 private String createdAt;

 // ─── Computed / Cached Payment Info ───────────────────────────────────────
 /** Cached sum of all non-voided payments; updated by ContractService.refreshTotals(). */
 private BigDecimal totalPaid;
 /** "UNPAID", "PARTIALLY_PAID", or "FULLY_PAID" */
 private String paymentStatus;

 // ─── Full Constructor ──────────────────────────────────────────────────────

 public VendorContract(String contractId, String bookingId, String customerId, String vendorId,
 String contractDate, String startDate, String endDate,
 BigDecimal contractAmount, String terms, ContractStatus status,
 String createdAt, BigDecimal totalPaid, String paymentStatus) {
 this.contractId = contractId;
 this.bookingId = bookingId;
 this.customerId = customerId;
 this.vendorId = vendorId;
 this.contractDate = contractDate;
 this.startDate = startDate;
 this.endDate = endDate;
 this.contractAmount = contractAmount;
 this.terms = terms;
 this.status = status;
 this.createdAt = createdAt;
 this.totalPaid = (totalPaid != null) ? totalPaid : BigDecimal.ZERO;
 this.paymentStatus = (paymentStatus != null && !paymentStatus.isEmpty()) ? paymentStatus : "UNPAID";
 }

 // ─── Computed Methods ──────────────────────────────────────────────────────

 /**
 * Returns the amount still owed on this contract.
 * Outstanding = contractAmount - totalPaid.
 * Always returns >= 0 (clamped at ZERO to prevent negative balances).
 */
 public BigDecimal getOutstandingBalance() {
 BigDecimal outstanding = contractAmount.subtract(totalPaid);
 return outstanding.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : outstanding;
 }

 /**
 * Recalculates and sets the paymentStatus field based on totalPaid vs contractAmount.
 * Should be called whenever totalPaid changes.
 */
 public void updatePaymentStatus() {
 if (totalPaid == null || totalPaid.compareTo(BigDecimal.ZERO) == 0) {
 this.paymentStatus = "UNPAID";
 } else if (totalPaid.compareTo(contractAmount) >= 0) {
 this.paymentStatus = "FULLY_PAID";
 } else {
 this.paymentStatus = "PARTIALLY_PAID";
 }
 }

 // ─── Getters ──────────────────────────────────────────────────────────────

 public String getContractId() { return contractId; }
 public String getBookingId() { return bookingId; }
 public String getCustomerId() { return customerId; }
 public String getVendorId() { return vendorId; }
 public String getContractDate() { return contractDate; }
 public String getStartDate() { return startDate; }
 public String getEndDate() { return endDate; }
 public BigDecimal getContractAmount() { return contractAmount; }
 public String getTerms() { return terms; }
 public ContractStatus getStatus() { return status; }
 public String getCreatedAt() { return createdAt; }
 public BigDecimal getTotalPaid() { return totalPaid; }
 public String getPaymentStatus() { return paymentStatus; }

 // ─── Setters ──────────────────────────────────────────────────────────────

 public void setContractId(String contractId) { this.contractId = contractId; }
 public void setBookingId(String bookingId) { this.bookingId = bookingId; }
 public void setCustomerId(String customerId) { this.customerId = customerId; }
 public void setVendorId(String vendorId) { this.vendorId = vendorId; }
 public void setContractDate(String contractDate) { this.contractDate = contractDate; }
 public void setStartDate(String startDate) { this.startDate = startDate; }
 public void setEndDate(String endDate) { this.endDate = endDate; }
 public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
 public void setTerms(String terms) { this.terms = terms; }
 public void setStatus(ContractStatus status) { this.status = status; }
 public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
 public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
 public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

 @Override
 public String toString() {
 return "VendorContract{id='" + contractId + "', booking='" + bookingId
 + "', amount=" + contractAmount + ", status=" + status
 + ", paymentStatus='" + paymentStatus + "'}";
 }
}
