# Éclat — Member 04 Booking Studio (Premium Edition)

This copy builds on the original team ZIP and keeps all other team modules, existing booking records and the current `bookings.csv` schema.

## What's included

- **Premium Booking Dashboard** at `/bookings`: all/pending/confirmed/completed/cancelled + rejected counters; keyword search for ID, vendor, event date (and customer name for Admin); vendor/status/date range filters.
- **Create / Edit / View / Cancel / Confirm / Reject / Complete** booking workflows, with existing customer vs admin permissions and hooks for vendor contracts, payments and verified reviews.
- **Vendor Availability Calendar** at `/bookings/calendar`: vendor selector, month navigation, available/pending/confirmed date indicators, and quick link to create a request from an available date. Pending enquiries do not lock the date; confirmed bookings do.
- **Booking Notifications** at `/bookings/notifications`: persistent *in-app* messages for new/updated/confirmed/rejected/cancelled/completed bookings, plus individual and bulk mark-read. New records use `data/booking_notifications.csv`, separate from the pre-existing data format. Customer and Admin notifications are scoped to their recipients.
- **Vendor-wise Admin Request Management**: filter Bookings by vendor + pending status; Admin can confirm/reject from booking details. **Note:** the current team's application has Admin/Customer user roles but no Vendor login. This does **not** invent a Vendor account role or modify Member 01's login system.
- **Reports** at `/bookings/reports` (Admin only): event-month and vendor filters, status totals, confirmed booking amounts, detailed records, plus **Download CSV**.
- **Booking Confirmation PDF** at `/bookings/receipt/{bookingId}`: download a printable single-page receipt after confirmation/completion; restricted to the owning Customer or Admin. Generated directly in Java with no extra Maven dependencies. Receipt text uses Latin basic fonts; lengthy free-form notes are condensed to the page.
- **Data/UX improvements**: all booking fees labeled LKR; customer and vendor names shown if their records exist (otherwise their IDs); browser and server future-date validation; checks for inactive/unapproved vendors; double-booking checks during create **and** confirm; scoped, responsive UI and escaped free-text output in new booking views.

## How to run (IntelliJ IDEA)

1. Open the **`OOP Project` folder** (the folder containing `pom.xml`) as a Maven project in IntelliJ IDEA.
2. Choose JDK **17 or newer** and allow Maven dependencies to download (requires internet the first time).
3. Run `com.wedding.WeddingApplication` (or `mvn spring-boot:run`) from the `OOP Project` working directory.
4. Visit `http://localhost:8080`, log in as a Customer or Admin, and open **Bookings**.
5. For customer booking requests, create an ACTIVE Wedding Plan first and choose an APPROVED active Vendor.

## URLs / permissions

| URL | Customer | Admin |
|---|---|---|
| `/bookings` | Own bookings | All bookings / vendor review |
| `/bookings/new` | New request | Not available |
| `/bookings/view/{id}` | Own booking | Any booking |
| `/bookings/calendar` | Vendor availability | Vendor availability |
| `/bookings/notifications` | Own updates | Admin booking updates |
| `/bookings/receipt/{id}` | Own confirmed/completed receipt | Confirmed/completed receipt |
| `/bookings/reports` | Redirected | Monthly report |
| `/bookings/reports/export` | 403 | CSV download |

## Integration notes

- **Original booking records are not modified** by this package. Old bookings won't have retrospectively generated notifications; new actions will create them.
- Customer names are looked up from Member 01's customer records. The supplied starter `data/customers.csv` is empty, so pre-seeded legacy bookings show their original customer IDs until matching Customer records exist. No names are invented.
- Confirming a booking does not automatically create a contract or payment; Member 05's existing contract link remains accessible from the confirmation details screen.
- Notifications are in-app **only**, not actual emails or SMS; no third-party service/credentials are required.
- The existing header adds one scoped stylesheet (`css/eclat-bookings.css`) without restyling other pages.

## Verification performed

- Java code for all non-Spring model/service/repository classes compiled with JDK 21.
- The updated Member 04 controllers and `AppConfig` compiled against minimal local API stubs, not the real Spring runtime.
- Local isolated-data smoke tests passed for booking create/edit/confirm/conflict detection, notification creation/read, complete/review linkage, admin/customer dashboard, calendar, reporting, CSV access, PDF access. A sample generated PDF rendered and was inspected successfully.
- **Full Maven build and live JSP UI runtime could not be run in the build environment** because Maven Central wasn't reachable and Spring Boot dependencies were not cached. Run `mvn test` and check screens in IntelliJ with internet access before project submission.

## Quick functional QA checklist

- Login as Customer, create an active plan, use `/bookings/new` and submit a future booking.
- Check **Booking Notifications** shows the request; edit the pending request.
- Login as Admin, filter `Pending`, choose a vendor and **Confirm** (or reject a separate request).
- Check Customer sees Confirmed, can download PDF, and sees new notification.
- Check that another confirmed booking for the same vendor/date is blocked; pending date is indicated on calendar.
- As Admin, generate a report, change month/vendor and export CSV; as Customer, verify report access is denied.
- Mark confirmed booking Completed, then verify the Customer review link works.

**No third-party software or dependency was added to `pom.xml`.**
## Module Ownership
Booking Management — Member 04 (IT26100881)
