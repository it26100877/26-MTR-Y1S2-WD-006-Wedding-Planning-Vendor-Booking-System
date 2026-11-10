package com.wedding.member04_booking.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member01_customer_admin.service.CustomerService;
import com.wedding.member02_wedding_plan.model.WeddingPlan;
import com.wedding.member02_wedding_plan.service.WeddingPlanService;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.time.LocalDate;

/**
 * Controller managing Vendor Bookings, scheduling conflict checks, and status lifecycle.
 * Integrates Customer, WeddingPlan, Vendor, and Contract modules.
 *
 * @author Member 04 – Booking Management
 */
@Controller
public class BookingController {

 private final BookingService bookingService;
 private final VendorService vendorService;
 private final WeddingPlanService planService;
 private final CustomerService customerService;
 private final com.wedding.member04_booking.service.BookingNotificationService notificationService;

 public BookingController(BookingService bookingService,
 VendorService vendorService,
 WeddingPlanService planService,
 CustomerService customerService,
 com.wedding.member04_booking.service.BookingNotificationService notificationService) {
 this.bookingService = bookingService;
 this.vendorService = vendorService;
 this.planService = planService;
 this.customerService = customerService;
 this.notificationService = notificationService;
 }

 // ─── Premium dashboard, scoped search and filters ─────────────────────────
 @GetMapping("/bookings")
 public String listBookings(@RequestParam(value = "status", required = false) String status,
                            @RequestParam(value = "q", required = false) String query,
                            @RequestParam(value = "vendorId", required = false) String vendorId,
                            @RequestParam(value = "from", required = false) String from,
                            @RequestParam(value = "to", required = false) String to,
                            HttpSession session, Model model) {
     User user = (User) session.getAttribute("currentUser");
     if (user == null) return "redirect:/login";
     boolean admin = "ADMIN".equalsIgnoreCase(user.getRole());
     List<Booking> all = admin ? bookingService.getAllBookings() : bookingService.getByCustomer(user.getUserId());
     Map<String, String> vendors = new HashMap<>();
     Map<String, String> customers = new HashMap<>();
     for (Booking b : all) {
         vendors.computeIfAbsent(b.getVendorId(), id -> vendorName(id));
         customers.computeIfAbsent(b.getCustomerId(), id -> customerName(id));
     }
     String term = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
     List<Booking> filtered = all.stream().filter(b -> status == null || status.isBlank()
             || b.getStatus().name().equalsIgnoreCase(status))
         .filter(b -> vendorId == null || vendorId.isBlank() || b.getVendorId().equalsIgnoreCase(vendorId))
         .filter(b -> from == null || from.isBlank() || b.getBookingDate().compareTo(from) >= 0)
         .filter(b -> to == null || to.isBlank() || b.getBookingDate().compareTo(to) <= 0)
         .filter(b -> term.isBlank() || b.getBookingId().toLowerCase(Locale.ROOT).contains(term)
              || b.getBookingDate().contains(term)
              || vendors.get(b.getVendorId()).toLowerCase(Locale.ROOT).contains(term)
              || (admin && customers.get(b.getCustomerId()).toLowerCase(Locale.ROOT).contains(term)))
         .sorted((a, b) -> b.getRequestedAt().compareTo(a.getRequestedAt()))
         .toList();
     model.addAttribute("bookings", filtered);
     model.addAttribute("totalCount", all.size());
     for (BookingStatus st : BookingStatus.values())
         model.addAttribute(st.name().toLowerCase(Locale.ROOT) + "Count",
                 all.stream().filter(b -> b.getStatus() == st).count());
     model.addAttribute("vendorNames", vendors);
     model.addAttribute("customerNames", customers);
     model.addAttribute("approvedVendors", vendorService.getApprovedVendors());
     model.addAttribute("statuses", BookingStatus.values());
     model.addAttribute("selectedStatus", status == null ? "" : status);
     model.addAttribute("query", query == null ? "" : query);
     model.addAttribute("selectedVendorId", vendorId == null ? "" : vendorId);
     model.addAttribute("dateFrom", from == null ? "" : from);
     model.addAttribute("dateTo", to == null ? "" : to);
     model.addAttribute("isBookingAdmin", admin);
     model.addAttribute("unreadNotifications", notificationService.unreadCount(admin ? "ADMIN" : user.getUserId()));
     model.addAttribute("pageTitle", "Booking Studio - Éclat");
     model.addAttribute("activePage", "bookings");
     return "member04/bookings";
 }

