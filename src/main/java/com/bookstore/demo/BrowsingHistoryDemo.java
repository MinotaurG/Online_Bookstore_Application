package com.bookstore.demo;

import com.bookstore.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Demo for Day 7: Browsing history.
 * - Uses existing BookService to retrieve books.
 * - Simulates a user viewing books and prints recent history.
 */
public class BrowsingHistoryDemo {
    public static void main(String[] args) {
        BookService bookService = new BookService();
        BrowsingHistoryService historyService = new BrowsingHistoryService(10);

        // pick a demo user id
        String user = "demoUser";

        // seed books if none exist (idempotent pattern similar to other demos)
        if (bookService.listAllBooks().isEmpty()) {
            // Use deterministic ids (title + author) to avoid duplicates on reseed
            String id1 = DeterministicId.forBook("Clean Code", "Robert C. Martin");
            String id2 = DeterministicId.forBook("Design Patterns", "Erich Gamma, et al.");
            String id3 = DeterministicId.forBook("Effective Java", "Joshua Bloch");

            // Use upsert by title to ensure idempotent seeding (won't create duplicates)
            bookService.saveOrUpdateBookByTitle(new Book(id1, "Clean Code", "Robert C. Martin", "Programming",
                    new BigDecimal("35.50"), 10));
            bookService.saveOrUpdateBookByTitle(new Book(id2, "Design Patterns", "Erich Gamma, et al.", "Programming",
                    new BigDecimal("42.00"), 5));
            bookService.saveOrUpdateBookByTitle(new Book(id3, "Effective Java", "Joshua Bloch", "Programming",
                    new BigDecimal("45.00"), 8));

            System.out.println("Seeded demo books for browsing demo.");
        }

        // retrieve some books
        List<Book> books = bookService.listAllBooks();
        if (books.size() < 3) {
            System.out.println("Not enough books seeded for browsing demo.");
            bookService.close();
            return;
        }

        Book b1 = books.get(0);
        Book b2 = books.get(1);
        Book b3 = books.get(2);

        // simulate views
        historyService.view(user, b1);
        historyService.view(user, b2);
        historyService.view(user, b3);

        // view b2 again (should move to front)
        historyService.view(user, b2);

        // print recent
        System.out.println("\nRecently browsed by " + user + ":");
        historyService.getRecent(user).forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthor()));

        // cleanup
        bookService.close();
    }
}
