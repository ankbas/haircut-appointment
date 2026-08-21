package com.example.demo.professional.entity;

import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.entity.ServiceAudience;
import com.example.demo.servicecatalog.entity.ServiceType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfessionalTests {

    @Test
    void connectsProfessionalToServices() {
        Professional professional = new Professional(
                "Alex Morgan", "Hair and beard specialist", true);
        SalonService haircut = new SalonService(
                ServiceAudience.MEN, ServiceType.HAIRCUT, new BigDecimal("30.00"), 30);

        professional.addService(haircut);

        assertEquals("Alex Morgan", professional.getName());
        assertTrue(professional.isActive());
        assertTrue(professional.getServices().contains(haircut));
    }

    @Test
    void rejectsMissingProfessionalDetailsAndNullServices() {
        assertThrows(IllegalArgumentException.class,
                () -> new Professional(" ", "Experienced stylist", true));
        assertThrows(IllegalArgumentException.class,
                () -> new Professional("Alex Morgan", null, true));

        Professional professional = new Professional(
                "Alex Morgan", "Experienced stylist", true);
        assertThrows(NullPointerException.class, () -> professional.addService(null));
    }
}
