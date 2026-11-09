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
import java.util.Locale;
import java.util.stream.Collectors;

/** Business-logic layer for Vendor Management. */
public class VendorService extends BaseService {

    private final VendorRepository vendorRepository;

    public VendorService() {
        this(new VendorRepository());
    }

    /** Accepts an externally constructed repository (useful for testing). */
    public VendorService(VendorRepository vendorRepository) {
        if (vendorRepository == null) {
            throw new IllegalArgumentException("Vendor repository is required.");
        }
        this.vendorRepository = vendorRepository;
    }

    /** Creates a vendor after validation. New registrations require admin approval. */
    public Vendor addVendor(String vendorName, VendorCategory category, String contactPerson,
                            String email, String phone, String address,
                            String serviceDescription, BigDecimal basePrice) {
        validateVendorDetails(vendorName, category, contactPerson, email, phone,
                address, serviceDescription, basePrice);

        String normalizedEmail = normalizeEmail(email);
        ensureEmailIsUnique(normalizedEmail, null);

        String vendorId = FileUtil.generateId("VND");
        Vendor vendor = new Vendor(vendorId, vendorName.trim(), category,
                contactPerson.trim(), normalizedEmail, phone.trim(), address.trim(),
                serviceDescription.trim(), basePrice, "AVAILABLE", "PENDING",
                ValidationUtil.nowDateTime(), true);

        vendorRepository.save(vendor);
        log("Added vendor: " + vendorId + " – " + vendorName);
        return vendor;
    }

    /** Updates editable vendor details without changing approval or booking state. */
    public Vendor updateVendor(String vendorId, String vendorName, VendorCategory category,
                               String contactPerson, String email, String phone, String address,
                               String serviceDescription, BigDecimal basePrice) {
        Vendor vendor = getById(vendorId);
        validateVendorDetails(vendorName, category, contactPerson, email, phone,
                address, serviceDescription, basePrice);

        String normalizedEmail = normalizeEmail(email);
        ensureEmailIsUnique(normalizedEmail, vendor.getVendorId());

        vendor.setVendorName(vendorName.trim());
        vendor.setCategory(category);
        vendor.setContactPerson(contactPerson.trim());
        vendor.setEmail(normalizedEmail);
        vendor.setPhone(phone.trim());
        vendor.setAddress(address.trim());
        vendor.setServiceDescription(serviceDescription.trim());
        vendor.setBasePrice(basePrice);

        vendorRepository.save(vendor);
        log("Updated vendor: " + vendorId);
        return vendor;
    }

    public Vendor approveVendor(String vendorId) {
        Vendor vendor = getById(vendorId);
        if ("REJECTED".equalsIgnoreCase(vendor.getApprovalStatus())) {
            throw new BusinessRuleException("A rejected vendor cannot be approved directly.");
        }
        if ("APPROVED".equalsIgnoreCase(vendor.getApprovalStatus())) {
            return vendor;
        }
        vendor.setApprovalStatus("APPROVED");
        vendorRepository.save(vendor);
        log("Approved vendor: " + vendorId);
        return vendor;
    }

    public Vendor rejectVendor(String vendorId) {
        Vendor vendor = getById(vendorId);
        if ("APPROVED".equalsIgnoreCase(vendor.getApprovalStatus())) {
            throw new BusinessRuleException("An approved vendor cannot be rejected directly. Deactivate it instead.");
        }
        if ("REJECTED".equalsIgnoreCase(vendor.getApprovalStatus())) {
            return vendor;
        }
        vendor.setApprovalStatus("REJECTED");
        vendorRepository.save(vendor);
        log("Rejected vendor: " + vendorId);
        return vendor;
    }

    public Vendor deactivateVendor(String vendorId) {
        Vendor vendor = getById(vendorId);
        vendor.setActive(false);
        vendor.setAvailability("INACTIVE");
        vendorRepository.save(vendor);
        log("Deactivated vendor: " + vendorId);
        return vendor;
    }

    public Vendor activateVendor(String vendorId) {
        Vendor vendor = getById(vendorId);
        if ("REJECTED".equalsIgnoreCase(vendor.getApprovalStatus())) {
            throw new BusinessRuleException("A rejected vendor cannot be activated.");
        }
        vendor.setActive(true);
        vendor.setAvailability("AVAILABLE");
        vendorRepository.save(vendor);
        log("Activated vendor: " + vendorId);
        return vendor;
    }

