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
 *   java -jar online-bookstore.jar full
 */
public class DemoLauncher {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar online-bookstore.jar [search|cart|recs|history|full]");
            return;
        }

        String demo = args[0].toLowerCase();

        try {
            switch (demo) {
                case "search":
                    System.out.println("Running Book Search Demo (Day 4)...");
                    com.bookstore.Main.main(new String[]{});
                    break;

                case "cart":
                    System.out.println("Running Cart & Checkout Demo (Day 5)...");
                    CartDemo.main(new String[]{});
                    break;

                case "recs":
                case "recommendations":
                    System.out.println("Running Recommendations Demo (Day 6)...");
                    RecommendationsDemo.main(new String[]{});
                    break;

                case "history":
                    System.out.println("Running Browsing History Demo (Day 7)...");
                    BrowsingHistoryDemo.main(new String[]{});
                    break;

                case "full":
                    System.out.println("Running Full End-to-End Demo (All Modules)...");
                    runFullDemo();
                    break;

                default:
                    System.out.println("Unknown demo: " + demo);
                    System.out.println("Valid options: search, cart, recs, history, full");
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.err.println("Demo interrupted.");
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void runFullDemo() throws InterruptedException {
        try {
            System.out.println("\n===Book Search (Day 4) ===");
            com.bookstore.Main.main(new String[]{});

            System.out.println("\n===Cart & Checkout (Day 5) ===");
            CartDemo.main(new String[]{});

            System.out.println("\n===Recommendations (Day 6) ===");
            RecommendationsDemo.main(new String[]{});

            System.out.println("\n===Browsing History (Day 7) ===");
            BrowsingHistoryDemo.main(new String[]{});

            System.out.println("\nFull demo completed successfully!");
        } catch (InterruptedException ie) {
            throw ie; // let main() handle this cleanly
        } catch (Exception e) {
            System.err.println("Error while running full demo: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
