package com.example.demo.professional.repository;

import com.example.demo.professional.entity.Professional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    @EntityGraph(attributePaths = {"services", "workingHours"})
    @Query("select p from Professional p where p.id = :id")
    Optional<Professional> findWithDetailsById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"services", "workingHours"})
    @Query("select p from Professional p where p.id = :id")
    Optional<Professional> findByIdForUpdate(@Param("id") Long id);
}
