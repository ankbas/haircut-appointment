# Haircut Appointment API

A Spring Boot REST API for creating and managing haircut appointments. Appointments are stored in PostgreSQL, and duplicate date/time bookings are rejected.

## Requirements

- Java 17+
- PostgreSQL 16+

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

The default connection uses `localhost:5432`, database `haircut_appointments`, and user `root`. The password has no default and must be supplied through `DB_PASSWORD`. Override the other values when needed with `DB_URL` and `DB_USERNAME`.

Flyway creates and versions the schema automatically. Hibernate validates the schema but does not modify it.

## Run locally

```powershell
# Windows
.\mvnw.cmd spring-boot:run
```

```bash
# macOS/Linux
./mvnw spring-boot:run
```

Base URL: `http://localhost:8080/api/appointments`

## API

| Method | Endpoint | Success | Description |
| --- | --- | --- | --- |
| `POST` | `/api/appointments` | `201 Created` | Create an appointment |
| `GET` | `/api/appointments` | `200 OK` | List appointments by date and time |
| `GET` | `/api/appointments/{id}` | `200 OK` | Get an appointment |
| `PUT` | `/api/appointments/{id}` | `200 OK` | Replace an appointment |
| `DELETE` | `/api/appointments/{id}` | `204 No Content` | Delete an appointment |

### Request body

Used by `POST` and `PUT`:

```json
{
  "name": "Alex Johnson",
  "phoneNumber": "555-123-4567",
  "email": "alex.johnson@example.com",
  "appointmentDate": "2099-08-20",
  "appointmentTime": "14:30"
}
```

All fields are required. The email must be valid, the date must be in the future, and the phone number must contain 7-25 valid phone characters. Unknown fields are rejected.

### Error responses

Errors use the standard `ProblemDetail` JSON format:

| Status | Meaning |
| --- | --- |
| `400 Bad Request` | Invalid request data |
| `404 Not Found` | Appointment ID does not exist |
| `409 Conflict` | Date and time are already booked |

Validation responses also include an `errors` object containing messages for each invalid field.

## Project structure

The appointment feature is organized into `controller`, `service`, `repository`, `entity`, `dto`, and `exception` packages.

## Tests

```powershell
.\mvnw.cmd test
```

The application uses PostgreSQL. Automated tests use a separate in-memory H2 database and do not modify local PostgreSQL data.
