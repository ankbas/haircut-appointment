package com.example.demo.availability.service;

import com.example.demo.appointment.entity.Appointment;
import com.example.demo.appointment.exception.InvalidBookingException;
import com.example.demo.appointment.exception.ProfessionalNotFoundException;
import com.example.demo.appointment.exception.SalonServiceNotFoundException;
import com.example.demo.appointment.repository.AppointmentRepository;
import com.example.demo.availability.dto.AvailabilitySlotResponse;
import com.example.demo.professional.entity.Professional;
import com.example.demo.professional.entity.ProfessionalWorkingHours;
import com.example.demo.professional.repository.ProfessionalRepository;
import com.example.demo.professional.repository.ProfessionalTimeOffRepository;
import com.example.demo.professional.entity.ProfessionalTimeOff;
import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AvailabilityService {

    private static final int SLOT_INTERVAL_MINUTES = 15;

    private final ProfessionalRepository professionalRepository;
    private final SalonServiceRepository salonServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProfessionalTimeOffRepository timeOffRepository;

    public AvailabilityService(
            ProfessionalRepository professionalRepository,
            SalonServiceRepository salonServiceRepository,
            AppointmentRepository appointmentRepository,
            ProfessionalTimeOffRepository timeOffRepository) {
        this.professionalRepository = professionalRepository;
        this.salonServiceRepository = salonServiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.timeOffRepository = timeOffRepository;
    }

    public List<AvailabilitySlotResponse> findAvailability(
            Long salonId, Long professionalId, Long serviceId, LocalDate date) {
        Professional professional = professionalRepository.findWithDetailsById(professionalId, salonId)
                .orElseThrow(() -> new ProfessionalNotFoundException(professionalId));
        SalonService service = salonServiceRepository.findByIdAndSalonId(serviceId, salonId)
                .orElseThrow(() -> new SalonServiceNotFoundException(serviceId));

        if (!professional.isActive()) {
            return List.of();
        }
        if (!professional.offers(service)) {
            throw new InvalidBookingException("Professional does not offer the selected service");
        }

        ProfessionalWorkingHours hours = professional.workingHoursFor(date.getDayOfWeek())
                .orElse(null);
        if (hours == null) {
            return List.of();
        }

        LocalDateTime dayStart = date.atStartOfDay();
        List<Appointment> appointments = appointmentRepository.findActiveForDay(
                salonId, professionalId, dayStart, dayStart.plusDays(1));
        List<ProfessionalTimeOff> timeOff = timeOffRepository
                .findByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThanOrderByStartsAt(
                        professionalId, dayStart.plusDays(1), dayStart);
        LocalDateTime cursor = date.atTime(hours.getStartTime());
        LocalDateTime closingTime = date.atTime(hours.getEndTime());
        LocalDateTime now = LocalDateTime.now();
        List<AvailabilitySlotResponse> slots = new ArrayList<>();

        while (!cursor.plusMinutes(service.getDurationMinutes()).isAfter(closingTime)) {
            LocalDateTime slotEnd = cursor.plusMinutes(service.getDurationMinutes());
            LocalDateTime slotStart = cursor;
            boolean overlaps = appointments.stream().anyMatch(appointment ->
                    appointment.getStartTime().isBefore(slotEnd)
                            && appointment.getEndTime().isAfter(slotStart));
            boolean overlapsTimeOff = timeOff.stream().anyMatch(block ->
                    block.getStartsAt().isBefore(slotEnd) && block.getEndsAt().isAfter(slotStart));
            if (cursor.isAfter(now) && !overlaps && !overlapsTimeOff) {
                slots.add(new AvailabilitySlotResponse(cursor, slotEnd));
            }
            cursor = cursor.plusMinutes(SLOT_INTERVAL_MINUTES);
        }
        return slots;
    }
}
