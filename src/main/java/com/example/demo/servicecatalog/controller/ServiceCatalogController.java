package com.example.demo.servicecatalog.controller;

import com.example.demo.servicecatalog.dto.ServiceResponseDto;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceCatalogController {
    private final SalonServiceRepository repository;

    public ServiceCatalogController(SalonServiceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> findAll() {
        return ResponseEntity.ok(repository.findAll(Sort.by("audience", "type")).stream()
                .map(ServiceResponseDto::from).toList());
    }
}
