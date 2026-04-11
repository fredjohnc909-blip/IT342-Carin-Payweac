@echo off
echo Starting PayWEAC Backend with XAMPP MySQL...
echo.
echo Prerequisites:
echo   1. XAMPP MySQL is running
echo   2. Java 17+ installed (Spring Boot 3.5 needs JDK 17, not Java 11)
echo   3. Database payweacdb exists (create with mysql or first successful app start)
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run
pause
