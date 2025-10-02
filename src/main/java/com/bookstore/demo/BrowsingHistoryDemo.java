package com.bookstore.demo;

import com.bookstore.*;

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
            // seed same as Day 4/5 demo — keep IDs predictable if you prefer
            bookService.saveBook(new Book(java.util.UUID.randomUUID().toString(), "Clean Code", "Robert C. Martin", "Programming", new java.math.BigDecimal("35.50"), 10));
            bookService.saveBook(new Book(java.util.UUID.randomUUID().toString(), "Design Patterns", "Erich Gamma, et al.", "Programming", new java.math.BigDecimal("42.00"), 5));
            bookService.saveBook(new Book(java.util.UUID.randomUUID().toString(), "Effective Java", "Joshua Bloch", "Programming", new java.math.BigDecimal("45.00"), 8));
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
