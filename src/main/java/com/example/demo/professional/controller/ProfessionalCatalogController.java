package com.example.demo.professional.controller;

import com.example.demo.professional.dto.ProfessionalResponseDto;
import com.example.demo.professional.repository.ProfessionalRepository;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/professionals")
public class ProfessionalCatalogController {
    private final ProfessionalRepository repository;

    public ProfessionalCatalogController(ProfessionalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalResponseDto>> findForService(
            @RequestParam @Positive Long salonId,
            @RequestParam @Positive Long serviceId) {
        return ResponseEntity.ok(repository.findActiveByServiceId(salonId, serviceId).stream()
                .map(ProfessionalResponseDto::from).toList());
    }
}
