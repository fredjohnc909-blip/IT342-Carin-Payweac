# Phase 1 Implementation Summary

## User Registration

**Registration fields used:**
- First Name (required)
- Last Name (required)
- Email (required)
- Room Number (optional)
- Password (required, min 8 characters)
- Confirm Password (required)

**Validation process:**
- All required fields are validated on both client and server
- Email format is validated
- Password must be at least 8 characters
- Confirm password must match password

**How duplicate accounts are prevented:**
- Before saving, the system checks if the email already exists in the database
- If duplicate email is found, returns HTTP 409 Conflict with error code DB-002

**How passwords are stored securely:**
- Passwords are hashed using BCrypt with 12 salt rounds
- Only the hash is stored in the `password_hash` column; plain text is never persisted

## User Login

**Login credentials used:**
- Email
- Password

**How the system verifies users:**
- Looks up user by email in the database
- Compares provided password with stored hash using BCrypt
- Returns AUTH-001 error if email not found or password incorrect

**What happens after successful login:**
- JWT access and refresh tokens are generated
- User data and tokens are returned to the client
- Client stores tokens and redirects to the dashboard

## Database Table

**Table:** users

**Columns:**
| Column        | Type         | Description                    |
|---------------|--------------|--------------------------------|
| id            | BIGINT (PK)  | Auto-increment primary key     |
| email         | VARCHAR      | Unique, not null               |
| password_hash | VARCHAR      | BCrypt hash                    |
| first_name    | VARCHAR(100) | Not null                       |
| last_name     | VARCHAR(100) | Not null                       |
| room_number   | VARCHAR(50)  | Optional                       |
| role          | VARCHAR      | TENANT or ADMIN                |
| created_at    | TIMESTAMP    | Auto-set on creation           |

## API Endpoints

| Method | Endpoint                 | Description           |
|--------|--------------------------|-----------------------|
| POST   | /api/v1/auth/register    | User registration     |
| POST   | /api/v1/auth/login       | User login            |
