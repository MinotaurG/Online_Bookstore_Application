package com.bookstore;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Deterministic CheckoutService tests:
 * - use deterministic ids so cleanup is reliable
 * - upsert books using saveOrUpdateBookByTitle so repeated test runs are safe
 * - delete test books at the end of each test to avoid test-data leakage
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckoutServiceTest {

    private BookService bookService;

    @BeforeAll
    void beforeAll() {
        bookService = new BookService();
    }

    @AfterAll
    void afterAll() {
        if (bookService != null) bookService.close();
    }

    @AfterEach
    void cleanupEach() {
        // Remove known test titles if present
        bookService.findByTitle("Clean Coder").forEach(b -> bookService.deleteBook(b.getId()));
        bookService.findByTitle("Refactoring").forEach(b -> bookService.deleteBook(b.getId()));
    }

    @Test
    void testCheckoutFailsIfInsufficientStock() {
        String id = DeterministicId.forBook("Clean Coder", "Robert C. Martin");
        Book b = new Book(id, "Clean Coder", "Robert C. Martin",
                "Programming", new BigDecimal("40.00"), 1);

        // upsert to avoid duplicates
        bookService.saveOrUpdateBookByTitle(b);

        Cart cart = new Cart();
        cart.addBook(b, 5);

        CheckoutService checkout = new CheckoutService(new InMemoryOrderRepository(), bookService);

        assertThrows(IllegalStateException.class,
                () -> checkout.checkout("bob", cart));

        // explicit cleanup (also handled by @AfterEach)
        bookService.findByTitle("Clean Coder").forEach(x -> bookService.deleteBook(x.getId()));
    }

    @Test
    void testCheckoutSucceedsAndClearsCart() {
        String id = DeterministicId.forBook("Refactoring", "Martin Fowler");
        Book b = new Book(id, "Refactoring", "Martin Fowler",
                "Programming", new BigDecimal("50.00"), 5);

        bookService.saveOrUpdateBookByTitle(b);

        Cart cart = new Cart();
        cart.addBook(b, 2);

        CheckoutService checkout = new CheckoutService(new InMemoryOrderRepository(), bookService);
        Order order = checkout.checkout("alice", cart);

        assertEquals(new BigDecimal("100.00"), order.getTotal());
        assertTrue(cart.isEmpty());

        // explicit cleanup
        bookService.findByTitle("Refactoring").forEach(x -> bookService.deleteBook(x.getId()));
    }
}
