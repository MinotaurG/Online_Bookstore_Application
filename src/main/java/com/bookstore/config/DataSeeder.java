package com.bookstore.config;

import com.bookstore.Book;
import com.bookstore.spring.BookServiceAdapter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds deterministic demo books at application startup (idempotent).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BookServiceAdapter adapter;

    public DataSeeder(BookServiceAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run(String... args) throws Exception {
        // If no books at all, seed two canonical books with deterministic ids.
        if (adapter.listAll().isEmpty()) {
            Book b1 = new Book("b-clean-code", "Clean Code", "Robert C. Martin", "Programming", new BigDecimal("35.50"), 10);
            Book b2 = new Book("b-design-patterns", "Design Patterns", "Erich Gamma, et al.", "Programming", new BigDecimal("42.00"), 5);

            adapter.save(b1);
            adapter.save(b2);

            System.out.println("DataSeeder: seeded demo books.");
        } else {
            System.out.println("DataSeeder: books exist; seeding skipped.");
        }
    }
}