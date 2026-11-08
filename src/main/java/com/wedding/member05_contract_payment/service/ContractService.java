package com.wedding.member05_contract_payment.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.repository.BookingRepository;
import com.wedding.member05_contract_payment.model.ContractStatus;
import com.wedding.member05_contract_payment.model.VendorContract;
import com.wedding.member05_contract_payment.repository.ContractRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service managing vendor contracts and financial status synchronization.
 * Integrates Booking and Payment modules.
 *
 * @author Member 05 – Vendor Contract & Payment Management
 */
public class ContractService extends BaseService {

 private final ContractRepository contractRepository;
 private final BookingRepository bookingRepository;
 private final PaymentService paymentService;

 public ContractService(ContractRepository contractRepository,
 BookingRepository bookingRepository,
 PaymentService paymentService) {
 this.contractRepository = contractRepository;
 this.bookingRepository = bookingRepository;
 this.paymentService = paymentService;
 }

 /**
 * Creates an active contract for a confirmed booking.
 */
 public synchronized VendorContract createContract(String bookingId, String customerId, String vendorId,
 String startDate, String endDate,
 BigDecimal contractAmount, String terms) {
 requireNonEmpty(bookingId, "Booking ID");
 requireNonEmpty(customerId, "Customer ID");
 requireNonEmpty(vendorId, "Vendor ID");
 requireNonEmpty(startDate, "Start Date");
 requireNonEmpty(endDate, "End Date");

 if (contractAmount == null || contractAmount.compareTo(BigDecimal.ZERO) <= 0) {
 throw new ValidationException("Contract amount must be greater than zero.");
 }
 if (contractAmount.stripTrailingZeros().scale() > 2) {
  throw new ValidationException("Contract amount can contain a maximum of 2 decimal places.");
 }
 validateContractDates(startDate, endDate);

 // Verify booking exists and is confirmed
 Booking booking = bookingRepository.findById(bookingId)
 .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));

 if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.COMPLETED) {
 throw new BusinessRuleException("Contracts can only be created for CONFIRMED bookings. Current status: " + booking.getStatus());
 }
 if (booking.getAgreedAmount() == null || contractAmount.compareTo(booking.getAgreedAmount()) != 0) {
  throw new BusinessRuleException("Initial contract amount must match the booking's agreed amount (LKR " + booking.getAgreedAmount() + ").");
 }
 if (!booking.getCustomerId().equalsIgnoreCase(customerId)
         || !booking.getVendorId().equalsIgnoreCase(vendorId)) {
  throw new BusinessRuleException("Contract customer and vendor must match the selected booking.");
 }
 LocalDate eventDate = LocalDate.parse(booking.getBookingDate(), ValidationUtil.DATE_FMT);
 if (eventDate.isBefore(LocalDate.parse(startDate, ValidationUtil.DATE_FMT))
         || eventDate.isAfter(LocalDate.parse(endDate, ValidationUtil.DATE_FMT))) {
  throw new ValidationException("Contract period must include the booking event date.");
 }

 // Prevent duplicate contracts for the same booking
 if (contractRepository.findByBookingId(bookingId).isPresent()) {
 throw new BusinessRuleException("A contract already exists for booking " + bookingId + ".");
 }

 VendorContract contract = new VendorContract(
 FileUtil.generateId("CON"),
 bookingId,
 customerId,
 vendorId,
 ValidationUtil.today(),
 startDate,
 endDate,
 contractAmount,
 terms != null ? terms : "Standard vendor service agreement.",
 ContractStatus.ACTIVE,
 ValidationUtil.nowDateTime(),
 BigDecimal.ZERO,
 "UNPAID"
 );

 contractRepository.save(contract);
 log("Created Contract " + contract.getContractId() + " for Booking " + bookingId + " (LKR " + contractAmount + ")");
 return contract;
 }

 /**
 * Updates an existing contract's terms or amount.
 */
 public synchronized VendorContract updateContract(String contractId, String startDate, String endDate,
 BigDecimal contractAmount, String terms) {
 refreshTotals(contractId);
 VendorContract contract = getById(contractId);
 if (contract.getStatus() == ContractStatus.COMPLETED || contract.getStatus() == ContractStatus.CANCELLED) {
 throw new BusinessRuleException("Cannot edit a " + contract.getStatus() + " contract.");
 }

 if (contractAmount != null) {
 if (contractAmount.compareTo(BigDecimal.ZERO) <= 0) {
  throw new ValidationException("Contract amount must be greater than zero.");
 }
 if (contractAmount.stripTrailingZeros().scale() > 2) {
  throw new ValidationException("Contract amount can contain a maximum of 2 decimal places.");
 }
 if (contractAmount.compareTo(contract.getTotalPaid()) < 0) {
 throw new BusinessRuleException("New contract amount (LKR " + contractAmount +
 ") cannot be less than already paid amount (LKR " + contract.getTotalPaid() + ").");
 }
 contract.setContractAmount(contractAmount);
 }

 if (startDate != null && !startDate.trim().isEmpty()) contract.setStartDate(startDate);
 if (endDate != null && !endDate.trim().isEmpty()) contract.setEndDate(endDate);
 validateContractDates(contract.getStartDate(), contract.getEndDate());
 Booking linkedBooking = bookingRepository.findById(contract.getBookingId())
         .orElseThrow(() -> new EntityNotFoundException("Booking", contract.getBookingId()));
 LocalDate eventDate = LocalDate.parse(linkedBooking.getBookingDate(), ValidationUtil.DATE_FMT);
 if (eventDate.isBefore(LocalDate.parse(contract.getStartDate(), ValidationUtil.DATE_FMT))
         || eventDate.isAfter(LocalDate.parse(contract.getEndDate(), ValidationUtil.DATE_FMT))) {
  throw new ValidationException("Contract period must include the booking event date.");
 }
 if (terms != null && !terms.trim().isEmpty()) contract.setTerms(terms);

 // Persist the edited contract before refreshing derived payment totals.
 // refreshTotals() reloads the contract from the repository; without this save,
 // the newly edited dates, amount and terms would be discarded.
 contractRepository.save(contract);
 refreshTotals(contractId);
 return getById(contractId);
 }

 /**
 * Synchronizes total payments and recalculates UNPAID / PARTIALLY_PAID / FULLY_PAID status.
 */
 public void refreshTotals(String contractId) {
 VendorContract contract = getById(contractId);
 BigDecimal totalPaid = paymentService.getTotalPaid(contractId);
 contract.setTotalPaid(totalPaid);
 contract.updatePaymentStatus();
 contractRepository.save(contract);
 }

 /**
 * Cancels an active contract.
 */
 public synchronized VendorContract cancelContract(String contractId) {
 VendorContract contract = getById(contractId);
 if (contract.getStatus() == ContractStatus.COMPLETED) {
  throw new BusinessRuleException("Completed contracts cannot be cancelled.");
 }
 if (contract.getStatus() == ContractStatus.CANCELLED) {
  throw new BusinessRuleException("Contract is already cancelled.");
 }
 refreshTotals(contractId);
 contract = getById(contractId);
 if (contract.getTotalPaid().compareTo(BigDecimal.ZERO) > 0) {
  throw new BusinessRuleException("Reverse all recorded payments before cancelling this contract.");
 }

 contract.setStatus(ContractStatus.CANCELLED);
 contractRepository.save(contract);
 log("Cancelled Contract " + contractId);
 return contract;
 }

 /**
 * Marks contract completed once fully settled.
 */
 public synchronized VendorContract completeContract(String contractId) {
 VendorContract contract = getById(contractId);
 refreshTotals(contractId);
 contract = getById(contractId);

 if (contract.getStatus() == ContractStatus.COMPLETED) {
  throw new BusinessRuleException("Contract " + contractId + " is already completed.");
 }
 if (contract.getStatus() == ContractStatus.CANCELLED) {
  throw new BusinessRuleException("A cancelled contract cannot be completed.");
 }
 if (!"FULLY_PAID".equalsIgnoreCase(contract.getPaymentStatus())) {
 throw new BusinessRuleException("Cannot complete contract " + contractId +
 ": Outstanding balance remains unpaid (LKR " + contract.getOutstandingBalance() + ").");
 }

 contract.setStatus(ContractStatus.COMPLETED);
 contractRepository.save(contract);
 log("Completed Contract " + contractId);
 return contract;
 }

 public VendorContract getById(String contractId) {
 return contractRepository.findById(contractId)
 .orElseThrow(() -> new EntityNotFoundException("Contract", contractId));
 }

 public VendorContract getByBooking(String bookingId) {
 return contractRepository.findByBookingId(bookingId)
 .orElseThrow(() -> new EntityNotFoundException("Contract for Booking", bookingId));
 }

 public List<VendorContract> getAllContracts() {
 return contractRepository.findAll();
 }

 public List<VendorContract> getByCustomer(String customerId) {
 return contractRepository.findByCustomerId(customerId);
 }

 public List<VendorContract> getByVendor(String vendorId) {
 return contractRepository.findByVendorId(vendorId);
 }

 public int count() {
 return contractRepository.count();
 }

 private void validateContractDates(String startDate, String endDate) {
  if (!ValidationUtil.isValidDate(startDate) || !ValidationUtil.isValidDate(endDate)) {
   throw new ValidationException("Contract dates must use yyyy-MM-dd format.");
  }
  LocalDate start = LocalDate.parse(startDate, ValidationUtil.DATE_FMT);
  LocalDate end = LocalDate.parse(endDate, ValidationUtil.DATE_FMT);
  if (end.isBefore(start)) {
   throw new ValidationException("Contract end date cannot be before its start date.");
  }
 }
}

