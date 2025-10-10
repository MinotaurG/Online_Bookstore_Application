package com.bookstore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Small utility to create deterministic ids based on title+author (stable across runs).
 * Use: DeterministicId.forBook("Clean Code", "Robert C. Martin")
 */
public class DeterministicId {
    public static String forBook(String title, String author) {
        String base = (title == null ? "" : title.trim()) + "|" + (author == null ? "" : author.trim());
        return "b-" + sha1Hex(base).substring(0, 12); // short but stable
    }

    private static String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            Formatter f = new Formatter();
            for (byte b : bytes) f.format("%02x", b);
            String result = f.toString();
            f.close();
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}