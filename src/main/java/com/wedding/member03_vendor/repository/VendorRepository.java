package com.wedding.member03_vendor.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.model.VendorCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File-based repository for {@link Vendor} entities.
 * Extends {@link AbstractFileRepository} (Inheritance + Abstraction).
 * Uses the pipe character '|' as the CSV field separator.
 *
 * <p>File format (one line per vendor):
 * vendorId|vendorName|category|contactPerson|email|phone|address|serviceDescription|basePrice|availability|approvalStatus|registeredAt|active
 *
 * @author Member 03 – Vendor Management
 */
public class VendorRepository extends AbstractFileRepository<Vendor> {

 private static final String FILE_NAME = "vendors.csv";
 private static final String SEP = "|";
 private static final String SEP_REGEX = "\\|";

 // ── Constructor ───────────────────────────────────────────────────────────
 public VendorRepository() {
 super(FILE_NAME);
 }

 // ── AbstractFileRepository contract ───────────────────────────────────────

 @Override
 protected String getId(Vendor vendor) {
 return vendor.getVendorId();
 }

 /**
 * Serializes a Vendor to a pipe-delimited line.
 * Pipe characters inside field values are replaced with a safe placeholder
 * to avoid parse corruption.
 */
 @Override
 protected String serialize(Vendor v) {
 return escape(v.getVendorId()) + SEP +
 escape(v.getVendorName()) + SEP +
 escape(v.getCategory().name()) + SEP +
 escape(v.getContactPerson()) + SEP +
 escape(v.getEmail()) + SEP +
 escape(v.getPhone()) + SEP +
 escape(v.getAddress()) + SEP +
 escape(v.getServiceDescription()) + SEP +
 (v.getBasePrice() != null ? v.getBasePrice().toPlainString() : "0.00") + SEP +
 escape(v.getAvailability()) + SEP +
 escape(v.getApprovalStatus()) + SEP +
 escape(v.getRegisteredAt()) + SEP +
 v.isActive();
 }

 /**
 * Deserializes a pipe-delimited line back into a Vendor.
 */
 @Override
 protected Vendor deserialize(String line) {
 if (line == null || line.trim().isEmpty()) return null;
 String[] parts = line.split(SEP_REGEX, -1);
 if (parts.length < 13) return null;

 String vendorId = unescape(parts[0]);
 String vendorName = unescape(parts[1]);
 VendorCategory category = VendorCategory.fromString(unescape(parts[2]));
 String contactPerson = unescape(parts[3]);
 String email = unescape(parts[4]);
 String phone = unescape(parts[5]);
 String address = unescape(parts[6]);
 String serviceDescription = unescape(parts[7]);
 BigDecimal basePrice;
 try {
 basePrice = new BigDecimal(parts[8].trim());
 } catch (NumberFormatException e) {
 basePrice = BigDecimal.ZERO;
 }
 String availability = unescape(parts[9]);
 String approvalStatus = unescape(parts[10]);
 String registeredAt = unescape(parts[11]);
 boolean active = Boolean.parseBoolean(parts[12].trim());

 return new Vendor(vendorId, vendorName, category, contactPerson, email,
 phone, address, serviceDescription, basePrice,
 availability, approvalStatus, registeredAt, active);
 }

 // ── Helper: pipe-safe escaping ────────────────────────────────────────────

 private String escape(String value) {
 if (value == null) return "";
 return value.replace("|", "[[PIPE]]").replace("\r", " ").replace("\n", " ");
 }

 private String unescape(String value) {
 if (value == null) return "";
 return value.replace("[[PIPE]]", "|");
 }

 // ── Custom Query Methods ──────────────────────────────────────────────────

 /**
 * Returns all vendors belonging to the given category.
 */
 public List<Vendor> findByCategory(VendorCategory cat) {
 return findAll().stream()
 .filter(v -> v.getCategory() == cat)
 .collect(Collectors.toList());
 }

 /**
 * Returns all vendors with the given approval status ("PENDING", "APPROVED", "REJECTED").
 */
 public List<Vendor> findByApprovalStatus(String status) {
 return findAll().stream()
 .filter(v -> status.equalsIgnoreCase(v.getApprovalStatus()))
 .collect(Collectors.toList());
 }

 /**
 * Returns all vendors with the given availability ("AVAILABLE", "BUSY", "INACTIVE").
 */
 public List<Vendor> findByAvailability(String availability) {
 return findAll().stream()
 .filter(v -> availability.equalsIgnoreCase(v.getAvailability()))
 .collect(Collectors.toList());
 }

 /**
 * Case-insensitive name search.
 */
 public List<Vendor> searchByName(String query) {
 if (query == null || query.trim().isEmpty()) return findAll();
 String lower = query.trim().toLowerCase();
 return findAll().stream()
 .filter(v -> v.getVendorName().toLowerCase().contains(lower))
 .collect(Collectors.toList());
 }

 /**
 * Returns vendors that are both APPROVED and active.
 */
 public List<Vendor> findApproved() {
 return findAll().stream()
 .filter(v -> "APPROVED".equalsIgnoreCase(v.getApprovalStatus()) && v.isActive())
 .collect(Collectors.toList());
 }
}
