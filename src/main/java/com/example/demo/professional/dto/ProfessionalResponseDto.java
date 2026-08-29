package com.example.demo.professional.dto;

import com.example.demo.professional.entity.Professional;

import java.util.Set;
import java.util.stream.Collectors;

public record ProfessionalResponseDto(
        Long id, String name, String bio, boolean active, Set<Long> serviceIds) {
    public static ProfessionalResponseDto from(Professional professional) {
        return new ProfessionalResponseDto(
                professional.getId(), professional.getName(), professional.getBio(),
                professional.isActive(), professional.getServices().stream()
                        .map(service -> service.getId()).collect(Collectors.toSet()));
    }
}
