package com.example.demo.appointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerAppointmentAccessDto(
        @NotBlank(message = "Confirmation number is required") String confirmationNumber,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email) {
}
