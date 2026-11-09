ÉCLAT — FULL PREMIUM UI REFRESH
================================

This update refreshes the shared visual system across the JSP application and
rebuilds the Login and Register screens in the Éclat premium marketplace style.

WHAT CHANGED
- New shared stylesheet: src/main/resources/static/css/eclat-premium-pages.css
- Shared navigation/header now loads the premium stylesheet on all authenticated
  and shared JSP pages.
- Login page rebuilt with split editorial image layout, Customer/Admin tabs,
  and the original POST endpoint and field names preserved.
- Registration page rebuilt with a premium two-column layout and the original
  POST endpoint and field names preserved.
- Shared guest navigation now provides public browsing links, Sign Up and Log In.
- Existing six-member Java modules, controllers, services, storage, JSP routes,
  and form field names have been retained.

INSTALL
1. Stop the currently running app with Ctrl+C.
2. Extract this ZIP into a NEW folder.
3. Open the extracted OOP Project folder.
4. Run run-web.bat.
5. Open http://localhost:8080/ and test Login/Register and each module.
6. If the browser shows old CSS, press Ctrl+F5.

BUILD NOTE
A Maven package attempt was made in the generation environment but dependency
resolution was unavailable because Maven Central DNS/network access failed.
Please run the project locally using the existing run-web.bat and report any
compile/runtime errors before submission.

IMPORTANT
This is a UI refresh, not a replacement of the academic OOP implementation.
Keep the six-member module logic and demonstrate the Java classes, CRUD,
file persistence and integration in the viva.
