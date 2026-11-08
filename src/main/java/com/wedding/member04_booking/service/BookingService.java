package com.wedding.member04_booking.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.repository.BookingRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic layer for Booking operations.
 * Demonstrates Abstraction and Encapsulation by extending BaseService.
 * Connects Customer, WeddingPlan, and Vendor modules.
 *
 * @author Member 04 – Booking Management
 */
public class BookingService extends BaseService {

 private final BookingRepository bookingRepository;
 private final VendorService vendorService;

 public BookingService(BookingRepository bookingRepository, VendorService vendorService) {
 this.bookingRepository = bookingRepository;
 this.vendorService = vendorService;
 }

 /**
 * Creates a new booking request.
 */
 public Booking createBooking(String customerId, String vendorId, String planId,
 String bookingDate, String serviceDetails,
 BigDecimal agreedAmount, String notes) {
 requireNonEmpty(customerId, "Customer ID");
 requireNonEmpty(vendorId, "Vendor ID");
 requireNonEmpty(planId, "Wedding Plan");
 requireNonEmpty(bookingDate, "Booking Date");
 requireNonEmpty(serviceDetails, "Service Details");

 if (!ValidationUtil.isValidDate(bookingDate)) {
 throw new ValidationException("Booking date must follow yyyy-MM-dd format.");
 }
 if (!ValidationUtil.isFutureDate(bookingDate)) {
  throw new BusinessRuleException("Booking date must be in the future.");
 }
 if (agreedAmount == null || agreedAmount.compareTo(BigDecimal.ZERO) <= 0) {
 throw new ValidationException("Agreed amount must be greater than zero.");
 }

 // Validate Vendor existence, approval, and availability
 Vendor vendor = vendorService.getById(vendorId);
 if (vendor == null) {
 throw new EntityNotFoundException("Vendor", vendorId);
 }
 if (!"APPROVED".equalsIgnoreCase(vendor.getApprovalStatus())) {
 throw new BusinessRuleException("Cannot book vendor " + vendor.getVendorName() + ": Vendor is not approved.");
 }
 if (!vendor.isActive()) {
 throw new BusinessRuleException("Cannot book vendor " + vendor.getVendorName() + ": Vendor is inactive.");
 }

 // Prevent conflicting bookings for the same vendor on the same date
 List<Booking> conflicts = bookingRepository.findByVendorAndDate(vendorId, bookingDate);
 boolean hasConfirmedConflict = conflicts.stream()
 .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED);
 if (hasConfirmedConflict) {
 throw new BusinessRuleException("Vendor is already booked and confirmed for " + bookingDate + ".");
 }

 Booking booking = new Booking();
 booking.setBookingId(FileUtil.generateId("BKG"));
 booking.setCustomerId(customerId);
 booking.setVendorId(vendorId);
 booking.setPlanId(planId != null ? planId : "");
 booking.setBookingDate(bookingDate);
 booking.setRequestedAt(ValidationUtil.nowDateTime());
 booking.setStatus(BookingStatus.PENDING);
 booking.setServiceDetails(serviceDetails);
 booking.setAgreedAmount(agreedAmount);
 booking.setNotes(notes != null ? notes : "");

