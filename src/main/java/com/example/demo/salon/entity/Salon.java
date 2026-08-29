package com.example.demo.salon.entity;

import jakarta.persistence.*;
import java.time.ZoneId;

@Entity
@Table(name = "salons")
public class Salon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String name;
    @Column(nullable = false, unique = true, length = 100) private String slug;
    @Column(nullable = false) private boolean active;
    @Column(name = "time_zone", nullable = false, length = 50) private String timeZone;

    protected Salon() {}
    public Salon(String name, String slug, String timeZone) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Salon name is required");
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException("Salon slug is required");
        ZoneId.of(timeZone);
        this.name = name.trim(); this.slug = slug.trim().toLowerCase(); this.timeZone = timeZone; this.active = true;
    }
    public Long getId(){return id;} public String getName(){return name;} public String getSlug(){return slug;}
    public boolean isActive(){return active;} public String getTimeZone(){return timeZone;}
}
