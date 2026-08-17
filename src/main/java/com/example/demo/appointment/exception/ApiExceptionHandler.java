package com.example.demo.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    ResponseEntity<ProblemDetail> handleNotFound(AppointmentNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "Appointment not found", exception.getMessage());
    }

    @ExceptionHandler(AppointmentConflictException.class)
    ResponseEntity<ProblemDetail> handleConflict(AppointmentConflictException exception) {
        return response(HttpStatus.CONFLICT, "Appointment slot unavailable", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception) {
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Invalid appointment", "One or more fields are invalid");
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
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
