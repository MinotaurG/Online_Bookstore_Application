package com.bookstore.spring;

import com.bookstore.InMemoryOrderRepository;
import com.bookstore.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final InMemoryOrderRepository repo;

    public OrderController(InMemoryOrderRepository repo) {
        this.repo = repo;
    }

    // admin/testing list of all orders
    @GetMapping
    public List<Order> list() {
        return repo.findAll();
    }

    // current user's orders
    @GetMapping("/me")
    public ResponseEntity<?> listMyOrders(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is logged in");
        }
        List<Order> orders = repo.findByUser(username);
        return ResponseEntity.ok(orders);
    }
}