 private String vendorName(String id) {
     try { return vendorService.getById(id).getVendorName(); } catch (RuntimeException e) { return id; }
 }
 private String customerName(String id) {
     try { return customerService.getById(id).getName(); } catch (RuntimeException e) { return id; }
 }

 // ─── Create Booking Form ─────────────────────────────────────────────────────
 @GetMapping("/bookings/new")
 public String showBookingForm(@RequestParam(value = "vendorId", required = false) String vendorId,
 @RequestParam(value = "date", required = false) String date,
 HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 List<Vendor> approvedVendors = vendorService.getApprovedVendors();
 List<WeddingPlan> customerPlans = planService.getByCustomerId(user.getUserId()).stream()
         .filter(plan -> "ACTIVE".equalsIgnoreCase(plan.getStatus()))
         .toList();

 if (customerPlans.isEmpty()) {
 redirectAttributes.addFlashAttribute("warningMessage",
 "Please create at least one Wedding Plan before requesting a vendor booking.");
 return "redirect:/plans/new";
 }

 Vendor selectedVendor = null;
 if (vendorId != null && !vendorId.isEmpty()) {
 try {
 Vendor candidate = vendorService.getById(vendorId);
 if ("APPROVED".equalsIgnoreCase(candidate.getApprovalStatus()) && candidate.isActive()) {
  selectedVendor = candidate;
 } else {
  redirectAttributes.addFlashAttribute("warningMessage", "That vendor is not currently available for booking.");
 }
 } catch (Exception ignored) {}
 }

 model.addAttribute("vendors", approvedVendors);
 model.addAttribute("plans", customerPlans);
 model.addAttribute("selectedVendor", selectedVendor);
 model.addAttribute("earliestDate", LocalDate.now().plusDays(1).toString());
 model.addAttribute("requestedDate", date != null && date.matches("\\d{4}-\\d{2}-\\d{2}") ? date : "");
 model.addAttribute("pageTitle", "Request Vendor Booking - Éclat");
 model.addAttribute("activePage", "bookings");
 return "member04/booking-form";
 }

