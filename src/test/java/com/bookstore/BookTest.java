package com.bookstore;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testNoArgsConstructor() {
        Book book = new Book();
        assertNotNull(book);
        assertNull(book.getId());
        assertNull(book.getTitle());
    }

    @Test
    void testAllArgsConstructor() {
        BigDecimal price = new BigDecimal("29.99");
        Book book = new Book("id123", "Clean Code", "Robert Martin",
                "Programming", price, 10);

        assertEquals("id123", book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("Programming", book.getGenre());
        assertEquals(price, book.getPrice());
        assertEquals(10, book.getStockQuantity());
    }

    @Test
    void testSettersAndGetters() {
        Book book = new Book();
        BigDecimal price = new BigDecimal("39.99");

        book.setId("abc");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Fiction");
        book.setPrice(price);
        book.setStockQuantity(5);

        assertEquals("abc", book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("Fiction", book.getGenre());
        assertEquals(price, book.getPrice());
        assertEquals(5, book.getStockQuantity());
    }

    @Test
    void testToString() {
        BigDecimal price = new BigDecimal("29.99");
        Book book = new Book("id1", "Clean Code", "Robert Martin",
                "Programming", price, 10);

        String str = book.toString();
        assertNotNull(str);
        assertTrue(str.contains("Clean Code"));
        assertTrue(str.contains("Robert Martin"));
        assertTrue(str.contains("Programming"));
        assertTrue(str.contains("29.99"));
        assertTrue(str.contains("10"));
    }

    @Test
    void testToString_handlesNullValues() {
        Book book = new Book();
        assertDoesNotThrow(() -> book.toString());
    }
}