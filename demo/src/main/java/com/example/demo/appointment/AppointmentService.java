package com.example.demo.appointment;

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

    public AppointmentResponse create(AppointmentRequest request) {
        ensureSlotIsAvailable(request, null);
        Appointment appointment = new Appointment(
                request.name().trim(), request.phoneNumber().trim(),
                request.appointmentDate(), request.appointmentTime());
        return AppointmentResponse.from(repository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> findAll() {
        return repository.findAll(Sort.by("appointmentDate", "appointmentTime"))
                .stream().map(AppointmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponse findById(Long id) {
        return AppointmentResponse.from(findEntity(id));
    }

    public AppointmentResponse update(Long id, AppointmentRequest request) {
        Appointment appointment = findEntity(id);
        ensureSlotIsAvailable(request, id);
        appointment.update(request.name().trim(), request.phoneNumber().trim(),
                request.appointmentDate(), request.appointmentTime());
        return AppointmentResponse.from(appointment);
    }

    public void delete(Long id) {
        repository.delete(findEntity(id));
    }

    private Appointment findEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private void ensureSlotIsAvailable(AppointmentRequest request, Long currentId) {
        boolean booked = currentId == null
                ? repository.existsByAppointmentDateAndAppointmentTime(request.appointmentDate(), request.appointmentTime())
                : repository.existsByAppointmentDateAndAppointmentTimeAndIdNot(
                        request.appointmentDate(), request.appointmentTime(), currentId);
        if (booked) {
            throw new AppointmentConflictException();
        }
    }
}
