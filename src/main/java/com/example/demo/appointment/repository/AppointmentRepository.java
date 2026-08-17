package com.example.demo.appointment.repository;

import com.example.demo.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByAppointmentDateAndAppointmentTime(LocalDate appointmentDate, LocalTime appointmentTime);

    boolean existsByAppointmentDateAndAppointmentTimeAndIdNot(
            LocalDate appointmentDate, LocalTime appointmentTime, Long id);
}
