@echo off
setlocal
title Eclat - SE1020 Wedding Planning System
cd /d "%~dp0"

echo ================================================================
echo   ECLAT - Wedding Planning and Vendor Booking System
echo   Spring Boot + JSP | SE1020 OOP Project
echo ================================================================
echo.

if exist "target\wedding-planning-system-1.0.0.war" (
    echo Starting the packaged application...
    echo Open http://localhost:8080
    java -jar "target\wedding-planning-system-1.0.0.war"
    goto :end
)

if exist "apache-maven-3.9.9\bin\mvn.cmd" (
    echo Building and starting with the bundled Maven 3.9.9...
    call "apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
    goto :end
)

if exist "mvnw.cmd" (
    echo Building and starting with Maven Wrapper...
    call "mvnw.cmd" spring-boot:run
    goto :end
)

echo ERROR: Maven was not found. Install Maven or restore the bundled Maven folder.
:end
echo.
echo Application stopped. If startup succeeded, browse to http://localhost:8080
pause
endlocal
