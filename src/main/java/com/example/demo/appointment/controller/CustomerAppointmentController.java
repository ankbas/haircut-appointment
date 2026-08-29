package com.example.demo.appointment.controller;

import com.example.demo.appointment.dto.AppointmentResponseDto;
import com.example.demo.appointment.dto.CustomerAppointmentAccessDto;
import com.example.demo.appointment.dto.CustomerRescheduleDto;
import com.example.demo.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/customer/appointments")
public class CustomerAppointmentController {

    private final AppointmentService appointmentService;

    public CustomerAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/lookup")
    public ResponseEntity<AppointmentResponseDto> lookup(
            @RequestParam @Positive Long salonId,
            @RequestParam @NotBlank String confirmationNumber,
            @RequestParam @NotBlank @Email String email) {
        return ResponseEntity.ok(appointmentService.findForCustomer(salonId, confirmationNumber, email));
    }

    @PatchMapping("/cancel")
    public ResponseEntity<AppointmentResponseDto> cancel(
            @Valid @RequestBody CustomerAppointmentAccessDto request) {
        return ResponseEntity.ok(appointmentService.cancelForCustomer(
                request.salonId(), request.confirmationNumber(), request.email()));
    }

    @PatchMapping("/reschedule")
    public ResponseEntity<AppointmentResponseDto> reschedule(
            @Valid @RequestBody CustomerRescheduleDto request) {
        return ResponseEntity.ok(appointmentService.rescheduleForCustomer(request));
    }
}
