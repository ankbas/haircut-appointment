package com.example.demo.professional.entity;

import com.example.demo.servicecatalog.entity.SalonService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "professionals")
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 2000)
    private String bio;

    @Column(nullable = false)
    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "professional_services",
            joinColumns = @JoinColumn(name = "professional_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<SalonService> services = new LinkedHashSet<>();

    protected Professional() {
    }

    public Professional(String name, String bio, boolean active) {
        update(name, bio, active);
    }

    public void update(String name, String bio, boolean active) {
        this.name = requireText(name, "Professional name is required");
        this.bio = requireText(bio, "Professional bio is required");
        this.active = active;
    }

    public void addService(SalonService service) {
        services.add(Objects.requireNonNull(service, "Service is required"));
    }

    public void removeService(SalonService service) {
        services.remove(service);
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public boolean isActive() { return active; }
    public Set<SalonService> getServices() { return Collections.unmodifiableSet(services); }
}
