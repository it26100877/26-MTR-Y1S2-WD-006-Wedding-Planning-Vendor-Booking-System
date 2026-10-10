package com.wedding.vendor.service;

import com.wedding.vendor.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VendorService {

    private final Map<String, Vendor> vendors = new LinkedHashMap<>();

    // Get all vendors
    public List<Vendor> getAllVendors() {
        return new ArrayList<>(vendors.values());
    }

    // Get vendor by ID
    public Optional<Vendor> getVendorById(String vendorId) {
        return Optional.ofNullable(vendors.get(vendorId));
    }

    // Add vendor
    public Vendor addVendor(Vendor vendor) {
        if (vendor.getVendorId() == null
                || vendor.getVendorId().isBlank()) {
            throw new IllegalArgumentException("Vendor ID is required.");
        }

        if (vendor.getVendorName() == null
                || vendor.getVendorName().isBlank()) {
            throw new IllegalArgumentException("Vendor name is required.");
        }

        String id = vendor.getVendorId().trim();

        if (vendors.containsKey(id)) {
            throw new IllegalArgumentException("Vendor ID already exists.");
        }

        if (vendor.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        vendor.setVendorId(id);
        vendors.put(id, vendor);

        return vendor;
    }

    // Update vendor
    public Optional<Vendor> updateVendor(
            String vendorId, Vendor updatedVendor) {

        Vendor existingVendor = vendors.get(vendorId);

        if (existingVendor == null) {
            return Optional.empty();
        }

        if (updatedVendor.getVendorName() == null
                || updatedVendor.getVendorName().isBlank()) {
            throw new IllegalArgumentException("Vendor name is required.");
        }

        if (updatedVendor.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        existingVendor.setVendorName(updatedVendor.getVendorName());
        existingVendor.setCategory(updatedVendor.getCategory());
        existingVendor.setContactNumber(updatedVendor.getContactNumber());
        existingVendor.setEmail(updatedVendor.getEmail());
        existingVendor.setPrice(updatedVendor.getPrice());
        existingVendor.setAvailability(updatedVendor.getAvailability());

        return Optional.of(existingVendor);
    }

    // Delete vendor
    public boolean deleteVendor(String vendorId) {
        return vendors.remove(vendorId) != null;
    }
}