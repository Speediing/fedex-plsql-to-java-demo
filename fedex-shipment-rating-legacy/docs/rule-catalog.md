# Rule catalog

Traceability from business behavior to legacy source and Java replacement.

## Eligibility rules

| ID | Rule | Legacy location | Java replacement |
| --- | --- | --- | --- |
| E1 | Weight > 150 lbs blocks EXP and PO | `SERVICE_ELIGIBILITY_PKG.check_service` | `WeightLimitPolicy` |
| E2 | DG blocks PO | `SERVICE_ELIGIBILITY_PKG.check_service` | `DangerousGoodsPolicy` |
| E3 | Oversize blocks EXP and PO | `SERVICE_ELIGIBILITY_PKG.check_service` | `OversizePolicy` |
| E4 | Non-US destination allows EXP only | `SERVICE_ELIGIBILITY_PKG.check_service` | `InternationalLanePolicy` |
| E5 | Duplicate PO weight check | `SHIPMENT_RATING_PKG.rate_shipment` (drift) | Removed; single policy path |

## Pricing rules

| ID | Rule | Legacy location | Java replacement |
| --- | --- | --- | --- |
| P1 | Zone lookup by zip prefix | Cursor on `zone_matrix` | `ZoneRepository` + `ShippingZone` |
| P2 | Base rate by zone + service | `service_base_rate` SELECT | `BaseRateTable` |
| P3 | Ground heavy weight: +$0.50/lb over 70 | Inline in `rate_shipment` | `HeavyWeightSurchargePolicy` |
| P4 | Residential: +$4.95 GND / +$5.95 air | Inline `p_dest_type = 'R'` | `ResidentialSurchargePolicy` |
| P5 | Saturday: +$16 on EXP/PO | Inline flag check | `SaturdayDeliveryPolicy` |
| P6 | Oversize: +$85 on GND only | Inline after `is_oversize` | `OversizeFeePolicy` |
| P7 | DG: +$35 on EXP | Inline flag check | `DangerousGoodsPolicy` surcharge leg |
| P8 | Fuel: 12% on subtotal | `c_fuel_factor` constant | `FuelSurchargePolicy` |
| P9 | Account tier discount | `tier_discount_pct` function | `AccountDiscountPolicy` |

## Side effects removed in rebuild

| Legacy | Problem | Java approach |
| --- | --- | --- |
| `INSERT INTO rating_audit` inside rating proc | Mixes calculation with persistence | `RateShipmentUseCase` persists after rating |
| `g_last_zone` package variable | Hidden shared state | Stateless `RatingEngine` |
