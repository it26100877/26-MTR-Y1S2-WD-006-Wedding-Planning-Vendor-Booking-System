package com.wedding.member04_booking.repository;

import com.wedding.common.repository.AbstractFileRepository;
import com.wedding.common.utils.FileUtil;
import com.wedding.member04_booking.model.BookingNotification;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/** Separate file store; original bookings.csv format remains unchanged. */
public class BookingNotificationRepository extends AbstractFileRepository<BookingNotification> {
    public BookingNotificationRepository() { super("booking_notifications.csv"); }
    @Override protected String getId(BookingNotification n) { return n.getNotificationId(); }
    private String safe(String value) {
        return value == null ? "" : value.replace('|', '/').replace('\r', ' ').replace('\n', ' ');
    }
    @Override protected String serialize(BookingNotification n) {
        return String.join("|", safe(n.getNotificationId()), safe(n.getRecipientId()),
            safe(n.getBookingId()), safe(n.getMessage()), safe(n.getCreatedAt()), safe(n.getReadAt()));
    }
    @Override protected BookingNotification deserialize(String line) {
        String[] v = line.split(Pattern.quote("|"), -1);
        if (v.length < 6) return null;
        BookingNotification n = new BookingNotification();
        n.setNotificationId(v[0]); n.setRecipientId(v[1]); n.setBookingId(v[2]);
        n.setMessage(v[3]); n.setCreatedAt(v[4]); n.setReadAt(v[5]);
        return n;
    }
    public List<BookingNotification> forRecipient(String id) {
        return findAll().stream().filter(n -> id.equalsIgnoreCase(n.getRecipientId()))
            .sorted(Comparator.comparing(BookingNotification::getCreatedAt).reversed())
            .toList();
    }
}
