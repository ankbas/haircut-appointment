package com.example.demo.salon.repository;

import com.example.demo.salon.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonRepository extends JpaRepository<Salon, Long> {}
