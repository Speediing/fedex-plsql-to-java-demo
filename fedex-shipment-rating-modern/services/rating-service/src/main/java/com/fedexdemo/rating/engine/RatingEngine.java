package com.fedexdemo.rating.engine;

import com.fedexdemo.rating.domain.RatingOutcome;
import com.fedexdemo.rating.domain.RatingRejection;
import com.fedexdemo.rating.domain.RatingResult;
import com.fedexdemo.rating.domain.RejectedRating;
import com.fedexdemo.rating.domain.Shipment;
import com.fedexdemo.rating.reference.ReferenceData;
import org.springframework.stereotype.Component;

/**
 * Modernized shipment rating engine.
 *
 * <p>Extracts business rules from legacy PL/SQL:
 * <ul>
 *   <li>{@code SERVICE_ELIGIBILITY_PKG} -> {@code EligibilityPolicy}</li>
 *   <li>{@code SHIPMENT_RATING_PKG} pricing block -> {@code PricingPolicy}</li>
 *   <li>Magic error codes -> typed {@code RatingRejection} reasons</li>
 * </ul>
 */
@Component
public class RatingEngine {

    private final EligibilityPolicy eligibilityPolicy = new EligibilityPolicy();
    private final PricingPolicy pricingPolicy = new PricingPolicy();

    public RatingOutcome rate(Shipment shipment) {
        var route = shipment.route();
        var zone = ReferenceData.findZone(route.originPrefix(), route.destination().zipPrefix());
        if (zone.isEmpty()) {
            return reject(shipment, RatingRejection.UNKNOWN_LANE);
        }

        int zoneCode = zone.get();

        var eligibilityFailure = eligibilityPolicy.check(shipment);
        if (eligibilityFailure.isPresent()) {
            return reject(shipment, eligibilityFailure.get());
        }

        var baseRate = ReferenceData.findBaseRate(zoneCode, shipment.requestedService());
        if (baseRate.isEmpty()) {
            return reject(shipment, RatingRejection.NO_RATE_FOR_SERVICE);
        }

        var total = pricingPolicy.price(shipment, baseRate.get());
        return new RatingResult(
                shipment.trackingRef(),
                shipment.requestedService(),
                zoneCode,
                total);
    }

    private RejectedRating reject(Shipment shipment, RatingRejection reason) {
        return new RejectedRating(
                shipment.trackingRef(),
                shipment.requestedService(),
                reason);
    }
}
