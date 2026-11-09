package com.wedding.member05_contract_payment.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member05_contract_payment.model.*;
import com.wedding.member05_contract_payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic service for recording and auditing financial payments.
 * Ensures:
 * - Reject zero/negative payments
 * - Reject payments exceeding outstanding balance
 * - Never allow negative outstanding balances
 * - Reversal / voiding of payments with audit notes
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
public class PaymentService extends BaseService {

 private final PaymentRepository paymentRepository;

 public PaymentService(PaymentRepository paymentRepository) {
 this.paymentRepository = paymentRepository;
 }

 /**
 * Records an Advance Payment.
 */
 public synchronized AdvancePayment recordAdvancePayment(VendorContract contract, BigDecimal amount,
 String paymentDate, String method,
 String transactionRef, String notes,
 double advancePct) {
 validatePaymentBasics(contract, amount, paymentDate, method, transactionRef);
 if (!Double.isFinite(advancePct) || advancePct <= 0.0 || advancePct > 100.0) {
  throw new ValidationException("Advance percentage must be greater than 0 and no more than 100.");
 }

 AdvancePayment payment = new AdvancePayment(
 FileUtil.generateId("PAY"),
 contract.getContractId(),
 amount,
 paymentDate.trim(),
 method.trim().toUpperCase(),
 transactionRef != null ? transactionRef.trim() : "",
 notes != null ? notes : "",
 ValidationUtil.nowDateTime(),
 false,
 advancePct
 );

 paymentRepository.save(payment);
 log("Recorded Advance Payment: " + payment.getPaymentId() + " of LKR " + amount + " for contract " + contract.getContractId());
 return payment;
 }

 /**
 * Records a Partial / Installment Payment.
 */
 public synchronized PartialPayment recordPartialPayment(VendorContract contract, BigDecimal amount,
 String paymentDate, String method,
 String transactionRef, String notes,
 int installmentNumber) {
 validatePaymentBasics(contract, amount, paymentDate, method, transactionRef);
 if (installmentNumber < 1) {
  throw new ValidationException("Installment number must be at least 1.");
 }
 boolean installmentExists = paymentRepository.findByContractId(contract.getContractId()).stream()
         .filter(p -> p instanceof PartialPayment)
         .map(p -> (PartialPayment) p)
         .anyMatch(p -> p.getInstallmentNumber() == installmentNumber);
 if (installmentExists) {
  throw new BusinessRuleException("Installment #" + installmentNumber + " has already been recorded for this contract.");
 }

 PartialPayment payment = new PartialPayment(
 FileUtil.generateId("PAY"),
 contract.getContractId(),
 amount,
 paymentDate.trim(),
 method.trim().toUpperCase(),
 transactionRef != null ? transactionRef.trim() : "",
 notes != null ? notes : "",
 ValidationUtil.nowDateTime(),
 false,
 installmentNumber
 );

 paymentRepository.save(payment);
 log("Recorded Partial Payment: " + payment.getPaymentId() + " (#" + installmentNumber + ") of LKR " + amount);
 return payment;
 }

 /**
 * Records a Final Settlement Payment.
 */
 public synchronized FinalPayment recordFinalPayment(VendorContract contract, BigDecimal amount,
 String paymentDate, String method,
 String transactionRef, String notes,
 String receiptNumber) {
 validatePaymentBasics(contract, amount, paymentDate, method, transactionRef);

 BigDecimal outstanding = getOutstandingBalance(contract);
 if (amount.compareTo(outstanding) != 0) {
 throw new BusinessRuleException("Final payment amount (LKR " + amount +
 ") must exactly settle the outstanding balance (LKR " + outstanding + ").");
 }

 FinalPayment payment = new FinalPayment(
 FileUtil.generateId("PAY"),
 contract.getContractId(),
 amount,
 paymentDate.trim(),
 method.trim().toUpperCase(),
 transactionRef != null ? transactionRef.trim() : "",
 notes != null ? notes : "",
 ValidationUtil.nowDateTime(),
 false,
 receiptNumber != null ? receiptNumber : FileUtil.generateId("REC")
 );

 paymentRepository.save(payment);
 log("Recorded Final Settlement Payment: " + payment.getPaymentId() + " of LKR " + amount);
 return payment;
 }

