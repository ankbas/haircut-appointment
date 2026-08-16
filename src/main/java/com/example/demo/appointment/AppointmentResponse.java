package com.example.demo.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponse(
        Long id,
        String name,
        String phoneNumber,
        LocalDate appointmentDate,
        LocalTime appointmentTime) {

    static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getName(),
                appointment.getPhoneNumber(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime());
    }
}
