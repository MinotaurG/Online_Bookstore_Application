package com.bookstore;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages browsing histories per user (in-memory).
 * Uses simple ConcurrentHashMap<userId, BrowsingHistory>.
 */
public class BrowsingHistoryService {
    private final ConcurrentHashMap<String, BrowsingHistory> store = new ConcurrentHashMap<>();
    private final int defaultCapacity;

    public BrowsingHistoryService(int defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
    }

    private BrowsingHistory historyFor(String userId) {
        return store.computeIfAbsent(userId, id -> new BrowsingHistory(defaultCapacity));
    }

    public void view(String userId, Book book) {
        historyFor(userId).view(book);
    }

    public List<Book> getRecent(String userId, int n) {
        BrowsingHistory h = store.get(userId);
        if (h == null) return java.util.Collections.emptyList();
        return h.getRecent(n);
    }

    public List<Book> getRecent(String userId) {
        BrowsingHistory h = store.get(userId);
        if (h == null) return java.util.Collections.emptyList();
        return h.getRecent();
    }

    public void clear(String userId) {
        BrowsingHistory h = store.get(userId);
        if (h != null) h.clear();
    }
}
