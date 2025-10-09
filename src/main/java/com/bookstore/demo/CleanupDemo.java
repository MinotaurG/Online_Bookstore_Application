package com.bookstore.demo;

import com.bookstore.Book;
import com.bookstore.BookService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cleanup duplicate books in the Books table.
 * Strategy:
 *  - group by title (case-insensitive)
 *  - for each group keep one record (choose the one with highest stockQuantity, else first)
 *  - delete the remaining duplicates by id
 *
 * Use with DynamoDB Local (inMemory) or sharedDb.
 */
public class CleanupDemo {
    public static void main(String[] args) {
        BookService svc = new BookService();
        try {
            List<Book> all = svc.listAllBooks();
            if (all.isEmpty()) {
                System.out.println("No books found. Nothing to clean.");
                return;
            }

            // group by normalized title
            Map<String, List<Book>> byTitle = all.stream()
                    .filter(b -> b.getTitle() != null)
                    .collect(Collectors.groupingBy(b -> b.getTitle().trim().toLowerCase()));

            int totalDeleted = 0;
            for (Map.Entry<String, List<Book>> e : byTitle.entrySet()) {
                List<Book> group = e.getValue();
                if (group.size() <= 1) continue;

                // choose keeper: prefer one with largest stockQuantity, then by non-null price, else first
                Book keeper = group.stream()
                        .max(Comparator.comparingInt(b -> (b.getStockQuantity() == null ? -1 : b.getStockQuantity())))
                        .orElse(group.get(0));

                System.out.println("Keeping: " + keeper.getTitle() + " (id=" + keeper.getId() + ", stock=" + keeper.getStockQuantity() + ")");

                // delete others
                for (Book b : group) {
                    if (!b.getId().equals(keeper.getId())) {
                        svc.deleteBook(b.getId());
                        totalDeleted++;
                        System.out.println("Deleted duplicate: id=" + b.getId() + " price=" + b.getPrice() + " stock=" + b.getStockQuantity());
                    }
                }
            }

            System.out.println("Cleanup complete. Total deleted: " + totalDeleted);
            System.out.println("\nRemaining books:");
            svc.listAllBooks().forEach(System.out::println);

        } finally {
            svc.close();
        }
    }
}