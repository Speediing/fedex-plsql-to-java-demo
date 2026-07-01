package com.fedexdemo.rating.engine;

import com.fedexdemo.rating.domain.Money;
import com.fedexdemo.rating.domain.ServiceLevel;
import com.fedexdemo.rating.domain.Shipment;

/**
 * Pricing rules ported from {@code SHIPMENT_RATING_PKG.rate_shipment} pricing block.
 * Catalog rules P3-P9.
 */
public final class PricingPolicy {

    private static final double FUEL_FACTOR = 0.12;
    private static final Money RESIDENTIAL_GROUND_SURCHARGE = Money.of(4.95);
    private static final Money RESIDENTIAL_AIR_SURCHARGE = Money.of(5.95);
    private static final Money SATURDAY_SURCHARGE = Money.of(16.00);
    private static final Money OVERSIZE_FEE = Money.of(85.00);
    private static final Money DG_SURCHARGE = Money.of(35.00);
    private static final double HEAVY_THRESHOLD_LBS = 70.0;
    private static final double HEAVY_PER_LB = 0.50;

    public Money price(Shipment shipment, Money baseRate) {
        Money subtotal = baseRate;

        subtotal = applyHeavyWeightSurcharge(subtotal, shipment);
        subtotal = applyResidentialSurcharge(subtotal, shipment);
        subtotal = applySaturdaySurcharge(subtotal, shipment);
        subtotal = applyOversizeFee(subtotal, shipment);
        subtotal = applyDangerousGoodsSurcharge(subtotal, shipment);

        subtotal = subtotal.multiply(1.0 + FUEL_FACTOR);
        return subtotal.applyDiscount(shipment.account().tier().discountRate());
    }

    private Money applyHeavyWeightSurcharge(Money subtotal, Shipment shipment) {
        if (shipment.requestedService() != ServiceLevel.GROUND) {
            return subtotal;
        }
        double weight = shipment.pkg().weightLbs();
        if (weight <= HEAVY_THRESHOLD_LBS) {
            return subtotal;
        }
        double extraLbs = weight - HEAVY_THRESHOLD_LBS;
        return subtotal.add(Money.of(extraLbs * HEAVY_PER_LB));
    }

    private Money applyResidentialSurcharge(Money subtotal, Shipment shipment) {
        if (!shipment.route().destination().isResidential()) {
            return subtotal;
        }
        if (shipment.requestedService() == ServiceLevel.GROUND) {
            return subtotal.add(RESIDENTIAL_GROUND_SURCHARGE);
        }
        return subtotal.add(RESIDENTIAL_AIR_SURCHARGE);
    }

    private Money applySaturdaySurcharge(Money subtotal, Shipment shipment) {
        var service = shipment.requestedService();
        if (!shipment.options().saturdayDelivery()) {
            return subtotal;
        }
        if (service == ServiceLevel.EXPRESS || service == ServiceLevel.PRIORITY_OVERNIGHT) {
            return subtotal.add(SATURDAY_SURCHARGE);
        }
        return subtotal;
    }

    private Money applyOversizeFee(Money subtotal, Shipment shipment) {
        if (shipment.requestedService() != ServiceLevel.GROUND) {
            return subtotal;
        }
        if (!shipment.pkg().isOversize()) {
            return subtotal;
        }
        return subtotal.add(OVERSIZE_FEE);
    }

    private Money applyDangerousGoodsSurcharge(Money subtotal, Shipment shipment) {
        if (!shipment.dangerousGoods()) {
            return subtotal;
        }
        if (shipment.requestedService() != ServiceLevel.EXPRESS) {
            return subtotal;
        }
        return subtotal.add(DG_SURCHARGE);
    }
}
