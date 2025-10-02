package com.bookstore;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutServiceTest {

    @Test
    void testCheckoutFailsIfInsufficientStock() {
        Book b = new Book(UUID.randomUUID().toString(), "Clean Coder", "Robert C. Martin",
                "Programming", new BigDecimal("40.00"), 1);

        BookService bookService = new BookService();
        bookService.saveBook(b);

        Cart cart = new Cart();
        cart.addBook(b, 5);

        CheckoutService checkout = new CheckoutService(new InMemoryOrderRepository(), bookService);

        assertThrows(IllegalStateException.class,
                () -> checkout.checkout("bob", cart));

        bookService.close();
    }

    @Test
    void testCheckoutSucceedsAndClearsCart() {
        Book b = new Book(UUID.randomUUID().toString(), "Refactoring", "Martin Fowler",
                "Programming", new BigDecimal("50.00"), 5);

        BookService bookService = new BookService();
        bookService.saveBook(b);

        Cart cart = new Cart();
        cart.addBook(b, 2);

        CheckoutService checkout = new CheckoutService(new InMemoryOrderRepository(), bookService);
        Order order = checkout.checkout("alice", cart);

        assertEquals(new BigDecimal("100.00"), order.getTotal());
        assertTrue(cart.isEmpty());

        bookService.close();
    }
}
