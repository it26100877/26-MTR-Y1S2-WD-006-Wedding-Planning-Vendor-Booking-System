# Member 06 — Review & Feedback

**Responsibility:** customer feedback, vendor ratings, review eligibility and moderation.

## Main classes
- `Review`: customer/vendor/booking identifiers, rating, title, feedback and moderation information.
- `ReviewStatus`: review moderation status.
- `ReviewService`: submit, update, approve, reject, delete and rating calculations.
- `ReviewRepository`: persisted review records and filtered lookups.
- `ReviewFeedbackController`: customer review and administrator moderation endpoints.
- `BookingRepository`: completed-booking eligibility lookup.

## Main relationships
- A review is linked to its customer, vendor and booking using identifiers.
- The service checks booking completion before accepting an eligible review.
- Moderation is represented by `ReviewStatus` and moderation fields.

## Class diagram
![Member 06 — Review & Feedback UML](diagrams/06-review-feedback-class-diagram.png)
