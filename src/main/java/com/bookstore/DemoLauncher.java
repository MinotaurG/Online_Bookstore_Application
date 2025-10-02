package com.bookstore;

import com.bookstore.demo.CartDemo;
import com.bookstore.demo.RecommendationsDemo;
import com.bookstore.demo.BrowsingHistoryDemo;

/**
 * Single entrypoint for running demos from the packaged JAR.
 * Usage:
 *   java -jar online-bookstore.jar search
 *   java -jar online-bookstore.jar cart
 *   java -jar online-bookstore.jar recs
 *   java -jar online-bookstore.jar history
 */
public class DemoLauncher {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("📚 Usage: java -jar online-bookstore.jar [search|cart|recs|history]");
            return;
        }

        String demo = args[0].toLowerCase();

        try {
            switch (demo) {
                case "search":
                    com.bookstore.Main.main(new String[]{});
                    break;
                case "cart":
                    CartDemo.main(new String[]{});
                    break;
                case "recs":
                case "recommendations":
                    RecommendationsDemo.main(new String[]{});
                    break;
                case "history":
                    BrowsingHistoryDemo.main(new String[]{});
                    break;
                default:
                    System.out.println("Unknown demo: " + demo);
                    System.out.println("Valid options: search, cart, recs, history");
            }
        } catch (Exception e) {
            // Catch all runtime and checked exceptions from demo entrypoints and log a friendly message.
            // For InterruptedException in particular, restore the interrupt status.
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                System.err.println("Demo interrupted.");
            } else {
                System.err.println("Demo failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace(System.err);
            }
            // Exit non-zero to indicate failure when running in scripts/CI
            System.exit(1);
        }
    }
}