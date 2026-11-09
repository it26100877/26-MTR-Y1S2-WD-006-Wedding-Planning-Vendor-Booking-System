# Éclat — Source Review & Security Findings

Review date: 2026-11-09
Reviewed branch: `main`
Scope: repository source tree (185 tracked files), shared infrastructure, authentication, six module controllers/services/repositories, JSP views, app configuration, build, and existing test suite.

This is a source-code review, not a penetration test or a claim that every runtime path has been exercised. The findings below are limited to issues supported by the current code and repository structure.

## Findings

### High — Default administrator credentials are embedded in source
File: `src/main/java/com/wedding/member01_customer_admin/repository/AdminRepository.java`

When the admin data file is empty, the repository seeds a known account:
- Email: `admin@wedding.com`
- Password: `admin123`

Anyone who can reach an instance that still has this default account can attempt to sign in as SUPER admin. Change the seeded password immediately on any shared/public deployment. Longer-term, bootstrap credentials should be supplied through a one-time environment-based setup or an administrator invitation flow, and the seed should not use a published password.

### Medium — Password minimum was too short
Files: `ValidationUtil.java`, `CustomerService.java`, `AdminService.java`, registration JSP.

The old policy accepted 6-character passwords. This review branch raises the minimum for newly created/changed accounts to 8 characters, aligns the registration form hint, and adds a unit test. Existing passwords are not forcibly changed, and their successful login flow remains compatible.

### Medium — Expired OTP challenge was retained in the session
File: `src/main/java/com/wedding/member01_customer_admin/service/OtpService.java`

The old verification path returned EXPIRED without removing the expired challenge. This review branch clears it as soon as expiry is detected. OTPs remain random 6-digit codes, salted-hash stored in the session, five-minute lifetime, five attempts and bounded resends.

### Medium — SMS gateway request/response contract should be verified against the actual Notify.lk account
File: `NotifyLkSmsService.java`

The implementation checks for HTTP 2xx and the word "success" in the response body, and posts form fields. The code comment itself says to verify request fields against the gateway account's API docs. Do not enable real SMS until the account-specific API format, sender ID, key handling and response format have been checked and tested with a controlled destination.

### Medium — File-based persistence is single-instance/demo oriented
Files: `FileUtil.java`, `AbstractFileRepository.java`, all file-backed repositories and payment-proof queue.

The repository uses in-memory caches and CSV files. File writes have an atomic replace in the shared utility, but independent application instances do not share a transaction/locking mechanism, and some domain workflows write files directly. Do not run multiple app replicas against the same data directory or use this persistence as a concurrent production database. For deployment beyond a single demo instance, migrate to a transactional database and centralize writes.

### Medium — Payment-proof storage is a local CSV workflow, not a payment gateway
File: `ContractPaymentController.java`

Payment proof submissions are stored in a local file queue and manually reviewed by an admin. This is suitable as a coursework workflow but does not verify bank/card payments with a provider. The admin decision process and audit record must be protected and backed up; never describe this as automatic online payment processing.

### Medium — No dedicated rate limiting / abuse protection was found around login, OTP start or resend
Files: `CustomerAdminController.java`, `OtpService.java`

OTP verification attempts and resends are bounded per challenge, but repeated fresh login attempts can initiate new challenges. Add an IP/account/device-aware rate limiter before exposing the app publicly. The current per-challenge counters do not replace endpoint-level throttling.

### Low — Global security headers and CSRF protection are not comprehensively configured
The app uses session-based authentication and state-changing POST routes. A full Spring Security/CSRF configuration and response security-header policy were not found in the reviewed configuration. Before public deployment, add CSRF protection for forms, secure cookie settings (Secure, HttpOnly, SameSite), and appropriate CSP/frame/content-type/referrer headers. Validate compatibility with all JSP forms first.

### Low — File persistence stores user-provided text
CSV serialization is escaped in the common utility and some modules use pipe-delimited sanitizers. Continue reviewing every new persisted field for delimiter/newline handling and HTML output escaping. JSP pages should consistently render user-provided content using JSTL `c:out` or safe escaped expressions.

## Positive controls observed

- Passwords are hashed with PBKDF2-HMAC-SHA256 (210,000 iterations) and per-password random salt; legacy hashes are upgraded after successful login.
- OTP codes use SecureRandom and are stored as salted SHA-256 hashes in the session.
- OTP verification has an attempt limit and resend cooldown/limit.
- Customer profile access checks active account status.
- Vendor public catalogue only returns approved/active vendors.
- Contract and payment actions check the current user and contract ownership in the reviewed paths.
- Review submission verifies a completed booking and verifies customer/vendor ownership in the service.
- Booking creation checks approved/active vendor status and duplicate confirmed vendor/date conflicts.
- Booking CSV export includes formula-injection mitigation.

## Autofill improvements

Login form fields now use standard browser password-manager attributes:
- Email: `autocomplete="username"`
- Password: `autocomplete="current-password"`
- Registration password and confirmation: `autocomplete="new-password"`

## Not covered / further work

- No live external Notify.lk message was sent.
- No Gmail SMTP credentials or real email delivery were tested.
- No external penetration test, dependency CVE scan, fuzz test or multi-user concurrency test was run.
- Deployment secrets, public port visibility and actual production infrastructure were not inspectable through the repository source.
- Payment-proof upload/decision lifecycle, all module edge cases and every UI workflow still need manual end-to-end regression testing.
