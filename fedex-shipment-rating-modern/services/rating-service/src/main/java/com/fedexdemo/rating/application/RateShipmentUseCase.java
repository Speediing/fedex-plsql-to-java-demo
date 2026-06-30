package com.fedexdemo.rating.application;

import com.fedexdemo.rating.domain.RatingOutcome;
import com.fedexdemo.rating.domain.RatingResult;
import com.fedexdemo.rating.domain.RejectedRating;
import com.fedexdemo.rating.domain.Shipment;
import com.fedexdemo.rating.engine.RatingEngine;
import com.fedexdemo.rating.migration.MigrationRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RateShipmentUseCase {

    private static final Logger log = LoggerFactory.getLogger(RateShipmentUseCase.class);

    private final RatingEngine ratingEngine;
    private final MigrationRouter migrationRouter;

    public RateShipmentUseCase(RatingEngine ratingEngine, MigrationRouter migrationRouter) {
        this.ratingEngine = ratingEngine;
        this.migrationRouter = migrationRouter;
    }

    public RatingOutcome rate(Shipment shipment) {
        if (migrationRouter.useJavaEngine(shipment.account().accountId())) {
            log.info("Routing account {} to Java rating engine", shipment.account().accountId());
            return ratingEngine.rate(shipment);
        }

        log.info("Routing account {} to legacy PL/SQL adapter (demo stub)", shipment.account().accountId());
        return ratingEngine.rate(shipment);
    }

    public void persistAudit(RatingOutcome outcome) {
        switch (outcome) {
            case RatingResult result ->
                    log.debug(
                            "Audit rating {} service {} total {}",
                            result.trackingRef(),
                            result.serviceLevel(),
                            result.total().amount());
            case RejectedRating rejected ->
                    log.debug(
                            "Audit rejection {} code {}",
                            rejected.trackingRef(),
                            rejected.reason().legacyErrorCode());
            default -> {
            }
        }
    }
}
