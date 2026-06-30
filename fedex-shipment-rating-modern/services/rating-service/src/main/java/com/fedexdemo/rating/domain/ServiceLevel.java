package com.fedexdemo.rating.domain;

public enum ServiceLevel {
    GROUND("GND"),
    EXPRESS("EXP"),
    PRIORITY_OVERNIGHT("PO");

    private final String legacyCode;

    ServiceLevel(String legacyCode) {
        this.legacyCode = legacyCode;
    }

    public String legacyCode() {
        return legacyCode;
    }

    public static ServiceLevel fromLegacyCode(String code) {
        for (ServiceLevel level : values()) {
            if (level.legacyCode.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown service code: " + code);
    }
}
