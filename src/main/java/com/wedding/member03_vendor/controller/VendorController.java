package com.wedding.member03_vendor.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.model.VendorCategory;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member06_review_feedback.service.ReviewService;
import com.wedding.member04_booking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * Controller managing Vendor registration, catalog browsing, filtering, and admin approvals.
 * Integrates with Member 06 for displaying dynamic ratings.
 *
 * @author Member 03 – Vendor Management
 */
@Controller
public class VendorController {

 private final VendorService vendorService;
 private final ReviewService reviewService;
 private final BookingService bookingService;

 public VendorController(VendorService vendorService, ReviewService reviewService, BookingService bookingService) {
 this.vendorService = vendorService;
 this.reviewService = reviewService;
 this.bookingService = bookingService;
 }

 // ─── Vendor Catalog ──────────────────────────────────────────────────────────
 @GetMapping("/vendors")
 public String listVendors(@RequestParam(value = "query", required = false) String query,
 @RequestParam(value = "category", required = false) String categoryStr,
 @RequestParam(value = "filter", required = false) String filter,
 @RequestParam(value = "location", required = false) String location,
 HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

 VendorCategory category = null;
 if (categoryStr != null && !categoryStr.trim().isEmpty()) {
     try {
         category = VendorCategory.valueOf(categoryStr.trim().toUpperCase());
     } catch (IllegalArgumentException ex) {
         // An unknown category should return no results, not silently show all vendors.
         model.addAttribute("vendors", List.of());
         model.addAttribute("categories", VendorCategory.values());
         model.addAttribute("selectedCategory", categoryStr);
         model.addAttribute("query", query);
         model.addAttribute("filter", filter);
         model.addAttribute("location", location);
         model.addAttribute("pageTitle", "Vendors Directory - Éclat");
         model.addAttribute("activePage", "vendors");
         return "member03/vendors";
     }
 }

 final VendorCategory selectedCategory = category;
 List<Vendor> vendors;
 if (isAdmin && "PENDING".equalsIgnoreCase(filter)) {
     vendors = vendorService.getAllVendors().stream()
             .filter(v -> "PENDING".equalsIgnoreCase(v.getApprovalStatus()))
             .filter(v -> selectedCategory == null || v.getCategory() == selectedCategory)
             .filter(v -> query == null || query.trim().isEmpty()
                     || (v.getVendorName() != null && v.getVendorName().toLowerCase(Locale.ROOT).contains(query.trim().toLowerCase(Locale.ROOT))))
             .toList();
 } else if (isAdmin) {
     // Administrators can inspect all vendors, including pending/rejected records.
     vendors = vendorService.searchVendors(query, selectedCategory, null);
 } else {
     // Customers and guests can only browse approved, active vendors.
     vendors = vendorService.searchApprovedVendors(query, selectedCategory, null);
 }
 if (location != null && !location.trim().isEmpty()) {
  String district = location.trim().toLowerCase(Locale.ROOT);
  vendors = vendors.stream()
          .filter(v -> v.getAddress() != null && v.getAddress().toLowerCase(Locale.ROOT).contains(district))
          .toList();
 }

 model.addAttribute("vendors", vendors);
 model.addAttribute("categories", VendorCategory.values());
 model.addAttribute("selectedCategory", categoryStr);
 model.addAttribute("query", query);
 model.addAttribute("filter", filter);
 model.addAttribute("location", location);
 model.addAttribute("pageTitle", "Vendors Directory - Éclat");
 model.addAttribute("activePage", "vendors");
 return "member03/vendors";
 }

 // ─── Register New Vendor ─────────────────────────────────────────────────────
 @GetMapping("/vendors/new")
 public String showAddVendorForm(HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";

 model.addAttribute("isEdit", false);
 model.addAttribute("categories", VendorCategory.values());
 model.addAttribute("pageTitle", "Register Vendor - Éclat");
 model.addAttribute("activePage", "vendors");
 return "member03/vendor-form";
 }

 @PostMapping("/vendors/new")
 public String registerVendor(@RequestParam("vendorName") String vendorName,
 @RequestParam("category") String categoryStr,
 @RequestParam("contactPerson") String contactPerson,
 @RequestParam("email") String email,
 @RequestParam("phone") String phone,
 @RequestParam("address") String address,
 @RequestParam("serviceDescription") String serviceDescription,
 @RequestParam(value = "basePrice", required = false) String basePriceValue,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (actor == null) return "redirect:/login";
 try {
 VendorCategory category = VendorCategory.fromStringStrict(categoryStr);
 BigDecimal basePrice = parseBasePrice(basePriceValue);
 Vendor vendor = vendorService.addVendor(
 vendorName, category, contactPerson, email, phone, address, serviceDescription, basePrice);
 redirectAttributes.addFlashAttribute("successMessage",
 "Vendor '" + vendor.getVendorName() + "' registered successfully! (Awaiting admin approval)");
 return "redirect:/vendors";
 } catch (AppException | IllegalArgumentException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/vendors/new";
 }
 }

