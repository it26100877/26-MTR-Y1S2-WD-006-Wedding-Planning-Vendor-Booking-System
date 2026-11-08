# Éclat — Member 06: Reviews & Ratings (Spring Boot)

This branch contribution contains ONLY the Reviews & Ratings module from the integrated Éclat project. It includes customer review browsing/submission, verified completed-booking validation, ratings and admin moderation.

## Folder structure

- `src/main/java/com/wedding/member06_review_feedback/controller/ReviewFeedbackController.java`
- `src/main/java/com/wedding/member06_review_feedback/model/Review.java`
- `src/main/java/com/wedding/member06_review_feedback/model/ReviewStatus.java`
- `src/main/java/com/wedding/member06_review_feedback/repository/ReviewRepository.java`
- `src/main/java/com/wedding/member06_review_feedback/service/ReviewService.java`
- `src/main/webapp/WEB-INF/jsp/member06/reviews.jsp`
- `src/main/webapp/WEB-INF/jsp/member06/review-form.jsp`
- `src/main/webapp/WEB-INF/jsp/member06/review-moderation.jsp`

## Integration guide for the group leader

Copy the included `src` folder into the root of the shared Spring Boot project, preserving paths. These files **cannot run on their own**: they depend on the integrated project and its Spring Boot/Maven configuration.

### Required shared components (not duplicated here)

- Spring Boot `WeddingApplication` and Maven `pom.xml` with Spring MVC, JSP, JSTL, and required web dependencies.
- `com.wedding.common` classes (exceptions, user model, base service, file repository utilities, validation/file helpers).
- Vendor module: `Vendor`, `VendorService`.
- Booking module: `Booking`, `BookingStatus`, `BookingService`, `BookingRepository`. Only `COMPLETED` bookings can be reviewed.
- Authentication/session setup that stores the logged-in `User` as `sessionScope.currentUser`; roles `CUSTOMER` and `ADMIN`.
- Shared JSP layout: `WEB-INF/jsp/common/header.jsp` and `footer.jsp`, plus its Bootstrap/Bootstrap Icons/fonts/base styles and navigation.
- Writable review persistence location used by `ReviewRepository` (`reviews.csv`); allow the integrated repository code to initialise storage. No customer or admin data has been included in this ZIP.

### Expected routes

- `GET /reviews` — public/approved reviews and customer-owned pending reviews.
- `GET /reviews/new` and `POST /reviews/new` — customer creates a verified review from a completed booking.
- `POST /reviews/update/{id}` — owner edits review as allowed by the service.
- `/admin/reviews/moderation` and admin review approve/reject/delete routes (all using `/{id}`) — admin moderation.

### Presentation/UI

- Review page has the existing wedding-photo hero and soft-glass statistics.
- Review cards are compact, text-based and do not display repeated large images or camera icons.
- Some decorative hero/form images load from Unsplash URLs; an Internet connection is needed for those backgrounds. No third-party images are stored in this ZIP.

### How to commit only your module

On your new `it26100876-review-springboot` orphan branch, extract this ZIP into the **root** of your cloned Git repository (not into an extra nested folder). Keep the hidden `.git` folder. Use `git status --short` to review what is staged. Then:

```cmd
git add src/main/java/com/wedding/member06_review_feedback src/main/webapp/WEB-INF/jsp/member06 REVIEW_INTEGRATION.md
git status --short
git commit -m "Add Member 06 Spring Boot reviews and admin moderation module"
git push -u origin it26100876-review-springboot
```

Do not add `data/`, `.idea/`, `target/`, passwords, or any files from the other five group members. The group leader can integrate these files into the complete Spring Boot system after aligning its shared interfaces.
