package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;
    private Book book1, book2;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        book1 = new Book("1", "Book1", "Author1", "Genre1", new BigDecimal("10.00"), 5);
        book2 = new Book("2", "Book2", "Author2", "Genre2", new BigDecimal("20.00"), 3);
    }

    @Test
    void testAddBook_addsNewBook() {
        cart.addBook(book1, 2);

        Map<String, CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertTrue(items.containsKey("1"));
    }

    @Test
    void testAddBook_increasesQuantityForExistingBook() {
        cart.addBook(book1, 2);
        cart.addBook(book1, 3);

        Optional<CartItem> item = cart.getItem("1");
        assertTrue(item.isPresent());
        assertEquals(5, item.get().getQuantity());
    }

    @Test
    void testAddBook_ignoresNullBook() {
        cart.addBook(null, 1);
        assertTrue(cart.isEmpty());
    }

    @Test
    void testAddBook_ignoresZeroQuantity() {
        cart.addBook(book1, 0);
        assertTrue(cart.isEmpty());
    }

    @Test
    void testAddBook_ignoresNegativeQuantity() {
        cart.addBook(book1, -1);
        assertTrue(cart.isEmpty());
    }

    @Test
    void testUpdateQuantity_updatesExistingItem() {
        cart.addBook(book1, 2);
        cart.updateQuantity("1", 5);

        Optional<CartItem> item = cart.getItem("1");
        assertTrue(item.isPresent());
        assertEquals(5, item.get().getQuantity());
    }

    @Test
    void testUpdateQuantity_removesItemWhenZero() {
        cart.addBook(book1, 2);
        cart.updateQuantity("1", 0);

        assertTrue(cart.isEmpty());
    }

    @Test
    void testUpdateQuantity_removesItemWhenNegative() {
        cart.addBook(book1, 2);
        cart.updateQuantity("1", -1);

        assertTrue(cart.isEmpty());
    }

    @Test
    void testUpdateQuantity_ignoresNonexistentItem() {
        cart.updateQuantity("999", 5);
        assertTrue(cart.isEmpty());
    }

    @Test
    void testRemoveBook_removesItem() {
        cart.addBook(book1, 2);
        cart.addBook(book2, 1);

        cart.removeBook("1");

        assertEquals(1, cart.getItems().size());
        assertFalse(cart.getItems().containsKey("1"));
    }

    @Test
    void testRemoveBook_ignoresNonexistentItem() {
        cart.addBook(book1, 2);
        cart.removeBook("999");

        assertEquals(1, cart.getItems().size());
    }

    @Test
    void testClear_removesAllItems() {
        cart.addBook(book1, 2);
        cart.addBook(book2, 3);

        cart.clear();

        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void testCalculateTotal_sumsItemPrices() {
        cart.addBook(book1, 2); // 10.00 * 2 = 20.00
        cart.addBook(book2, 3); // 20.00 * 3 = 60.00

        BigDecimal total = cart.calculateTotal();
        assertEquals(new BigDecimal("80.00"), total);
    }

    @Test
    void testCalculateTotal_returnsZeroForEmptyCart() {
        BigDecimal total = cart.calculateTotal();
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void testIsEmpty_returnsTrueForNewCart() {
        assertTrue(cart.isEmpty());
    }

    @Test
    void testIsEmpty_returnsFalseWhenItemsPresent() {
        cart.addBook(book1, 1);
        assertFalse(cart.isEmpty());
    }

    @Test
    void testGetItem_returnsItemWhenPresent() {
        cart.addBook(book1, 2);

        Optional<CartItem> item = cart.getItem("1");
        assertTrue(item.isPresent());
        assertEquals("1", item.get().getBookId());
    }

    @Test
    void testGetItem_returnsEmptyWhenNotPresent() {
        Optional<CartItem> item = cart.getItem("999");
        assertFalse(item.isPresent());
    }

    @Test
    void testGetItems_returnsUnmodifiableMap() {
        cart.addBook(book1, 1);
        Map<String, CartItem> items = cart.getItems();

        assertThrows(UnsupportedOperationException.class, () -> {
            items.put("999", new CartItem("999", "Test", BigDecimal.ONE, 1));
        });
    }
}