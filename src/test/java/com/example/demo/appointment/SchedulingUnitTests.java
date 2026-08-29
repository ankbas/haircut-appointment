package com.example.demo.appointment;

import com.example.demo.appointment.dto.AppointmentRequestDto;
import com.example.demo.appointment.entity.Appointment;
import com.example.demo.appointment.entity.AppointmentStatus;
import com.example.demo.appointment.exception.AppointmentConflictException;
import com.example.demo.appointment.repository.AppointmentRepository;
import com.example.demo.appointment.service.AppointmentService;
import com.example.demo.availability.service.AvailabilityService;
import com.example.demo.professional.entity.Professional;
import com.example.demo.professional.repository.ProfessionalRepository;
import com.example.demo.professional.repository.ProfessionalTimeOffRepository;
import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.entity.ServiceAudience;
import com.example.demo.servicecatalog.entity.ServiceType;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulingUnitTests {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private ProfessionalRepository professionalRepository;
    @Mock private SalonServiceRepository salonServiceRepository;
    @Mock private ProfessionalTimeOffRepository timeOffRepository;

    private AppointmentService appointmentService;
    private AvailabilityService availabilityService;
    private Professional professional;
    private SalonService haircut;
    private LocalDate bookingDate;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepository, professionalRepository, salonServiceRepository, timeOffRepository);
        availabilityService = new AvailabilityService(
                professionalRepository, salonServiceRepository, appointmentRepository, timeOffRepository);
        haircut = new SalonService(
                ServiceAudience.MEN, ServiceType.HAIRCUT, new BigDecimal("30.00"), 30);
        professional = new Professional("Alex Morgan", "Hair specialist", true);
        professional.addService(haircut);
        bookingDate = LocalDate.now().plusDays(10);
    }

    @Test
    void availabilityExcludesEverySlotThatWouldOverlapAnAppointment() {
        Appointment existing = appointment(bookingDate.atTime(10, 0));
        when(professionalRepository.findWithDetailsById(1L)).thenReturn(Optional.of(professional));
        when(salonServiceRepository.findById(1L)).thenReturn(Optional.of(haircut));
        when(appointmentRepository.findActiveForDay(anyLong(), any(), any()))
                .thenReturn(List.of(existing));

        var slots = availabilityService.findAvailability(1L, 1L, bookingDate);

        assertFalse(slots.stream().anyMatch(slot ->
                slot.startTime().isBefore(existing.getEndTime())
                        && slot.endTime().isAfter(existing.getStartTime())));
    }

    @Test
    void doubleBookingThrowsConflict() {
        prepareBookingLookups();
        when(appointmentRepository.hasOverlap(nullable(Long.class), any(), any(), isNull()))
                .thenReturn(true);

        assertThrows(AppointmentConflictException.class,
                () -> appointmentService.create(request(bookingDate.atTime(10, 0))));
    }

    @Test
    void cancellationChangesStatusWithoutDeletingTheAppointment() {
        Appointment appointment = appointment(bookingDate.atTime(10, 0));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        var response = appointmentService.cancel(1L);

        assertEquals(AppointmentStatus.CANCELLED, response.status());
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }

    @Test
    void reschedulingMovesStartAndRecalculatesEndTime() {
        LocalDateTime originalStart = bookingDate.atTime(10, 0);
        LocalDateTime newStart = bookingDate.atTime(11, 15);
        Appointment appointment = appointment(originalStart);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        prepareBookingLookups();
        when(appointmentRepository.hasOverlap(nullable(Long.class), any(), any(), anyLong()))
                .thenReturn(false);

        var response = appointmentService.update(1L, request(newStart));

        assertEquals(newStart, response.startTime());
        assertEquals(newStart.plusMinutes(30), response.endTime());
    }

    private void prepareBookingLookups() {
        when(professionalRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(professional));
        when(salonServiceRepository.findById(1L)).thenReturn(Optional.of(haircut));
    }

    private Appointment appointment(LocalDateTime startTime) {
        return new Appointment(
                "John Smith", "312-555-1234", "john@example.com",
                professional, haircut, startTime);
    }

    private AppointmentRequestDto request(LocalDateTime startTime) {
        return new AppointmentRequestDto(
                "John Smith", "312-555-1234", "john@example.com", 1L, 1L, startTime);
    }
}
