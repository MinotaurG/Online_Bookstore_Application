package com.bookstore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple thread-safe in-memory repository.
 */
public class InMemoryOrderRepository implements OrderRepository {
    private final ConcurrentHashMap<String, Order> store = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.getOrderId(), order);
    }

    @Override
    public java.util.Optional<Order> findById(String orderId) {
        return java.util.Optional.ofNullable(store.get(orderId));
    }

    @Override
    public List<Order> findByUser(String username) {
        List<Order> out = new ArrayList<>();
        for (Order o : store.values()) {
            if (o.getUsername() != null && o.getUsername().equals(username)) out.add(o);
        }
        return out;
    }

    @Override
    public List<Order> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }
}
