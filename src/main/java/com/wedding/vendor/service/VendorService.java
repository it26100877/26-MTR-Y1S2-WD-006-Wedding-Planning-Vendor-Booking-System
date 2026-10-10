package com.wedding.vendor.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.wedding.vendor.model.Vendor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VendorService {

    private final Map<String, Vendor> vendors = new LinkedHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Path storageFile = Paths.get("vendors.json");

    public VendorService() {
        loadFromFile();
    }

    // READ: Get all vendors
    public List<Vendor> getAllVendors() {
        return new ArrayList<>(vendors.values());
    }

    // READ: Get vendor by ID
    public Optional<Vendor> getVendorById(String vendorId) {
        return Optional.ofNullable(vendors.get(vendorId));
    }

    // CREATE: Add vendor
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

        saveToFile();

        return vendor;
    }

    // UPDATE: Update vendor
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

        saveToFile();

        return Optional.of(existingVendor);
    }

    // DELETE: Delete vendor
    public boolean deleteVendor(String vendorId) {

        Vendor removedVendor = vendors.remove(vendorId);

        if (removedVendor != null) {
            saveToFile();
            return true;
        }

        return false;
    }

    // SAVE: Write vendor data to JSON file
    private void saveToFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(
                    storageFile.toFile(),
                    new ArrayList<>(vendors.values())
            );
        } catch (RuntimeException e)  {
            throw new IllegalStateException(
                    "Could not save vendor data: " + e.getMessage(), e);
        }
    }

    // LOAD: Read vendor data when the application starts
    private void loadFromFile() {

        if (!Files.exists(storageFile)) {
            return;
        }

        try {
            List<Vendor> savedVendors = objectMapper.readValue(
                    storageFile.toFile(),
                    new TypeReference<List<Vendor>>() {}
            );

            for (Vendor vendor : savedVendors) {
                if (vendor != null
                        && vendor.getVendorId() != null
                        && !vendor.getVendorId().isBlank()) {
                    vendors.put(vendor.getVendorId(), vendor);
                }
            }

        } catch (RuntimeException e) {
            System.err.println(
                    "Could not load vendor data: " + e.getMessage());
        }
    }
}