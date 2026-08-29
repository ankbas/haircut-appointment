package com.example.demo.appointment.exception;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import com.example.demo.salon.exception.SalonNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    ResponseEntity<ProblemDetail> handleNotFound(AppointmentNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "Appointment not found", exception.getMessage());
    }

    @ExceptionHandler(ProfessionalNotFoundException.class)
    ResponseEntity<ProblemDetail> handleProfessionalNotFound(ProfessionalNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "Professional not found", exception.getMessage());
    }

    @ExceptionHandler(SalonNotFoundException.class)
    ResponseEntity<ProblemDetail> handleSalonNotFound(SalonNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "Salon not found", exception.getMessage());
    }

    @ExceptionHandler(SalonServiceNotFoundException.class)
    ResponseEntity<ProblemDetail> handleServiceNotFound(SalonServiceNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "Service not found", exception.getMessage());
    }

    @ExceptionHandler(InvalidBookingException.class)
    ResponseEntity<ProblemDetail> handleInvalidBooking(InvalidBookingException exception) {
        return response(HttpStatus.BAD_REQUEST, "Invalid booking", exception.getMessage());
    }

    @ExceptionHandler(AppointmentConflictException.class)
    ResponseEntity<ProblemDetail> handleConflict(AppointmentConflictException exception) {
        return response(HttpStatus.CONFLICT, "Appointment slot unavailable", exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> handleDataConflict(DataIntegrityViolationException exception) {
        return response(
                HttpStatus.CONFLICT,
                "Appointment slot unavailable",
                "That appointment date and time is already booked");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return validationResponse(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String field = path.substring(path.lastIndexOf('.') + 1);
            errors.putIfAbsent(field, violation.getMessage());
        });
        return validationResponse(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ProblemDetail> handleUnreadableRequest(HttpMessageNotReadableException exception) {
        return validationResponse(Map.of("request", "Request body is missing or contains invalid JSON"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return validationResponse(Map.of(exception.getName(), "Value has an invalid format"));
    }

    private ResponseEntity<ProblemDetail> validationResponse(Map<String, String> errors) {
        ProblemDetail detail = problem(
                HttpStatus.BAD_REQUEST, "Validation failed", "One or more request values are invalid");
        detail.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(detail);
    }

    private ResponseEntity<ProblemDetail> response(HttpStatus status, String title, String message) {
        return ResponseEntity.status(status).body(problem(status, title, message));
    }

    private ProblemDetail problem(HttpStatus status, String title, String message) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setTitle(title);
        detail.setType(URI.create("about:blank"));
        return detail;
    }
}
