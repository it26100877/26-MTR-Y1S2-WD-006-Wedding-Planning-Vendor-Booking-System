package com.wedding.member04_booking.service;

import com.wedding.common.utils.FileUtil;
import com.wedding.common.utils.ValidationUtil;
import com.wedding.member04_booking.model.BookingNotification;
import com.wedding.member04_booking.repository.BookingNotificationRepository;
import java.util.List;

/** In-app notifications (no email or third-party service required). */
public class BookingNotificationService {
    private final BookingNotificationRepository repository;
    public BookingNotificationService(BookingNotificationRepository repository) { this.repository = repository; }
    public void send(String recipient, String bookingId, String message) {
        BookingNotification n = new BookingNotification();
        n.setNotificationId(FileUtil.generateId("NTF"));
        n.setRecipientId(recipient); n.setBookingId(bookingId);
        n.setMessage(message); n.setCreatedAt(ValidationUtil.nowDateTime()); n.setReadAt("");
        repository.save(n);
    }
    public List<BookingNotification> forRecipient(String recipient) { return repository.forRecipient(recipient); }
    public long unreadCount(String recipient) {
        return forRecipient(recipient).stream().filter(BookingNotification::isUnread).count();
    }
    public void markRead(String recipient, String id) {
        repository.findById(id).ifPresent(n -> {
            if (recipient.equalsIgnoreCase(n.getRecipientId()) && n.isUnread()) {
                n.setReadAt(ValidationUtil.nowDateTime()); repository.save(n);
            }
        });
    }
    public void markAllRead(String recipient) {
        for (BookingNotification n : forRecipient(recipient)) markRead(recipient, n.getNotificationId());
    }
}
