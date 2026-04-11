@echo off
echo Starting PayWEAC Backend with XAMPP MySQL...
echo.
echo Prerequisites:
echo   1. Neon PostgreSQL: copy application-local.properties.example to application-local.properties
echo      and set spring.datasource.password (file is gitignored).
echo   2. Java 17+ (Spring Boot 3.5)
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run
pause
