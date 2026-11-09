package com.wedding.member06_review_feedback.controller;

import com.wedding.common.exceptions.AppException;
import com.wedding.common.model.User;
import com.wedding.member03_vendor.model.Vendor;
import com.wedding.member03_vendor.service.VendorService;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.service.BookingService;
import com.wedding.member06_review_feedback.model.Review;
import com.wedding.member06_review_feedback.model.ReviewStatus;
import com.wedding.member06_review_feedback.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller managing customer reviews, star ratings, and administrator moderation.
 * Enforces verified booking validation (only COMPLETED bookings).
 *
 * @author Member 06 – Review & Feedback Management
 */
@Controller
public class ReviewFeedbackController {

 private final ReviewService reviewService;
 private final BookingService bookingService;
 private final VendorService vendorService;

 public ReviewFeedbackController(ReviewService reviewService,
 BookingService bookingService,
 VendorService vendorService) {
 this.reviewService = reviewService;
 this.bookingService = bookingService;
 this.vendorService = vendorService;
 }

 // ─── Browse Reviews ──────────────────────────────────────────────────────────
 @GetMapping("/reviews")
 public String listReviews(@RequestParam(value = "vendorId", required = false) String vendorId,
 HttpSession httpSession, Model model) {
 User user = (User) httpSession.getAttribute("currentUser");
 boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());
 boolean isCustomer = user != null && "CUSTOMER".equalsIgnoreCase(user.getRole());

 // Community feedback should be shared across customers, but unpublished reviews
 // must remain private. An owner can still view and edit their own pending review.
 List<Review> allReviews = reviewService.getAllReviews();
 List<Review> reviews = allReviews.stream()
     .filter(r -> vendorId == null || vendorId.isBlank()
             || vendorId.equalsIgnoreCase(r.getVendorId()))
     .filter(r -> isAdmin
             || r.getStatus() == ReviewStatus.APPROVED
             || (isCustomer && user.getUserId().equalsIgnoreCase(r.getCustomerId())))
     .collect(Collectors.toList());

 // Public statistics count ONLY approved reviews, not unpublished submissions,
 // so every visitor sees the same community numbers for the same vendor filter.
 List<Review> approvedReviews = allReviews.stream()
     .filter(r -> r.getStatus() == ReviewStatus.APPROVED)
     .filter(r -> vendorId == null || vendorId.isBlank()
             || vendorId.equalsIgnoreCase(r.getVendorId()))
     .collect(Collectors.toList());
 long ratingSum = approvedReviews.stream().mapToLong(Review::getRating).sum();
 long fiveStarCount = approvedReviews.stream().filter(r -> r.getRating() == 5).count();
 double averageRating = approvedReviews.isEmpty() ? 0.0
         : (double) ratingSum / approvedReviews.size();

