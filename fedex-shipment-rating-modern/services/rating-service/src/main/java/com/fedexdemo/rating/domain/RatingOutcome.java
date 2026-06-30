package com.fedexdemo.rating.domain;

public sealed interface RatingOutcome permits RatingResult, RejectedRating {

    String trackingRef();

    ServiceLevel serviceLevel();
}
