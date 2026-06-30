package com.fedexdemo.rating.web;

import com.fedexdemo.rating.domain.CustomerAccount;
import com.fedexdemo.rating.domain.DeliveryLocation;
import com.fedexdemo.rating.domain.DeliveryOptions;
import com.fedexdemo.rating.domain.PackageDimensions;
import com.fedexdemo.rating.domain.Route;
import com.fedexdemo.rating.domain.ServiceLevel;
import com.fedexdemo.rating.domain.Shipment;
import com.fedexdemo.rating.reference.ReferenceData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RatingRequest(
        @NotBlank String trackingRef,
        @NotBlank String originZip,
        @NotBlank String destZip,
        @NotBlank String destType,
        @NotBlank String destRegion,
        @Positive double weightLbs,
        @Positive double lengthIn,
        @Positive double widthIn,
        @Positive double heightIn,
        @NotBlank String serviceCode,
        @NotBlank String accountId,
        boolean saturdayDelivery,
        boolean dangerousGoods) {

    public Shipment toShipment() {
        return new Shipment(
                trackingRef,
                new Route(
                        originZip,
                        new DeliveryLocation(
                                destZip,
                                DeliveryLocation.LocationType.fromLegacyFlag(destType.charAt(0)),
                                destRegion)),
                new PackageDimensions(lengthIn, widthIn, heightIn, weightLbs),
                ServiceLevel.fromLegacyCode(serviceCode),
                new CustomerAccount(accountId, ReferenceData.accountTierFor(accountId)),
                new DeliveryOptions(saturdayDelivery),
                dangerousGoods);
    }
}
