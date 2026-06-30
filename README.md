# fedex-plsql-to-java-demo (SHIPRATE)

A FedEx-inspired **modernization demo**: a fictional shipment rating system, shown as a
*before* (Oracle PL/SQL packages) and a *scaffolded after* (Java 21 + Spring Boot).
Built for a "Cursor 101 + full SDLC" walkthrough — understand undocumented legacy →
extract domain → plan → characterization tests → migrate → verify parity → ship.

## Layout

```
fedex-plsql-to-java-demo/
├── fedex-shipment-rating-legacy/     # the "before": buried rules in PL/SQL
│   ├── legacy/oracle/                # schema, packages, SQL*Plus scripts
│   ├── scenarios/                    # JSON rating inputs for demo curls
│   └── docs/                         # glossary, rule catalog, expected-ratings.csv
└── fedex-shipment-rating-modern/     # the "after": scaffold only, migration is live demo
    └── services/rating-service/        # Spring Boot; RatingEngine.rate() is a TODO stub
```

## Run legacy PL/SQL (optional)

Requires Docker.

```bash
docker compose up -d
docker compose exec oracle sqlplus rating/rating@//localhost:1521/FREEPDB1 \
  @/opt/oracle/scripts/legacy/run_rating.sql
```

Or browse `fedex-shipment-rating-legacy/legacy/oracle/packages/SHIPMENT_RATING_PKG.sql`.

## Run the modern service (scaffold)

Requires JDK 21.

```bash
cd fedex-shipment-rating-modern/services/rating-service
mvn spring-boot:run
# GET  http://localhost:8080/scenarios          -> demo scenarios as JSON
# POST http://localhost:8080/ratings            -> 501 until the migration is done
```

The rating business rules are intentionally **not** migrated — `RatingEngine` is a `TODO`
stub and `CharacterizationTest` is `@Disabled`. Extracting the domain, implementing the
engine, and enabling the tests (they must match `expected-ratings.csv`) is the live-demo
exercise.

> Synthetic demo only. No real FedEx systems, data, or production code are involved.
