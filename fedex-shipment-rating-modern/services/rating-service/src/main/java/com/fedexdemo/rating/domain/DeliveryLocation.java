package com.fedexdemo.rating.domain;

public record DeliveryLocation(String postalCode, LocationType type, String region) {

    public enum LocationType {
        RESIDENTIAL('R'),
        COMMERCIAL('C');

        private final char legacyFlag;

        LocationType(char legacyFlag) {
            this.legacyFlag = legacyFlag;
        }

        public char legacyFlag() {
            return legacyFlag;
        }

        public static LocationType fromLegacyFlag(char flag) {
            for (LocationType type : values()) {
                if (type.legacyFlag == flag) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown dest type: " + flag);
        }
    }

    public boolean isResidential() {
        return type == LocationType.RESIDENTIAL;
    }

    public boolean isInternational() {
        return !"US".equals(region);
    }

    public String zipPrefix() {
        if (isInternational()) {
            return "000";
        }
        return postalCode.substring(0, Math.min(3, postalCode.length()));
    }
}
