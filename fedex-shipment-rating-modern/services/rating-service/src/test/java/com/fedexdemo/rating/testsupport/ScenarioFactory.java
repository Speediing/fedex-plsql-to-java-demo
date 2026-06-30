package com.fedexdemo.rating.testsupport;

import com.fedexdemo.rating.domain.AccountTier;
import com.fedexdemo.rating.domain.CustomerAccount;
import com.fedexdemo.rating.domain.DeliveryLocation;
import com.fedexdemo.rating.domain.DeliveryOptions;
import com.fedexdemo.rating.domain.PackageDimensions;
import com.fedexdemo.rating.domain.Route;
import com.fedexdemo.rating.domain.ServiceLevel;
import com.fedexdemo.rating.domain.Shipment;
import com.fedexdemo.rating.reference.ReferenceData;

public final class ScenarioFactory {

    private ScenarioFactory() {
    }

    public static Shipment fromRow(
            String trackingRef,
            String originZip,
            String destZip,
            char destType,
            String destRegion,
            double weightLbs,
            double lengthIn,
            double widthIn,
            double heightIn,
            String serviceCode,
            String accountId,
            boolean saturday,
            boolean dg) {
        return new Shipment(
                trackingRef,
                new Route(
                        originZip,
                        new DeliveryLocation(
                                destZip,
                                DeliveryLocation.LocationType.fromLegacyFlag(destType),
                                destRegion)),
                new PackageDimensions(lengthIn, widthIn, heightIn, weightLbs),
                ServiceLevel.fromLegacyCode(serviceCode),
                new CustomerAccount(accountId, ReferenceData.accountTierFor(accountId)),
                new DeliveryOptions(saturday),
                dg);
    }
}
