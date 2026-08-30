# SmartRide 🚗

A production-grade ride-sharing platform built with Spring Boot 3.2, JWT authentication, MySQL, and Razorpay payment integration.

## Features

- Email-based JWT authentication with role-based access (Driver / Passenger / Admin)
- Driver: post rides, view bookings, track earnings
- Passenger: search rides, book seats, payment flow, booking history
- Full payment flow with Razorpay (test mode)
- Admin dashboard: platform stats, user and ride management
- Swagger UI at `/swagger-ui.html`

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.2, Spring Security, JPA/Hibernate |
| Database | MySQL 8 |
| Auth | JWT (Bearer token) |
| Payments | Razorpay |
| Frontend | HTML, CSS, JavaScript |
| Deployment | Render (Docker), Aiven MySQL |

## Run Locally

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8

### Setup

```bash
# 1. Clone
git clone https://github.com/Mahalakshmi2101/Smartride.git
cd Smartride

# 2. Create database
mysql -u root -p -e "CREATE DATABASE smartride_db;"

# 3. Configure environment
cp .env.example .env
# Fill in your values in .env

# 4. Run
mvn spring-boot:run
```

App runs at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui.html`

## API Endpoints

| Method | Endpoint | Role |
|---|---|---|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |
| GET | /api/rides/search | Passenger |
| POST | /api/rides | Driver |
| POST | /api/bookings | Passenger |
| POST | /api/payments/create-order | Passenger |
| POST | /api/payments/verify | Passenger |
| GET | /api/admin/stats | Admin |
| GET | /api/admin/users | Admin |
| GET | /api/admin/rides | Admin |

## Test Credentials

| Role | Email | Password |
|---|---|---|
| Driver | driver@test.com | test123 |
| Passenger | passenger@test.com | test123 |

## Live Demo

🔗 https://smartride-cyur.onrender.com

## Author

Mahalakshmi P — [LinkedIn](https://linkedin.com/in/yourprofile) · [GitHub](https://github.com/Mahalakshmi2101)
