package com.example.demo.appointment.service;

import com.example.demo.appointment.dto.AppointmentRequestDto;
import com.example.demo.appointment.dto.AppointmentResponseDto;
import com.example.demo.appointment.dto.CustomerRescheduleDto;
import com.example.demo.appointment.entity.Appointment;
import com.example.demo.appointment.entity.AppointmentStatus;
import com.example.demo.appointment.exception.AppointmentConflictException;
import com.example.demo.appointment.exception.AppointmentNotFoundException;
import com.example.demo.appointment.exception.InvalidBookingException;
import com.example.demo.appointment.exception.ProfessionalNotFoundException;
import com.example.demo.appointment.exception.SalonServiceNotFoundException;
import com.example.demo.appointment.repository.AppointmentRepository;
import com.example.demo.professional.entity.Professional;
import com.example.demo.professional.entity.ProfessionalWorkingHours;
import com.example.demo.professional.repository.ProfessionalRepository;
import com.example.demo.professional.repository.ProfessionalTimeOffRepository;
import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import com.example.demo.salon.entity.Salon;
import com.example.demo.salon.exception.SalonNotFoundException;
import com.example.demo.salon.repository.SalonRepository;
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
    private final ProfessionalTimeOffRepository timeOffRepository;
    private final SalonRepository salonRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ProfessionalRepository professionalRepository,
            SalonServiceRepository salonServiceRepository,
            ProfessionalTimeOffRepository timeOffRepository,
            SalonRepository salonRepository) {
        this.appointmentRepository = appointmentRepository;
        this.professionalRepository = professionalRepository;
        this.salonServiceRepository = salonServiceRepository;
        this.timeOffRepository = timeOffRepository;
        this.salonRepository = salonRepository;
    }

    public AppointmentResponseDto create(AppointmentRequestDto request) {
        Booking booking = resolveBooking(request);
        ensureBookable(booking, null);
        Appointment appointment = new Appointment(
                booking.salon(), request.customerName(), request.customerPhone(), request.customerEmail(),
                booking.professional(), booking.service(), request.startTime());
        return AppointmentResponseDto.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAll(Long salonId) {
        return appointmentRepository.findBySalonIdAndStartTimeBetweenOrderByStartTime(salonId, LocalDateTime.MIN, LocalDateTime.MAX)
                .stream().map(AppointmentResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto findById(Long id, Long salonId) {
        return AppointmentResponseDto.from(findEntity(id, salonId));
    }

    public AppointmentResponseDto update(Long id, AppointmentRequestDto request, Long salonId) {
        if (!salonId.equals(request.salonId())) throw new InvalidBookingException("Booking does not belong to your salon");
        Appointment appointment = findEntity(id, salonId);
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidBookingException("Only booked appointments can be rescheduled");
        }
        Booking booking = resolveBooking(request);
        ensureBookable(booking, id);
        appointment.update(
                request.customerName(), request.customerPhone(), request.customerEmail(),
                booking.professional(), booking.service(), request.startTime());
        return AppointmentResponseDto.from(appointment);
    }

    public AppointmentResponseDto cancel(Long id, Long salonId) {
        Appointment appointment = findEntity(id, salonId);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidBookingException("Completed appointments cannot be cancelled");
        }
        appointment.changeStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponseDto.from(appointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto findForCustomer(Long salonId, String confirmationNumber, String email) {
        return AppointmentResponseDto.from(findForCustomerEntity(salonId, confirmationNumber, email));
    }

    public AppointmentResponseDto cancelForCustomer(Long salonId, String confirmationNumber, String email) {
        Appointment appointment = findForCustomerEntity(salonId, confirmationNumber, email);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidBookingException("Completed appointments cannot be cancelled");
        }
        appointment.changeStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponseDto.from(appointment);
    }

    public AppointmentResponseDto rescheduleForCustomer(CustomerRescheduleDto request) {
        Appointment appointment = findForCustomerEntity(request.salonId(), request.confirmationNumber(), request.email());
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidBookingException("Only booked appointments can be rescheduled");
        }
        AppointmentRequestDto bookingRequest = new AppointmentRequestDto(
                appointment.getSalon().getId(), appointment.getCustomerName(), appointment.getCustomerPhone(),
                appointment.getCustomerEmail(), appointment.getProfessional().getId(),
                appointment.getService().getId(), request.startTime());
        Booking booking = resolveBooking(bookingRequest);
        ensureBookable(booking, appointment.getId());
        appointment.update(
                appointment.getCustomerName(), appointment.getCustomerPhone(), appointment.getCustomerEmail(),
                booking.professional(), booking.service(), request.startTime());
        return AppointmentResponseDto.from(appointment);
    }

    public void delete(Long id, Long salonId) {
        appointmentRepository.delete(findEntity(id, salonId));
    }

    private Booking resolveBooking(AppointmentRequestDto request) {
        Salon salon = salonRepository.findById(request.salonId()).filter(Salon::isActive)
                .orElseThrow(() -> new SalonNotFoundException(request.salonId()));
        Professional professional = professionalRepository.findByIdForUpdate(request.professionalId(), request.salonId())
                .orElseThrow(() -> new ProfessionalNotFoundException(request.professionalId()));
        SalonService service = salonServiceRepository.findByIdAndSalonId(request.serviceId(), request.salonId())
                .orElseThrow(() -> new SalonServiceNotFoundException(request.serviceId()));
        return new Booking(salon, professional, service, request.startTime(),
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
                booking.salon().getId(), booking.professional().getId(), booking.startTime(),
                booking.endTime(), excludedAppointmentId)) {
            throw new AppointmentConflictException();
        }
        if (timeOffRepository.existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(
                booking.professional().getId(), booking.endTime(), booking.startTime())) {
            throw new InvalidBookingException("Professional is unavailable during the selected time");
        }
    }

    private Appointment findEntity(Long id, Long salonId) {
        return appointmentRepository.findByIdAndSalonId(id, salonId)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findBetween(Long salonId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findBySalonIdAndStartTimeBetweenOrderByStartTime(salonId, start, end).stream()
                .map(AppointmentResponseDto::from).toList();
    }

    public AppointmentResponseDto changeStatus(Long id, Long salonId, AppointmentStatus status) {
        Appointment appointment = findEntity(id, salonId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED && status != AppointmentStatus.CANCELLED) {
            throw new InvalidBookingException("Cancelled appointments cannot be reactivated");
        }
        appointment.changeStatus(status);
        return AppointmentResponseDto.from(appointment);
    }

    private Appointment findForCustomerEntity(Long salonId, String confirmationNumber, String email) {
        return appointmentRepository
                .findBySalonIdAndConfirmationNumberIgnoreCaseAndCustomerEmailIgnoreCase(
                        salonId, confirmationNumber.trim(), email.trim())
                .orElseThrow(AppointmentNotFoundException::new);
    }

    private record Booking(
            Salon salon, Professional professional, SalonService service,
            LocalDateTime startTime, LocalDateTime endTime) {
    }
}