 bookingRepository.save(booking);
 log("Created booking " + booking.getBookingId() + " for customer " + customerId + " with vendor " + vendor.getVendorName());
 return booking;
 }

 /**
 * Confirms a pending booking.
 */
 public Booking confirmBooking(String bookingId) {
 Booking booking = getById(bookingId);
 if (booking.getStatus() != BookingStatus.PENDING) {
 throw new BusinessRuleException("Only PENDING bookings can be confirmed.");
 }
 boolean alreadyConfirmed = bookingRepository.findByVendorAndDate(booking.getVendorId(), booking.getBookingDate())
         .stream()
         .anyMatch(other -> !other.getBookingId().equalsIgnoreCase(bookingId)
                 && other.getStatus() == BookingStatus.CONFIRMED);
 if (alreadyConfirmed) {
  throw new BusinessRuleException("This vendor already has another confirmed booking on " + booking.getBookingDate() + ".");
 }

 booking.setStatus(BookingStatus.CONFIRMED);
 booking.setConfirmedAt(ValidationUtil.nowDateTime());
 bookingRepository.save(booking);

 log("Confirmed booking " + bookingId);
 return booking;
 }

 /**
 * Rejects a pending booking.
 */
 public Booking rejectBooking(String bookingId) {
 Booking booking = getById(bookingId);
 if (booking.getStatus() != BookingStatus.PENDING) {
 throw new BusinessRuleException("Only PENDING bookings can be rejected.");
 }

 booking.setStatus(BookingStatus.REJECTED);
 booking.setConfirmedAt(ValidationUtil.nowDateTime());
 bookingRepository.save(booking);

 log("Rejected booking " + bookingId);
 return booking;
 }

 /**
 * Cancels a booking.
 */
 public Booking cancelBooking(String bookingId) {
 Booking booking = getById(bookingId);
 if (booking.getStatus() == BookingStatus.CANCELLED) {
 throw new BusinessRuleException("Booking is already cancelled.");
 }
 if (booking.getStatus() != BookingStatus.PENDING) {
  throw new BusinessRuleException("Only pending booking requests can be cancelled. Confirmed bookings cannot be cancelled through this workflow.");
 }

 booking.setStatus(BookingStatus.CANCELLED);
 booking.setCancelledAt(ValidationUtil.nowDateTime());
 bookingRepository.save(booking);

 log("Cancelled booking " + bookingId);
 return booking;
 }

 /**
 * Completes a booking once the event is concluded.
 */
 public Booking completeBooking(String bookingId) {
 Booking booking = getById(bookingId);
 if (booking.getStatus() != BookingStatus.CONFIRMED) {
 throw new BusinessRuleException("Only CONFIRMED bookings can be marked as COMPLETED.");
 }

 booking.setStatus(BookingStatus.COMPLETED);
 bookingRepository.save(booking);

 log("Completed booking " + bookingId);
 return booking;
 }

 /**
 * Updates an existing pending booking.
 */
 public Booking updateBooking(String bookingId, String serviceDetails, BigDecimal agreedAmount, String notes) {
 Booking booking = getById(bookingId);
 if (booking.getStatus() != BookingStatus.PENDING) {
 throw new BusinessRuleException("Only PENDING bookings can be edited.");
 }
 requireNonEmpty(serviceDetails, "Service Details");
 if (agreedAmount == null || agreedAmount.compareTo(BigDecimal.ZERO) <= 0) {
 throw new ValidationException("Agreed amount must be greater than zero.");
 }

 booking.setServiceDetails(serviceDetails);
 booking.setAgreedAmount(agreedAmount);
 booking.setNotes(notes != null ? notes : "");
 bookingRepository.save(booking);
 return booking;
 }

 public Booking getById(String bookingId) {
 return bookingRepository.findById(bookingId)
 .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));
 }

 public List<Booking> getAllBookings() {
 return bookingRepository.findAll();
 }

 public List<Booking> getByCustomer(String customerId) {
 return bookingRepository.findByCustomerId(customerId);
 }

 public List<Booking> getByVendor(String vendorId) {
 return bookingRepository.findByVendorId(vendorId);
 }

 public List<Booking> getByPlan(String planId) {
 return bookingRepository.findByPlanId(planId);
 }

 public List<Booking> getByStatus(BookingStatus status) {
 return bookingRepository.findByStatus(status);
 }

 /**
 * Verification check: Returns true only if the customer has a COMPLETED booking for the vendor.
 * Required for verified review submission in Member 06.
 */
 public boolean hasEligibleCompletedBooking(String customerId, String vendorId) {
 return !bookingRepository.findCompletedByCustomerAndVendor(customerId, vendorId).isEmpty();
 }

 public int count() {
 return bookingRepository.count();
 }
}

