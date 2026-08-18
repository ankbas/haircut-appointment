package com.example.demo.appointment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments", uniqueConstraints =
        @UniqueConstraint(name = "uk_appointment_slot", columnNames = {"appointment_date", "appointment_time"}))
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 25)
    private String phoneNumber;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    protected Appointment() {
    }

    public Appointment(
            String name, String phoneNumber, String email,
            LocalDate appointmentDate, LocalTime appointmentTime) {
        update(name, phoneNumber, email, appointmentDate, appointmentTime);
    }

    public void update(
            String name, String phoneNumber, String email,
            LocalDate appointmentDate, LocalTime appointmentTime) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
}
