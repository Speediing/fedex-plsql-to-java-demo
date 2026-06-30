# fedex-shipment-rating-legacy

Legacy Oracle PL/SQL shipment rating for the SHIPRATE demo.

## What's here

- `legacy/oracle/schema/` — zone matrix, base rates, account tiers, audit table
- `legacy/oracle/packages/` — `SHIPMENT_RATING_PKG` and `SERVICE_ELIGIBILITY_PKG`
- `legacy/oracle/scripts/run_rating.sql` — SQL*Plus demo of a Memphis → Dallas ground rate
- `scenarios/` — JSON inputs shared with the modern service
- `docs/expected-ratings.csv` — parity oracle (verified against the PL/SQL packages)
- `docs/domain-glossary.md`, `rule-catalog.md`, `decision-tables.md` — extraction artifacts

## Run in Oracle (Docker)

From repo root:

```bash
docker compose up -d
docker compose exec oracle sqlplus rating/rating@//localhost:1521/FREEPDB1 \
  @/opt/oracle/scripts/legacy/run_rating.sql
```

Expected output for the default script: total **26.26**, error code **0**.

## Legacy smells (intentional)

- Shared package state (`g_last_zone`)
- Duplicated weight guard between packages
- Magic error codes (`-2041`, `-2042`, …)
- Rating procedure mixes calculation with audit insert

These are talking points for the domain-extraction phase of the demo.
