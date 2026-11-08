ECLAT V7 - MEMBER 02 ONLY - GITHUB TRANSFER PACKAGE
===================================================
This package is prepared from the V7 complete project, but contains ONLY Member 02 code and assets.
The root of this ZIP is src/ so its files merge into a Git working-tree root.

COPY DESTINATION (your new branch worktree):
  C:\Users\NADULA\Documents\Wedding-Planning-GitHub-V7

Fast PowerShell copy (after saving ZIP to Downloads):
  Expand-Archive -LiteralPath "C:\Users\NADULA\Downloads\Eclat_V7_Member02_ONLY_GitHub_Copy.zip" -DestinationPath "C:\Users\NADULA\Documents\Wedding-Planning-GitHub-V7" -Force

Before copying, make sure you are on branch IT26100875-wedding-v7:
  cd "C:\Users\NADULA\Documents\Wedding-Planning-GitHub-V7"
  git branch --show-current
  git status

Stage ONLY member02:
  git add src/main/java/com/wedding/member02_wedding_plan
  git add src/main/webapp/WEB-INF/jsp/member02
  git add src/main/resources/static/css/member02-planner.css
  git add src/main/resources/static/css/member02-admin-dashboard.css
  git add src/main/resources/static/css/member02-admin-portal-integration.css
  git add src/main/resources/static/js/member02-planner.js
  git add src/main/resources/static/images/member02
  git add src/test/java/com/wedding/member02_wedding_plan

Review before committing:
  git diff --cached --name-only
  git diff --cached --stat

Then commit and push the BRANCH (not main):
  git commit -m "Add Member 02 Wedding Planning V7 module"
  git push -u origin IT26100875-wedding-v7

WARNING: The leader's current origin/main is NOT a complete Spring Boot application.
This member-only module depends on additional code (com.wedding.common, customer and booking modules), pom.xml, WeddingApplication, and common JSP layout files that are NOT part of this ZIP.
This package is suitable for uploading your member contribution; it is NOT runnable on its own in the current origin/main layout.
The shared admin homepage/navigation is NOT changed by this ZIP. Leader must integrate it to expose the Wedding Management admin link.
Do NOT delete the existing vendor files, do NOT commit .idea, and do NOT run `git add .`.
