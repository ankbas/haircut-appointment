package com.example.demo.servicecatalog.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SalonServiceTests {

    @Test
    void createsSupportedMensAndWomensServices() {
        SalonService beardTrim = new SalonService(
                ServiceAudience.MEN, ServiceType.BEARD_TRIM, new BigDecimal("15.00"), 20);
        SalonService makeup = new SalonService(
                ServiceAudience.WOMEN, ServiceType.MAKEUP, new BigDecimal("80.00"), 60);

        assertEquals(ServiceType.BEARD_TRIM, beardTrim.getType());
        assertEquals(ServiceType.MAKEUP, makeup.getType());
    }

    @Test
    void rejectsUnsupportedAudienceAndInvalidPricing() {
        assertThrows(IllegalArgumentException.class, () -> new SalonService(
                ServiceAudience.WOMEN, ServiceType.BEARD_TRIM, new BigDecimal("15.00"), 20));
        assertThrows(IllegalArgumentException.class, () -> new SalonService(
                ServiceAudience.MEN, ServiceType.HAIRCUT, new BigDecimal("-1.00"), 30));
        assertThrows(IllegalArgumentException.class, () -> new SalonService(
                ServiceAudience.MEN, ServiceType.HAIRCUT, new BigDecimal("25.00"), 0));
    }
}
