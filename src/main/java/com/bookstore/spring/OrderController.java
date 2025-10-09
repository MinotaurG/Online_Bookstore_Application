package com.bookstore.spring;

import com.bookstore.InMemoryOrderRepository;
import com.bookstore.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Exposes orders stored in the in-memory repo (demo only) */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final InMemoryOrderRepository repo;

    public OrderController(InMemoryOrderRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Order> list() {
        return repo.findAll();
    }
}