package com.wedding.member03_vendor.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.model.VendorCategory;
import com.wedding.member03_vendor.repository.VendorRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business-logic layer for Vendor Management.
 * Extends {@link BaseService} (Inheritance) and delegates persistence to
 * {@link VendorRepository}.
 *
 * @author Member 03 – Vendor Management
 */
public class VendorService extends BaseService {

 // ── Repository ────────────────────────────────────────────────────────────
 private final VendorRepository vendorRepository;

 // ── Constructor ───────────────────────────────────────────────────────────
 public VendorService() {
 this.vendorRepository = new VendorRepository();
 }

 /** Accepts an externally constructed repository (useful for testing). */
 public VendorService(VendorRepository vendorRepository) {
 this.vendorRepository = vendorRepository;
 }

 // ── CRUD ──────────────────────────────────────────────────────────────────

 /**
 * Validates inputs, creates a new Vendor with PENDING approval status,
 * persists it, and returns it.
 */
 public Vendor addVendor(String vendorName,
 VendorCategory category,
 String contactPerson,
 String email,
 String phone,
 String address,
 String serviceDescription,
 BigDecimal basePrice) {
 // Required field validation (uses BaseService helper)
 requireNonEmpty(vendorName, "Vendor Name");
 requireNonEmpty(contactPerson, "Contact Person");
 requireNonEmpty(address, "Address");
 requireNonEmpty(serviceDescription, "Service Description");

 if (category == null) {
 throw new ValidationException("Vendor category is required.");
 }
 if (!ValidationUtil.isValidEmail(email)) {
 throw new ValidationException("Invalid email address: " + email);
 }
 if (!ValidationUtil.isValidPhone(phone)) {
 throw new ValidationException("Invalid phone number: " + phone);
 }
 if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
 throw new ValidationException("Base price must be greater than zero.");
 }

 String vendorId = FileUtil.generateId("VND");
 Vendor vendor = new Vendor(
 vendorId,
 vendorName.trim(),
 category,
 contactPerson.trim(),
 email.trim(),
 phone.trim(),
 address.trim(),
 serviceDescription.trim(),
 basePrice,
 "AVAILABLE",
 "PENDING",
 ValidationUtil.nowDateTime(),
 true
 );

