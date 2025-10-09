package com.bookstore.testhelpers;

import com.bookstore.Book;
import com.bookstore.Recommendation;
import com.bookstore.RecommendationRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryRecommendationRepository implements RecommendationRepository {

    private final Map<String, List<Recommendation>> storage = new ConcurrentHashMap<>();

    @Override
    public void saveRecommendations(String userId, List<Recommendation> recs) {
        if (userId == null || recs == null) return;
        storage.put(userId, new ArrayList<>(recs));
    }

    @Override
    public List<Recommendation> getTopRecommendations(String userId, int limit) {
        List<Recommendation> recs = storage.getOrDefault(userId, Collections.emptyList());
        return recs.stream()
                .sorted(Comparator.comparingInt(Recommendation::getRank))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecommendations(String userId) {
        if (userId == null) return;
        storage.remove(userId);
    }

    @Override
    public List<Book> findRecommendationsForUser(String userId, int limit) {
        List<Recommendation> recs = getTopRecommendations(userId, limit);
        return recs.stream()
                .map(this::extractBook)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Book extractBook(Recommendation r) {
        try {
            // Prefer direct getter if available
            var method = r.getClass().getMethod("getBook");
            Object result = method.invoke(r);
            if (result instanceof Book) return (Book) result;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        // fallback: attempt to build minimal Book
        try {
            Book b = new Book();
            var setTitle = Book.class.getMethod("setTitle", String.class);
            setTitle.invoke(b, r.toString());
            return b;
        } catch (Exception ignored) {
        }
        return null;
    }

    // optional helper for tests
    public void clear() {
        storage.clear();
    }
}