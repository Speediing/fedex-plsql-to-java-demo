package com.fedexdemo.rating.domain;

public record PackageDimensions(double lengthIn, double widthIn, double heightIn, double weightLbs) {

    private static final double OVERSIZE_GIRTH_LIMIT = 165.0;

    public double girthInches() {
        return lengthIn + 2 * (widthIn + heightIn);
    }

    public boolean isOversize() {
        return girthInches() > OVERSIZE_GIRTH_LIMIT;
    }
}
