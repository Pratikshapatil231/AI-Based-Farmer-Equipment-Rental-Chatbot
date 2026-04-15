package com.farmbot.service;

import com.farmbot.model.Equipment;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationEngine {
    public List<Equipment> recommendTop(List<Equipment> allEquipment, int topN) {
        return allEquipment.stream()
                .filter(Equipment::isAvailable)
                .sorted(Comparator.comparingDouble(this::score).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    private double score(Equipment eq) {
        double availability = eq.isAvailable() ? 1.0 : 0.0;
        double normalizedRating = eq.getRating() / 5.0;
        double normalizedDistance = Math.max(0, 1 - (eq.getDistanceKm() / 25.0));
        double normalizedPrice = Math.max(0, 1 - (eq.getPricePerDay() / 6000.0));

        return (0.35 * availability)
                + (0.30 * normalizedRating)
                + (0.20 * normalizedDistance)
                + (0.15 * normalizedPrice);
    }
}
