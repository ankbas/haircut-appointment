# Haircut Appointment API

A Spring Boot REST API for creating and managing haircut appointments. Appointments are stored in a local H2 database, and duplicate date/time bookings are rejected.

## Requirements

- Java 17+
- No external database required

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
  "appointmentDate": "2099-08-20",
  "appointmentTime": "14:30"
}
```

All fields are required. The date must be today or later, and the phone number must contain 7–25 valid phone characters. Unknown fields are rejected.

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

The application uses a file-based H2 database at `./data/appointments`. Tests use a separate in-memory database.
