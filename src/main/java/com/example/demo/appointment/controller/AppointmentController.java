package com.example.demo.appointment.controller;

import com.example.demo.appointment.dto.AppointmentRequestDto;
import com.example.demo.appointment.dto.AppointmentResponseDto;
import com.example.demo.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.demo.security.AuthenticatedUser;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Validated
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> create(@Valid @RequestBody AppointmentRequestDto request) {
        AppointmentResponseDto created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> findAll(@AuthenticationPrincipal Object principal,@RequestParam @Positive Long salonId) {
        return ResponseEntity.ok(service.findAll(tenant(principal,salonId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> findById(
            @PathVariable @Positive(message = "Appointment ID must be positive") Long id,
            @AuthenticationPrincipal Object principal,
            @RequestParam @Positive Long salonId) {
        return ResponseEntity.ok(service.findById(id, tenant(principal,salonId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> update(
            @PathVariable @Positive(message = "Appointment ID must be positive") Long id,
            @AuthenticationPrincipal Object principal,
            @Valid @RequestBody AppointmentRequestDto request) {
        return ResponseEntity.ok(service.update(id, request, tenant(principal,request.salonId())));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancel(
            @PathVariable @Positive(message = "Appointment ID must be positive") Long id,
            @AuthenticationPrincipal Object principal,
            @RequestParam @Positive Long salonId) {
        return ResponseEntity.ok(service.cancel(id, tenant(principal,salonId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "Appointment ID must be positive") Long id,
            @AuthenticationPrincipal Object principal,
            @RequestParam @Positive Long salonId) {
        service.delete(id, tenant(principal,salonId));
        return ResponseEntity.noContent().build();
    }

    private Long tenant(Object principal, Long requestedSalonId) {
        return principal instanceof AuthenticatedUser user ? user.salonId() : requestedSalonId;
    }
}
