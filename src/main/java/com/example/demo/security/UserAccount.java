package com.example.demo.security;

import com.example.demo.professional.entity.Professional;
import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 100) private String username;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private UserRole role;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "professional_id") private Professional professional;
    @Column(nullable = false) private boolean active;
    protected UserAccount() {}
    public UserAccount(String username, String passwordHash, UserRole role, Professional professional) { this.username=username.trim().toLowerCase(); this.passwordHash=passwordHash; this.role=role; this.professional=professional; this.active=true; }
    public Long getId(){return id;} public String getUsername(){return username;} public String getPasswordHash(){return passwordHash;} public UserRole getRole(){return role;} public Professional getProfessional(){return professional;} public boolean isActive(){return active;}
}
