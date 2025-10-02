package com.bookstore;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CartCheckoutTest {

    @Test
    public void testCartAddAndCheckoutCreatesOrder() {
        // arrange
        Book b1 = new Book("id-1", "Clean Code", "Robert C. Martin", "Programming", new BigDecimal("35.50"), 10);
        Book b2 = new Book("id-2", "Design Patterns", "Erich Gamma, et al.", "Programming", new BigDecimal("42.00"), 5);

        Cart cart = new Cart();
        cart.addBook(b1, 1);
        cart.addBook(b2, 2);

        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        // BookService not required for this test; pass null
        CheckoutService cs = new CheckoutService(repo, null);

        // act
        Order order = cs.checkout("alice", cart);

        // assert
        assertNotNull(order);
        assertEquals("alice", order.getUsername());
        assertEquals(new BigDecimal("119.50"), order.getTotal());
        assertTrue(cart.isEmpty(), "Cart should be empty after checkout");
        assertEquals(1, repo.findAll().size());
    }
}
