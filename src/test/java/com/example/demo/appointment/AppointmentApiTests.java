package com.example.demo.appointment;

import com.example.demo.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:appointments-api-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class AppointmentApiTests {

    private static final String APPOINTMENT = """
            {
              "name": "John Smith",
              "phoneNumber": "312-555-1234",
              "appointmentDate": "2099-08-20",
              "appointmentTime": "14:30"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository repository;

    @BeforeEach
    void clearAppointments() {
        repository.deleteAll();
    }

    @Test
    void createsAndRetrievesAnAppointment() throws Exception {
        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(APPOINTMENT))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("John Smith"));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value("312-555-1234"))
                .andExpect(jsonPath("$[0].appointmentDate").value("2099-08-20"))
                .andExpect(jsonPath("$[0].appointmentTime").value("14:30:00"));
    }

    @Test
    void rejectsAnAlreadyBookedTime() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON).content(APPOINTMENT))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON).content(APPOINTMENT))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Appointment slot unavailable"));
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"", "phoneNumber":"abc", "appointmentDate":"2000-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.phoneNumber").exists())
                .andExpect(jsonPath("$.errors.appointmentDate").exists())
                .andExpect(jsonPath("$.errors.appointmentTime").exists());
    }
}
