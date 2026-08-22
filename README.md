# Haircut Appointment API

A Spring Boot REST API for managing salon professionals, services, appointments, and real-time availability. PostgreSQL stores the data, Flyway versions the schema, and bookings are checked against working hours and existing appointments.

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

Alternatively, create an `application-secrets.properties` file in the project root:

```properties
spring.datasource.username=your-local-username
spring.datasource.password=your-local-password
```

This file is ignored by Git and loaded automatically for local development.

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

Base URL: `http://localhost:8080`

## API

| Method | Endpoint | Success | Description |
| --- | --- | --- | --- |
| `POST` | `/api/appointments` | `201 Created` | Create an appointment |
| `GET` | `/api/appointments` | `200 OK` | List appointments by date and time |
| `GET` | `/api/appointments/{id}` | `200 OK` | Get an appointment |
| `PUT` | `/api/appointments/{id}` | `200 OK` | Replace an appointment |
| `DELETE` | `/api/appointments/{id}` | `204 No Content` | Delete an appointment |
| `GET` | `/availability?professionalId=1&serviceId=1&date=2099-08-20` | `200 OK` | List bookable time slots |

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
```

The application and automated tests both use PostgreSQL. Tests use the separate `haircut_appointments_test` database and never modify development data.
