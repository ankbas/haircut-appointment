# Atelier Hair & Beauty

A full-stack salon booking platform with a Spring Boot/PostgreSQL API and a luxury React/TypeScript frontend. It supports real availability, customer booking management, JWT-protected operations, and admin catalog/schedule management.

## Requirements

- Java 17+
- PostgreSQL 16+
- Node.js 22+

## Database setup

PostgreSQL must be running locally. In DBeaver, connect to the default `postgres` database as an administrator, open a SQL editor, and run:

```sql
CREATE USER root WITH PASSWORD 'replace-with-a-strong-password';
CREATE DATABASE haircut_appointments OWNER root;
```

Set the database password in the terminal before starting the application:

```powershell
$env:DB_PASSWORD="your-local-password"
```

Alternatively, create an `application-secrets.properties` file in the project root:

```properties
spring.datasource.username=your-local-username
spring.datasource.password=your-local-password
```

This file is ignored by Git and loaded automatically for local development.

The default connection uses `localhost:5432`, database `haircut_appointments`, and user `root`. The password has no default and must be supplied through `DB_PASSWORD`. Override the other values when needed with `DB_URL` and `DB_USERNAME`.

Flyway creates and versions the schema automatically. Hibernate validates the schema but does not modify it.

## Run locally

Start the API:

```powershell
# Windows
.\mvnw.cmd spring-boot:run
```

```bash
# macOS/Linux
./mvnw spring-boot:run
```

Base URL: `http://localhost:8090`

In a second PowerShell window, start the frontend:

```powershell
cd frontend
npm install
npm run dev
```

Frontend URL: `http://localhost:5173`

Vite proxies API requests to port `8090`. Override the backend with `SERVER_PORT` and the frontend target with `VITE_API_TARGET` when needed.

### First admin account

Set these before the first API startup. The password is BCrypt-hashed in PostgreSQL and is never committed:

```powershell
$env:ADMIN_USERNAME="your-admin-name"
$env:ADMIN_PASSWORD="use-a-strong-unique-password"
$env:JWT_SECRET="use-a-long-random-signing-secret"
.\mvnw.cmd spring-boot:run
```

## API

| Method | Endpoint | Success | Description |
| --- | --- | --- | --- |
| `POST` | `/api/appointments` | `201 Created` | Create an appointment |
| `GET` | `/api/services` | `200 OK` | List services and pricing |
| `GET` | `/api/professionals?serviceId=1` | `200 OK` | List professionals for a service |
| `GET` | `/api/appointments` | `200 OK` | List appointments by date and time |
| `GET` | `/api/appointments/{id}` | `200 OK` | Get an appointment |
| `PUT` | `/api/appointments/{id}` | `200 OK` | Replace an appointment |
| `PATCH` | `/api/appointments/{id}/cancel` | `200 OK` | Cancel an appointment without deleting it |
| `DELETE` | `/api/appointments/{id}` | `204 No Content` | Delete an appointment |
| `GET` | `/availability?professionalId=1&serviceId=1&date=2099-08-20` | `200 OK` | List bookable time slots |
| `GET` | `/api/customer/appointments/lookup` | `200 OK` | Find a booking by confirmation number and email |
| `PATCH` | `/api/customer/appointments/cancel` | `200 OK` | Customer cancellation |
| `PATCH` | `/api/customer/appointments/reschedule` | `200 OK` | Customer rescheduling |
| `POST` | `/api/auth/login` | `200 OK` | Issue an admin/professional JWT |

Routes under `/api/admin/**` require `Authorization: Bearer <token>` and provide appointment status, service, professional, working-hours, and time-off management.

### Request body

Used by `POST` and `PUT`:

```json
{
  "customerName": "Alex Johnson",
  "customerPhone": "555-123-4567",
  "customerEmail": "alex.johnson@example.com",
  "professionalId": 1,
  "serviceId": 1,
  "startTime": "2099-08-20T14:30:00"
}
```

All fields are required. The email and phone must be valid and the start time must be in the future. The API calculates the end time from the service duration. A booking must be within the professional's working hours, the professional must offer the service, and active appointments cannot overlap. Professionals receive a default Monday-Sunday schedule of 9:00 AM-6:00 PM.

### Error responses

Errors use the standard `ProblemDetail` JSON format:

| Status | Meaning |
| --- | --- |
| `400 Bad Request` | Invalid request data |
| `404 Not Found` | Appointment, professional, or service does not exist |
| `409 Conflict` | The professional already has an overlapping appointment |

Validation responses also include an `errors` object containing messages for each invalid field.

## Project structure

Features are organized into `controller`, `service`, `repository`, `entity`, `dto`, and `exception` packages.

## Tests

```powershell
.\mvnw.cmd test
cd frontend
npm run build
```

The application and automated tests both use PostgreSQL. Tests use the separate `haircut_appointments_test` database and never modify development data.
