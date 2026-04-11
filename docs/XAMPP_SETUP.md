# XAMPP MySQL Setup for PayWEAC

## Configuration

The backend uses XAMPP MySQL:

- **Host:** localhost:3306
- **Database:** payweacdb (created automatically)
- **Username:** root
- **Password:** (empty - edit application.properties if you use one)

## Running

1. Start MySQL in XAMPP Control Panel
2. Run: `cd backend` then `mvn spring-boot:run`
3. API: http://localhost:8080

## View Data

Use phpMyAdmin (XAMPP) to view the `users` table in database `payweacdb`.
