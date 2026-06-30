package com.fedexdemo.rating.web;

import com.fedexdemo.rating.application.RateShipmentUseCase;
import com.fedexdemo.rating.migration.MigrationRouter;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private final RateShipmentUseCase rateShipment;
    private final MigrationRouter migrationRouter;

    public RatingController(RateShipmentUseCase rateShipment, MigrationRouter migrationRouter) {
        this.rateShipment = rateShipment;
        this.migrationRouter = migrationRouter;
    }

    @PostMapping
    public RatingResponse rate(@Valid @RequestBody RatingRequest request) {
        var shipment = request.toShipment();
        var outcome = rateShipment.rate(shipment);
        rateShipment.persistAudit(outcome);

        String engine = migrationRouter.useJavaEngine(shipment.account().accountId())
                ? "java-domain"
                : "legacy-plsql-adapter";

        return RatingResponse.from(outcome, engine);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, String>> notImplemented(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "status", "NOT_IMPLEMENTED",
                        "message", ex.getMessage()));
    }
}
