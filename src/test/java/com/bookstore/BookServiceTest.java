package com.bookstore;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Deterministic BookService tests:
 * - uses deterministic ids (DeterministicId.forBook) so records can be deleted reliably
 * - cleans up test data after each test
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookServiceTest {
    private BookService bookService;

    @BeforeAll
    void setup() {
        bookService = new BookService();
    }

    @AfterAll
    void teardown() {
        if (bookService != null) bookService.close();
    }

    @AfterEach
    void cleanupEach() {
        // Optional: remove known test titles if they exist
        // This is defensive cleanup; keep it small and safe for test environment
        bookService.findByTitle("Test Driven Development").forEach(b -> bookService.deleteBook(b.getId()));
        bookService.findByTitle("Domain-Driven Design").forEach(b -> bookService.deleteBook(b.getId()));
    }

    @Test
    void testSaveAndFetchBook() {
        // deterministic id based on title+author (helper in repo)
        String id = DeterministicId.forBook("Test Driven Development", "Kent Beck");
        Book b = new Book(id, "Test Driven Development", "Kent Beck",
                "Programming", new BigDecimal("29.99"), 3);

        // upsert to avoid duplicates if test re-runs
        bookService.saveOrUpdateBookByTitle(b);

        Book fetched = bookService.getBookById(id);
        assertNotNull(fetched, "Fetched book should not be null");
        assertEquals("Test Driven Development", fetched.getTitle());

        // cleanup (redundant because of @AfterEach but explicit is clearer)
        bookService.deleteBook(id);
    }

    @Test
    void testQueryByTitle() {
        String id = DeterministicId.forBook("Domain-Driven Design", "Eric Evans");
        Book b = new Book(id, "Domain-Driven Design", "Eric Evans",
                "Architecture", new BigDecimal("55.00"), 5);

        bookService.saveOrUpdateBookByTitle(b);

        List<Book> results = bookService.findByTitle("Domain-Driven Design");
        assertFalse(results.isEmpty());
        // find the one with our deterministic id
        Book got = results.stream().filter(x -> id.equals(x.getId())).findFirst().orElse(null);
        assertNotNull(got);
        assertEquals("Eric Evans", got.getAuthor());

        // cleanup
        bookService.deleteBook(id);
    }
}
