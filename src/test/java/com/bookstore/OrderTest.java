package com.bookstore;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testConstructor() {
        Map<String, CartItem> items = new HashMap<>();
        items.put("b1", new CartItem("b1", "Book1", new BigDecimal("25.00"), 2));

        Order order = new Order("order123", "user1", items, new BigDecimal("50.00"));

        assertEquals("order123", order.getOrderId());
        assertEquals("user1", order.getUsername());
        assertEquals(new BigDecimal("50.00"), order.getTotal());
        assertNotNull(order.getCreatedAt());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void testConstructor_throwsExceptionForNullOrderId() {
        Map<String, CartItem> items = new HashMap<>();

        assertThrows(NullPointerException.class, () -> {
            new Order(null, "user1", items, BigDecimal.TEN);
        });
    }

    @Test
    void testConstructor_allowsNullUsername() {
        Map<String, CartItem> items = new HashMap<>();
        Order order = new Order("order123", null, items, BigDecimal.TEN);

        assertNull(order.getUsername());
    }

    @Test
    void testGetItems_returnsUnmodifiableMap() {
        Map<String, CartItem> items = new HashMap<>();
        items.put("b1", new CartItem("b1", "Book1", BigDecimal.TEN, 1));

        Order order = new Order("order123", "user1", items, BigDecimal.TEN);
        Map<String, CartItem> retrievedItems = order.getItems();

        assertThrows(UnsupportedOperationException.class, () -> {
            retrievedItems.put("b2", new CartItem("b2", "Book2", BigDecimal.ONE, 1));
        });
    }

    @Test
    void testGetItems_returnsIndependentCopy() {
        Map<String, CartItem> items = new HashMap<>();
        CartItem item = new CartItem("b1", "Book1", BigDecimal.TEN, 1);
        items.put("b1", item);

        Order order = new Order("order123", "user1", items, BigDecimal.TEN);

        // Modify original map
        items.put("b2", new CartItem("b2", "Book2", BigDecimal.ONE, 1));

        // Order should not be affected
        assertEquals(1, order.getItems().size());
    }

    @Test
    void testToString_containsOrderDetails() {
        Map<String, CartItem> items = new HashMap<>();
        items.put("b1", new CartItem("b1", "Book1", new BigDecimal("25.00"), 2));

        Order order = new Order("order123", "user1", items, new BigDecimal("50.00"));

        String str = order.toString();
        assertNotNull(str);
        assertTrue(str.contains("order123"));
        assertTrue(str.contains("user1"));
        assertTrue(str.contains("50.00"));
    }

    @Test
    void testToString_handlesEmptyItems() {
        Map<String, CartItem> items = new HashMap<>();
        Order order = new Order("order123", "user1", items, BigDecimal.ZERO);

        assertDoesNotThrow(() -> order.toString());
    }

    @Test
    void testGetCreatedAt_isNotNull() {
        Map<String, CartItem> items = new HashMap<>();
        Order order = new Order("order123", "user1", items, BigDecimal.TEN);

        assertNotNull(order.getCreatedAt());
    }

    @Test
    void testGetCreatedAt_isRecent() throws InterruptedException {
        Map<String, CartItem> items = new HashMap<>();
        long before = System.currentTimeMillis();
        Thread.sleep(10); // small delay
        Order order = new Order("order123", "user1", items, BigDecimal.TEN);
        Thread.sleep(10);
        long after = System.currentTimeMillis();

        long createdMs = order.getCreatedAt().toEpochMilli();
        assertTrue(createdMs > before);
        assertTrue(createdMs < after);
    }
}