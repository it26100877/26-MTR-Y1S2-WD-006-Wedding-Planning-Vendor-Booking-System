# Éclat — Member 04 Booking Management: GitHub Integration Guide

This package contains only **Member 04 Booking Management** files from the provided `Eclat_Booking_Full_Image_Hero_Only (2).zip`. Paths begin at the Spring Boot project root. No other members' source code, application credentials, or live CSV records are included.

## Files to copy

- `src/main/java/com/wedding/member04_booking/` — 10 booking Java sources: models, repositories, services, controllers, booking notifications and PDF receipts
- `src/main/webapp/WEB-INF/jsp/member04/` — 7 Booking JSP views, for customer and administrator
- `src/main/resources/static/css/eclat-bookings.css` — Booking Studio-specific stylesheet, including the full-image banner rules
- `src/main/resources/static/booking/booking-hero.jpg` and `booking-hero-full.jpg` — images referenced by the booking stylesheet
- `src/test/java/com/wedding/member04_booking/BookingPdfReceiptTest.java` — Booking PDF test
- `MEMBER04_BOOKING_STUDIO_README.md` — the original module documentation

**Do not put this project inside another `OOP Project` directory.** Extract/copy these contents to the root of the leader's Spring Boot repository, retaining their paths. Do not replace the leader's entire `src` directory or `pom.xml`.

## Dependencies the group leader must integrate

The Booking module is **not independently runnable**. It imports classes supplied by other members and shared infrastructure:

- `com.wedding.common.*` for user session, base repository/service, exceptions and validation/file utilities
- `member01_customer_admin.service.CustomerService` and session `currentUser` for customer/admin permissions
- `member02_wedding_plan.service.WeddingPlanService` (plus WeddingPlan model) for selecting an active plan
- `member03_vendor.service.VendorService` and Vendor model for booking approved and active vendors
- Member 05's `/contracts/new?bookingId=...` route and Member 06's `/reviews/new?bookingId=...` route are linked from booking details, but those modules are not bundled

**Booking bean wiring:** `AppConfig.java` in the integrated project must register `BookingRepository`, `BookingNotificationRepository`, `BookingNotificationService`, and `BookingService`, and construct `BookingService` with a `VendorService` and `BookingNotificationService`. The supplied source project's `AppConfig.java` has these bean definitions. Do not replace the leader's `AppConfig.java` wholesale; compare/merge its booking-related bean methods.

**JSP shared files:** the booking pages include `/WEB-INF/jsp/common/header.jsp` and `/WEB-INF/jsp/common/footer.jsp`; these are supplied by the shared site and not bundled. Ensure the shared header loads `/css/eclat-bookings.css` and the rest of the Éclat site CSS. The source project uses `eclat-royal.css`, `eclat-premium-pages.css`, `eclat-champagne.css`, Bootstrap 5 and Bootstrap Icons; those shared files and CDN dependencies are not bundled. Recommended booking stylesheet link in shared `header.jsp`:

```jsp
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/eclat-bookings.css">
```

**Runtime data files:** `BookingRepository` reads/writes `bookings.csv` and `BookingNotificationRepository` reads/writes `booking_notifications.csv` in the project's shared file data directory (via `AbstractFileRepository`). The existing format is pipe-delimited. Do not overwrite current booking/notification records when integrating. The CSVs in the supplied full-system ZIP were deliberately excluded. Set up correct writable data storage in the shared app.

**Application:** requires the main Spring Boot `WeddingApplication`, Java 17+, JSP/JSTL dependencies, and the established JSP view resolver (`/WEB-INF/jsp/`, `.jsp`) from the group project. These are not included to avoid conflicts.

## How to upload only this module to the EXISTING friend's branch

The target branch is `IT26100881-02`, already cloned in `Friend-Part`. **Do not create an orphan branch, delete `main`, or use `--force`.** Confirm with your friend before pushing on her behalf.

1. In the `Friend-Part` clone, run `git branch --show-current` and verify `IT26100881-02`.
2. Run `git status --short` first; if it shows changes you did not make, stop and resolve them rather than overwriting.
3. Extract this ZIP and copy its `src` contents, plus these two Markdown files, into the **root** of `Friend-Part`. Keep the folder structure.
4. Run the explicit staging commands:

```bash
git add src/main/java/com/wedding/member04_booking
git add src/main/webapp/WEB-INF/jsp/member04
git add src/main/resources/static/css/eclat-bookings.css
git add src/main/resources/static/booking/booking-hero.jpg src/main/resources/static/booking/booking-hero-full.jpg
git add src/test/java/com/wedding/member04_booking
git add BOOKING_INTEGRATION.md MEMBER04_BOOKING_STUDIO_README.md
git diff --cached --name-only
```

5. Check that only booking-specific files are staged; never stage `.idea`, `target`, `.git`, admin/customer data files or unrelated vendor/review modules.
6. Once reviewed, commit and push:

```bash
git commit -m "Add Member 04 Booking Management module and booking UI"
git push -u origin IT26100881-02
```

**Note:** this branch currently derives from the older project structure, so the leader should manually integrate these Spring Boot files into the final Spring Boot base instead of assuming the old `main` can merge the module cleanly. The current ZIP is module-only and has not been run as a standalone app.

## Suggested functional checks after integration

- Customer submits/edits/cancels a booking for an approved vendor and active wedding plan
- Admin confirms/rejects/completes requests; customer only sees their own bookings
- Availability calendar prevents double confirmation for the same vendor/date
- In-app booking notifications, receipt PDF, admin reports and CSV export function
- Completed booking links into the Review form
- Full-photo booking hero renders from the included `booking-hero-full.jpg` and is readable at desktop and mobile widths
