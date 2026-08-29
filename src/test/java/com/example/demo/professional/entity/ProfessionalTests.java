package com.example.demo.professional.entity;

import com.example.demo.servicecatalog.entity.SalonService;
import com.example.demo.servicecatalog.entity.ServiceAudience;
import com.example.demo.servicecatalog.entity.ServiceType;
import org.junit.jupiter.api.Test;
import com.example.demo.salon.entity.Salon;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfessionalTests {
    private final Salon salon = new Salon("Atelier", "atelier", "America/Chicago");

    @Test
    void connectsProfessionalToServices() {
        Professional professional = new Professional(salon,
                "Alex Morgan", "Hair and beard specialist", true);
        SalonService haircut = new SalonService(salon,
                ServiceAudience.MEN, ServiceType.HAIRCUT, new BigDecimal("30.00"), 30);

        professional.addService(haircut);

        assertEquals("Alex Morgan", professional.getName());
        assertTrue(professional.isActive());
        assertTrue(professional.getServices().contains(haircut));
    }

    @Test
    void rejectsMissingProfessionalDetailsAndNullServices() {
        assertThrows(IllegalArgumentException.class,
                () -> new Professional(salon, " ", "Experienced stylist", true));
        assertThrows(IllegalArgumentException.class,
                () -> new Professional(salon, "Alex Morgan", null, true));

        Professional professional = new Professional(salon,
                "Alex Morgan", "Experienced stylist", true);
        assertThrows(NullPointerException.class, () -> professional.addService(null));
    }

    @Test
    void rejectsServiceOwnedByAnotherSalon() {
        Salon otherSalon = new Salon("Other Salon", "other-salon", "America/Chicago");
        Professional professional = new Professional(salon, "Alex Morgan", "Experienced stylist", true);
        SalonService foreignService = new SalonService(otherSalon, ServiceAudience.MEN,
                ServiceType.HAIRCUT, new BigDecimal("35.00"), 30);

        assertThrows(IllegalArgumentException.class, () -> professional.addService(foreignService));
    }
}