    /** Sets availability and keeps the active flag consistent with INACTIVE. */
    public Vendor setAvailability(String vendorId, String availability) {
        String normalized = availability == null ? "" : availability.trim().toUpperCase(Locale.ROOT);
        if (!"AVAILABLE".equals(normalized) && !"BUSY".equals(normalized)
                && !"INACTIVE".equals(normalized)) {
            throw new ValidationException("Availability must be AVAILABLE, BUSY, or INACTIVE.");
        }

        Vendor vendor = getById(vendorId);
        if ("REJECTED".equalsIgnoreCase(vendor.getApprovalStatus())
                && !"INACTIVE".equals(normalized)) {
            throw new BusinessRuleException("A rejected vendor cannot be made available.");
        }
        vendor.setAvailability(normalized);
        vendor.setActive(!"INACTIVE".equals(normalized));
        vendorRepository.save(vendor);
        log("Set availability for vendor " + vendorId + " → " + normalized);
        return vendor;
    }

    public List<Vendor> searchVendors(String query, VendorCategory category, String availability) {
        String normalizedQuery = normalizeQuery(query);
        String normalizedAvailability = availability == null ? "" : availability.trim();
        return vendorRepository.findAll().stream()
                .filter(v -> v != null)
                .filter(v -> normalizedQuery.isEmpty() || containsIgnoreCase(v.getVendorName(), normalizedQuery))
                .filter(v -> category == null || v.getCategory() == category)
                .filter(v -> normalizedAvailability.isEmpty()
                        || normalizedAvailability.equalsIgnoreCase(v.getAvailability()))
                .collect(Collectors.toList());
    }

    /** Public catalogue: only approved and active vendors are returned. */
    public List<Vendor> searchApprovedVendors(String query, VendorCategory category, String availability) {
        String normalizedQuery = normalizeQuery(query);
        String normalizedAvailability = availability == null ? "" : availability.trim();
        return vendorRepository.findApproved().stream()
                .filter(v -> normalizedQuery.isEmpty() || containsIgnoreCase(v.getVendorName(), normalizedQuery))
                .filter(v -> category == null || v.getCategory() == category)
                .filter(v -> normalizedAvailability.isEmpty()
                        || normalizedAvailability.equalsIgnoreCase(v.getAvailability()))
                .collect(Collectors.toList());
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public List<Vendor> getApprovedVendors() {
        return vendorRepository.findApproved();
    }

    public Vendor getById(String vendorId) {
        if (vendorId == null || vendorId.trim().isEmpty()) {
            throw new ValidationException("Vendor ID is required.");
        }
        return vendorRepository.findById(vendorId.trim())
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found with ID: " + vendorId));
    }

    public boolean deleteVendor(String vendorId) {
        if (vendorId == null || vendorId.trim().isEmpty()) {
            throw new ValidationException("Vendor ID is required.");
        }
        Vendor vendor = getById(vendorId.trim());
        // Keep historical booking, contract and review references valid.
        vendor.setActive(false);
        vendor.setApprovalStatus("REJECTED");
        vendorRepository.save(vendor);
        log("Archived vendor instead of hard delete: " + vendorId);
        return true;
    }

    public int count() {
        return vendorRepository.count();
    }

    private void validateVendorDetails(String vendorName, VendorCategory category,
                                       String contactPerson, String email, String phone,
                                       String address, String serviceDescription, BigDecimal basePrice) {
        requireNonEmpty(vendorName, "Vendor Name");
        requireNonEmpty(contactPerson, "Contact Person");
        requireNonEmpty(address, "Address");
        requireNonEmpty(serviceDescription, "Service Description");
        if (category == null) {
            throw new ValidationException("Vendor category is required.");
        }
        if (!ValidationUtil.isValidEmail(normalizeEmail(email))) {
            throw new ValidationException("Invalid email address: " + email);
        }
        if (!ValidationUtil.isValidPhone(phone == null ? null : phone.trim())) {
            throw new ValidationException(
                    "Invalid Sri Lankan mobile number. Use 07XXXXXXXX or +947XXXXXXXX.");
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Base price must be greater than zero.");
        }
    }

    private void ensureEmailIsUnique(String email, String currentVendorId) {
        boolean duplicate = vendorRepository.findAll().stream()
                .filter(v -> v != null && v.getEmail() != null)
                .anyMatch(v -> email.equalsIgnoreCase(v.getEmail().trim())
                        && (currentVendorId == null || !currentVendorId.equals(v.getVendorId())));
        if (duplicate) {
            throw new BusinessRuleException("A vendor with this email address already exists.");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeQuery(String query) {
        return query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }
}
