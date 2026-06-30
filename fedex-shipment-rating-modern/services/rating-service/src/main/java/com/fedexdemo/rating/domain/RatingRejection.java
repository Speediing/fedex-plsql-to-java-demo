package com.fedexdemo.rating.domain;

public enum RatingRejection {
    UNKNOWN_LANE(-2001, "Unknown lane"),
    NO_RATE_FOR_SERVICE(-2002, "No rate for service/zone"),
    WEIGHT_EXCEEDS_AIR_LIMIT(-2041, "Weight exceeds air service limit"),
    DG_NOT_ALLOWED_ON_OVERNIGHT(-2042, "Dangerous goods not allowed on Priority Overnight"),
    OVERSIZE_AIR_INELIGIBLE(-2043, "Oversize package limited to Ground"),
    INTERNATIONAL_REQUIRES_EXPRESS(-2050, "International destination requires EXPRESS");

    private final int legacyErrorCode;
    private final String message;

    RatingRejection(int legacyErrorCode, String message) {
        this.legacyErrorCode = legacyErrorCode;
        this.message = message;
    }

    public int legacyErrorCode() {
        return legacyErrorCode;
    }

    public String message() {
        return message;
    }
}
