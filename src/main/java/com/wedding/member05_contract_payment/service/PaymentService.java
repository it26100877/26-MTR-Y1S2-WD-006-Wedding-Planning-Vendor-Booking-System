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
 public AdvancePayment recordAdvancePayment(VendorContract contract, BigDecimal amount,
 String paymentDate, String method,
 String transactionRef, String notes,
 double advancePct) {
 validatePaymentBasics(contract, amount, paymentDate, method);
 if (!Double.isFinite(advancePct) || advancePct <= 0.0 || advancePct > 100.0) {
  throw new ValidationException("Advance percentage must be greater than 0 and no more than 100.");
 }

 AdvancePayment payment = new AdvancePayment(
 FileUtil.generateId("PAY"),
 contract.getContractId(),
 amount,
 paymentDate,
 method,
 transactionRef != null ? transactionRef : "",
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
 public PartialPayment recordPartialPayment(VendorContract contract, BigDecimal amount,
 String paymentDate, String method,
 String transactionRef, String notes,
 int installmentNumber) {
 validatePaymentBasics(contract, amount, paymentDate, method);
 if (installmentNumber < 1) {
  throw new ValidationException("Installment number must be at least 1.");
 }

 PartialPayment payment = new PartialPayment(
 FileUtil.generateId("PAY"),
 contract.getContractId(),
 amount,
 paymentDate,
 method,
 transactionRef != null ? transactionRef : "",
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
 public FinalPayment recordFinalPayment(VendorContract contract, BigDecimal amount,
 String paymentDate, String method,
 String transactionRef, String notes,
 String receiptNumber) {
 validatePaymentBasics(contract, amount, paymentDate, method);

 BigDecimal outstanding = getOutstandingBalance(contract);
 if (amount.compareTo(outstanding) != 0) {
 throw new BusinessRuleException("Final payment amount (LKR " + amount +
 ") must exactly settle the outstanding balance (LKR " + outstanding + ").");
 }

 FinalPayment payment = new FinalPayment(
 FileUtil.generateId("PAY"),
 contract.getContractId(),
 amount,
 paymentDate,
 method,
 transactionRef != null ? transactionRef : "",
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
 public Payment voidPayment(String paymentId, String reason) {
 requireNonEmpty(reason, "Void Reason");
 Payment payment = paymentRepository.findById(paymentId)
 .orElseThrow(() -> new EntityNotFoundException("Payment", paymentId));

 if (payment.isVoided()) {
 throw new BusinessRuleException("Payment " + paymentId + " is already voided.");
 }

 payment.setVoided(true);
 payment.setNotes(payment.getNotes() + " [VOIDED on " + ValidationUtil.nowDateTime() + ": " + reason + "]");
 paymentRepository.save(payment);

 log("Voided payment " + paymentId + " reason: " + reason);
 return payment;
 }

 private void validatePaymentBasics(VendorContract contract, BigDecimal amount, String paymentDate, String method) {
 if (contract == null) {
 throw new ValidationException("Contract reference is required.");
 }
 if (contract.getStatus() == ContractStatus.CANCELLED || contract.getStatus() == ContractStatus.COMPLETED) {
 throw new BusinessRuleException("Cannot record payments on a " + contract.getStatus() + " contract.");
 }
 if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
 throw new ValidationException("Payment amount must be strictly greater than zero.");
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
 BigDecimal totalPaid = getTotalPaid(contract.getContractId());
 BigDecimal outstanding = contract.getContractAmount().subtract(totalPaid);
 return outstanding.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : outstanding;
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

