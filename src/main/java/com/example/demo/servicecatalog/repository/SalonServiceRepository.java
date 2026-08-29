package com.example.demo.servicecatalog.repository;

import com.example.demo.servicecatalog.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
    List<SalonService> findBySalonId(Long salonId, Sort sort);
    Optional<SalonService> findByIdAndSalonId(Long id, Long salonId);
    boolean existsByIdAndSalonId(Long id, Long salonId);
}
