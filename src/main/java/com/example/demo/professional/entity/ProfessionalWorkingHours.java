package com.example.demo.professional.entity;

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
import jakarta.persistence.UniqueConstraint;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "professional_working_hours", uniqueConstraints =
        @UniqueConstraint(
                name = "uk_professional_working_day", columnNames = {"professional_id", "day_of_week"}))
public class ProfessionalWorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    protected ProfessionalWorkingHours() {
    }

    ProfessionalWorkingHours(
            Professional professional, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.professional = Objects.requireNonNull(professional);
        this.dayOfWeek = Objects.requireNonNull(dayOfWeek);
        update(startTime, endTime);
    }

    void update(LocalTime startTime, LocalTime endTime) {
        Objects.requireNonNull(startTime, "Working-hours start time is required");
        Objects.requireNonNull(endTime, "Working-hours end time is required");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("Working-hours end time must be after start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}
