# Éclat — SE1020 Wedding Planning & Vendor Booking System

## Run on Windows
1. Back up your current project's `data/` folder if it contains your customer accounts or work.
2. Extract this ZIP to a new folder.
3. If you need to retain your current records, copy your backed-up `data/` folder into the extracted `OOP Project` folder, replacing the sample data files.
4. Open the `OOP Project` folder and run `run-web.bat`.
5. Open `http://localhost:8080/` for the public marketplace.
6. Sign in to access customer/admin dashboards and protected actions.
7. Stop the server with Ctrl+C in the command window.

## Build and tests
From the `OOP Project` folder:
```bat
mvnw.cmd test
mvnw.cmd clean package
```
The first Maven build requires internet access to download dependencies. The project targets Java 17 (`maven.compiler.release=17`); a compatible JDK is recommended.

## Demo administrator
The sample administrator record is `admin@wedding.com`. The sample guide lists `admin123` as the demo password. Change demo credentials before any real deployment.

## Important documents
- `docs/FULL_SYSTEM_AUDIT_REPORT.md` — source-level audit findings and changes
- `docs/QA_TEST_MATRIX.md` — manual acceptance checklist
- `docs/SE1020_FINALIZATION_GUIDE.md` — rubric traceability and OOP evidence
- `docs/FINAL_REPORT_TEMPLATE.md` — report structure
- `docs/VIVA_PREPARATION.md` — member viva preparation
- `docs/diagrams/Eclat_Class_Diagram.puml` — PlantUML class diagram source

## Verification status
- ZIP structure and integrity are checked during packaging.
- Static checks cover Java brace/parenthesis balance, duplicate route mappings and JSP form-action/controller route matching.
- A fresh Maven build and JUnit run could not be executed in the packaging environment because Maven Central DNS resolution was unavailable. Run the test/build commands above on the team's Windows PC before submission.
- Do not claim a test passed until it has actually been run and recorded in `docs/QA_TEST_MATRIX.md`.

## Academic integrity and marking
This release retains the six-member Java OOP architecture. Each member must understand their assigned module and demonstrate their actual contribution. Keep genuine GitHub commits/PRs, update the report with real member names/IDs, and do not claim work that a member did not complete. No software package can guarantee full marks.
