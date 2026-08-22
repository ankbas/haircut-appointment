package com.example.demo.appointment.exception;

public class ProfessionalNotFoundException extends RuntimeException {
    public ProfessionalNotFoundException(Long id) {
        super("Professional " + id + " was not found");
    }
}
