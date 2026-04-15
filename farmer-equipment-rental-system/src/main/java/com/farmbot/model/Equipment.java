package com.farmbot.model;

public class Equipment {
    private final String name;
    private final String category;
    private final double pricePerDay;
    private final boolean available;
    private final double distanceKm;
    private final double rating;
    private final String ownerName;
    private final String ownerMobile;

    public Equipment(String name, String category, double pricePerDay, boolean available, double distanceKm, double rating,
                     String ownerName, String ownerMobile) {
        this.name = name;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.available = available;
        this.distanceKm = distanceKm;
        this.rating = rating;
        this.ownerName = ownerName;
        this.ownerMobile = ownerMobile;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public boolean isAvailable() {
        return available;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getRating() {
        return rating;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }
}
