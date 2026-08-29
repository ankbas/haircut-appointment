package com.example.demo.professional.repository;

import com.example.demo.professional.entity.ProfessionalTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ProfessionalTimeOffRepository extends JpaRepository<ProfessionalTimeOff, Long> {
    List<ProfessionalTimeOff> findByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThanOrderByStartsAt(
            Long professionalId, LocalDateTime end, LocalDateTime start);
    boolean existsByProfessionalIdAndStartsAtLessThanAndEndsAtGreaterThan(
            Long professionalId, LocalDateTime end, LocalDateTime start);
}
