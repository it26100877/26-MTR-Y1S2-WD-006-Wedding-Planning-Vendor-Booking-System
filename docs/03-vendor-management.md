# Member 03 — Vendor Management

**Responsibility:** vendor profiles, service categories, availability, directory search and approval.

## Main classes
- `Vendor`: vendor details, contact information, category, pricing and approval status.
- `VendorCategory`: vendor classification values.
- `VendorService`: validation, search, status and lifecycle business rules.
- `VendorRepository`: file-backed storage and filtered lookup.
- `VendorController`: vendor-facing and administration web endpoints.

## Main relationships
- A vendor category classifies multiple vendor records.
- `VendorController` depends on `VendorService`.
- `VendorService` uses `VendorRepository` for persistence.

## Class diagram
![Member 03 — Vendor Management UML](diagrams/03-vendor-management-class-diagram.png)
