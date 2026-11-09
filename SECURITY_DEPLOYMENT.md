# Éclat security deployment configuration

Never commit real credentials to Git. Configure these as environment variables in the hosting platform / Codespaces secrets.

## Initial administrator bootstrap

The application no longer creates `admin@wedding.com / admin123`. If there is no admin record, it can create the first SUPER administrator only when all four variables are supplied:

- `ECLAT_BOOTSTRAP_ADMIN_EMAIL`
- `ECLAT_BOOTSTRAP_ADMIN_PASSWORD` (minimum 12 characters)
- `ECLAT_BOOTSTRAP_ADMIN_NAME`
- `ECLAT_BOOTSTRAP_ADMIN_PHONE`

Set these before the first start. Once the account is created, remove the bootstrap variables from the runtime environment. The application also removes the historical default account when it finds the exact legacy email and password in an existing CSV data file. Back up your data before upgrading.

If an existing installation has a custom admin already, it will not be overwritten.

## Login abuse protection

- Login: up to 8 submissions per account + client address per 15-minute window.
- OTP resend: up to 10 submissions per client address per 15-minute window.
- OTP verification retains its per-challenge 5-attempt limit and resend cooldown.
- Rate-limit counters are in memory and reset on application restart. The enforced single-instance CSV deployment model keeps one limiter authoritative. For a future horizontally scaled database deployment, move counters to Redis or another shared store.

## CSV persistence safety

CSV repositories retain in-memory caches, so multiple JVMs must not access the same data directory. Startup now acquires an exclusive OS file lock at `<data-dir>/.eclat-instance.lock` and refuses to start a second process using that directory. This is a safety guard, not a database migration. For multiple replicas, migrate the repositories to a shared transactional database before removing this guard.

## CSRF, headers and cookies

A session synchronizer token is issued on page requests and automatically added to POST forms by the shared JSP footer. State-changing requests without a matching `_csrf` parameter (or `X-CSRF-TOKEN` header) receive HTTP 403.

The application sets CSP, X-Content-Type-Options, X-Frame-Options, Referrer-Policy and Permissions-Policy headers. The CSP currently permits inline scripts/styles because existing JSP views use inline code; remove that allowance after moving inline code to static assets and adding nonces/hashes.

Session cookies are HttpOnly and SameSite=Lax. Set `ECLAT_COOKIE_SECURE=true` when deployed behind HTTPS. Do not expose production sessions over plain HTTP.

## Notify.lk SMS

Notify.lk is disabled by default (`ECLAT_SMS_PROVIDER=console`). To enable real SMS, configure:

- `ECLAT_SMS_PROVIDER=notifylk`
- `NOTIFYLK_USER_ID`
- `NOTIFYLK_API_KEY`
- `NOTIFYLK_SENDER_ID` (your approved Sender ID; do not use `NotifyDEMO` for OTP/production messages)

The integration uses the official HTTPS endpoint `https://app.notify.lk/api/v1/send` with form-encoded `user_id`, `api_key`, `sender_id`, `to` (Sri Lankan international format, e.g. 94771234567) and `message` fields. It accepts only a 2xx response with JSON `status: success`. Do not send a live test until you have added valid account credentials and an approved Sender ID. The first live test should target a phone number controlled by the project owner and be initiated deliberately.

Official provider documentation: https://developer.notify.lk/api-endpoints/

## Deployment validation checklist

1. Configure one-time bootstrap admin secrets before first launch, then remove them.
2. Confirm the legacy default account is absent from `admins.csv`.
3. Configure HTTPS and set `ECLAT_COOKIE_SECURE=true`.
4. Confirm a second process pointed at the same data directory fails startup.
5. Confirm a POST without a CSRF token receives HTTP 403 and a normal JSP form POST succeeds.
6. Leave SMS in console mode until valid Notify.lk credentials and an approved Sender ID are configured.
7. Verify a single controlled SMS delivery in Notify.lk dashboard before enabling OTP delivery for users.
