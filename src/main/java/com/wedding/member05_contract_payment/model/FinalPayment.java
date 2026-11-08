package com.wedding.member05_contract_payment.model;

import java.math.BigDecimal;

/**
 * Concrete payment type for final contract balance settlement.
 * Demonstrates Inheritance (extends Payment) and Polymorphism (overrides getPaymentType).
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
public class FinalPayment extends Payment {

 private String receiptNumber;

 public FinalPayment(String paymentId, String contractId, BigDecimal amount,
 String paymentDate, String paymentMethod, String transactionRef,
 String notes, String recordedAt, boolean voided,
 String receiptNumber) {
 super(paymentId, contractId, amount, paymentDate, paymentMethod, transactionRef, notes, recordedAt, voided);
 this.receiptNumber = receiptNumber;
 }

 @Override
 public String getPaymentType() {
 return "FINAL";
 }

 public String getReceiptNumber() {
 return receiptNumber;
 }

 public void setReceiptNumber(String receiptNumber) {
 this.receiptNumber = receiptNumber;
 }

 @Override
 public String toString() {
 return "FinalPayment [" + getPaymentId() + "] Receipt: " + receiptNumber + " Amount: " + getAmount();
 }
}

