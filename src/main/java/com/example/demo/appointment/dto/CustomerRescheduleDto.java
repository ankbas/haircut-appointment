package com.example.demo.appointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CustomerRescheduleDto(
        @NotNull(message = "Salon ID is required") @Positive Long salonId,
        @NotBlank(message = "Confirmation number is required") String confirmationNumber,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        @NotNull(message = "Start time is required") @Future(message = "Start time must be in the future") LocalDateTime startTime) {
}
