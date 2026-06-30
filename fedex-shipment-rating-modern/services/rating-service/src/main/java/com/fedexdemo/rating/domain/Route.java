package com.fedexdemo.rating.domain;

public record Route(String originZip, DeliveryLocation destination) {

    public String originPrefix() {
        return originZip.substring(0, Math.min(3, originZip.length()));
    }
}
