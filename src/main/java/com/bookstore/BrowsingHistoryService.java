package com.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BrowsingHistoryService {

    private final int capacity;
    private final Map<String, Deque<Book>> histories = new ConcurrentHashMap<>();

    // Spring will inject property browsing.history.capacity or fallback to 10
    public BrowsingHistoryService(@Value("${browsing.history.capacity:10}") int capacity) {
        this.capacity = capacity;
    }

    public void view(String user, Book book) {
        histories.computeIfAbsent(user, k -> new ArrayDeque<>());

        Deque<Book> dq = histories.get(user);

        dq.removeIf(b -> b.getId().equals(book.getId()));
        dq.addFirst(book);
        while (dq.size() > capacity) dq.removeLast();
    }

    public List<Book> getRecent(String user) {
        Deque<Book> dq = histories.getOrDefault(user, new ArrayDeque<>());
        return new ArrayList<>(dq);
    }
}