 // ─── View Vendor Details ─────────────────────────────────────────────────────
 @GetMapping("/vendors/view/{id}")
 public String viewVendorDetail(@PathVariable("id") String id, HttpSession httpSession,
                                 Model model, RedirectAttributes redirectAttributes) {
 Vendor vendor = vendorService.getById(id);
 User viewer = (User) httpSession.getAttribute("currentUser");
 boolean admin = viewer != null && "ADMIN".equalsIgnoreCase(viewer.getRole());
 if (!admin && (!"APPROVED".equalsIgnoreCase(vendor.getApprovalStatus()) || !vendor.isActive())) {
  redirectAttributes.addFlashAttribute("errorMessage", "This vendor profile is not publicly available.");
  return "redirect:/vendors";
 }
 double avgRating = reviewService.getAverageRating(id);
 int reviewCount = reviewService.getByVendor(id).size();

 model.addAttribute("vendor", vendor);
 model.addAttribute("avgRating", avgRating);
 model.addAttribute("reviewCount", reviewCount);
 model.addAttribute("pageTitle", vendor.getVendorName() + " - Vendor Details");
 model.addAttribute("activePage", "vendors");
 return "member03/vendor-detail";
 }

 // ─── Edit Vendor ─────────────────────────────────────────────────────────────
 @GetMapping("/vendors/edit/{id}")
 public String showEditVendorForm(@PathVariable("id") String id, HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";
 if (!"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/vendors";

 Vendor vendor = vendorService.getById(id);
 model.addAttribute("vendor", vendor);
 model.addAttribute("categories", VendorCategory.values());
 model.addAttribute("isEdit", true);
 model.addAttribute("pageTitle", "Edit Vendor - " + vendor.getVendorName());
 model.addAttribute("activePage", "vendors");
 return "member03/vendor-form";
 }

 @PostMapping("/vendors/edit/{id}")
 public String updateVendor(@PathVariable("id") String id,
 @RequestParam("vendorName") String vendorName,
 @RequestParam("category") String categoryStr,
 @RequestParam("contactPerson") String contactPerson,
 @RequestParam("email") String email,
 @RequestParam("phone") String phone,
 @RequestParam("address") String address,
 @RequestParam("serviceDescription") String serviceDescription,
 @RequestParam(value = "basePrice", required = false) String basePriceValue,
 HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (actor == null || !"ADMIN".equalsIgnoreCase(actor.getRole())) return "redirect:/login";
 try {
 VendorCategory category = VendorCategory.fromStringStrict(categoryStr);
 BigDecimal basePrice = parseBasePrice(basePriceValue);
 vendorService.updateVendor(id, vendorName, category, contactPerson, email, phone, address, serviceDescription, basePrice);
 redirectAttributes.addFlashAttribute("successMessage", "Vendor details updated successfully!");
 } catch (AppException | IllegalArgumentException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors/view/" + id;
 }

 // ─── Admin Moderation Actions ────────────────────────────────────────────────
 @PostMapping("/admin/vendors/approve/{id}")
 public String approveVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 vendorService.approveVendor(id);
 redirectAttributes.addFlashAttribute("successMessage", "Vendor has been approved!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors";
 }

 @PostMapping("/admin/vendors/reject/{id}")
 public String rejectVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 vendorService.rejectVendor(id);
 redirectAttributes.addFlashAttribute("warningMessage", "Vendor registration rejected.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors";
 }

 @PostMapping("/admin/vendors/deactivate/{id}")
 public String deactivateVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 vendorService.deactivateVendor(id);
 redirectAttributes.addFlashAttribute("warningMessage", "Vendor deactivated.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors";
 }


 @PostMapping("/admin/vendors/activate/{id}")
 public String activateVendor(@PathVariable("id") String id, HttpSession httpSession,
                              RedirectAttributes redirectAttributes) {
  User user = (User) httpSession.getAttribute("currentUser");
  if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";
  try {
   vendorService.activateVendor(id);
   redirectAttributes.addFlashAttribute("successMessage", "Vendor activated.");
  } catch (AppException e) {
   redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
  }
  return "redirect:/vendors";
 }

 @PostMapping("/admin/vendors/delete/{id}")
 public String deleteVendor(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 if (!bookingService.getByVendor(id).isEmpty()) {
  redirectAttributes.addFlashAttribute("errorMessage", "This vendor has booking history. Deactivate the vendor instead of deleting it.");
  return "redirect:/vendors";
 }
 vendorService.deleteVendor(id);
 redirectAttributes.addFlashAttribute("successMessage", "Vendor permanently removed.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/vendors";
 }

 private BigDecimal parseBasePrice(String value) {
  if (value == null || value.trim().isEmpty()) {
   throw new com.wedding.common.exceptions.ValidationException("Base price is required.");
  }
  try {
   return new BigDecimal(value.trim());
  } catch (NumberFormatException ex) {
   throw new com.wedding.common.exceptions.ValidationException("Base price must be a valid number.");
  }
 }
}
