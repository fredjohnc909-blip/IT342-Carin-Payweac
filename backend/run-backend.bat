@echo off
echo Starting PayWEAC Backend with XAMPP MySQL...
echo.
echo Make sure XAMPP MySQL is running.
echo Database payweacdb is created automatically.
echo.

cd /d "%~dp0"
mvn spring-boot:run
pause
