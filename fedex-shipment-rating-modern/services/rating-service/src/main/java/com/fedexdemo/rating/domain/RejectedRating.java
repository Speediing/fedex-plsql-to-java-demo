package com.fedexdemo.rating.domain;

public record RejectedRating(
        String trackingRef,
        ServiceLevel serviceLevel,
        RatingRejection reason) implements RatingOutcome {

    @Override
    public String trackingRef() {
        return trackingRef;
    }

    @Override
    public ServiceLevel serviceLevel() {
        return serviceLevel;
    }
}
