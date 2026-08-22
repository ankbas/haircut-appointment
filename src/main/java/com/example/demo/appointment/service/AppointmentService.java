package com.example.demo.appointment.service;

import com.example.demo.appointment.dto.AppointmentRequestDto;
import com.example.demo.appointment.dto.AppointmentResponseDto;
import com.example.demo.appointment.entity.Appointment;
import com.example.demo.appointment.exception.AppointmentConflictException;
import com.example.demo.appointment.exception.AppointmentNotFoundException;
import com.example.demo.appointment.exception.InvalidBookingException;
import com.example.demo.appointment.exception.ProfessionalNotFoundException;
import com.example.demo.appointment.exception.SalonServiceNotFoundException;
import com.example.demo.appointment.repository.AppointmentRepository;
import com.example.demo.professional.entity.Professional;
import com.example.demo.professional.entity.ProfessionalWorkingHours;
import com.example.demo.professional.repository.ProfessionalRepository;
import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final SalonServiceRepository salonServiceRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ProfessionalRepository professionalRepository,
            SalonServiceRepository salonServiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.professionalRepository = professionalRepository;
        this.salonServiceRepository = salonServiceRepository;
    }

    public AppointmentResponseDto create(AppointmentRequestDto request) {
        Booking booking = resolveBooking(request);
        ensureBookable(booking, null);
        Appointment appointment = new Appointment(
                request.customerName(), request.customerPhone(), request.customerEmail(),
                booking.professional(), booking.service(), request.startTime());
        return AppointmentResponseDto.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAll() {
        return appointmentRepository.findAll(Sort.by("startTime"))
                .stream().map(AppointmentResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto findById(Long id) {
        return AppointmentResponseDto.from(findEntity(id));
    }

    public AppointmentResponseDto update(Long id, AppointmentRequestDto request) {
        Appointment appointment = findEntity(id);
        Booking booking = resolveBooking(request);
        ensureBookable(booking, id);
        appointment.update(
                request.customerName(), request.customerPhone(), request.customerEmail(),
                booking.professional(), booking.service(), request.startTime());
        return AppointmentResponseDto.from(appointment);
    }

    public void delete(Long id) {
        appointmentRepository.delete(findEntity(id));
    }

    private Booking resolveBooking(AppointmentRequestDto request) {
        Professional professional = professionalRepository.findByIdForUpdate(request.professionalId())
                .orElseThrow(() -> new ProfessionalNotFoundException(request.professionalId()));
        SalonService service = salonServiceRepository.findById(request.serviceId())
                .orElseThrow(() -> new SalonServiceNotFoundException(request.serviceId()));
        return new Booking(professional, service, request.startTime(),
                request.startTime().plusMinutes(service.getDurationMinutes()));
    }

    private void ensureBookable(Booking booking, Long excludedAppointmentId) {
        if (!booking.professional().isActive()) {
            throw new InvalidBookingException("Professional is not active");
        }
        if (!booking.professional().offers(booking.service())) {
            throw new InvalidBookingException("Professional does not offer the selected service");
        }
        ProfessionalWorkingHours hours = booking.professional()
                .workingHoursFor(booking.startTime().getDayOfWeek())
                .orElseThrow(() -> new InvalidBookingException("Professional is not working on this day"));
        if (!booking.startTime().toLocalDate().equals(booking.endTime().toLocalDate())
                || booking.startTime().toLocalTime().isBefore(hours.getStartTime())
                || booking.endTime().toLocalTime().isAfter(hours.getEndTime())) {
            throw new InvalidBookingException("Appointment must fit within professional working hours");
        }
        if (appointmentRepository.hasOverlap(
                booking.professional().getId(), booking.startTime(),
                booking.endTime(), excludedAppointmentId)) {
            throw new AppointmentConflictException();
        }
    }

    private Appointment findEntity(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private record Booking(
            Professional professional, SalonService service,
            LocalDateTime startTime, LocalDateTime endTime) {
    }
}
