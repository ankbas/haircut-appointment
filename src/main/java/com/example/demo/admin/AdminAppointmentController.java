package com.example.demo.admin;

import com.example.demo.appointment.dto.AppointmentResponseDto;
import com.example.demo.appointment.entity.AppointmentStatus;
import com.example.demo.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated @RestController @RequestMapping("/api/admin/appointments")
public class AdminAppointmentController {
    private final AppointmentService service;
    public AdminAppointmentController(AppointmentService service){this.service=service;}
    @GetMapping public ResponseEntity<List<AppointmentResponseDto>> findByRange(
            @RequestParam LocalDate from, @RequestParam LocalDate to) {
        return ResponseEntity.ok(service.findBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay()));
    }
    @PatchMapping("/{id}/status") public ResponseEntity<AppointmentResponseDto> status(
            @PathVariable @Positive Long id, @Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(service.changeStatus(id, request.status()));
    }
    public record StatusRequest(@NotNull AppointmentStatus status) {}
}
