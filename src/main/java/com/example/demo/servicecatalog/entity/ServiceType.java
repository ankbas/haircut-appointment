package com.example.demo.servicecatalog.entity;

import java.util.EnumSet;
import java.util.Set;

public enum ServiceType {
    HAIRCUT("Haircut", ServiceAudience.MEN, ServiceAudience.WOMEN),
    BEARD_TRIM("Beard Trim", ServiceAudience.MEN),
    HAIR_AND_BEARD("Hair + Beard", ServiceAudience.MEN),
    HAIR_STYLING("Hair Styling", ServiceAudience.WOMEN),
    WAXING("Waxing", ServiceAudience.WOMEN),
    THREADING("Threading", ServiceAudience.WOMEN),
    FACIAL("Facial", ServiceAudience.WOMEN),
    CLEANSING("Cleansing", ServiceAudience.WOMEN),
    MAKEUP("Makeup", ServiceAudience.WOMEN),
    NAILS("Nails", ServiceAudience.WOMEN);

    private final String displayName;
    private final Set<ServiceAudience> supportedAudiences;

    ServiceType(String displayName, ServiceAudience first, ServiceAudience... others) {
        this.displayName = displayName;
        this.supportedAudiences = EnumSet.of(first, others);
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean supports(ServiceAudience audience) {
        return supportedAudiences.contains(audience);
    }
}
