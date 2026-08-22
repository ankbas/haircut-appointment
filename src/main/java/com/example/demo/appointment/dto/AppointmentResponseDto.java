package com.example.demo.appointment.dto;

import com.example.demo.appointment.entity.Appointment;
import com.example.demo.appointment.entity.AppointmentStatus;
import com.example.demo.servicecatalog.entity.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentResponseDto(
        Long id,
        String customerName,
        String customerPhone,
        String customerEmail,
        Long professionalId,
        String professionalName,
        Long serviceId,
        ServiceType serviceType,
        BigDecimal price,
        Integer durationMinutes,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AppointmentStatus status) {

    public static AppointmentResponseDto from(Appointment appointment) {
        return new AppointmentResponseDto(
                appointment.getId(), appointment.getCustomerName(), appointment.getCustomerPhone(),
                appointment.getCustomerEmail(), appointment.getProfessional().getId(),
                appointment.getProfessional().getName(), appointment.getService().getId(),
                appointment.getService().getType(), appointment.getService().getPrice(),
                appointment.getService().getDurationMinutes(), appointment.getStartTime(),
                appointment.getEndTime(), appointment.getStatus());
    }
}
