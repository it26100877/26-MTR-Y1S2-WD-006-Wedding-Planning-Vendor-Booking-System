package com.wedding.vendor.controller;

import com.wedding.vendor.model.Vendor;
import com.wedding.vendor.service.VendorService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    // GET: View all vendors
    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    // GET: View one vendor
    @GetMapping("/{vendorId}")
    public ResponseEntity<?> getVendorById(
            @PathVariable String vendorId) {

        Optional<Vendor> vendor =
                vendorService.getVendorById(vendorId);

        if (vendor.isPresent()) {
            return ResponseEntity.ok(vendor.get());
        }

        return ResponseEntity.notFound().build();
    }

    // POST: Add a vendor
    @PostMapping
    public ResponseEntity<?> addVendor(
            @RequestBody Vendor vendor) {

        try {
            Vendor savedVendor = vendorService.addVendor(vendor);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedVendor);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // PUT: Update a vendor
    @PutMapping("/{vendorId}")
    public ResponseEntity<?> updateVendor(
            @PathVariable String vendorId,
            @RequestBody Vendor vendor) {

        try {
            Optional<Vendor> updatedVendor =
                    vendorService.updateVendor(vendorId, vendor);

            if (updatedVendor.isPresent()) {
                return ResponseEntity.ok(updatedVendor.get());
            }

            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // DELETE: Delete a vendor
    @DeleteMapping("/{vendorId}")
    public ResponseEntity<Void> deleteVendor(
            @PathVariable String vendorId) {

        if (vendorService.deleteVendor(vendorId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}