 vendorRepository.save(vendor);
 log("Added vendor: " + vendorId + " – " + vendorName);
 return vendor;
 }

 /**
 * Updates an existing vendor's editable fields.
 */
 public Vendor updateVendor(String vendorId,
 String vendorName,
 VendorCategory category,
 String contactPerson,
 String email,
 String phone,
 String address,
 String serviceDescription,
 BigDecimal basePrice) {
 Vendor vendor = getById(vendorId);

 requireNonEmpty(vendorName, "Vendor Name");
 requireNonEmpty(contactPerson, "Contact Person");
 requireNonEmpty(address, "Address");
 requireNonEmpty(serviceDescription, "Service Description");

 if (category == null) {
 throw new ValidationException("Vendor category is required.");
 }
 if (!ValidationUtil.isValidEmail(email)) {
 throw new ValidationException("Invalid email address: " + email);
 }
 if (!ValidationUtil.isValidPhone(phone)) {
 throw new ValidationException("Invalid phone number: " + phone);
 }
 if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
 throw new ValidationException("Base price must be greater than zero.");
 }

 vendor.setVendorName(vendorName.trim());
 vendor.setCategory(category);
 vendor.setContactPerson(contactPerson.trim());
 vendor.setEmail(email.trim());
 vendor.setPhone(phone.trim());
 vendor.setAddress(address.trim());
 vendor.setServiceDescription(serviceDescription.trim());
 vendor.setBasePrice(basePrice);

 vendorRepository.save(vendor);
 log("Updated vendor: " + vendorId);
 return vendor;
 }

 /**
 * Approves a vendor (sets approvalStatus = "APPROVED").
 */
 public Vendor approveVendor(String vendorId) {
 Vendor vendor = getById(vendorId);
 if ("REJECTED".equalsIgnoreCase(vendor.getApprovalStatus())) {
 throw new BusinessRuleException("Cannot approve a previously rejected vendor.");
 }
 vendor.setApprovalStatus("APPROVED");
 vendorRepository.save(vendor);
 log("Approved vendor: " + vendorId);
 return vendor;
 }

 /**
 * Rejects a vendor (sets approvalStatus = "REJECTED").
 */
 public Vendor rejectVendor(String vendorId) {
 Vendor vendor = getById(vendorId);
 vendor.setApprovalStatus("REJECTED");
 vendorRepository.save(vendor);
 log("Rejected vendor: " + vendorId);
 return vendor;
 }

 /**
 * Deactivates a vendor: sets active = false and availability = "INACTIVE".
 */
 public Vendor deactivateVendor(String vendorId) {
 Vendor vendor = getById(vendorId);
 vendor.setActive(false);
 vendor.setAvailability("INACTIVE");
 vendorRepository.save(vendor);
 log("Deactivated vendor: " + vendorId);
 return vendor;
 }

 /**
 * Activates a vendor: sets active = true and availability = "AVAILABLE".
 */
 public Vendor activateVendor(String vendorId) {
 Vendor vendor = getById(vendorId);
 vendor.setActive(true);
 vendor.setAvailability("AVAILABLE");
 vendorRepository.save(vendor);
 log("Activated vendor: " + vendorId);
 return vendor;
 }

 /**
 * Updates the availability flag of a vendor.
 *
 * @param availability one of "AVAILABLE", "BUSY", "INACTIVE"
 */
 public Vendor setAvailability(String vendorId, String availability) {
 if (!"AVAILABLE".equals(availability) &&
 !"BUSY".equals(availability) &&
 !"INACTIVE".equals(availability)) {
 throw new ValidationException("Availability must be AVAILABLE, BUSY, or INACTIVE.");
 }
 Vendor vendor = getById(vendorId);
 vendor.setAvailability(availability);
 vendorRepository.save(vendor);
 log("Set availability for vendor " + vendorId + " → " + availability);
 return vendor;
 }

 // ── Query Methods ─────────────────────────────────────────────────────────

 /**
 * Returns vendors filtered by optional name query, category, and availability.
 * Pass null/empty to skip a filter.
 */
 public List<Vendor> searchVendors(String query, VendorCategory category, String availability) {
 return vendorRepository.findAll().stream()
 .filter(v -> (query == null || query.trim().isEmpty() ||
 v.getVendorName().toLowerCase().contains(query.trim().toLowerCase())))
 .filter(v -> category == null || v.getCategory() == category)
 .filter(v -> availability == null || availability.trim().isEmpty() ||
 availability.equalsIgnoreCase(v.getAvailability()))
 .collect(Collectors.toList());
 }

 /**
 * Searches the public vendor catalogue. Only approved and active vendors are
 * visible to customers and public visitors; optional name, category and
 * availability filters are applied independently.
 */
 public List<Vendor> searchApprovedVendors(String query, VendorCategory category, String availability) {
 return vendorRepository.findApproved().stream()
 .filter(v -> query == null || query.trim().isEmpty()
 || (v.getVendorName() != null
 && v.getVendorName().toLowerCase().contains(query.trim().toLowerCase())))
 .filter(v -> category == null || v.getCategory() == category)
 .filter(v -> availability == null || availability.trim().isEmpty()
 || availability.equalsIgnoreCase(v.getAvailability()))
 .collect(Collectors.toList());
 }

 /** Returns all vendors (no filter). */
 public List<Vendor> getAllVendors() {
 return vendorRepository.findAll();
 }

 /** Returns vendors that are APPROVED and active. */
 public List<Vendor> getApprovedVendors() {
 return vendorRepository.findApproved();
 }

 /**
 * Finds a vendor by its ID.
 *
 * @throws EntityNotFoundException if not found
 */
 public Vendor getById(String vendorId) {
 return vendorRepository.findById(vendorId)
 .orElseThrow(() -> new EntityNotFoundException(
 "Vendor not found with ID: " + vendorId));
 }

 /**
 * Deletes a vendor by ID.
 *
 * @return true if deleted, false if not found
 */
 public boolean deleteVendor(String vendorId) {
 boolean result = vendorRepository.deleteById(vendorId);
 if (result) log("Deleted vendor: " + vendorId);
 return result;
 }

 /** Returns the total number of vendors. */
 public int count() {
 return vendorRepository.count();
 }
}
