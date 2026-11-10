# Éclat — System Architecture & UML Documentation

**SE1020 · Object-Oriented Programming · Wedding Planning & Vendor Booking System**

This documentation provides an overall system domain diagram and a separate UML class diagram for each of the six assigned modules.

## Documentation map

| Diagram / guide | Description |
|---|---|
| [Overall System Class Diagram](diagrams/00-overall-system-class-diagram.png) | Cross-module core domain model |
| [Member 01 — Customer & Admin](diagrams/01-customer-admin-class-diagram.png) | Accounts, OTP, services and repositories |
| [Member 02 — Wedding Planning](diagrams/02-wedding-planning-class-diagram.png) | Plan subtypes, checklist, budget and guests |
| [Member 03 — Vendor Management](diagrams/03-vendor-management-class-diagram.png) | Vendor profiles, category and approval |
| [Member 04 — Booking Management](diagrams/04-booking-management-class-diagram.png) | Booking lifecycle, notifications and receipt |
| [Member 05 — Contract & Payment](diagrams/05-contract-payment-class-diagram.png) | Contracts and payment inheritance |
| [Member 06 — Review & Feedback](diagrams/06-review-feedback-class-diagram.png) | Review submission, booking verification and moderation |
| [Combined UML PDF](Eclat-UML-Class-Diagrams.pdf) | Multi-page printable architecture reference |

PNG diagrams are provided for reports and presentations. Module-level SVG files support zooming without pixelation, and Graphviz DOT files are included as editable sources.

## System overview

Éclat is a Java 17 / Spring Boot 3.5.6 web application using Spring MVC, JSP/JSTL, embedded Tomcat and CSV file-based persistence. Its six functional areas are Customer & Admin, Wedding Planning, Vendor Management, Booking Management, Contract & Payment, and Review & Feedback.

At domain level, a customer owns wedding plans, requests bookings and submits reviews. Vendors receive bookings and reviews. A booking may have a contract, and a contract may have multiple payment records. A completed booking may be linked to a review.

## UML notation

- `-` means a private attribute; `+` means a public operation.
- Italicised class names indicate abstract classes where represented.
- `<<interface>>` identifies an interface.
- Hollow triangle arrows point from a child class to its parent (generalization).
- Solid lines represent domain associations.
- Dashed arrows represent usage dependencies, e.g. Controller → Service → Repository.
- Multiplicities: `1` = exactly one; `0..1` = zero or one; `0..*` = zero or many.

## Module responsibilities

### Member 01 — Customer & Admin
Customer/admin account models and flows such as registration, login, profile operations and OTP verification. Services contain business logic; repositories provide file-backed access; controllers expose web endpoints.

### Member 02 — Wedding Planning
Wedding plans and the records used to organise an event. Plan subtypes demonstrate inheritance and polymorphic behaviour. Checklist tasks, budget items and guests are associated plan items.

### Member 03 — Vendor Management
Vendor profiles, registration, updates, category-based search, availability and approval. VendorService coordinates business rules and VendorRepository persists data.

### Member 04 — Booking Management
Customer-to-vendor booking requests, status transitions and notifications. BookingPdfReceipt generates booking receipts.

### Member 05 — Contract & Payment
Vendor contracts, financial totals and payment records. AdvancePayment, PartialPayment and FinalPayment specialise the abstract Payment type.

### Member 06 — Review & Feedback
Customer reviews, vendor ratings and moderation. Review eligibility is validated against completed booking records.

## Cross-module relationships

| Relationship | Multiplicity | Meaning |
|---|---|---|
| Customer — WeddingPlan | 1 to 0..* | Customer may have several plans |
| WeddingPlan — PlanItem | 1 to 0..* | Plan groups multiple planning items |
| Customer — Booking | 1 to 0..* | Customer may request multiple bookings |
| Vendor — Booking | 1 to 0..* | Vendor may receive multiple bookings |
| WeddingPlan — Booking | 1 to 0..* | Booking may be linked to a plan |
| Booking — VendorContract | 1 to 0..1 | Booking may have a contract |
| VendorContract — Payment | 1 to 0..* | Contract may have multiple payments |
| Customer — Review | 1 to 0..* | Customer may write multiple reviews |
| Vendor — Review | 1 to 0..* | Vendor may receive multiple reviews |
| Booking — Review | 1 to 0..1 | Completed booking may be reviewed |

## CSV persistence and association note

The project uses CSV repositories rather than JPA/Hibernate entity relationships. Related records are linked using identifiers such as `customerId`, `vendorId`, `planId`, `bookingId` and `contractId`. Therefore, diagram associations describe logical relationships and service/repository rules; they are not ORM object references or database foreign-key constraints.

## Scope and maintenance

The diagrams summarise core classes, key attributes, selected operations and important relationships; they are not an exhaustive API specification. Some service/controller method lists are representative. When class structures or business relationships change, update the relevant diagram and this documentation in the same pull request.

---
**Éclat · System Architecture Documentation**
