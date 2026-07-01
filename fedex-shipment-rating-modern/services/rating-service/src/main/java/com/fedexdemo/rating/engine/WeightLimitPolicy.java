package com.fedexdemo.rating.engine;

import com.fedexdemo.rating.domain.RatingRejection;
import com.fedexdemo.rating.domain.ServiceLevel;
import com.fedexdemo.rating.domain.Shipment;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class WeightLimitPolicy {

    public static final double MAX_AIR_WEIGHT_LBS = 150.0;

    public Optional<RatingRejection> evaluate(Shipment shipment) {
        if (shipment.pkg().weightLbs() > MAX_AIR_WEIGHT_LBS && isAirService(shipment.requestedService())) {
            return Optional.of(RatingRejection.WEIGHT_EXCEEDS_AIR_LIMIT);
        }

        return Optional.empty();
    }

    private boolean isAirService(ServiceLevel serviceLevel) {
        return switch (serviceLevel) {
            case EXPRESS, PRIORITY_OVERNIGHT -> true;
            case GROUND -> false;
        };
    }
}
