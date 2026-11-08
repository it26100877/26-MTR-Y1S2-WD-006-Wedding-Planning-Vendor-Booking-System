package com.wedding.member05_contract_payment.model;

import java.math.BigDecimal;

/**
 * Abstract base class for all payment types in the Wedding Planning System.
 * Demonstrates Abstraction and Inheritance — subclasses provide concrete payment types.
 *
 * Payment types supported:
 * - AdvancePayment (percentage-based upfront)
 * - PartialPayment (installment-based)
 * - FinalPayment (settlement with receipt)
 *
 * @author Member 05
 */
public abstract class Payment {

 // ─── Fields ───────────────────────────────────────────────────────────────
 private String paymentId;
 private String contractId;
 private BigDecimal amount;
 private String paymentDate;
 /** "CASH", "BANK_TRANSFER", "CARD" */
 private String paymentMethod;
 private String transactionRef;
 private String notes;
 private String recordedAt;
 /** True if this payment has been reversed / voided. */
 private boolean voided;

 // ─── Full Constructor ──────────────────────────────────────────────────────

 protected Payment(String paymentId, String contractId, BigDecimal amount,
 String paymentDate, String paymentMethod, String transactionRef,
 String notes, String recordedAt, boolean voided) {
 this.paymentId = paymentId;
 this.contractId = contractId;
 this.amount = amount;
 this.paymentDate = paymentDate;
 this.paymentMethod = paymentMethod;
 this.transactionRef = transactionRef;
 this.notes = notes;
 this.recordedAt = recordedAt;
 this.voided = voided;
 }

 // ─── Abstract Method (Polymorphism hook) ───────────────────────────────────

 /**
 * Returns the specific type label of this payment.
 * Each concrete subclass returns its own distinct type string.
 *
 * @return e.g. "ADVANCE", "PARTIAL", "FINAL"
 */
 public abstract String getPaymentType();

 // ─── Getters ──────────────────────────────────────────────────────────────

 public String getPaymentId() { return paymentId; }
 public String getContractId() { return contractId; }
 public BigDecimal getAmount() { return amount; }
 public String getPaymentDate() { return paymentDate; }
 public String getPaymentMethod() { return paymentMethod; }
 public String getTransactionRef() { return transactionRef; }
 public String getNotes() { return notes; }
 public String getRecordedAt() { return recordedAt; }
 public boolean isVoided() { return voided; }

 // ─── Setters ──────────────────────────────────────────────────────────────

 public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
 public void setContractId(String contractId) { this.contractId = contractId; }
 public void setAmount(BigDecimal amount) { this.amount = amount; }
 public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
 public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
 public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
 public void setNotes(String notes) { this.notes = notes; }
 public void setRecordedAt(String recordedAt) { this.recordedAt = recordedAt; }
 public void setVoided(boolean voided) { this.voided = voided; }

 @Override
 public String toString() {
 return getPaymentType() + " Payment: " + amount
 + (voided ? " [VOIDED]" : "")
 + " | Date: " + paymentDate
 + " | Method: " + paymentMethod;
 }
}
