package com.wedding.common.model;

/**
 * Session model that holds the currently logged-in user.
 * Singleton pattern — shared across all modules.
 *
 * @author Common Module
 */
public class Session {

 private static Session instance;

 private User currentUser;
 private String selectedPlanId; // Plan context for sub-panels
 private String selectedVendorId; // Vendor context
 private String selectedBookingId;

 private Session() {}

 public static Session getInstance() {
 if (instance == null) {
 instance = new Session();
 }
 return instance;
 }

 public void login(User user) {
 this.currentUser = user;
 this.selectedPlanId = null;
 this.selectedVendorId = null;
 this.selectedBookingId = null;
 }

 public void logout() {
 this.currentUser = null;
 this.selectedPlanId = null;
 this.selectedVendorId = null;
 this.selectedBookingId = null;
 }

 public boolean isLoggedIn() { return currentUser != null; }
 public boolean isAdmin() { return currentUser != null && "ADMIN".equals(currentUser.getRole()); }
 public boolean isCustomer() { return currentUser != null && "CUSTOMER".equals(currentUser.getRole()); }

 public User getCurrentUser() { return currentUser; }
 public String getCurrentUserId() { return currentUser != null ? currentUser.getUserId() : null; }
 public String getCurrentUserName(){ return currentUser != null ? currentUser.getName() : ""; }

 public String getSelectedPlanId() { return selectedPlanId; }
 public void setSelectedPlanId(String id) { this.selectedPlanId = id; }

 public String getSelectedVendorId() { return selectedVendorId; }
 public void setSelectedVendorId(String id) { this.selectedVendorId = id; }

 public String getSelectedBookingId() { return selectedBookingId; }
 public void setSelectedBookingId(String id) { this.selectedBookingId = id; }
}

