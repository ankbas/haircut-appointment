package com.example.demo.servicecatalog.controller;

import com.example.demo.servicecatalog.dto.ServiceResponseDto;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/services")
public class ServiceCatalogController {
    private final SalonServiceRepository repository;

    public ServiceCatalogController(SalonServiceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> findAll(@RequestParam @Positive Long salonId) {
        return ResponseEntity.ok(repository.findBySalonId(salonId, Sort.by("audience", "type")).stream()
                .map(ServiceResponseDto::from).toList());
    }
}
