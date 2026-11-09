package com.wedding.member06_review_feedback.service;

import com.wedding.common.exceptions.BusinessRuleException;
import com.wedding.common.exceptions.EntityNotFoundException;
import com.wedding.common.exceptions.ValidationException;
import com.wedding.common.service.BaseService;
import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;
import com.wedding.member04_booking.repository.BookingRepository;
import com.wedding.member06_review_feedback.model.Review;
import com.wedding.member06_review_feedback.model.ReviewStatus;
import com.wedding.member06_review_feedback.repository.ReviewRepository;

import java.util.List;

/**
 * Service managing customer reviews, moderation, and dynamic rating calculations.
 * Integrates with Booking module for verified customer feedback.
 *
 * @author Member 06 – Review & Feedback Management
 */
public class ReviewService extends BaseService {

 private final ReviewRepository reviewRepository;
 private final BookingRepository bookingRepository;

 public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository) {
 this.reviewRepository = reviewRepository;
 this.bookingRepository = bookingRepository;
 }

 /**
 * Submits a verified review for a completed booking.
 */
 public synchronized Review submitReview(String customerId, String vendorId, String bookingId,
 int rating, String title, String feedback) {
 requireNonEmpty(customerId, "Customer ID");
 requireNonEmpty(vendorId, "Vendor ID");
 requireNonEmpty(bookingId, "Booking ID");
 requireNonEmpty(title, "Review Title");
 requireNonEmpty(feedback, "Feedback Comments");

 if (rating < 1 || rating > 5) {
 throw new ValidationException("Rating must be between 1 and 5 stars.");
 }
 if (feedback.trim().length() < 10) {
 throw new ValidationException("Feedback comment must contain at least 10 characters.");
 }

 // Verify booking existence and completion
 Booking booking = bookingRepository.findById(bookingId)
 .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));

 if (booking.getStatus() != BookingStatus.COMPLETED) {
 throw new BusinessRuleException("Only COMPLETED bookings can be reviewed. Current booking status: " + booking.getStatus());
 }
 if (!customerId.equalsIgnoreCase(booking.getCustomerId())) {
 throw new BusinessRuleException("You can only review bookings associated with your own account.");
 }
 if (!vendorId.equalsIgnoreCase(booking.getVendorId())) {
 throw new BusinessRuleException("Booking does not match the specified vendor.");
 }

 // Check for duplicate reviews on the same booking
 if (reviewRepository.hasReviewForBooking(bookingId)) {
 throw new BusinessRuleException("A review has already been submitted for booking " + bookingId + ".");
 }

 Review review = new Review(
 FileUtil.generateId("REV"),
 customerId,
 vendorId,
 bookingId,
 rating,
 title,
 feedback,
 ValidationUtil.nowDateTime(),
 ValidationUtil.nowDateTime(),
 ReviewStatus.PENDING_MODERATION,
 "",
 ""
 );

 reviewRepository.save(review);
 log("Submitted review " + review.getReviewId() + " for vendor " + vendorId + " (Rating: " + rating + " stars)");
 return review;
 }

 /**
 * Allows customer to edit their pending review before moderation.
 */
 public synchronized Review updateReview(String reviewId, int rating, String title, String feedback) {
 Review review = getById(reviewId);
 if (review.getStatus() != ReviewStatus.PENDING_MODERATION) {
 throw new BusinessRuleException("Only reviews in PENDING_MODERATION status can be edited.");
 }

 if (rating < 1 || rating > 5) {
 throw new ValidationException("Rating must be between 1 and 5 stars.");
 }
 requireNonEmpty(title, "Review Title");
 requireNonEmpty(feedback, "Feedback Comments");
 if (feedback.trim().length() < 10) {
  throw new ValidationException("Feedback comment must contain at least 10 characters.");
 }

 review.setRating(rating);
 review.setTitle(title);
 review.setFeedback(feedback);
 review.setUpdatedAt(ValidationUtil.nowDateTime());
 reviewRepository.save(review);
 return review;
 }

 public synchronized Review updateReviewForCustomer(String reviewId, String customerId,
                                                         int rating, String title, String feedback) {
  Review review = getById(reviewId);
  if (customerId == null || !customerId.equalsIgnoreCase(review.getCustomerId())) {
   throw new BusinessRuleException("You can only update your own review.");
  }
  return updateReview(reviewId, rating, title, feedback);
 }

 /**
 * Approves a review (Admin moderation).
 */
 public synchronized Review approveReview(String reviewId, String adminId) {
 Review review = getById(reviewId);
 if (review.getStatus() != ReviewStatus.PENDING_MODERATION) {
  throw new BusinessRuleException("Only pending reviews can be approved.");
 }
 review.setStatus(ReviewStatus.APPROVED);
 review.setModeratedBy(adminId != null ? adminId : "ADMIN");
 review.setUpdatedAt(ValidationUtil.nowDateTime());
 reviewRepository.save(review);
 log("Approved review " + reviewId + " by " + adminId);
 return review;
 }

 /**
 * Rejects a review with a reason (Admin moderation).
 */
 public synchronized Review rejectReview(String reviewId, String adminId, String reason) {
 requireNonEmpty(reason, "Rejection Reason");
 Review review = getById(reviewId);
 if (review.getStatus() != ReviewStatus.PENDING_MODERATION) {
  throw new BusinessRuleException("Only pending reviews can be rejected.");
 }
 review.setStatus(ReviewStatus.REJECTED);
 review.setModeratedBy(adminId != null ? adminId : "ADMIN");
 review.setModerationNote(reason);
 review.setUpdatedAt(ValidationUtil.nowDateTime());
 reviewRepository.save(review);
 log("Rejected review " + reviewId + " by " + adminId + ": " + reason);
 return review;
 }

 public boolean deleteReview(String reviewId) {
 return reviewRepository.deleteById(reviewId);
 }

 /**
 * Calculates the dynamic average rating for a vendor based on all approved reviews.
 */
 public double getAverageRating(String vendorId) {
 List<Review> approved = reviewRepository.findByVendorId(vendorId);
 if (approved.isEmpty()) return 0.0;
 double sum = approved.stream().mapToInt(Review::getRating).sum();
 return Math.round((sum / approved.size()) * 10.0) / 10.0;
 }

 public Review getById(String reviewId) {
 return reviewRepository.findById(reviewId)
 .orElseThrow(() -> new EntityNotFoundException("Review", reviewId));
 }

 public List<Review> getByVendor(String vendorId) {
 return reviewRepository.findByVendorId(vendorId);
 }

 public List<Review> getByCustomer(String customerId) {
 return reviewRepository.findAllByCustomerId(customerId);
 }

 public List<Review> getPendingReviews() {
 return reviewRepository.findByStatus(ReviewStatus.PENDING_MODERATION);
 }

 public List<Review> getAllReviews() {
 return reviewRepository.findAll();
 }

 public int count() {
 return reviewRepository.count();
 }
}

