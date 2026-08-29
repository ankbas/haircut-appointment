package com.example.demo.servicecatalog.dto;

import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.entity.ServiceAudience;
import com.example.demo.servicecatalog.entity.ServiceType;

import java.math.BigDecimal;

public record ServiceResponseDto(
        Long id, ServiceAudience audience, ServiceType type, String displayName,
        BigDecimal price, Integer durationMinutes) {
    public static ServiceResponseDto from(SalonService service) {
        return new ServiceResponseDto(
                service.getId(), service.getAudience(), service.getType(),
                service.getType().getDisplayName(), service.getPrice(), service.getDurationMinutes());
    }
}
