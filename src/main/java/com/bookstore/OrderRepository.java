package com.bookstore;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String orderId);
    List<Order> findByUser(String username);
    List<Order> findAll();
}
