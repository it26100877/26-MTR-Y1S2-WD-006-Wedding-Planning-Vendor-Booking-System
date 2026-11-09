package com.wedding.member05_contract_payment.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member05_contract_payment.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * File-based repository for polymorphic {@link Payment} entities.
 * Demonstrates Polymorphism: instantiates AdvancePayment, PartialPayment, or FinalPayment
 * dynamically based on the stored paymentType discriminator.
 * Stored in payments.csv with pipe delimiter.
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
public class PaymentRepository extends AbstractFileRepository<Payment> {

 private static final String FILE_NAME = "payments.csv";
 private static final String DELIM = "|";

 public PaymentRepository() {
 super(FILE_NAME);
 }

 @Override
 protected String getId(Payment p) {
 return p.getPaymentId();
 }

 @Override
 protected String serialize(Payment p) {
 String extra1 = "";
 if (p instanceof AdvancePayment) {
 extra1 = String.valueOf(((AdvancePayment) p).getAdvancePercentage());
 } else if (p instanceof PartialPayment) {
 extra1 = String.valueOf(((PartialPayment) p).getInstallmentNumber());
 } else if (p instanceof FinalPayment) {
 extra1 = ((FinalPayment) p).getReceiptNumber();
 }

 return String.join(DELIM,
 safe(p.getPaymentId()),
 safe(p.getContractId()),
 safe(p.getPaymentType()),
 p.getAmount() != null ? p.getAmount().toPlainString() : "0.00",
 safe(p.getPaymentDate()),
 safe(p.getPaymentMethod()),
 safe(p.getTransactionRef()),
 safe(p.getNotes()),
 safe(p.getRecordedAt()),
 String.valueOf(p.isVoided()),
 safe(extra1)
 );
 }

 @Override
 protected Payment deserialize(String line) {
 if (line == null || line.trim().isEmpty()) return null;
 String[] p = line.split(Pattern.quote(DELIM), -1);
 if (p.length < 10) return null;

 String paymentId = p[0];
 String contractId = p[1];
 String type = p[2];
 BigDecimal amount;
 try { amount = new BigDecimal(p[3]); } catch (Exception e) { return null; }
 if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.stripTrailingZeros().scale() > 2) return null;
 if (p[0].isBlank() || p[1].isBlank() || p[4].isBlank() || p[5].isBlank()) return null;
 String date = p[4];
 String method = p[5];
 String ref = p[6];
 String notes = p[7];
 String recordedAt = p[8];
 boolean voided = Boolean.parseBoolean(p[9]);
 String extra1 = p.length > 10 ? p[10] : "";

 // Polymorphic instantiation based on type discriminator
 if ("ADVANCE".equalsIgnoreCase(type)) {
 double pct = 0.0;
 try { pct = Double.parseDouble(extra1); } catch (Exception ignored) {}
 return new AdvancePayment(paymentId, contractId, amount, date, method, ref, notes, recordedAt, voided, pct);
 } else if ("PARTIAL".equalsIgnoreCase(type)) {
 int inst = 1;
 try { inst = Integer.parseInt(extra1); } catch (Exception ignored) {}
 return new PartialPayment(paymentId, contractId, amount, date, method, ref, notes, recordedAt, voided, inst);
 } else if ("FINAL".equalsIgnoreCase(type)) {
 return new FinalPayment(paymentId, contractId, amount, date, method, ref, notes, recordedAt, voided, extra1);
 } else {
 // Unknown discriminator: skip the malformed row instead of misclassifying it as an advance.
 return null;
 }
 }

 private String safe(String s) {
 return s == null ? "" : s.replace("|", "/").replace("\r", " ").replace("\n", " ");
 }

 public List<Payment> findByContractId(String contractId) {
 return findAll().stream()
 .filter(p -> contractId != null && contractId.equalsIgnoreCase(p.getContractId()))
 .collect(Collectors.toList());
 }

 public List<Payment> findNonVoidedByContractId(String contractId) {
 return findAll().stream()
 .filter(p -> contractId != null && contractId.equalsIgnoreCase(p.getContractId()))
 .filter(p -> !p.isVoided())
 .collect(Collectors.toList());
 }

 public BigDecimal getTotalPaidForContract(String contractId) {
 return findNonVoidedByContractId(contractId).stream()
 .map(Payment::getAmount)
 .reduce(BigDecimal.ZERO, BigDecimal::add);
 }
}

