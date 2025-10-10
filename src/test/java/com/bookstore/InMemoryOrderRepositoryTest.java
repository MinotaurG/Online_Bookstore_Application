package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;
    private Order order1, order2, order3;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();

        Map<String, CartItem> items1 = new HashMap<>();
        items1.put("b1", new CartItem("b1", "Book1", new BigDecimal("10"), 1));
        order1 = new Order("order1", "user1", items1, new BigDecimal("10"));

        Map<String, CartItem> items2 = new HashMap<>();
        items2.put("b2", new CartItem("b2", "Book2", new BigDecimal("20"), 2));
        order2 = new Order("order2", "user1", items2, new BigDecimal("40"));

        Map<String, CartItem> items3 = new HashMap<>();
        items3.put("b3", new CartItem("b3", "Book3", new BigDecimal("15"), 1));
        order3 = new Order("order3", "user2", items3, new BigDecimal("15"));
    }

    @Test
    void testSave_storesOrder() {
        repository.save(order1);

        Optional<Order> found = repository.findById("order1");
        assertTrue(found.isPresent());
        assertEquals(order1, found.get());
    }

    @Test
    void testSave_overwritesExistingOrder() {
        repository.save(order1);

        Map<String, CartItem> newItems = new HashMap<>();
        Order updatedOrder = new Order("order1", "user1", newItems, new BigDecimal("99"));
        repository.save(updatedOrder);

        Optional<Order> found = repository.findById("order1");
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("99"), found.get().getTotal());
    }

    @Test
    void testFindById_returnsEmptyWhenNotFound() {
        Optional<Order> found = repository.findById("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUser_returnsUserOrders() {
        repository.save(order1);
        repository.save(order2);
        repository.save(order3);

        List<Order> user1Orders = repository.findByUser("user1");
        assertEquals(2, user1Orders.size());
        assertTrue(user1Orders.contains(order1));
        assertTrue(user1Orders.contains(order2));
    }

    @Test
    void testFindByUser_returnsEmptyForNonexistentUser() {
        List<Order> orders = repository.findByUser("nonexistent");
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void testFindByUser_handlesNullUsername() {
        Map<String, CartItem> items = new HashMap<>();
        Order orderWithNullUser = new Order("order4", null, items, BigDecimal.TEN);
        repository.save(orderWithNullUser);

        List<Order> orders = repository.findByUser(null);
        assertTrue(orders.isEmpty(), "Should not match null usernames");
    }

    @Test
    void testFindAll_returnsAllOrders() {
        repository.save(order1);
        repository.save(order2);
        repository.save(order3);

        List<Order> all = repository.findAll();
        assertEquals(3, all.size());
    }

    @Test
    void testFindAll_returnsEmptyWhenNoOrders() {
        List<Order> all = repository.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void testFindAll_returnsUnmodifiableList() {
        repository.save(order1);
        List<Order> all = repository.findAll();

        assertThrows(UnsupportedOperationException.class, () -> {
            all.add(order2);
        });
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        // Test concurrent saves
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                Map<String, CartItem> items = new HashMap<>();
                Order o = new Order("order-t1-" + i, "user1", items, BigDecimal.ONE);
                repository.save(o);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                Map<String, CartItem> items = new HashMap<>();
                Order o = new Order("order-t2-" + i, "user2", items, BigDecimal.ONE);
                repository.save(o);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        List<Order> all = repository.findAll();
        assertEquals(200, all.size());
    }
}