package com.fedexdemo.rating.web;

import com.fedexdemo.rating.domain.RatingOutcome;
import com.fedexdemo.rating.domain.RatingResult;
import com.fedexdemo.rating.domain.RejectedRating;

public record RatingResponse(
        String trackingRef,
        String serviceCode,
        String status,
        Double totalAmount,
        Integer errorCode,
        String errorMessage,
        Integer zoneCode,
        String engine) {

    public static RatingResponse from(RatingOutcome outcome, String engine) {
        return switch (outcome) {
            case RatingResult result -> new RatingResponse(
                    result.trackingRef(),
                    result.serviceLevel().legacyCode(),
                    "RATED",
                    result.total().amount().doubleValue(),
                    0,
                    null,
                    result.zoneCode(),
                    engine);
            case RejectedRating rejected -> new RatingResponse(
                    rejected.trackingRef(),
                    rejected.serviceLevel().legacyCode(),
                    "REJECTED",
                    0.0,
                    rejected.reason().legacyErrorCode(),
                    rejected.reason().message(),
                    null,
                    engine);
            default -> throw new IllegalStateException("Unexpected outcome: " + outcome);
        };
    }
}
