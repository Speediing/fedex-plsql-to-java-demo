package com.fedexdemo.rating.legacy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class LegacyRuleOwnershipTest {

    private static final Pattern DRIFTED_PRIORITY_OVERNIGHT_WEIGHT_GUARD = Pattern.compile(
            "IF\\s+p_weight_lbs\\s*>\\s*150\\s+AND\\s+p_service_code\\s*=\\s*'PO'\\s+THEN",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern SERVICE_ELIGIBILITY_AIR_WEIGHT_LIMIT = Pattern.compile(
            "c_max_air_weight\\s+CONSTANT\\s+NUMBER\\s*:=\\s*\\d+\\s*;"
                    + "[\\s\\S]*p_weight_lbs\\s*>\\s*c_max_air_weight"
                    + "\\s+AND\\s+p_service_code\\s+IN\\s*\\(\\s*'EXP'\\s*,\\s*'PO'\\s*\\)",
            Pattern.CASE_INSENSITIVE);

    @Test
    void rateShipmentDoesNotRepeatPriorityOvernightWeightEligibility() throws IOException {
        String ratingPackage = Files.readString(legacyFile("legacy/oracle/packages/SHIPMENT_RATING_PKG.sql"));

        assertFalse(
                DRIFTED_PRIORITY_OVERNIGHT_WEIGHT_GUARD.matcher(ratingPackage).find(),
                "SHIPMENT_RATING_PKG.rate_shipment must not repeat the air weight guard owned by"
                        + " SERVICE_ELIGIBILITY_PKG.check_service");
        assertFalse(
                ratingPackage.contains("-2041"),
                "Weight limit rejection -2041 belongs to SERVICE_ELIGIBILITY_PKG.check_service");
    }

    @Test
    void serviceEligibilityOwnsSingleAirWeightLimitForBothAirServices() throws IOException {
        String eligibilityPackage = Files.readString(legacyFile("legacy/oracle/packages/SERVICE_ELIGIBILITY_PKG.sql"));

        assertTrue(
                SERVICE_ELIGIBILITY_AIR_WEIGHT_LIMIT.matcher(eligibilityPackage).find(),
                "SERVICE_ELIGIBILITY_PKG.check_service should enforce air weight with c_max_air_weight for EXP and PO");
    }

    private static Path legacyFile(String relativePath) {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            Path candidate = current.resolve("fedex-shipment-rating-legacy").resolve(relativePath);
            if (Files.exists(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Could not locate fedex-shipment-rating-legacy/" + relativePath);
    }
}
