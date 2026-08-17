package com.example.demo.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
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
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 25)
        @Pattern(regexp = "^[0-9+().\\- ]{7,25}$", message = "must be a valid phone number")
        String phoneNumber,
        @NotNull @FutureOrPresent LocalDate appointmentDate,
        @NotNull LocalTime appointmentTime) {
}