 /**
 * Voids / Reverses an incorrect payment with an audit note.
 */
 public synchronized Payment voidPayment(String paymentId, String reason) {
 requireNonEmpty(reason, "Void Reason");
 Payment payment = paymentRepository.findById(paymentId)
 .orElseThrow(() -> new EntityNotFoundException("Payment", paymentId));

 if (payment.isVoided()) {
 throw new BusinessRuleException("Payment " + paymentId + " is already voided.");
 }
 String trimmedReason = reason.trim();
 if (trimmedReason.length() < 5) {
  throw new ValidationException("Void reason must contain at least 5 characters.");
 }
 payment.setVoided(true);
 String previousNotes = payment.getNotes() == null ? "" : payment.getNotes().trim();
 payment.setNotes((previousNotes.isEmpty() ? "" : previousNotes + " | ")
         + "VOIDED on " + ValidationUtil.nowDateTime() + " | Reason: " + trimmedReason);
 paymentRepository.save(payment);

 log("Voided payment " + paymentId + " reason: " + reason);
 return payment;
 }

 private void validatePaymentBasics(VendorContract contract, BigDecimal amount, String paymentDate, String method, String transactionRef) {
 if (contract == null) {
 throw new ValidationException("Contract reference is required.");
 }
 if (contract.getStatus() == ContractStatus.CANCELLED || contract.getStatus() == ContractStatus.COMPLETED) {
 throw new BusinessRuleException("Cannot record payments on a " + contract.getStatus() + " contract.");
 }
 if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
 throw new ValidationException("Payment amount must be strictly greater than zero.");
 }
 if (amount.stripTrailingZeros().scale() > 2) {
  throw new ValidationException("Payment amount can contain a maximum of 2 decimal places.");
 }
 requireNonEmpty(paymentDate, "Payment Date");
 requireNonEmpty(method, "Payment Method");
 if (!ValidationUtil.isValidDate(paymentDate)) {
  throw new ValidationException("Payment date must use yyyy-MM-dd format.");
 }
 String normalizedMethod = method.trim().toUpperCase();
 if (!List.of("CASH", "CARD", "BANK_TRANSFER").contains(normalizedMethod)) {
  throw new ValidationException("Payment method must be CASH, CARD, or BANK_TRANSFER.");
 }
 String normalizedRef = transactionRef == null ? "" : transactionRef.trim();
 if ((normalizedMethod.equals("CARD") || normalizedMethod.equals("BANK_TRANSFER")) && normalizedRef.isEmpty()) {
  throw new ValidationException("A transaction reference is required for CARD and BANK_TRANSFER demo records.");
 }
 if (!normalizedRef.isEmpty()) {
  // Keep references unique even after a payment is voided: the audit history must not be reused.
  boolean duplicateReference = paymentRepository.findAll().stream()
          .anyMatch(p -> normalizedRef.equalsIgnoreCase(p.getTransactionRef() == null ? "" : p.getTransactionRef().trim()));
  if (duplicateReference) {
   throw new BusinessRuleException("Transaction reference '" + normalizedRef + "' has already been used.");
  }
 }

 BigDecimal outstanding = getOutstandingBalance(contract);
 if (amount.compareTo(outstanding) > 0) {
 throw new BusinessRuleException("Payment amount (LKR " + amount +
 ") exceeds the outstanding contract balance (LKR " + outstanding + ").");
 }
 }

 public BigDecimal getTotalPaid(String contractId) {
 return paymentRepository.getTotalPaidForContract(contractId);
 }

 public BigDecimal getOutstandingBalance(VendorContract contract) {
 if (contract == null || contract.getContractId() == null) {
  throw new ValidationException("A valid contract is required to calculate the outstanding balance.");
 }
 BigDecimal totalPaid = getTotalPaid(contract.getContractId());
 BigDecimal outstanding = contract.getContractAmount().subtract(totalPaid);
 return outstanding.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : outstanding;
 }

 public Payment getAllPaymentsForReceipt(String paymentId) {
  return paymentRepository.findById(paymentId)
          .orElseThrow(() -> new EntityNotFoundException("Payment", paymentId));
 }

 public List<Payment> getPaymentsByContract(String contractId) {
 return paymentRepository.findNonVoidedByContractId(contractId);
 }

 public List<Payment> getAllPayments(String contractId) {
 return paymentRepository.findByContractId(contractId);
 }

 public int count() {
 return paymentRepository.count();
 }
}

