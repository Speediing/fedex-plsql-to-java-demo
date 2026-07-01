package com.fedexdemo.rating.engine;

import com.fedexdemo.rating.domain.RatingRejection;
import com.fedexdemo.rating.domain.ServiceLevel;
import com.fedexdemo.rating.domain.Shipment;
import java.util.Optional;

/**
 * Eligibility rules ported from {@code SERVICE_ELIGIBILITY_PKG.check_service}.
 * Catalog rules E1-E4; E5 (duplicate weight guard) intentionally omitted.
 */
public final class EligibilityPolicy {

    private static final double MAX_AIR_WEIGHT_LBS = 150.0;

    public Optional<RatingRejection> check(Shipment shipment) {
        var destination = shipment.route().destination();
        var service = shipment.requestedService();
        var pkg = shipment.pkg();

        if (destination.isInternational() && service != ServiceLevel.EXPRESS) {
            return Optional.of(RatingRejection.INTERNATIONAL_REQUIRES_EXPRESS);
        }

        if (pkg.weightLbs() > MAX_AIR_WEIGHT_LBS && isAirService(service)) {
            return Optional.of(RatingRejection.WEIGHT_EXCEEDS_AIR_LIMIT);
        }

        if (shipment.dangerousGoods() && service == ServiceLevel.PRIORITY_OVERNIGHT) {
            return Optional.of(RatingRejection.DG_NOT_ALLOWED_ON_OVERNIGHT);
        }

        if (pkg.isOversize() && isAirService(service)) {
            return Optional.of(RatingRejection.OVERSIZE_AIR_INELIGIBLE);
        }

        return Optional.empty();
    }

    private static boolean isAirService(ServiceLevel service) {
        return service == ServiceLevel.EXPRESS || service == ServiceLevel.PRIORITY_OVERNIGHT;
    }
}
