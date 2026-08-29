package com.example.demo.salon.controller;

import com.example.demo.salon.entity.Salon;
import com.example.demo.salon.exception.SalonNotFoundException;
import com.example.demo.salon.repository.SalonRepository;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated @RestController @RequestMapping("/api/salons")
public class SalonController {
    private final SalonRepository repository;
    public SalonController(SalonRepository repository){this.repository=repository;}
    @GetMapping("/{id}") public ResponseEntity<SalonResponse> find(@PathVariable @Positive Long id){
        Salon salon=repository.findById(id).filter(Salon::isActive).orElseThrow(()->new SalonNotFoundException(id));
        return ResponseEntity.ok(new SalonResponse(salon.getId(),salon.getName(),salon.getSlug(),salon.getTimeZone()));
    }
    public record SalonResponse(Long id,String name,String slug,String timeZone){}
}
