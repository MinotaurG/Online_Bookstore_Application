package com.bookstore;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Simple shopping cart. Uses a Map<bookId, CartItem>.
 * Not highly concurrent — intended per-user session. If you need concurrency,
 * wrap calls or use a concurrent map and atomic updates.
 */
public class Cart {
    // preserve insertion order for nicer printing
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public synchronized void addBook(Book book, int qty) {
        if (book == null || qty <= 0) return;
        String id = book.getId();
        CartItem existing = items.get(id);
        if (existing == null) {
            items.put(id, new CartItem(id, book.getTitle(), book.getPrice(), qty));
        } else {
            existing.increaseQuantity(qty);
        }
    }

    public synchronized void updateQuantity(String bookId, int qty) {
        CartItem item = items.get(bookId);
        if (item == null) return;
        if (qty <= 0) items.remove(bookId);
        else item.setQuantity(qty);
    }

    public synchronized void removeBook(String bookId) {
        items.remove(bookId);
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized Map<String, CartItem> getItems() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(items));
    }

    public synchronized BigDecimal calculateTotal() {
        return items.values().stream()
                .map(CartItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    public synchronized Optional<CartItem> getItem(String bookId) {
        return Optional.ofNullable(items.get(bookId));
    }
}
