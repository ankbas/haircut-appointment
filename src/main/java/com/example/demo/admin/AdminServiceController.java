package com.example.demo.admin;

import com.example.demo.appointment.exception.SalonServiceNotFoundException;
import com.example.demo.servicecatalog.dto.ServiceResponseDto;
import com.example.demo.servicecatalog.entity.*;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@RestController @RequestMapping("/api/admin/services") @PreAuthorize("hasRole('ADMIN')") @Transactional
public class AdminServiceController {
    private final SalonServiceRepository repository;
    public AdminServiceController(SalonServiceRepository repository){this.repository=repository;}
    @PostMapping public ResponseEntity<ServiceResponseDto> create(@Valid @RequestBody ServiceRequest request){ SalonService saved=repository.save(request.toEntity()); return ResponseEntity.status(HttpStatus.CREATED).body(ServiceResponseDto.from(saved)); }
    @PutMapping("/{id}") public ResponseEntity<ServiceResponseDto> update(@PathVariable @Positive Long id,@Valid @RequestBody ServiceRequest request){ SalonService item=repository.findById(id).orElseThrow(()->new SalonServiceNotFoundException(id)); item.update(request.audience(),request.type(),request.price(),request.durationMinutes()); return ResponseEntity.ok(ServiceResponseDto.from(item)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id){ if(!repository.existsById(id))throw new SalonServiceNotFoundException(id); repository.deleteById(id); return ResponseEntity.noContent().build(); }
    public record ServiceRequest(@NotNull ServiceAudience audience,@NotNull ServiceType type,@NotNull @DecimalMin("0.00") BigDecimal price,@NotNull @Positive Integer durationMinutes){ SalonService toEntity(){return new SalonService(audience,type,price,durationMinutes);} }
}
