package com.fedexdemo.rating.domain;

public record Shipment(
        String trackingRef,
        Route route,
        PackageDimensions pkg,
        ServiceLevel requestedService,
        CustomerAccount account,
        DeliveryOptions options,
        boolean dangerousGoods) {
}
