# Haircut Appointment API

A simple Spring Boot REST API for scheduling haircut appointments. It stores appointments in a local H2 database and prevents two customers from booking the same date and time.

## What you need

- Java 17 or newer
- No separate database setup is required

## Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api/appointments`.

## API endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/appointments` | Create an appointment |
| `GET` | `/api/appointments` | List all appointments |
| `GET` | `/api/appointments/{id}` | Get one appointment |
| `PUT` | `/api/appointments/{id}` | Update an appointment |
| `DELETE` | `/api/appointments/{id}` | Delete an appointment |

## Example request

Create an appointment:

```powershell
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/api/appointments `
  -ContentType "application/json" `
  -Body '{
    "name": "Alex Johnson",
    "phoneNumber": "555-123-4567",
    "appointmentDate": "2026-08-20",
    "appointmentTime": "14:30"
  }'
```

The appointment date must be today or later. Names, phone numbers, dates, and times are required.

## Run the tests

```powershell
.\mvnw.cmd test
```

The local database is created in the `data` folder. Build output, database files, IDE settings, and local secrets are excluded from Git.
