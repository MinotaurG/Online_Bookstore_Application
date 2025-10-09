package com.bookstore;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BookService service = new BookService();

        // Deterministic IDs for seed books (same title+author -> same id)
        String id1 = UUID.nameUUIDFromBytes(("Clean Code|Robert C. Martin").getBytes(StandardCharsets.UTF_8)).toString();
        String id2 = UUID.nameUUIDFromBytes(("Design Patterns|Erich Gamma, et al.").getBytes(StandardCharsets.UTF_8)).toString();

        Book b1 = new Book(id1, "Clean Code", "Robert C. Martin",
                "Programming", new BigDecimal("35.50"), 10);
        Book b2 = new Book(id2, "Design Patterns", "Erich Gamma, et al.",
                "Programming", new BigDecimal("42.00"), 5);

// Save (plain put) — deterministic id ensures repeated runs overwrite the same record
        service.saveOrUpdateBookByTitle(b1);
        service.saveOrUpdateBookByTitle(b2);
        System.out.println("Seeded sample books (idempotent).");




        // brief pause (table creation/consistency)
        Thread.sleep(500);

        System.out.println("\nAll books:");
        List<Book> all = service.listAllBooks();
        all.forEach(System.out::println);

        System.out.println("\nExact query by title (GSI):");
        service.findByTitle("Clean Code").forEach(System.out::println);

        System.out.println("\nContains search 'Design':");
        service.searchByTitleOrAuthorContains("Design").forEach(System.out::println);

        service.close();
    }
}
