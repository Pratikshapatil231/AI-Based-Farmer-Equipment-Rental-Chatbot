package com.farmbot.service;

import com.farmbot.model.Intent;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class IntentClassifier {
    private final Map<Intent, String[]> intentKeywords = new LinkedHashMap<>();

    public IntentClassifier() {
        intentKeywords.put(Intent.GREETING, new String[]{
                "hello", "hi", "hey", "namaste", "namaskar", "good morning", "good afternoon", "good evening"
        });
        intentKeywords.put(Intent.HELP, new String[]{
                "help", "madad", "help me",
                "what can you", "what do you do", "how do i", "how to use", "commands", "options", "menu",
                "guide", "assist", "support", "batao"
        });
        intentKeywords.put(Intent.LIST_EQUIPMENT, new String[]{
                "list", "list all", "equipment", "available", "availability",
                "show me", "show all", "catalog", "inventory", "machinery", "machines", "machine",
                "tractor", "harvester", "tiller", "what equipment", "which equipment"
        });
        intentKeywords.put(Intent.RENT_REQUEST, new String[]{
                "rent", "rental", "hire", "lease",
                "want to rent", "need to rent", "book equipment", "rent a", "hire a",
                "recommend", "recommendation", "recommendations", "suggestion", "suggestions", "suggest",
                "best equipment", "what to rent", "which to rent", "booking recommendation"
        });
        intentKeywords.put(Intent.PRICE_QUERY, new String[]{
                "price", "rate", "cost", "charge", "fee", "pricing",
                "how much", "how many rupee", "kitna", "kitne",
                "₹", " rupee", " rs", "inr", "expensive", "cheap", "affordable"
        });
        intentKeywords.put(Intent.EXIT, new String[]{
                "exit", "quit", "bye", "goodbye", "see you", "stop chat", "thanks", "thank you"
        });
    }

    public Intent classify(String message) {
        String normalized = message == null ? "" : message.toLowerCase(Locale.ROOT).trim();

        int bestScore = 0;
        Intent bestIntent = Intent.UNKNOWN;

        for (Map.Entry<Intent, String[]> entry : intentKeywords.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (normalized.contains(keyword.toLowerCase(Locale.ROOT))) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestIntent = entry.getKey();
            }
        }

        if (bestScore > 0) {
            return refineRecommendationIntent(normalized, bestIntent);
        }
        Intent guessed = guessIntentFromLoosePhrases(normalized);
        return refineRecommendationIntent(normalized, guessed);
    }

    /**
     * If the user clearly asks for suggestions/recommendations, use the rental recommendation
     * path instead of a plain equipment list (LIST is checked before RENT in the map and often ties).
     */
    private Intent refineRecommendationIntent(String normalized, Intent intent) {
        if (asksForRecommendation(normalized)) {
            if (intent == Intent.LIST_EQUIPMENT || intent == Intent.HELP || intent == Intent.UNKNOWN) {
                return Intent.RENT_REQUEST;
            }
        }
        return intent;
    }

    private static boolean asksForRecommendation(String n) {
        return n.contains("recommend") || n.contains("suggest") || n.contains("best equipment")
                || n.contains("what to rent") || n.contains("which to rent") || n.contains("booking recommendation");
    }

    /** Catches common questions that do not match a keyword substring exactly. */
    private Intent guessIntentFromLoosePhrases(String n) {
        if (n.isEmpty()) {
            return Intent.UNKNOWN;
        }
        if (asksForRecommendation(n)) {
            return Intent.RENT_REQUEST;
        }
        if (n.contains("?") && (n.contains("what") || n.contains("how") || n.contains("which"))) {
            return Intent.HELP;
        }
        if (n.contains("how much") || n.contains("kitna") || n.contains("kitne")) {
            return Intent.PRICE_QUERY;
        }
        if (n.contains("thank")) {
            return Intent.EXIT;
        }
        if (n.contains("tractor") || n.contains("harvest") || n.contains("farm")) {
            return Intent.RENT_REQUEST;
        }
        return Intent.UNKNOWN;
    }
}
