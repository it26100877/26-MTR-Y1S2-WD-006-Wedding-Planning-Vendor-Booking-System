package com.wedding.vendor.service;

import com.wedding.vendor.model.Vendor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VendorService implements VendorOperations {

    private final Map<String, Vendor> vendors = new LinkedHashMap<>();

    // Vendor data is stored in the project working directory.
    private final Path dataFile = Path.of("vendors.txt");

    public VendorService() {
        loadFromFile();
    }

    public List<Vendor> getAllVendors() {
        return new ArrayList<>(vendors.values());
    }

    public Optional<Vendor> getVendorById(String vendorId) {
        return Optional.ofNullable(vendors.get(vendorId));
    }

    public Vendor addVendor(Vendor vendor) {
        validateVendor(vendor);

        String id = vendor.getVendorId();

        if (vendors.containsKey(id)) {
            throw new IllegalArgumentException("Vendor ID already exists.");
        }

        vendors.put(id, vendor);
        saveToFile();

        return vendor;
    }

    public Optional<Vendor> updateVendor(String vendorId, Vendor vendor) {
        if (!vendors.containsKey(vendorId)) {
            return Optional.empty();
        }

        validateVendor(vendor);
        vendor.setVendorId(vendorId);

        vendors.put(vendorId, vendor);
        saveToFile();

        return Optional.of(vendor);
    }

    public boolean deleteVendor(String vendorId) {
        Vendor removed = vendors.remove(vendorId);

        if (removed != null) {
            saveToFile();
            return true;
        }

        return false;
    }

    private void validateVendor(Vendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException("Vendor details are required.");
        }

        if (vendor.getVendorId() == null
                || vendor.getVendorId().isBlank()) {
            throw new IllegalArgumentException("Vendor ID is required.");
        }

        if (vendor.getVendorName() == null
                || vendor.getVendorName().isBlank()) {
            throw new IllegalArgumentException("Vendor name is required.");
        }
    }

    private void loadFromFile() {
        if (!Files.exists(dataFile)) {
            return;
        }

        try {
            List<String> lines =
                    Files.readAllLines(dataFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 7) {
                    continue;
                }

                Vendor vendor = new Vendor();
                vendor.setVendorId(fields[0]);
                vendor.setVendorName(fields[1]);
                vendor.setCategory(fields[2]);
                vendor.setContactNumber(fields[3]);
                vendor.setEmail(fields[4]);
                vendor.setPrice(Double.parseDouble(fields[5]));
                vendor.setAvailability(fields[6]);

                vendors.put(vendor.getVendorId(), vendor);
            }

        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException(
                    "Could not load vendor data from "
                            + dataFile.toAbsolutePath(), e);
        }
    }

    private void saveToFile() {
        List<String> lines = new ArrayList<>();

        for (Vendor vendor : vendors.values()) {
            String line = String.join("|",
                    safe(vendor.getVendorId()),
                    safe(vendor.getVendorName()),
                    safe(vendor.getCategory()),
                    safe(vendor.getContactNumber()),
                    safe(vendor.getEmail()),
                    Double.toString(vendor.getPrice()),
                    safe(vendor.getAvailability()));

            lines.add(line);
        }

        try {
            Files.write(
                    dataFile,
                    lines,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not save vendor data to "
                            + dataFile.toAbsolutePath(), e);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("|", "/")
                                    .replace("\n", " ")
                                    .replace("\r", " ");
    }
}