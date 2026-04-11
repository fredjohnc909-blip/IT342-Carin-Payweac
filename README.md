# PayWEAC - Rent Payment & Tracking Platform

**IT342 - System Integration and Architecture**  
**Author:** Carin, Wilfred John R.

A simplified payment and tracking platform that enables tenants to track rent dues and pay through online transactions.

## Project Structure

```
├─ /web          # React web application
├─ /backend      # Spring Boot REST API
├─ /mobile       # Android app (planned)
├─ /docs         # Documentation
├─ README.md
└─ TASK_CHECKLIST.md
```

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.5.x, Spring Security, Spring Data JPA, JWT
- **Frontend:** React 18, Vite
- **Database:** MySQL (XAMPP) / H2 (optional)

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Maven 3.8+

### Backend

```bash
cd backend
mvn spring-boot:run
```

API runs at `http://localhost:8080`

**XAMPP MySQL:** Ensure MySQL is running in XAMPP. The `payweacdb` database is created automatically. See `docs/XAMPP_SETUP.md` for details.

### Web Frontend

```bash
cd web
npm install
npm run dev
```

Web app runs at `http://localhost:5173`

### API Endpoints (Phase 1)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | User registration |
| POST | `/api/v1/auth/login` | User login |

## License

MIT
