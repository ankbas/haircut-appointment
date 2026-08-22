package com.example.demo.appointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequestDto(
        @NotBlank(message = "Customer name is required")
        @Size(max = 100, message = "Customer name must not exceed 100 characters")
        String customerName,
        @NotBlank(message = "Customer phone is required")
        @Size(max = 25, message = "Customer phone must not exceed 25 characters")
        @Pattern(regexp = "^[0-9+().\\- ]{7,25}$", message = "Customer phone format is invalid")
        String customerPhone,
        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email format is invalid")
        @Size(max = 254, message = "Customer email must not exceed 254 characters")
        String customerEmail,
        @NotNull(message = "Professional ID is required")
        @Positive(message = "Professional ID must be positive")
        Long professionalId,
        @NotNull(message = "Service ID is required")
        @Positive(message = "Service ID must be positive")
        Long serviceId,
        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime) {
}
