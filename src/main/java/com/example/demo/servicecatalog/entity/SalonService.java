package com.example.demo.servicecatalog.entity;

import com.example.demo.salon.entity.Salon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "services", uniqueConstraints =
        @UniqueConstraint(
                name = "uk_service_salon_audience_type", columnNames = {"salon_id", "audience", "service_type"}))
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ServiceAudience audience;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private ServiceType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    protected SalonService() {
    }

    public SalonService(
            Salon salon, ServiceAudience audience, ServiceType type, BigDecimal price, Integer durationMinutes) {
        this.salon = Objects.requireNonNull(salon, "Salon is required");
        update(audience, type, price, durationMinutes);
    }

    public void update(
            ServiceAudience audience, ServiceType type, BigDecimal price, Integer durationMinutes) {
        this.audience = Objects.requireNonNull(audience, "Audience is required");
        this.type = requireSupportedType(type, audience);
        this.price = requirePrice(price);
        this.durationMinutes = requireDuration(durationMinutes);
    }

    private ServiceType requireSupportedType(ServiceType type, ServiceAudience audience) {
        Objects.requireNonNull(type, "Service type is required");
        if (!type.supports(audience)) {
            throw new IllegalArgumentException(type.getDisplayName() + " is not offered for " + audience);
        }
        return type;
    }

    private BigDecimal requirePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Price must be zero or greater");
        }
        return price;
    }

    private Integer requireDuration(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero");
        }
        return durationMinutes;
    }

    public Long getId() { return id; }
    public Salon getSalon() { return salon; }
    public ServiceAudience getAudience() { return audience; }
    public ServiceType getType() { return type; }
    public BigDecimal getPrice() { return price; }
    public Integer getDurationMinutes() { return durationMinutes; }
}
