package com.example.demo.appointment.exception;

public class AppointmentConflictException extends RuntimeException {
    public AppointmentConflictException() {
        super("That time overlaps an existing appointment for this professional");
    }
}
