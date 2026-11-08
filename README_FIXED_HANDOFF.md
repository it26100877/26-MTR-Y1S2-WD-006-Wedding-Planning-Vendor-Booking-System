# Wedding Planning & Vendor Booking System — Handoff

## Run the packaged application (Windows)
1. Install a 64-bit JDK (Java 17 or newer; Java 21 was used for the smoke test).
2. Extract the ZIP without changing the folder structure.
3. Double-click `run-web.bat`, or run `java -jar target\wedding-planning-system-1.0.0.war` from PowerShell in this folder.
4. Open http://localhost:8080.
5. Stop the server with Ctrl+C.

## Build from source
Run `mvnw.cmd clean package` from this folder. The first build requires internet access to download Maven dependencies. The packaged WAR is included so the app can be launched without a fresh Maven build.

## Demo login
The seeded admin account shown by the original project is `admin@wedding.com`. The password is not documented in this handoff; use the password configured by the project seed/setup or register a customer account. Do not deploy the demo credentials publicly.

## Current verification and limitations
- The prebuilt WAR included in this archive was smoke-tested on Java 21: application startup succeeded and login, admin dashboard, and contract page returned successful HTTP responses.
- The existing Surefire report records 7 tests passing in the supplied build output.
- A fresh Maven clean build could not be independently repeated in this environment because the Spring Boot parent/dependencies were not available in the local Maven cache and outbound Maven Central access was unavailable.
- Automated test-created records were removed from the demonstration data files to restore a clean sample dataset.
- This is a development/demo handoff, not a production security certification. Before submission/deployment, perform a full authorization/ownership audit on every POST route, add CSRF protection, and test every CRUD flow with customer and admin accounts.
