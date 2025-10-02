package com.bookstore;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Order {
    private final String orderId;
    private final String username; // or userId
    private final Map<String, CartItem> items; // copied at checkout
    private final BigDecimal total;
    private final Instant createdAt;

    public Order(String orderId, String username, Map<String, CartItem> items, BigDecimal total) {
        this.orderId = Objects.requireNonNull(orderId);
        this.username = username;
        this.items = Collections.unmodifiableMap(new LinkedHashMap<>(items));
        this.total = total;
        this.createdAt = Instant.now();
    }

    public String getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public Map<String, CartItem> getItems() { return items; }
    public BigDecimal getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ").append(orderId).append(" for ").append(username)
                .append(" at ").append(createdAt).append("\n");
        items.values().forEach(i -> sb.append(" - ").append(i.toString()).append("\n"));
        sb.append("Total: ").append(total);
        return sb.toString();
    }
}
