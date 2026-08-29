package com.example.demo.appointment.entity;

import com.example.demo.professional.entity.Professional;
import com.example.demo.servicecatalog.entity.SalonService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "confirmation_number", nullable = false, unique = true, length = 20)
    private String confirmationNumber;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 25)
    private String customerPhone;

    @Column(name = "customer_email", nullable = false, length = 254)
    private String customerEmail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private SalonService service;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    protected Appointment() {
    }

    public Appointment(
            String customerName, String customerPhone, String customerEmail,
            Professional professional, SalonService service, LocalDateTime startTime) {
        this.status = AppointmentStatus.BOOKED;
        this.confirmationNumber = "ATL-" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 8).toUpperCase();
        update(customerName, customerPhone, customerEmail, professional, service, startTime);
    }

    public void update(
            String customerName, String customerPhone, String customerEmail,
            Professional professional, SalonService service, LocalDateTime startTime) {
        this.customerName = requireText(customerName, "Customer name is required");
        this.customerPhone = requireText(customerPhone, "Customer phone is required");
        this.customerEmail = requireText(customerEmail, "Customer email is required");
        this.professional = Objects.requireNonNull(professional, "Professional is required");
        this.service = Objects.requireNonNull(service, "Service is required");
        this.startTime = Objects.requireNonNull(startTime, "Start time is required");
        this.endTime = startTime.plusMinutes(service.getDurationMinutes());
    }

    public void changeStatus(AppointmentStatus status) {
        this.status = Objects.requireNonNull(status, "Appointment status is required");
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public Long getId() { return id; }
    public String getConfirmationNumber() { return confirmationNumber; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public Professional getProfessional() { return professional; }
    public SalonService getService() { return service; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public AppointmentStatus getStatus() { return status; }
}
