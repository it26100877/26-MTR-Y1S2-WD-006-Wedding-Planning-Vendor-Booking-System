package com.wedding.member03_vendor.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.model.VendorCategory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** File-based repository for Vendor entities. */
public class VendorRepository extends AbstractFileRepository<Vendor> {

    private static final String FILE_NAME = "vendors.csv";
    private static final String SEP = "|";
    private static final String SEP_REGEX = "\\|";

    public VendorRepository() {
        super(FILE_NAME);
    }

    @Override
    protected String getId(Vendor vendor) {
        return vendor.getVendorId();
    }

    /** Serializes one vendor while preserving delimiters and line breaks in values. */
    @Override
    protected String serialize(Vendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException("Vendor cannot be null.");
        }
        return escape(vendor.getVendorId()) + SEP
                + escape(vendor.getVendorName()) + SEP
                + escape(vendor.getCategory() == null ? VendorCategory.OTHER.name() : vendor.getCategory().name()) + SEP
                + escape(vendor.getContactPerson()) + SEP
                + escape(vendor.getEmail()) + SEP
                + escape(vendor.getPhone()) + SEP
                + escape(vendor.getAddress()) + SEP
                + escape(vendor.getServiceDescription()) + SEP
                + (vendor.getBasePrice() == null ? "0.00" : vendor.getBasePrice().toPlainString()) + SEP
                + escape(vendor.getAvailability()) + SEP
                + escape(vendor.getApprovalStatus()) + SEP
                + escape(vendor.getRegisteredAt()) + SEP
                + vendor.isActive();
    }

    @Override
    protected Vendor deserialize(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split(SEP_REGEX, -1);
        if (parts.length != 13) {
            return null;
        }

        BigDecimal basePrice;
        try {
            basePrice = new BigDecimal(parts[8].trim());
        } catch (NumberFormatException ex) {
            return null;
        }

        String activeValue = parts[12].trim();
        if (!"true".equalsIgnoreCase(activeValue) && !"false".equalsIgnoreCase(activeValue)) {
            return null;
        }

        return new Vendor(
                unescape(parts[0]),
                unescape(parts[1]),
                VendorCategory.fromString(unescape(parts[2])),
                unescape(parts[3]),
                unescape(parts[4]),
                unescape(parts[5]),
                unescape(parts[6]),
                unescape(parts[7]),
                basePrice,
                unescape(parts[9]),
                unescape(parts[10]),
                unescape(parts[11]),
                Boolean.parseBoolean(activeValue));
    }

    /** Percent-style escaping prevents delimiter collisions and preserves literal placeholders. */
    private String escape(String value) {
        if (value == null) return "";
        return value.replace("%", "%25")
                .replace("[", "%5B")
                .replace("]", "%5D")
                .replace("|", "%7C")
                .replace("\r", "%0D")
                .replace("\n", "%0A");
    }

    private String unescape(String value) {
        if (value == null) return "";
        // Keep compatibility with records written by the previous [[PIPE]] format.
        return value.replace("[[PIPE]]", "|")
                .replace("%0A", "\n")
                .replace("%0D", "\r")
                .replace("%7C", "|")
                .replace("%5B", "[")
                .replace("%5D", "]")
                .replace("%25", "%");
    }

    public List<Vendor> findByCategory(VendorCategory category) {
        return findAll().stream()
                .filter(v -> v != null && v.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Vendor> findByApprovalStatus(String status) {
        return findAll().stream()
                .filter(v -> v != null && status != null && status.equalsIgnoreCase(v.getApprovalStatus()))
                .collect(Collectors.toList());
    }

    public List<Vendor> findByAvailability(String availability) {
        return findAll().stream()
                .filter(v -> v != null && availability != null && availability.equalsIgnoreCase(v.getAvailability()))
                .collect(Collectors.toList());
    }

    public List<Vendor> searchByName(String query) {
        if (query == null || query.trim().isEmpty()) return findAll();
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        return findAll().stream()
                .filter(v -> v != null && v.getVendorName() != null
                        && v.getVendorName().toLowerCase(Locale.ROOT).contains(normalized))
                .collect(Collectors.toList());
    }

    /** Returns only approved vendors that are currently active. */
    public List<Vendor> findApproved() {
        return findAll().stream()
                .filter(v -> v != null && "APPROVED".equalsIgnoreCase(v.getApprovalStatus()) && v.isActive())
                .collect(Collectors.toList());
    }
}