 @PostMapping("/bookings/new")
 public String processBooking(@RequestParam("vendorId") String vendorId,
 @RequestParam("planId") String planId,
 @RequestParam("bookingDate") String bookingDate,
 @RequestParam("serviceDetails") String serviceDetails,
 @RequestParam("agreedAmount") BigDecimal agreedAmount,
 @RequestParam(value = "notes", required = false) String notes,
 HttpSession httpSession,
 RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 WeddingPlan selectedPlan = planService.getById(planId);
 if (!user.getUserId().equalsIgnoreCase(selectedPlan.getCustomerId())) {
  redirectAttributes.addFlashAttribute("errorMessage", "You can only book using your own wedding plan.");
  return "redirect:/bookings";
 }
 if (!"ACTIVE".equalsIgnoreCase(selectedPlan.getStatus())) {
  redirectAttributes.addFlashAttribute("errorMessage", "Only active wedding plans can be used for a booking.");
  return "redirect:/plans";
 }
 Booking booking = bookingService.createBooking(
 user.getUserId(), vendorId, planId, bookingDate, serviceDetails, agreedAmount, notes);
 redirectAttributes.addFlashAttribute("successMessage",
 "Booking request " + booking.getBookingId() + " submitted successfully! Status: PENDING.");
 return "redirect:/bookings";
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/bookings/new?vendorId=" + vendorId;
 }
 }

 // ─── Booking Details ─────────────────────────────────────────────────────────
 @GetMapping("/bookings/view/{id}")
 public String viewBookingDetail(@PathVariable("id") String id, HttpSession httpSession,
 Model model, RedirectAttributes redirectAttributes) {
 User viewer = (User) httpSession.getAttribute("currentUser");
 if (viewer == null) return "redirect:/login";
 Booking booking;
 try { booking = bookingService.getById(id); } catch (AppException e) {
     redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); return "redirect:/bookings";
 }
 if (!"ADMIN".equalsIgnoreCase(viewer.getRole())
     && !booking.getCustomerId().equals(viewer.getUserId())) {
 redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to view this booking.");
 return "redirect:/bookings";
 }
 Vendor vendor = null;
 WeddingPlan plan = null;
 try { vendor = vendorService.getById(booking.getVendorId()); } catch (Exception ignored) {}
 try { plan = planService.getById(booking.getPlanId()); } catch (Exception ignored) {}

 model.addAttribute("booking", booking);
 model.addAttribute("vendor", vendor);
 model.addAttribute("plan", plan);
 model.addAttribute("customerName", customerName(booking.getCustomerId()));
 model.addAttribute("pageTitle", "Booking " + booking.getBookingId());
 model.addAttribute("activePage", "bookings");
 return "member04/booking-detail";
 }


 @GetMapping("/bookings/edit/{id}")
 public String showEditBookingForm(@PathVariable("id") String id, HttpSession httpSession,
                                   Model model, RedirectAttributes redirectAttributes) {
  User user = (User) httpSession.getAttribute("currentUser");
  if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";
  try {
   Booking booking = bookingService.getById(id);
   if (!booking.getCustomerId().equalsIgnoreCase(user.getUserId())
           || booking.getStatus() != BookingStatus.PENDING) {
    redirectAttributes.addFlashAttribute("errorMessage", "Only your pending booking requests can be edited.");
    return "redirect:/bookings";
   }
   model.addAttribute("booking", booking);
   model.addAttribute("pageTitle", "Edit Booking - Éclat");
   model.addAttribute("activePage", "bookings");
   return "member04/booking-edit-form";
  } catch (AppException e) {
   redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
   return "redirect:/bookings";
  }
 }

 @PostMapping("/bookings/edit/{id}")
 public String updateBooking(@PathVariable("id") String id,
                             @RequestParam("serviceDetails") String serviceDetails,
                             @RequestParam("agreedAmount") BigDecimal agreedAmount,
                             @RequestParam(value = "notes", required = false) String notes,
                             HttpSession httpSession, RedirectAttributes redirectAttributes) {
  User user = (User) httpSession.getAttribute("currentUser");
  if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";
  try {
   Booking booking = bookingService.getById(id);
   if (!booking.getCustomerId().equalsIgnoreCase(user.getUserId())) {
    redirectAttributes.addFlashAttribute("errorMessage", "You cannot edit another customer's booking.");
    return "redirect:/bookings";
   }
   bookingService.updateBooking(id, serviceDetails, agreedAmount, notes);
   redirectAttributes.addFlashAttribute("successMessage", "Booking request updated successfully.");
  } catch (AppException e) {
   redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
  }
  return "redirect:/bookings/view/" + id;
 }

 // ─── Status Transitions ──────────────────────────────────────────────────────
 @PostMapping("/bookings/confirm/{id}")
 public String confirmBooking(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 if (!isAdmin(httpSession)) {
 redirectAttributes.addFlashAttribute("errorMessage", "Administrator access required.");
 return "redirect:/login";
 }

 try {
 bookingService.confirmBooking(id);
 redirectAttributes.addFlashAttribute("successMessage", "Booking confirmed! A contract can now be generated.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/bookings/view/" + id;
 }

 @PostMapping("/bookings/reject/{id}")
 public String rejectBooking(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 if (!isAdmin(httpSession)) {
 redirectAttributes.addFlashAttribute("errorMessage", "Administrator access required.");
 return "redirect:/login";
 }

 try {
 bookingService.rejectBooking(id);
 redirectAttributes.addFlashAttribute("warningMessage", "Booking request rejected.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/bookings/view/" + id;
 }

 @PostMapping("/bookings/cancel/{id}")
 public String cancelBooking(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User actor = (User) httpSession.getAttribute("currentUser");
 if (actor == null) return "redirect:/login";
 Booking existing = bookingService.getById(id);
 if (!isAdmin(httpSession) && !existing.getCustomerId().equals(actor.getUserId())) {
 redirectAttributes.addFlashAttribute("errorMessage", "You are not allowed to cancel this booking.");
 return "redirect:/bookings";
 }
 if (!isAdmin(httpSession) && existing.getStatus() != BookingStatus.PENDING) {
  redirectAttributes.addFlashAttribute("errorMessage", "Customers may cancel pending requests only. Contact an administrator for confirmed bookings.");
  return "redirect:/bookings/view/" + id;
 }
 try {
 bookingService.cancelBooking(id);
 redirectAttributes.addFlashAttribute("warningMessage", "Booking cancelled.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/bookings/view/" + id;
 }

 @PostMapping("/bookings/complete/{id}")
 public String completeBooking(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 if (!isAdmin(httpSession)) {
 redirectAttributes.addFlashAttribute("errorMessage", "Administrator access required.");
 return "redirect:/login";
 }

 try {
 bookingService.completeBooking(id);
 redirectAttributes.addFlashAttribute("successMessage",
 "Booking marked COMPLETED! The customer is now eligible to submit a verified review.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/bookings/view/" + id;
 }

 private boolean isAdmin(HttpSession session) {
 User user = (User) session.getAttribute("currentUser");
 return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
 }
}

