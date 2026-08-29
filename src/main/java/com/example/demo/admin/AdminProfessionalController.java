package com.example.demo.admin;

import com.example.demo.appointment.exception.ProfessionalNotFoundException;
import com.example.demo.appointment.exception.SalonServiceNotFoundException;
import com.example.demo.professional.dto.ProfessionalResponseDto;
import com.example.demo.professional.entity.*;
import com.example.demo.professional.repository.*;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.demo.security.AuthenticatedUser;
import com.example.demo.salon.repository.SalonRepository;

@RestController @RequestMapping("/api/admin/professionals") @PreAuthorize("hasRole('ADMIN')") @Transactional
public class AdminProfessionalController {
    private final ProfessionalRepository professionals; private final SalonServiceRepository services; private final ProfessionalTimeOffRepository timeOff; private final SalonRepository salons;
    public AdminProfessionalController(ProfessionalRepository professionals,SalonServiceRepository services,ProfessionalTimeOffRepository timeOff,SalonRepository salons){this.professionals=professionals;this.services=services;this.timeOff=timeOff;this.salons=salons;}
    @GetMapping public List<ProfessionalResponseDto> all(@AuthenticationPrincipal AuthenticatedUser user){return professionals.findBySalonIdOrderByName(user.salonId()).stream().map(ProfessionalResponseDto::from).toList();}
    @PostMapping public ResponseEntity<ProfessionalResponseDto> create(@AuthenticationPrincipal AuthenticatedUser user,@Valid @RequestBody ProfessionalRequest request){ Professional p=new Professional(salons.getReferenceById(user.salonId()),request.name(),request.bio(),request.active()); attachServices(p,request.serviceIds(),user.salonId()); p=professionals.save(p); return ResponseEntity.status(HttpStatus.CREATED).body(ProfessionalResponseDto.from(p)); }
    @PutMapping("/{id}") public ProfessionalResponseDto update(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long id,@Valid @RequestBody ProfessionalRequest request){ Professional p=find(id,user.salonId()); p.update(request.name(),request.bio(),request.active()); new HashSet<>(p.getServices()).forEach(p::removeService); attachServices(p,request.serviceIds(),user.salonId()); return ProfessionalResponseDto.from(p); }
    @PutMapping("/{id}/working-hours/{day}") public ResponseEntity<Void> hours(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long id,@PathVariable DayOfWeek day,@Valid @RequestBody HoursRequest request){find(id,user.salonId()).setWorkingHours(day,request.startTime(),request.endTime());return ResponseEntity.noContent().build();}
    @PostMapping("/{id}/time-off") public ResponseEntity<TimeOffResponse> timeOff(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long id,@Valid @RequestBody TimeOffRequest request){ ProfessionalTimeOff saved=timeOff.save(new ProfessionalTimeOff(find(id,user.salonId()),request.startsAt(),request.endsAt(),request.reason())); return ResponseEntity.status(HttpStatus.CREATED).body(TimeOffResponse.from(saved)); }
    @DeleteMapping("/time-off/{timeOffId}") public ResponseEntity<Void> deleteTimeOff(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long timeOffId){timeOff.findByIdAndProfessionalSalonId(timeOffId,user.salonId()).ifPresent(timeOff::delete);return ResponseEntity.noContent().build();}
    private Professional find(Long id,Long salonId){return professionals.findWithDetailsById(id,salonId).orElseThrow(()->new ProfessionalNotFoundException(id));}
    private void attachServices(Professional p,Set<Long> ids,Long salonId){ids.forEach(id->p.addService(services.findByIdAndSalonId(id,salonId).orElseThrow(()->new SalonServiceNotFoundException(id))));}
    public record ProfessionalRequest(@NotBlank @Size(max=100) String name,@NotBlank @Size(max=2000) String bio,boolean active,@NotEmpty Set<@Positive Long> serviceIds){}
    public record HoursRequest(@NotNull LocalTime startTime,@NotNull LocalTime endTime){}
    public record TimeOffRequest(@NotNull @Future LocalDateTime startsAt,@NotNull @Future LocalDateTime endsAt,@Size(max=300) String reason){}
    public record TimeOffResponse(Long id,Long professionalId,LocalDateTime startsAt,LocalDateTime endsAt,String reason){static TimeOffResponse from(ProfessionalTimeOff t){return new TimeOffResponse(t.getId(),t.getProfessional().getId(),t.getStartsAt(),t.getEndsAt(),t.getReason());}}
}
