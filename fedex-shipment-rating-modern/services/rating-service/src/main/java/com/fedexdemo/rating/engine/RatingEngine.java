package com.fedexdemo.rating.engine;

import com.fedexdemo.rating.domain.RatingOutcome;
import com.fedexdemo.rating.domain.Shipment;
import org.springframework.stereotype.Component;

/**
 * Modernized shipment rating engine.
 *
 * <p>THIS IS THE LIVE-DEMO TARGET. The body is intentionally NOT implemented.
 * During the demo we extract business rules from legacy PL/SQL and rebuild them here:
 * <ul>
 *   <li>{@code SERVICE_ELIGIBILITY_PKG} -> {@code EligibilityPolicy}</li>
 *   <li>{@code SHIPMENT_RATING_PKG} pricing block -> {@code PricingPolicy}</li>
 *   <li>Magic error codes -> typed {@code RatingRejection} reasons</li>
 * </ul>
 *
 * <p>Standards when implementing:
 * <ul>
 *   <li>Keep Spring and JDBC out of {@code domain/}.</li>
 *   <li>Use {@link com.fedexdemo.rating.domain.Money} for currency math.</li>
 *   <li>Every result must match {@code expected-ratings.csv} (enforced by the disabled
 *       characterization tests).</li>
 * </ul>
 */
@Component
public class RatingEngine {

    public RatingOutcome rate(Shipment shipment) {
        // TODO(demo): extract domain from fedex-shipment-rating-legacy PL/SQL packages.
        // Implement EligibilityPolicy + PricingPolicy, wire ReferenceData, and make
        // CharacterizationTest pass for all 9 scenarios.
        throw new UnsupportedOperationException(
                "Rating migration not implemented yet - this is the live-demo task");
    }
}
