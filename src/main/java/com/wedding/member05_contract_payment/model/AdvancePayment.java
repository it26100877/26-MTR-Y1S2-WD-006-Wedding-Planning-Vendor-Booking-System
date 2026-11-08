package com.wedding.member05_contract_payment.model;

import java.math.BigDecimal;

/**
 * Concrete payment type for upfront advance payments.
 * Demonstrates Inheritance (extends Payment) and Polymorphism (overrides getPaymentType).
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
public class AdvancePayment extends Payment {

 private double advancePercentage;

 public AdvancePayment(String paymentId, String contractId, BigDecimal amount,
 String paymentDate, String paymentMethod, String transactionRef,
 String notes, String recordedAt, boolean voided,
 double advancePercentage) {
 super(paymentId, contractId, amount, paymentDate, paymentMethod, transactionRef, notes, recordedAt, voided);
 this.advancePercentage = advancePercentage;
 }

 @Override
 public String getPaymentType() {
 return "ADVANCE";
 }

 public double getAdvancePercentage() {
 return advancePercentage;
 }

 public void setAdvancePercentage(double advancePercentage) {
 this.advancePercentage = advancePercentage;
 }

 @Override
 public String toString() {
 return "AdvancePayment [" + getPaymentId() + "] Amount: " + getAmount() + " (" + advancePercentage + "%)";
 }
}

