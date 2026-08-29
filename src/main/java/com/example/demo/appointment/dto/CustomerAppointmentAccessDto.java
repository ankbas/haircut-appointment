package com.example.demo.appointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CustomerAppointmentAccessDto(
        @NotNull @Positive Long salonId,
        @NotBlank(message = "Confirmation number is required") String confirmationNumber,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email) {
}
