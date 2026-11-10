# Éclat — Wedding Planning & Vendor Booking System

**SE1020 | Object-Oriented Programming Project | Six-member integrated web application**

Éclat is a Java web application for planning weddings and managing wedding-service vendors. Customers can explore vendors, manage wedding plans and bookings, and work with contracts, payments and reviews. Administrators manage users and oversee the platform.

## Run the application in GitHub Codespaces

You can run the system from Codespaces and use it entirely through a web browser. You do not need to install Java or Maven on your personal computer when using the configured Codespace.

### 1. Open the project
1. Open this GitHub repository.
2. Select **Code → Codespaces**.
3. Open the existing Codespace, or create one for the `main` branch.
4. Wait until the VS Code browser workspace has finished loading.

### 2. Start the web application
Open **Terminal → New Terminal** in Codespaces and run:

```bash
./mvnw spring-boot:run
```

Wait until Spring Boot reports that the application has started. The application listens on port **8080**.

> The first run may take a few minutes because Maven downloads the required dependencies. Codespaces needs internet access for this step.

### 3. Open it in your browser
1. In the Codespaces bottom panel, open the **Ports** tab.
2. Find port **8080**. If it is not listed, forward port 8080.
3. Select **Open in Browser** (the globe icon).
4. The Éclat public homepage should open.

Useful application pages:

| Page | Browser path |
|---|---|
| Public homepage | `/` |
| Login | `/login` |
| Customer/admin dashboard | `/dashboard` |
| Vendor directory | `/vendors` |

The browser address is the forwarded Codespaces URL, not necessarily `http://localhost:8080` on your own computer.

**Port visibility:** Keep port 8080 private for normal development. Only change it to public when you intentionally need to share the running demo with someone; do not expose real customer data or use real passwords in a public demo.

### 4. Stop the application
Return to the Codespaces terminal where Spring Boot is running and press **Ctrl + C**.

## Demo sign-in and sample data

A local/demo run automatically creates a realistic sample dataset unless demo seeding is disabled. The seeder is excluded from the `prod` profile and is idempotent; it checks stable demo emails before adding records.

The sample dataset includes:
- **30 approved vendor profiles** across the available wedding-service categories.
- A demo customer and a wedding plan with checklist, budget items and guest RSVP records.
- A linked booking, contract, advance-payment record, completed booking and verified review.

Demo customer:
- **Email:** `demo.customer@example.test`
- **Password:** `DemoWedding#2026`

All sample vendors, customer details, payments and reviews are fictional. The payment is a demonstration record only; it does not represent a real bank transaction.

To disable demo seeding locally, set `ECLAT_DEMO_DATA_ENABLED=false` or `app.demo-data.enabled=false`.

There is **no published/default administrator password**. To bootstrap the first administrator, configure `ECLAT_BOOTSTRAP_ADMIN_EMAIL`, `ECLAT_BOOTSTRAP_ADMIN_PASSWORD` (at least 12 characters), `ECLAT_BOOTSTRAP_ADMIN_NAME` and `ECLAT_BOOTSTRAP_ADMIN_PHONE` before starting the application.

## Main system modules

| Member module | Main responsibilities |
|---|---|
| Member 01 — Customer & Admin | Customer registration, login, admin access and account management |
| Member 02 — Wedding Planning | Wedding plans, checklist, budget, guests and planning progress |
| Member 03 — Vendor Management | Vendor directory, search/filter, vendor registration, details and admin approval |
| Member 04 — Booking Management | Vendor bookings and booking status/notifications |
| Member 05 — Contract & Payment | Contracts, payment records and payment tracking |
| Member 06 — Reviews & Feedback | Customer reviews, ratings and vendor feedback |

## Technology stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.5.6
- **Web layer:** Spring MVC, JSP and JSTL
- **Build tool:** Maven Wrapper
- **Persistence:** CSV file-based repositories
- **Runtime:** Embedded Tomcat
- **Testing:** JUnit / Spring Boot Test

## Project structure

```text
src/
 └── main/
     ├── java/com/wedding/
     │   ├── common/                  # Shared models, repositories and utilities
     │   ├── member01_customer_admin/
     │   ├── member02_wedding_plan/
     │   ├── member03_vendor/
     │   ├── member04_booking/
     │   ├── member05_contract_payment/
     │   ├── member06_review_feedback/
     │   ├── AppConfig.java           # Shared dependency configuration
     │   ├── HomeController.java
     │   └── WeddingApplication.java  # Spring Boot entry point
     ├── resources/
     └── webapp/WEB-INF/jsp/           # JSP user interface
```

Each module is organized around its own controllers, models, repositories and services. Shared file-persistence and validation utilities are located in the common package.

## Build and test

Run the full verification suite from the Codespaces terminal:

```bash
./mvnw clean verify
```

The integrated project was verified successfully in Codespaces on 9 November 2026:

- **49 tests executed**
- **0 failures**
- **0 errors**
- **WAR package built successfully**
- GitHub Actions run on **10 November 2026** completed successfully after the Éclat 2026 integration.

To build without running tests:

```bash
./mvnw clean package -DskipTests
```

## Data storage and demo preparation

The application stores runtime CSV records in the root-level `data/` directory. This directory is intentionally excluded from Git to avoid committing personal information, account details or changing demo records.

A fresh local/demo Codespace seeds the fictional sample records at startup. The seeder is disabled during Maven tests and is excluded from the `prod` profile. Runtime data remains local and is not committed. Do not commit real customer information, password data, API keys or personal records.

## Academic project notes

This is an academic demonstration system, not a production-ready service. The six module areas are integrated into one Spring Boot application. Team members should be able to explain their own Java classes, OOP concepts, CRUD operations, file persistence and module integration during the assessment.

---

**Repository:** [26-MTR-Y1S2-WD-006 Wedding Planning & Vendor Booking System](https://github.com/it26100877/26-MTR-Y1S2-WD-006-Wedding-Planning-Vendor-Booking-System)
