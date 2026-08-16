package com.example.demo.appointment;

public class AppointmentConflictException extends RuntimeException {
    public AppointmentConflictException() {
        super("That appointment date and time is already booked");
    }
}
