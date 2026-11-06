package com.wedding.member04_booking.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.common.utils.FileUtil;
import com.wedding.member04_booking.model.Booking;
import com.wedding.member04_booking.model.BookingStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * File-based repository for {@link Booking} entities.
 * Inherits from AbstractFileRepository.
 * Stored in bookings.csv using pipe delimiter.
 *
 * @author Member 04 – Booking Management
 */
public class BookingRepository extends AbstractFileRepository<Booking> {

 private static final String FILE_NAME = "bookings.csv";
 private static final String DELIM = "|";

 public BookingRepository() {
 super(FILE_NAME);
 }

 @Override
 protected String getId(Booking b) {
 return b.getBookingId();
 }

 @Override
 protected String serialize(Booking b) {
 return String.join(DELIM,
 safe(b.getBookingId()),
 safe(b.getCustomerId()),
 safe(b.getVendorId()),
 safe(b.getPlanId()),
 safe(b.getBookingDate()),
 safe(b.getRequestedAt()),
 b.getStatus() != null ? b.getStatus().name() : BookingStatus.PENDING.name(),
 safe(b.getServiceDetails()),
 b.getAgreedAmount() != null ? b.getAgreedAmount().toPlainString() : "0.00",
 safe(b.getNotes()),
 safe(b.getConfirmedAt()),
 safe(b.getCancelledAt())
 );
 }

 @Override
 protected Booking deserialize(String line) {
 if (line == null || line.trim().isEmpty()) return null;
 String[] parts = line.split(Pattern.quote(DELIM), -1);
 if (parts.length < 10) return null;

 Booking b = new Booking();
 b.setBookingId(parts[0]);
 b.setCustomerId(parts[1]);
 b.setVendorId(parts[2]);
 b.setPlanId(parts[3]);
 b.setBookingDate(parts[4]);
 b.setRequestedAt(parts[5]);
 b.setStatus(BookingStatus.fromString(parts[6]));
 b.setServiceDetails(parts[7]);
 try {
 b.setAgreedAmount(new BigDecimal(parts[8]));
 } catch (Exception e) {
 b.setAgreedAmount(BigDecimal.ZERO);
 }
 b.setNotes(parts[9]);
 if (parts.length > 10) b.setConfirmedAt(parts[10]);
 if (parts.length > 11) b.setCancelledAt(parts[11]);
 return b;
 }

 private String safe(String s) {
 return s == null ? "" : s.replace("|", "/").replace("\r", " ").replace("\n", " ");
 }

 public List<Booking> findByCustomerId(String cid) {
 return findAll().stream()
 .filter(b -> cid != null && cid.equalsIgnoreCase(b.getCustomerId()))
 .collect(Collectors.toList());
 }

 public List<Booking> findByVendorId(String vid) {
 return findAll().stream()
 .filter(b -> vid != null && vid.equalsIgnoreCase(b.getVendorId()))
 .collect(Collectors.toList());
 }

 public List<Booking> findByPlanId(String pid) {
 return findAll().stream()
 .filter(b -> pid != null && pid.equalsIgnoreCase(b.getPlanId()))
 .collect(Collectors.toList());
 }

 public List<Booking> findByStatus(BookingStatus status) {
 return findAll().stream()
 .filter(b -> b.getStatus() == status)
 .collect(Collectors.toList());
 }

 public List<Booking> findByVendorAndDate(String vendorId, String date) {
 return findAll().stream()
 .filter(b -> vendorId != null && vendorId.equalsIgnoreCase(b.getVendorId()))
 .filter(b -> date != null && date.equals(b.getBookingDate()))
 .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING)
 .collect(Collectors.toList());
 }

 public List<Booking> findCompletedByCustomerAndVendor(String cid, String vid) {
 return findAll().stream()
 .filter(b -> cid != null && cid.equalsIgnoreCase(b.getCustomerId()))
 .filter(b -> vid != null && vid.equalsIgnoreCase(b.getVendorId()))
 .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
 .collect(Collectors.toList());
 }
}

