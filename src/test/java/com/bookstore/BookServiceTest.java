package com.bookstore;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BookServiceTest {
    private static BookService bookService;

    @BeforeAll
    static void setup() {
        bookService = new BookService();
    }

    @AfterAll
    static void cleanup() {
        bookService.close();
    }

    @Test
    void testSaveAndFetchBook() {
        Book b = new Book(UUID.randomUUID().toString(), "Test Driven Development", "Kent Beck",
                "Programming", new BigDecimal("29.99"), 3);
        bookService.saveBook(b);

        Book fetched = bookService.getBookById(b.getId());
        assertNotNull(fetched);
        assertEquals("Test Driven Development", fetched.getTitle());
    }

    @Test
    void testQueryByTitle() {
        Book b = new Book(UUID.randomUUID().toString(), "Domain-Driven Design", "Eric Evans",
                "Architecture", new BigDecimal("55.00"), 5);
        bookService.saveBook(b);

        List<Book> results = bookService.findByTitle("Domain-Driven Design");
        assertFalse(results.isEmpty());
        assertEquals("Eric Evans", results.get(0).getAuthor());
    }
}