 model.addAttribute("reviews", reviews);
 model.addAttribute("approvedReviewCount", approvedReviews.size());
 model.addAttribute("approvedAverageRating", averageRating);
 model.addAttribute("approvedFiveStarCount", fiveStarCount);
 model.addAttribute("visibleReviewCount", reviews.size());
 model.addAttribute("vendors", isAdmin
         ? vendorService.getAllVendors() : vendorService.getApprovedVendors());
 model.addAttribute("selectedVendorId", vendorId);
 model.addAttribute("pageTitle", "Verified Reviews & Ratings - Éclat");
 model.addAttribute("activePage", "reviews");
 return "member06/reviews";
 }

 // ─── Submit Review Form ──────────────────────────────────────────────────────
 @GetMapping("/reviews/new")
 public String showReviewForm(@RequestParam(value = "bookingId", required = false) String bookingId,
 @RequestParam(value = "vendorId", required = false) String vendorId,
 HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) {
 redirectAttributes.addFlashAttribute("errorMessage", "Only logged-in customers can submit reviews.");
 return "redirect:/login";
 }

 // Get completed bookings for this customer
 List<Booking> eligibleBookings = bookingService.getByCustomer(user.getUserId()).stream()
 .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
 .collect(Collectors.toList());

 if (eligibleBookings.isEmpty()) {
 redirectAttributes.addFlashAttribute("warningMessage",
 "You do not have any COMPLETED vendor bookings eligible for review yet.");
 return "redirect:/bookings";
 }

 model.addAttribute("eligibleBookings", eligibleBookings);
 model.addAttribute("preselectedBookingId", bookingId);
 model.addAttribute("preselectedVendorId", vendorId);
 model.addAttribute("pageTitle", "Submit Verified Review - Éclat");
 model.addAttribute("activePage", "reviews");
 return "member06/review-form";
 }

 @PostMapping("/reviews/new")
 public String submitReview(@RequestParam("bookingId") String bookingId,
 @RequestParam("vendorId") String vendorId,
 @RequestParam("rating") int rating,
 @RequestParam("title") String title,
 @RequestParam("feedback") String feedback,
 HttpSession httpSession,
 RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null) return "redirect:/login";

 try {
 Review review = reviewService.submitReview(user.getUserId(), vendorId, bookingId, rating, title, feedback);
 redirectAttributes.addFlashAttribute("successMessage",
 "Thank you! Your verified review has been submitted for moderation (Status: PENDING_MODERATION).");
 return "redirect:/reviews";
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 return "redirect:/reviews/new?bookingId=" + bookingId + "&vendorId=" + vendorId;
 }
 }


 // Customer may update only their own review while it is awaiting moderation.
 @PostMapping("/reviews/update/{id}")
 public String updateReview(@PathVariable("id") String id,
         @RequestParam("rating") int rating,
         @RequestParam("title") String title,
         @RequestParam("feedback") String feedback,
         HttpSession httpSession, RedirectAttributes redirectAttributes) {
     User user = (User) httpSession.getAttribute("currentUser");
     if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) return "redirect:/login";
     try {
         Review existing = reviewService.getById(id);
         if (!existing.getCustomerId().equals(user.getUserId())) {
             redirectAttributes.addFlashAttribute("errorMessage", "You can only edit your own review.");
             return "redirect:/reviews";
         }
         reviewService.updateReviewForCustomer(id, user.getUserId(), rating, title, feedback);
         redirectAttributes.addFlashAttribute("successMessage", "Review updated successfully.");
     } catch (AppException e) {
         redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
     }
     return "redirect:/reviews";
 }

 // ─── Admin Review Moderation Panel ───────────────────────────────────────────
 @GetMapping("/admin/reviews/moderation")
 public String showModerationPanel(HttpSession httpSession, Model model, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
 redirectAttributes.addFlashAttribute("errorMessage", "Admin access required.");
 return "redirect:/login";
 }

 List<Review> pendingReviews = reviewService.getPendingReviews();
 model.addAttribute("pendingReviews", pendingReviews);
 model.addAttribute("pageTitle", "Review Moderation - Admin");
 model.addAttribute("activePage", "admin");
 return "member06/review-moderation";
 }

 @PostMapping("/admin/reviews/approve/{id}")
 public String approveReview(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 reviewService.approveReview(id, user.getUserId());
 redirectAttributes.addFlashAttribute("successMessage", "Review approved and published to vendor catalog!");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/admin/reviews/moderation";
 }

 @PostMapping("/admin/reviews/reject/{id}")
 public String rejectReview(@PathVariable("id") String id,
 @RequestParam(value = "reason", defaultValue = "Violates guidelines") String reason,
 HttpSession httpSession,
 RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 reviewService.rejectReview(id, user.getUserId(), reason);
 redirectAttributes.addFlashAttribute("warningMessage", "Review rejected.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/admin/reviews/moderation";
 }

 @PostMapping("/admin/reviews/delete/{id}")
 public String deleteReview(@PathVariable("id") String id, HttpSession httpSession, RedirectAttributes redirectAttributes) {
 User user = (User) httpSession.getAttribute("currentUser");
 if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) return "redirect:/login";

 try {
 reviewService.deleteReview(id);
 redirectAttributes.addFlashAttribute("successMessage", "Review deleted permanently.");
 } catch (AppException e) {
 redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
 }
 return "redirect:/reviews";
 }
}

