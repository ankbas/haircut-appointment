package com.example.demo.professional.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "professional_time_off")
public class ProfessionalTimeOff {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "professional_id", nullable = false) private Professional professional;
    @Column(name = "starts_at", nullable = false) private LocalDateTime startsAt;
    @Column(name = "ends_at", nullable = false) private LocalDateTime endsAt;
    @Column(length = 300) private String reason;
    protected ProfessionalTimeOff() {}
    public ProfessionalTimeOff(Professional professional, LocalDateTime startsAt, LocalDateTime endsAt, String reason) { if(!endsAt.isAfter(startsAt)) throw new IllegalArgumentException("Time off end must be after start"); this.professional=professional; this.startsAt=startsAt; this.endsAt=endsAt; this.reason=reason==null?null:reason.trim(); }
    public Long getId(){return id;} public Professional getProfessional(){return professional;} public LocalDateTime getStartsAt(){return startsAt;} public LocalDateTime getEndsAt(){return endsAt;} public String getReason(){return reason;}
}
