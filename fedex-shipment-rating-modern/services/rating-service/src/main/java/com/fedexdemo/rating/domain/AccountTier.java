package com.fedexdemo.rating.domain;

public enum AccountTier {
    STANDARD(0.0),
    SILVER(0.05),
    GOLD(0.10);

    private final double discountRate;

    AccountTier(double discountRate) {
        this.discountRate = discountRate;
    }

    public double discountRate() {
        return discountRate;
    }
}
