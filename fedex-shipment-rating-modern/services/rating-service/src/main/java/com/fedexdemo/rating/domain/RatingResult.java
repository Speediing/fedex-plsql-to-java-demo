package com.fedexdemo.rating.domain;

public record RatingResult(
        String trackingRef,
        ServiceLevel serviceLevel,
        int zoneCode,
        Money total) implements RatingOutcome {
}
