package com.example.demo.professional.repository;

import com.example.demo.professional.entity.Professional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    @EntityGraph(attributePaths = {"services", "workingHours"})
    @Query("select p from Professional p where p.id = :id")
    Optional<Professional> findWithDetailsById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"services", "workingHours"})
    @Query("select p from Professional p where p.id = :id")
    Optional<Professional> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"services"})
    @Query("select distinct p from Professional p join p.services s where p.active = true and s.id = :serviceId order by p.name")
    List<Professional> findActiveByServiceId(@Param("serviceId") Long serviceId);
}
