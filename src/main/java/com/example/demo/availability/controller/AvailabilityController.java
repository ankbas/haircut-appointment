package com.example.demo.availability.controller;

import com.example.demo.availability.dto.AvailabilitySlotResponse;
import com.example.demo.availability.service.AvailabilityService;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public ResponseEntity<List<AvailabilitySlotResponse>> findAvailability(
            @RequestParam @Positive(message = "Professional ID must be positive") Long professionalId,
            @RequestParam @Positive(message = "Service ID must be positive") Long serviceId,
            @RequestParam @FutureOrPresent(message = "Date must be today or in the future") LocalDate date) {
        return ResponseEntity.ok(
                availabilityService.findAvailability(professionalId, serviceId, date));
    }
}
