package com.example.demo.availability.dto;

import java.time.LocalDateTime;

public record AvailabilitySlotResponse(LocalDateTime startTime, LocalDateTime endTime) {
}
