package com.example.demo.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data accepted when an appointment is created or replaced.
 */
public record AppointmentRequestDto(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,
        @NotBlank(message = "Phone number is required")
        @Size(max = 25, message = "Phone number must not exceed 25 characters")
        @Pattern(regexp = "^[0-9+().\\- ]{7,25}$", message = "Phone number format is invalid")
        String phoneNumber,
        @NotNull(message = "Appointment date is required")
        @Future(message = "Appointment date must be in the future")
        LocalDate appointmentDate,
        @NotNull(message = "Appointment time is required")
        LocalTime appointmentTime) {
}
