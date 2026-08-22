package com.example.demo.appointment;

import com.example.demo.appointment.repository.AppointmentRepository;
import com.example.demo.professional.entity.Professional;
import com.example.demo.professional.repository.ProfessionalRepository;
import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.entity.ServiceAudience;
import com.example.demo.servicecatalog.entity.ServiceType;
import com.example.demo.servicecatalog.repository.SalonServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties =
        "spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://localhost:5432/haircut_appointments_test}")
@AutoConfigureMockMvc
class AppointmentApiTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private ProfessionalRepository professionalRepository;
    @Autowired private SalonServiceRepository salonServiceRepository;

    private SalonService haircut;
    private Professional professional;
    private LocalDate bookingDate;

    @BeforeEach
    void setUpCatalog() {
        appointmentRepository.deleteAll();
        professionalRepository.deleteAll();
        salonServiceRepository.deleteAll();

        haircut = salonServiceRepository.save(
                new SalonService(ServiceAudience.MEN, ServiceType.HAIRCUT,
                        new BigDecimal("30.00"), 30));
        professional = new Professional("Alex Morgan", "Hair specialist", true);
        professional.addService(haircut);
        professional = professionalRepository.save(professional);
        bookingDate = LocalDate.now().plusDays(10);
    }

    @Test
    void createsAppointmentAndCalculatesEndTimeFromServiceDuration() throws Exception {
        LocalDateTime start = bookingDate.atTime(10, 0);
        String location = mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON).content(request("John Smith", start)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.customerName").value("John Smith"))
                .andExpect(jsonPath("$.professionalId").value(professional.getId()))
                .andExpect(jsonPath("$.serviceId").value(haircut.getId()))
                .andExpect(jsonPath("$.startTime").value(start + ":00"))
                .andExpect(jsonPath("$.endTime").value(start.plusMinutes(30) + ":00"))
                .andExpect(jsonPath("$.status").value("BOOKED"))
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location)).andExpect(status().isOk());
    }

    @Test
    void preventsDoubleBookingButAllowsAdjacentAppointments() throws Exception {
        LocalDateTime start = bookingDate.atTime(10, 0);
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON).content(request("John Smith", start)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Jane Smith", start.plusMinutes(15))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Appointment slot unavailable"));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Jane Smith", start.plusMinutes(30))))
                .andExpect(status().isCreated());
    }

    @Test
    void rejectsAppointmentsOutsideWorkingHours() throws Exception {

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Jane Smith", bookingDate.atTime(17, 45))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid booking"));
    }

    @Test
    void availabilityReturnsOnlySlotsThatFitAndDoNotOverlap() throws Exception {
        LocalDateTime bookedStart = bookingDate.atTime(10, 0);
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("John Smith", bookedStart)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/availability")
                        .param("professionalId", professional.getId().toString())
                        .param("serviceId", haircut.getId().toString())
                        .param("date", bookingDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].startTime", hasItem(bookingDate + "T09:00:00")))
                .andExpect(jsonPath("$[*].startTime", hasItem(bookingDate + "T10:30:00")))
                .andExpect(jsonPath("$[*].startTime", not(hasItem(bookingDate + "T09:45:00"))))
                .andExpect(jsonPath("$[*].startTime", not(hasItem(bookingDate + "T10:00:00"))))
                .andExpect(jsonPath("$[*].startTime", not(hasItem(bookingDate + "T10:15:00"))));
    }

    @Test
    void cancellationPreservesAppointmentAndReleasesItsTimeSlot() throws Exception {
        LocalDateTime start = bookingDate.atTime(10, 0);
        String location = createAppointment("John Smith", start);

        mockMvc.perform(patch(location + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Jane Smith", start)))
                .andExpect(status().isCreated());
    }

    @Test
    void reschedulingReleasesOldSlotAndReservesNewSlot() throws Exception {
        LocalDateTime start = bookingDate.atTime(10, 0);
        LocalDateTime rescheduledStart = start.plusHours(1);
        String location = createAppointment("John Smith", start);

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Jane Smith", rescheduledStart)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Jane Smith"))
                .andExpect(jsonPath("$.startTime").value(rescheduledStart + ":00"))
                .andExpect(jsonPath("$.endTime").value(rescheduledStart.plusMinutes(30) + ":00"));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Open old slot", start)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Blocked new slot", rescheduledStart)))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsReschedulingIntoAnOccupiedSlotWithoutChangingAppointment() throws Exception {
        LocalDateTime originalStart = bookingDate.atTime(10, 0);
        LocalDateTime occupiedStart = bookingDate.atTime(11, 0);
        String location = createAppointment("John Smith", originalStart);
        createAppointment("Jane Smith", occupiedStart);

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("John Smith", occupiedStart)))
                .andExpect(status().isConflict());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value(originalStart + ":00"));
    }

    @Test
    void deletesAppointment() throws Exception {
        String location = createAppointment("John Smith", bookingDate.atTime(10, 0));
        mockMvc.perform(delete(location)).andExpect(status().isNoContent());
        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void validatesBookingRequestAndAvailabilityParameters() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerName").exists())
                .andExpect(jsonPath("$.errors.professionalId").exists())
                .andExpect(jsonPath("$.errors.serviceId").exists())
                .andExpect(jsonPath("$.errors.startTime").exists());

        mockMvc.perform(get("/availability")
                        .param("professionalId", "0")
                        .param("serviceId", haircut.getId().toString())
                        .param("date", bookingDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.professionalId").exists());
    }

    private String request(String customerName, LocalDateTime startTime) {
        return """
                {
                  "customerName": "%s",
                  "customerPhone": "312-555-1234",
                  "customerEmail": "customer@example.com",
                  "professionalId": %d,
                  "serviceId": %d,
                  "startTime": "%s"
                }
                """.formatted(customerName, professional.getId(), haircut.getId(), startTime);
    }

    private String createAppointment(String customerName, LocalDateTime startTime) throws Exception {
        return mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request(customerName, startTime)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }
}
