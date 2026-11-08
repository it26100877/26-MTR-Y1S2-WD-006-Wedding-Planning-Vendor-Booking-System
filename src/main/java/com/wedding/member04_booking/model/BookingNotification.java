package com.wedding.member04_booking.model;

/** Persistent in-app booking update for the customer or administrator. */
public class BookingNotification {
    private String notificationId;
    private String recipientId;
    private String bookingId;
    private String message;
    private String createdAt;
    private String readAt;

    public BookingNotification() {}
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String v) { notificationId = v; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String v) { recipientId = v; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String v) { bookingId = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { message = v; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { createdAt = v; }
    public String getReadAt() { return readAt; }
    public void setReadAt(String v) { readAt = v; }
    public boolean isUnread() { return readAt == null || readAt.isBlank(); }
}
