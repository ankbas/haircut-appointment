package com.example.demo.appointment.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Long id) {
        super("Appointment " + id + " was not found");
    }

    public AppointmentNotFoundException() {
        super("No appointment matched the supplied confirmation number and email");
    }
}
