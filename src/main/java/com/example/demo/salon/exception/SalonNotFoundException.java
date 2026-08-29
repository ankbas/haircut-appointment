package com.example.demo.salon.exception;

public class SalonNotFoundException extends RuntimeException {
    public SalonNotFoundException(Long id) { super("Salon not found with ID " + id); }
}
