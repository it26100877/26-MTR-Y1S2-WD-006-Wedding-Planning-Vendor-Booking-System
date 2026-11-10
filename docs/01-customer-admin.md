# Member 01 — Customer & Admin Management

**Responsibility:** customer and administrator account models, authentication-related flows, customer profile and administration.

## Main classes
- `User`: common account abstraction and shared identity/credential data.
- `Customer`: customer-specific profile attributes.
- `Admin`: administrator-specific attributes.
- `OtpChallenge`: time-limited verification challenge state.
- `CustomerService`, `AdminService`, `OtpService`: business operations and verification.
- `SmsService`: SMS delivery abstraction.
- `CustomerRepository`, `AdminRepository`: file-backed persistence.
- `CustomerAdminController`: web requests and views.

## Main relationships and OOP
- `Customer` and `Admin` inherit from `User`.
- A customer may have multiple OTP challenges.
- Controllers depend on services; services depend on repositories.
- Encapsulation is represented by private fields and public operations.

## Class diagram
![Member 01 — Customer & Admin UML](diagrams/01-customer-admin-class-diagram.png)
