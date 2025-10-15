package com.bookstore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Generates Amazon-style ASINs (10-character alphanumeric identifiers)
 * Format: B0XXXXXXXX (starts with B0, followed by 8 alphanumeric chars)
 *
 * Why ASIN?
 * - Shorter than UUID (10 vs 36 chars)
 * - User-friendly (can be typed/shared easily)
 * - Deterministic option available (same input = same ASIN)
 */
public class AsinGenerator {

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random random = new Random();

    /**
     * Generate a random ASIN
     * Example: B012ABC3XY
     */
    public static String generateRandom() {
        StringBuilder sb = new StringBuilder("B0"); // Amazon ASINs typically start with B0

        for (int i = 0; i < 8; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return sb.toString();
    }

    /**
     * Generate deterministic ASIN from book title + author
     * Same title+author will always produce the same ASIN
     * Useful for preventing duplicates
     */
    public static String generateFromBook(String title, String author) {
        if (title == null || title.isBlank()) {
            return generateRandom();
        }

        String input = (title + "|" + (author != null ? author : "")).toLowerCase().trim();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());

            StringBuilder sb = new StringBuilder("B0");

            // Use first 8 bytes of hash to generate 8 characters
            for (int i = 0; i < 8; i++) {
                int index = Math.abs(hash[i] % CHARS.length());
                sb.append(CHARS.charAt(index));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Fallback to random if SHA-256 not available
            return generateRandom();
        }
    }

    /**
     * Validate ASIN format
     * Must be 10 chars, start with B0, and contain only alphanumerics
     */
    public static boolean isValid(String asin) {
        if (asin == null || asin.length() != 10) {
            return false;
        }

        if (!asin.startsWith("B0")) {
            return false;
        }

        for (char c : asin.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }

        return true;
    }
}