package com.farmbot.service;



import com.farmbot.model.Equipment;

import com.farmbot.model.Intent;

import com.farmbot.model.BookingRecord;



import java.util.Locale;

import java.util.List;

import java.util.Optional;

import java.util.UUID;

import java.util.regex.Pattern;



public class ChatbotService {

    private static final Pattern WHOLE_WORD_BOOK = Pattern.compile("(?iU)\\bbook\\b");



    private enum BookingStep {

        NONE,

        ASK_NAME,

        ASK_MOBILE,

        ASK_EQUIPMENT,

        ASK_DAYS,

        ASK_CONFIRM

    }



    private final DataStore dataStore = new DataStore();

    private final IntentClassifier intentClassifier = new IntentClassifier();

    private final RecommendationEngine recommendationEngine = new RecommendationEngine();

    private final BookingDatabaseService bookingDatabaseService = new BookingDatabaseService();



    private BookingStep bookingStep = BookingStep.NONE;

    private String bookingFarmerName;

    private String bookingFarmerMobile;

    private Equipment bookingEquipment;
    private Equipment bookingRecommendedEquipment;

    private int bookingDays;



    public String reply(String userInput) {

        if (userInput == null || userInput.isBlank()) {

            return "Please type something. Use 'help' for options.";

        }



        if (bookingStep != BookingStep.NONE) {

            return continueBookingFlow(userInput);

        }



        String normalized = userInput.toLowerCase(Locale.ROOT);

        if (wantsFullBookingWizard(normalized)) {

            bookingStep = BookingStep.ASK_NAME;

            return bookingStartMessage();

        }



        Intent intent = intentClassifier.classify(userInput);

        List<Equipment> allEquipment = dataStore.equipmentList();



        switch (intent) {

            case GREETING:

                return greeting();

            case HELP:

                return help();

            case LIST_EQUIPMENT:

                return listEquipment(allEquipment);

            case RENT_REQUEST:

                return recommend(allEquipment) + System.lineSeparator() + bookingHint();

            case PRICE_QUERY:

                return prices(allEquipment);

            case EXIT:

                return exitMessage();

            default:

                return fallback() + System.lineSeparator() + bookingHint();

        }

    }



    private String greeting() {

        return "Hello! Welcome to AI Based Farmer Equipment Rental System. Ask me about tractors, harvesters, tillers, and prices.";

    }



    private String help() {

        return "You can ask: 1) available equipment 2) rental price 3) booking recommendation 4) type 'book now' for complete booking 5) exit";

    }



    private String listEquipment(List<Equipment> allEquipment) {

        StringBuilder sb = new StringBuilder();

        sb.append("Available equipment list:\n");



        allEquipment.forEach(eq -> sb.append("- ")

                .append(eq.getName())

                .append(" | ")

                .append(eq.getCategory())

                .append(" | ₹")

                .append(eq.getPricePerDay())

                .append("/day | ")

                .append(eq.isAvailable() ? "Available" : "Not Available")

                .append(" | Owner: ")

                .append(eq.getOwnerName())

                .append("\n"));

        return sb.toString();

    }



    private String prices(List<Equipment> allEquipment) {

        StringBuilder sb = new StringBuilder();

        sb.append("Rental pricing:\n");

        allEquipment.forEach(eq -> sb.append("- ").append(eq.getName()).append(": ₹").append(eq.getPricePerDay()).append(" per day\n"));

        return sb.toString();

    }



