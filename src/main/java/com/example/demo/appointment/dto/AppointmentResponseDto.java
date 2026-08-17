package com.example.demo.appointment.dto;

import com.example.demo.appointment.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Public representation of an appointment returned by the API.
 */
public record AppointmentResponseDto(
        Long id,
        String name,
        String phoneNumber,
        LocalDate appointmentDate,
        LocalTime appointmentTime) {

    public static AppointmentResponseDto from(Appointment appointment) {
        return new AppointmentResponseDto(
                appointment.getId(),
                appointment.getName(),
                appointment.getPhoneNumber(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime());
    }
}
