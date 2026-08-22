package com.example.demo.servicecatalog.repository;

import com.example.demo.servicecatalog.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
}
