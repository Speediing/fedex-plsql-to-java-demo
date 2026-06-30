# fedex-shipment-rating-modern

Modernization target for the legacy `fedex-shipment-rating-legacy` (SHIPRATE) system.

This repo is **scaffolded for the demo, not migrated.** Everything around the business
logic is wired up so the live migration is fast:

- `services/rating-service/` — Java 21 + Spring Boot.
  - Domain model: `Shipment`, `ServiceLevel`, `Money`, `RatingOutcome`, etc.
  - `ReferenceData` — same zone/rate/tier tables as the Oracle seed data.
  - REST endpoints: `POST /ratings`, `GET /scenarios`.
  - **`RatingEngine.rate(...)` is an intentional `TODO` stub** — this is the live-demo
    task (extract domain from PL/SQL, implement policies, verify parity).
  - `CharacterizationTest` — pre-wired with all 9 expected values from
    `fedex-shipment-rating-legacy/docs/expected-ratings.csv`, **`@Disabled`** until the
    engine is implemented.

## Run it

```bash
cd services/rating-service
mvn spring-boot:run
```

Then:

```bash
# plumbing works (demo scenarios loaded):
curl localhost:8080/scenarios

# rating endpoint exists but logic isn't migrated yet -> HTTP 501:
curl -i -X POST localhost:8080/ratings \
  -H 'Content-Type: application/json' \
  -d @../../fedex-shipment-rating-legacy/scenarios/memphis-to-dallas-ground.json
```

## Demo flow

1. Show the app runs and serves demo scenarios (`GET /scenarios`).
2. Show `POST /ratings` returns `501 NOT_IMPLEMENTED` — the migration target.
3. Walk `fedex-shipment-rating-legacy/docs/rule-catalog.md` and name the domain.
4. Implement `RatingEngine` live from the PL/SQL (policies, not lift-and-shift).
5. Remove `@Disabled` from `CharacterizationTest` and watch all 9 scenarios go green.
