package com.bookstore;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Thread-safe, capacity-limited browsing history implemented with LinkedList.
 * Most-recent-first. Viewing the same book again moves it to the head.
 */
public class BrowsingHistory {
    private final LinkedList<Book> list = new LinkedList<>();
    private final int capacity;

    public BrowsingHistory(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
    }

    /**
     * Record that a user viewed this book.
     * If the book already exists in history, move it to the front.
     */
    public synchronized void view(Book book) {
        Objects.requireNonNull(book, "book cannot be null");
        // remove existing occurrence if present (by id)
        list.removeIf(b -> b.getId().equals(book.getId()));
        // add to front
        list.addFirst(book);
        // trim tail if exceed capacity
        while (list.size() > capacity) {
            list.removeLast();
        }
    }

    /**
     * Return a snapshot list of recent books, most-recent-first.
     * The returned list is unmodifiable.
     */
    public synchronized List<Book> getRecent() {
        return Collections.unmodifiableList(new LinkedList<>(list));
    }

    /**
     * Return up to n most recent books (n <= capacity).
     */
    public synchronized List<Book> getRecent(int n) {
        if (n <= 0) return Collections.emptyList();
        int limit = Math.min(n, list.size());
        LinkedList<Book> sub = new LinkedList<>();
        int i = 0;
        for (Book b : list) {
            if (i++ >= limit) break;
            sub.add(b);
        }
        return Collections.unmodifiableList(sub);
    }

    public synchronized void clear() {
        list.clear();
    }

    public synchronized int size() {
        return list.size();
    }
}
