package com.farmbot.service;

import com.farmbot.model.BookingRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BookingDatabaseService {
    private static final Path DB_PATH = Paths.get("data", "bookings.csv");
    private static final int COL_FARMER_NAME = 1;
    private static final int COL_FARMER_MOBILE = 2;
    private static final int COL_EQUIPMENT_NAME = 3;

    public BookingDatabaseService() {
        ensureFile();
    }

    public void saveBooking(BookingRecord bookingRecord) {
        try {
            Files.writeString(
                    DB_PATH,
                    bookingRecord.toCsvRow() + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to save booking in local database file.", e);
        }
    }

    public Optional<String> findMostUsedEquipmentForFarmer(String farmerMobile, String farmerName) {
        try {
            if (!Files.exists(DB_PATH)) {
                return Optional.empty();
            }

            String normalizedMobile = farmerMobile == null ? "" : farmerMobile.trim();
            String normalizedName = farmerName == null ? "" : farmerName.trim().toLowerCase();
            Map<String, Integer> usageCount = new HashMap<>();

            for (String line : Files.readAllLines(DB_PATH, StandardCharsets.UTF_8)) {
                if (line == null || line.isBlank() || line.startsWith("bookingId,")) {
                    continue;
                }
                String[] cols = line.split(",", -1);
                if (cols.length <= COL_EQUIPMENT_NAME) {
                    continue;
                }

                String rowName = cols[COL_FARMER_NAME].trim().toLowerCase();
                String rowMobile = cols[COL_FARMER_MOBILE].trim();

                boolean sameFarmer = !normalizedMobile.isEmpty() && rowMobile.equals(normalizedMobile);
                if (!sameFarmer && !normalizedName.isEmpty()) {
                    sameFarmer = rowName.equals(normalizedName);
                }
                if (!sameFarmer) {
                    continue;
                }

                String equipmentName = cols[COL_EQUIPMENT_NAME].trim();
                if (!equipmentName.isEmpty()) {
                    usageCount.merge(equipmentName, 1, Integer::sum);
                }
            }

            return usageCount.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read booking history from local database file.", e);
        }
    }

    private void ensureFile() {
        try {
            if (!Files.exists(DB_PATH.getParent())) {
                Files.createDirectories(DB_PATH.getParent());
            }
            if (!Files.exists(DB_PATH)) {
                Files.writeString(
                        DB_PATH,
                        "bookingId,farmerName,farmerMobile,equipmentName,days,totalAmount,ownerName,ownerMobile,status"
                                + System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize local database file.", e);
        }
    }
}
