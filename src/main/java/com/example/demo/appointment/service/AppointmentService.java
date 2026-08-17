package com.example.demo.appointment.service;

import com.example.demo.appointment.dto.AppointmentRequestDto;
import com.example.demo.appointment.dto.AppointmentResponseDto;
import com.example.demo.appointment.entity.Appointment;
import com.example.demo.appointment.exception.AppointmentConflictException;
import com.example.demo.appointment.exception.AppointmentNotFoundException;
import com.example.demo.appointment.repository.AppointmentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public AppointmentResponseDto create(AppointmentRequestDto request) {
        ensureSlotIsAvailable(request, null);
        Appointment appointment = new Appointment(
                request.name().trim(), request.phoneNumber().trim(),
                request.appointmentDate(), request.appointmentTime());
        return AppointmentResponseDto.from(repository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAll() {
        return repository.findAll(Sort.by("appointmentDate", "appointmentTime"))
                .stream().map(AppointmentResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto findById(Long id) {
        return AppointmentResponseDto.from(findEntity(id));
    }

    public AppointmentResponseDto update(Long id, AppointmentRequestDto request) {
        Appointment appointment = findEntity(id);
        ensureSlotIsAvailable(request, id);
        appointment.update(request.name().trim(), request.phoneNumber().trim(),
                request.appointmentDate(), request.appointmentTime());
        return AppointmentResponseDto.from(appointment);
    }

    public void delete(Long id) {
        repository.delete(findEntity(id));
    }

    private Appointment findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private void ensureSlotIsAvailable(AppointmentRequestDto request, Long currentId) {
        boolean booked = currentId == null
                ? repository.existsByAppointmentDateAndAppointmentTime(request.appointmentDate(), request.appointmentTime())
                : repository.existsByAppointmentDateAndAppointmentTimeAndIdNot(
                        request.appointmentDate(), request.appointmentTime(), currentId);
        if (booked) {
            throw new AppointmentConflictException();
        }
    }
}
