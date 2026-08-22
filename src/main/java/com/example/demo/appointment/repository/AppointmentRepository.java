package com.example.demo.appointment.repository;

import com.example.demo.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            select (count(a) > 0) from Appointment a
            where a.professional.id = :professionalId
              and a.status <> com.example.demo.appointment.entity.AppointmentStatus.CANCELLED
              and a.startTime < :endTime
              and a.endTime > :startTime
              and (:excludedId is null or a.id <> :excludedId)
            """)
    boolean hasOverlap(
            @Param("professionalId") Long professionalId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludedId") Long excludedId);

    @Query("""
            select a from Appointment a
            where a.professional.id = :professionalId
              and a.status <> com.example.demo.appointment.entity.AppointmentStatus.CANCELLED
              and a.startTime < :dayEnd
              and a.endTime > :dayStart
            order by a.startTime
            """)
    List<Appointment> findActiveForDay(
            @Param("professionalId") Long professionalId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);
}
