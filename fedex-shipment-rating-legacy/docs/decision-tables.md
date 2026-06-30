# Decision tables

## Service eligibility (after lane resolution)

| Condition | GND | EXP | PO |
| --- | --- | --- | --- |
| Weight <= 150, domestic, not oversize, no DG block | Allowed | Allowed | Allowed |
| Weight > 150 | Allowed | Reject -2041 | Reject -2041 |
| DG = Y | Allowed | Allowed (+$35) | Reject -2042 |
| Oversize (girth > 165) | Allowed (+$85) | Reject -2043 | Reject -2043 |
| dest_region != US | Reject -2050 | Allowed | Reject -2050 |

## Residential surcharge

| dest_type | GND | EXP/PO |
| --- | --- | --- |
| R | +$4.95 | +$5.95 |
| C | $0 | $0 |

## Account discount (applied after fuel)

| tier_code | Discount |
| --- | --- |
| GOLD | 10% |
| SILVER | 5% |
| STANDARD / unknown | 0% |

## Sample calculation: Memphis (38118) -> Dallas (75201), 25 lb, residential GND

1. Zone 4, base GND $18.50
2. Residential +$4.95 -> $23.45
3. Fuel 12% -> $26.26
4. STANDARD account -> **$26.26**
