# Member 04 — Booking Management

**Responsibility:** customer booking requests, vendor responses, booking status transitions and notifications.

## Main classes
- `Booking`: customer/vendor/plan identifiers, event date, status, service detail and agreed amount.
- `BookingStatus`: booking lifecycle state.
- `BookingNotification`: notification record and read state.
- `BookingService`: create, confirm, reject, cancel and complete operations.
- `BookingNotificationService`: recipient notifications and read actions.
- `BookingRepository`, `BookingNotificationRepository`: persistence.
- `BookingController`, `BookingInsightsController`: web operations.
- `BookingPdfReceipt`: booking receipt generation.

## Main relationships
- One booking can have zero or more notification records.
- Controllers depend on services; services access repositories.
- Booking records are associated with customer, vendor and plan via identifiers.

## Class diagram
![Member 04 — Booking Management UML](diagrams/04-booking-management-class-diagram.png)
