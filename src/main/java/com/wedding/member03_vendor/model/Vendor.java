package com.wedding.member03_vendor.model;

import java.math.BigDecimal;

/**
 * Domain model representing a Wedding Service Vendor.
 * Demonstrates encapsulation: all fields are private with public getters/setters.
 *
 * @author Member 03 – Vendor Management
 */
public class Vendor {

 // ── Fields ────────────────────────────────────────────────────────────────
 private String vendorId;
 private String vendorName;
 private VendorCategory category;
 private String contactPerson;
 private String email;
 private String phone;
 private String address;
 private String serviceDescription;
 private BigDecimal basePrice;
 /** "AVAILABLE" | "BUSY" | "INACTIVE" */
 private String availability;
 /** "PENDING" | "APPROVED" | "REJECTED" */
 private String approvalStatus;
 private String registeredAt;
 private boolean active;

 // ── Full Constructor ──────────────────────────────────────────────────────
 public Vendor(String vendorId,
 String vendorName,
 VendorCategory category,
 String contactPerson,
 String email,
 String phone,
 String address,
 String serviceDescription,
 BigDecimal basePrice,
 String availability,
 String approvalStatus,
 String registeredAt,
 boolean active) {
 this.vendorId = vendorId;
 this.vendorName = vendorName;
 this.category = category;
 this.contactPerson = contactPerson;
 this.email = email;
 this.phone = phone;
 this.address = address;
 this.serviceDescription = serviceDescription;
 this.basePrice = basePrice;
 this.availability = availability;
 this.approvalStatus = approvalStatus;
 this.registeredAt = registeredAt;
 this.active = active;
 }

 // ── Getters ───────────────────────────────────────────────────────────────
 public String getVendorId() { return vendorId; }
 public String getVendorName() { return vendorName; }
 public VendorCategory getCategory() { return category; }
 public String getContactPerson() { return contactPerson; }
 public String getEmail() { return email; }
 public String getPhone() { return phone; }
 public String getAddress() { return address; }
 public String getServiceDescription() { return serviceDescription; }
 public BigDecimal getBasePrice() { return basePrice; }
 public String getAvailability() { return availability; }
 public String getApprovalStatus() { return approvalStatus; }
 public String getRegisteredAt() { return registeredAt; }
 public boolean isActive() { return active; }

 // ── Setters ───────────────────────────────────────────────────────────────
 public void setVendorId(String vendorId) { this.vendorId = vendorId; }
 public void setVendorName(String vendorName) { this.vendorName = vendorName; }
 public void setCategory(VendorCategory category) { this.category = category; }
 public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
 public void setEmail(String email) { this.email = email; }
 public void setPhone(String phone) { this.phone = phone; }
 public void setAddress(String address) { this.address = address; }
 public void setServiceDescription(String serviceDescription) { this.serviceDescription = serviceDescription; }
 public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
 public void setAvailability(String availability) { this.availability = availability; }
 public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
 public void setRegisteredAt(String registeredAt) { this.registeredAt = registeredAt; }
 public void setActive(boolean active) { this.active = active; }

 // ── toString ──────────────────────────────────────────────────────────────
 @Override
 public String toString() {
 return "Vendor{" +
 "vendorId='" + vendorId + '\'' +
 ", vendorName='" + vendorName + '\'' +
 ", category=" + category +
 ", contactPerson='" + contactPerson + '\'' +
 ", email='" + email + '\'' +
 ", phone='" + phone + '\'' +
 ", basePrice=" + basePrice +
 ", availability='" + availability + '\'' +
 ", approvalStatus='"+ approvalStatus + '\'' +
 ", registeredAt='" + registeredAt + '\'' +
 ", active=" + active +
 '}';
 }
}
