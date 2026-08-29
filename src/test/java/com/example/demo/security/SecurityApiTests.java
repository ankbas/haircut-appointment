package com.example.demo.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import com.example.demo.salon.repository.SalonRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties =
        "spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://localhost:5432/haircut_appointments_test}")
@AutoConfigureMockMvc
class SecurityApiTests {
    @Autowired MockMvc mockMvc;
    @Autowired UserAccountRepository repository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired SalonRepository salons;

    @BeforeEach void setUp() {
        repository.deleteAll();
        repository.save(new UserAccount(salons.findById(1L).orElseThrow(),
                "test-admin", passwordEncoder.encode("Strong-Test-Password"),
                UserRole.ADMIN, null));
    }

    @Test void loginReturnsSignedTokenAndTokenAllowsAdminAccess() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test-admin","password":"Strong-Test-Password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn().getResponse().getContentAsString();
        String token = com.jayway.jsonpath.JsonPath.read(response, "$.token");
        LocalDate today = LocalDate.now();

        mockMvc.perform(get("/api/admin/appointments")
                        .header("Authorization", "Bearer " + token)
                        .param("from", today.toString()).param("to", today.plusDays(7).toString()))
                .andExpect(status().isOk());
    }

    @Test void missingOrInvalidTokenCannotAccessAdminApi() throws Exception {
        LocalDate today = LocalDate.now();
        mockMvc.perform(get("/api/admin/appointments")
                        .param("from", today.toString()).param("to", today.toString()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/admin/appointments")
                        .header("Authorization", "Bearer invalid.token.value")
                        .param("from", today.toString()).param("to", today.toString()))
                .andExpect(status().isForbidden());
    }
}
