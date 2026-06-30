package com.fedexdemo.rating;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fedexdemo.rating.domain.RatingResult;
import com.fedexdemo.rating.domain.RejectedRating;
import com.fedexdemo.rating.domain.Shipment;
import com.fedexdemo.rating.engine.RatingEngine;
import com.fedexdemo.rating.testsupport.ScenarioFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Golden-master / characterization tests. Expected values come from
 * fedex-shipment-rating-legacy/docs/expected-ratings.csv, verified against the
 * legacy PL/SQL packages.
 *
 * DISABLED on purpose: RatingEngine is still a stub. Remove @Disabled during the
 * demo once the migration logic is implemented.
 */
@Disabled("Enable after porting RatingEngine - see fedex-shipment-rating-legacy/docs/expected-ratings.csv")
@SpringBootTest
class CharacterizationTest {

    @Autowired
    private RatingEngine ratingEngine;

    record ScenarioRow(
            String scenarioId,
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
            boolean dg,
            BigDecimal expectedTotal,
            int expectedErrorCode) {
    }

    static Stream<ScenarioRow> scenarios() throws Exception {
        List<ScenarioRow> rows = new ArrayList<>();
        try (InputStream in = CharacterizationTest.class.getResourceAsStream("/expected-ratings.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(",", -1);
                rows.add(new ScenarioRow(
                        p[0],
                        p[1],
                        p[2],
                        p[3],
                        p[4].charAt(0),
                        p[5],
                        Double.parseDouble(p[6]),
                        Double.parseDouble(p[7]),
                        Double.parseDouble(p[8]),
                        Double.parseDouble(p[9]),
                        p[10],
                        p[11],
                        "Y".equals(p[12]),
                        "Y".equals(p[13]),
                        new BigDecimal(p[14]),
                        Integer.parseInt(p[15])));
            }
        }
        return rows.stream();
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void everyScenarioMatchesLegacyPlsql(ScenarioRow row) {
        Shipment shipment = ScenarioFactory.fromRow(
                row.trackingRef(),
                row.originZip(),
                row.destZip(),
                row.destType(),
                row.destRegion(),
                row.weightLbs(),
                row.lengthIn(),
                row.widthIn(),
                row.heightIn(),
                row.serviceCode(),
                row.accountId(),
                row.saturday(),
                row.dg());

        var outcome = ratingEngine.rate(shipment);

        if (row.expectedErrorCode() != 0) {
            var rejected = (RejectedRating) outcome;
            assertEquals(row.expectedErrorCode(), rejected.reason().legacyErrorCode(), row.scenarioId());
            return;
        }

        var result = (RatingResult) outcome;
        assertEquals(row.expectedTotal(), result.total().amount(), row.scenarioId());
    }
}