    private String recommend(List<Equipment> allEquipment) {

        List<Equipment> top = recommendationEngine.recommendTop(allEquipment, 3);

        if (top.isEmpty()) {
            return "No available equipment right now, so there are no recommendations. Try 'list' to see the full catalog.";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("AI/ML based recommendations (Top 3):\n");

        top.forEach(eq -> sb.append("- ")

                .append(eq.getName())

                .append(" | ₹")

                .append(eq.getPricePerDay())

                .append("/day | ")

                .append(eq.getDistanceKm())

                .append(" km | rating ")

                .append(eq.getRating())

                .append("\n"));

        return sb.toString();

    }



    private String fallback() {

        return "Sorry, I did not understand. Try: help, list equipment, price, recommendation, rent, or book now.";

    }



    private String exitMessage() {

        return "Thank you! Visit again.";

    }



    private String bookingHint() {

        return "Type 'book now' to complete full booking process.";

    }



    private String bookingStartMessage() {

        return "Booking started. Please enter farmer name:";

    }



    private String continueBookingFlow(String userInput) {

        String input = userInput.trim();

        switch (bookingStep) {

            case ASK_NAME:

                bookingFarmerName = input;

                bookingStep = BookingStep.ASK_MOBILE;

                return "Enter farmer mobile number (10 digits):";

            case ASK_MOBILE:

                if (!isValidMobile(input)) {

                    return "Invalid number. Please enter a 10-digit mobile number:";

                }

                bookingFarmerMobile = input;

                bookingStep = BookingStep.ASK_EQUIPMENT;
                bookingRecommendedEquipment = findRecommendedEquipmentFromHistory();
                return pastUsageRecommendationMessage() + System.lineSeparator() + equipmentNamesPrompt();

            case ASK_EQUIPMENT:

                Optional<Equipment> selected = findSelectedEquipment(input);

                if (selected.isEmpty()) {
                    if ("yes".equalsIgnoreCase(input.trim()) && bookingRecommendedEquipment != null && !bookingRecommendedEquipment.isAvailable()) {
                        return "Previously most-used equipment is currently not available. Please choose another equipment:\n"
                                + equipmentNamesPrompt();
                    }

                    return "Equipment not found. Enter correct equipment name:\n" + equipmentNamesPrompt();

                }

                bookingEquipment = selected.get();

                if (!bookingEquipment.isAvailable()) {

                    bookingStep = BookingStep.ASK_EQUIPMENT;

                    return "This equipment is not available. Please choose another one:";

                }

                bookingStep = BookingStep.ASK_DAYS;

                return "For how many days do you need it?";

            case ASK_DAYS:

                if (!isValidDays(input)) {

                    return "Please enter days between 1 and 30:";

                }

                bookingDays = Integer.parseInt(input);

                bookingStep = BookingStep.ASK_CONFIRM;

                double total = bookingDays * bookingEquipment.getPricePerDay();

                return "Booking summary:\nFarmer: " + bookingFarmerName + "\nEquipment: " + bookingEquipment.getName()

                        + "\nDays: " + bookingDays + "\nTotal: ₹" + total + "\nType 'yes' to confirm.";

            case ASK_CONFIRM:

                if (!input.equalsIgnoreCase("yes")) {

                    resetBookingFlow();

                    return "Booking cancelled. Type 'book now' to start again.";

                }

                return finalizeBooking();

            default:

                return fallback();

        }

    }



    private String finalizeBooking() {

        String bookingId = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);

        double totalAmount = bookingDays * bookingEquipment.getPricePerDay();

        BookingRecord bookingRecord = new BookingRecord(

                bookingId,

                bookingFarmerName,

                bookingFarmerMobile,

                bookingEquipment.getName(),

                bookingDays,

                totalAmount,

                bookingEquipment.getOwnerName(),

                bookingEquipment.getOwnerMobile(),

                "CONFIRMED"

        );

        bookingDatabaseService.saveBooking(bookingRecord);



        String message = "Booking successful!\nBooking ID: " + bookingId

                + "\nOwner: " + bookingEquipment.getOwnerName()

                + "\nOwner Mobile: " + bookingEquipment.getOwnerMobile()

                + "\nFarmer Mobile: " + bookingFarmerMobile

                + "\nNow farmer and owner can contact each other using mobile numbers.";

        resetBookingFlow();

        return message;

    }



    private String equipmentNamesPrompt() {

        StringBuilder sb = new StringBuilder();

        sb.append("Choose equipment name:\n");

        for (Equipment eq : dataStore.equipmentList()) {

            sb.append("- ").append(eq.getName()).append("\n");

        }

        return sb.toString();

    }

    private String pastUsageRecommendationMessage() {
        if (bookingRecommendedEquipment != null) {
            return "Based on this farmer's past bookings, most used equipment is: "
                    + bookingRecommendedEquipment.getName()
                    + ". Type 'yes' to select it, or type another equipment name.";
        }
        return "No past booking history found for this farmer. Please choose equipment:";
    }

    private Optional<Equipment> findSelectedEquipment(String input) {
        if ("yes".equalsIgnoreCase(input.trim()) && bookingRecommendedEquipment != null) {
            if (bookingRecommendedEquipment.isAvailable()) {
                return Optional.of(bookingRecommendedEquipment);
            }
            return Optional.empty();
        }
        return findEquipmentByName(input);
    }

    private Equipment findRecommendedEquipmentFromHistory() {
        Optional<String> mostUsed = bookingDatabaseService.findMostUsedEquipmentForFarmer(
                bookingFarmerMobile,
                bookingFarmerName
        );
        if (mostUsed.isEmpty()) {
            return null;
        }
        return findEquipmentByName(mostUsed.get()).orElse(null);
    }



    private Optional<Equipment> findEquipmentByName(String input) {

        String normalized = input.toLowerCase(Locale.ROOT);

        return dataStore.equipmentList().stream()

                .filter(eq -> eq.getName().toLowerCase(Locale.ROOT).contains(normalized)

                        || normalized.contains(eq.getName().toLowerCase(Locale.ROOT)))

                .findFirst();

    }



    private boolean isValidMobile(String input) {

        return input.matches("\\d{10}");

    }



    private boolean isValidDays(String input) {

        if (!input.matches("\\d+")) {

            return false;

        }

        int days = Integer.parseInt(input);

        return days >= 1 && days <= 30;

    }



    private void resetBookingFlow() {

        bookingStep = BookingStep.NONE;

        bookingFarmerName = null;

        bookingFarmerMobile = null;

        bookingEquipment = null;
        bookingRecommendedEquipment = null;

        bookingDays = 0;

    }



    /** Starts the step-by-step booking flow; avoids matching substrings like "textbook". */

    private static boolean wantsFullBookingWizard(String normalized) {

        if (normalized.contains("book now")

                || normalized.contains("start booking")

                || normalized.contains("full booking")

                || normalized.contains("complete booking")) {

            return true;

        }

        return WHOLE_WORD_BOOK.matcher(normalized).find();

    }

}


