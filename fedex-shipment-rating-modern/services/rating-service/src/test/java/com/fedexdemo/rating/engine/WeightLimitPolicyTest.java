package com.fedexdemo.rating.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fedexdemo.rating.domain.RatingRejection;
import com.fedexdemo.rating.domain.Shipment;
import com.fedexdemo.rating.testsupport.ScenarioFactory;
import org.junit.jupiter.api.Test;

class WeightLimitPolicyTest {

    private final WeightLimitPolicy policy = new WeightLimitPolicy();

    @Test
    void overweightExpressAndPriorityOvernightRejectWithSameReason() {
        assertEquals(
                RatingRejection.WEIGHT_EXCEEDS_AIR_LIMIT,
                policy.evaluate(shipment("EXP", WeightLimitPolicy.MAX_AIR_WEIGHT_LBS + 1)).orElseThrow());
        assertEquals(
                RatingRejection.WEIGHT_EXCEEDS_AIR_LIMIT,
                policy.evaluate(shipment("PO", WeightLimitPolicy.MAX_AIR_WEIGHT_LBS + 1)).orElseThrow());
    }

    @Test
    void groundCanExceedAirWeightLimit() {
        assertTrue(policy.evaluate(shipment("GND", WeightLimitPolicy.MAX_AIR_WEIGHT_LBS + 1)).isEmpty());
    }

    @Test
    void airShipmentAtLimitIsAllowed() {
        assertTrue(policy.evaluate(shipment("PO", WeightLimitPolicy.MAX_AIR_WEIGHT_LBS)).isEmpty());
    }

    private Shipment shipment(String serviceCode, double weightLbs) {
        return ScenarioFactory.fromRow(
                "FED-3",
                "38118",
                "75201",
                'C',
                "US",
                weightLbs,
                12,
                10,
                8,
                serviceCode,
                "ACCT-1001",
                false,
                false);
    }
}
