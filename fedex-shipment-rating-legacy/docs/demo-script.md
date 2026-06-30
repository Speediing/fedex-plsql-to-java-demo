# Demo script (~20 minutes)

## 1. Set the stage (2 min)

Synthetic FedEx-inspired shipment rating. Legacy side is Oracle PL/SQL. The modern side
is scaffolded. The live demo is extracting the domain and rebuilding in Java.

## 2. Show the pain in PL/SQL (5 min)

Open `fedex-shipment-rating-legacy/legacy/oracle/packages/SHIPMENT_RATING_PKG.sql`.

Ask the room:
- Where is the residential surcharge?
- What happens when DG meets Priority Overnight?
- Why does `g_last_zone` exist?

Optional Docker run:

```bash
docker compose up -d
docker compose exec oracle sqlplus rating/rating@//localhost:1521/FREEPDB1 \
  @/opt/oracle/scripts/legacy/run_rating.sql
```

## 3. Extract the domain (4 min)

Open `fedex-shipment-rating-legacy/docs/domain-glossary.md` and `rule-catalog.md`.

Show error `-2042` -> `RatingRejection.DG_NOT_ALLOWED_ON_OVERNIGHT`.

## 4. Show the scaffold runs (3 min)

```bash
cd fedex-shipment-rating-modern/services/rating-service
mvn spring-boot:run
curl localhost:8080/scenarios
curl -i -X POST localhost:8080/ratings \
  -H 'Content-Type: application/json' \
  -d @../../fedex-shipment-rating-legacy/scenarios/memphis-to-dallas-ground.json
```

`501 NOT_IMPLEMENTED` is the migration target.

## 5. Implement live (5 min)

Open `RatingEngine.java`. Build `EligibilityPolicy` and `PricingPolicy` from the rule
catalog. Wire `ReferenceData`. No lift-and-shift of PL/SQL structure.

## 6. Prove parity (3 min)

Remove `@Disabled` from `CharacterizationTest`, run `mvn test`. All 9 scenarios green.

## Closing

Characterize first, name the domain second, rebuild third. Parity tests gate the strangler
rollout account by account.
