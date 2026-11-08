package com.wedding.member05_contract_payment.model;

import java.math.BigDecimal;

/**
 * Concrete payment type for milestone or installment payments.
 * Demonstrates Inheritance (extends Payment) and Polymorphism (overrides getPaymentType).
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
public class PartialPayment extends Payment {

 private int installmentNumber;

 public PartialPayment(String paymentId, String contractId, BigDecimal amount,
 String paymentDate, String paymentMethod, String transactionRef,
 String notes, String recordedAt, boolean voided,
 int installmentNumber) {
 super(paymentId, contractId, amount, paymentDate, paymentMethod, transactionRef, notes, recordedAt, voided);
 this.installmentNumber = installmentNumber;
 }

 @Override
 public String getPaymentType() {
 return "PARTIAL";
 }

 public int getInstallmentNumber() {
 return installmentNumber;
 }

 public void setInstallmentNumber(int installmentNumber) {
 this.installmentNumber = installmentNumber;
 }

 @Override
 public String toString() {
 return "PartialPayment [" + getPaymentId() + "] Installment #" + installmentNumber + " Amount: " + getAmount();
 }
}

