# Domain glossary (extracted from legacy PL/SQL)

Synthetic FedEx-inspired shipment rating vocabulary. Terms below replace legacy flags,
codes, and package variables in the Java rebuild.

| Legacy artifact | Domain term | Java type |
| --- | --- | --- |
| `p_dest_type = 'R'` | Residential delivery | `DeliveryLocation.residential()` |
| `p_dest_type = 'C'` | Commercial delivery | `DeliveryLocation.commercial()` |
| `p_dest_region <> 'US'` | International lane | `Route.international()` |
| `p_service_code` GND/EXP/PO | Service level | `ServiceLevel` enum |
| `zone_matrix` lookup | Shipping lane zone | `ShippingZone` |
| `p_dg_flag = 'Y'` | Dangerous goods shipment | `Shipment.dangerousGoods()` |
| `is_oversize()` girth > 165 | Oversize package | `Package.oversize()` |
| `p_saturday_flag = 'Y'` | Saturday delivery option | `DeliveryOptions.saturdayDelivery()` |
| `tier_code` GOLD/SILVER | Account discount tier | `AccountTier` |
| Error `-2041` | Weight exceeds air limit | `RatingRejection.WEIGHT_EXCEEDS_AIR_LIMIT` |
| Error `-2042` | DG blocked on PO | `RatingRejection.DG_NOT_ALLOWED_ON_OVERNIGHT` |
| Error `-2043` | Oversize limited to ground | `RatingRejection.OVERSIZE_AIR_INELIGIBLE` |
| Error `-2050` | International requires express | `RatingRejection.INTERNATIONAL_REQUIRES_EXPRESS` |
| `g_last_zone` package state | (removed) | Zone resolved per request, no shared state |
| Fuel factor 0.12 | Fuel surcharge policy | `FuelSurchargePolicy` |
| Audit insert in rating proc | Side effect separated | `RatingEngine` pure; persistence in application layer |

## Ubiquitous language

- **Shipment**: A package moving from origin to destination under a customer account.
- **Lane**: Origin/destination pair mapped to a pricing zone.
- **Rating**: Computing total charge and eligibility for a requested service level.
- **Rejection**: Business rule failure with a typed reason, replacing negative error codes.
