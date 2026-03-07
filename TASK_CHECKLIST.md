# PayWEAC - Phase 1 Task Checklist

## Phase 1: User Registration and Login

### User Registration
- [x] Registration form with name, email, password
- [x] Additional fields: firstName, lastName, roomNumber (per SDD)
- [x] Validate required fields
- [x] Prevent duplicate email registration (409 Conflict)
- [x] Store user in database
- [x] Store passwords securely (bcrypt, salt rounds 12)

### User Login
- [x] Login form with email and password
- [x] Validate credentials against database
- [x] Prevent login with invalid credentials
- [x] Successful login redirects to dashboard

### Backend
- [x] Spring Boot 3.5.x
- [x] Maven build
- [x] Group ID: edu.cit.carin
- [x] Artifact ID: payweac
- [x] REST API architecture
- [x] POST /api/v1/auth/register
- [x] POST /api/v1/auth/login
- [x] JWT token generation
- [x] Standard API response format

### Database
- [x] users table: id, email, password_hash, first_name, last_name, room_number, role, created_at

### Submission
- [ ] Create GitHub repository
- [ ] Final commit: "IT342 Phase 1 – User Registration and Login Completed"
- [ ] Submit PDF with screenshots and implementation summary
