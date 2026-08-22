package com.example.demo.appointment.exception;

public class SalonServiceNotFoundException extends RuntimeException {
    public SalonServiceNotFoundException(Long id) {
        super("Service " + id + " was not found");
    }
}
