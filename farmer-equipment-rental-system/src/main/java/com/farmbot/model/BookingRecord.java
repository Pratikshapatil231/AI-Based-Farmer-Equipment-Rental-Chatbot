package com.farmbot.model;

public class BookingRecord {
    private final String bookingId;
    private final String farmerName;
    private final String farmerMobile;
    private final String equipmentName;
    private final int days;
    private final double totalAmount;
    private final String ownerName;
    private final String ownerMobile;
    private final String status;

    public BookingRecord(String bookingId, String farmerName, String farmerMobile, String equipmentName, int days,
                         double totalAmount, String ownerName, String ownerMobile, String status) {
        this.bookingId = bookingId;
        this.farmerName = farmerName;
        this.farmerMobile = farmerMobile;
        this.equipmentName = equipmentName;
        this.days = days;
        this.totalAmount = totalAmount;
        this.ownerName = ownerName;
        this.ownerMobile = ownerMobile;
        this.status = status;
    }

    public String toCsvRow() {
        return String.join(",",
                bookingId, farmerName, farmerMobile, equipmentName, String.valueOf(days),
                String.valueOf(totalAmount), ownerName, ownerMobile, status);
    }
}
