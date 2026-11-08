package com.wedding.member06_review_feedback.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.member06_review_feedback.model.Review;
import com.wedding.member06_review_feedback.model.ReviewStatus;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * File-based repository for {@link Review} entities.
 * Stored in reviews.csv with pipe delimiter.
 *
 * @author Member 06 – Review & Feedback Management
 */
public class ReviewRepository extends AbstractFileRepository<Review> {

 private static final String FILE_NAME = "reviews.csv";
 private static final String DELIM = "|";

 public ReviewRepository() {
 super(FILE_NAME);
 }

 @Override
 protected String getId(Review r) {
 return r.getReviewId();
 }

 @Override
 protected String serialize(Review r) {
 return String.join(DELIM,
 safe(r.getReviewId()),
 safe(r.getCustomerId()),
 safe(r.getVendorId()),
 safe(r.getBookingId()),
 String.valueOf(r.getRating()),
 safe(r.getTitle()),
 safe(r.getFeedback()),
 safe(r.getSubmittedAt()),
 safe(r.getUpdatedAt()),
 r.getStatus() != null ? r.getStatus().name() : ReviewStatus.PENDING_MODERATION.name(),
 safe(r.getModeratedBy()),
 safe(r.getModerationNote())
 );
 }

 @Override
 protected Review deserialize(String line) {
 if (line == null || line.trim().isEmpty()) return null;
 String[] p = line.split(Pattern.quote(DELIM), -1);
 if (p.length < 10) return null;

 int rating = 5;
 try { rating = Integer.parseInt(p[4]); } catch (Exception ignored) {}

 return new Review(
 p[0], p[1], p[2], p[3], rating, p[5], p[6], p[7],
 p[8], ReviewStatus.fromString(p[9]),
 p.length > 10 ? p[10] : "",
 p.length > 11 ? p[11] : ""
 );
 }

 private String safe(String s) {
 return s == null ? "" : s.replace("|", "/").replace("\r", " ").replace("\n", " ");
 }

 public List<Review> findByVendorId(String vid) {
 return findAll().stream()
 .filter(r -> vid != null && vid.equalsIgnoreCase(r.getVendorId()))
 .filter(r -> r.getStatus() == ReviewStatus.APPROVED)
 .collect(Collectors.toList());
 }

 public List<Review> findByCustomerId(String cid) {
 return findAll().stream()
 .filter(r -> cid != null && cid.equalsIgnoreCase(r.getCustomerId()))
 .collect(Collectors.toList());
 }

 public List<Review> findByStatus(ReviewStatus status) {
 return findAll().stream()
 .filter(r -> r.getStatus() == status)
 .collect(Collectors.toList());
 }

 public Optional<Review> findByBookingId(String bookingId) {
 return findAll().stream()
 .filter(r -> bookingId != null && bookingId.equalsIgnoreCase(r.getBookingId()))
 .findFirst();
 }

 public boolean hasReviewForBooking(String bookingId) {
 return findByBookingId(bookingId).isPresent();
 }
}

