package com.wedding.vendor.service;

import com.wedding.vendor.model.Vendor;
import java.util.List;
import java.util.Optional;

public interface VendorOperations {

    List<Vendor> getAllVendors();

    Optional<Vendor> getVendorById(String vendorId);

    Vendor addVendor(Vendor vendor);

    Optional<Vendor> updateVendor(String vendorId, Vendor vendor);

    boolean deleteVendor(String vendorId);
}