package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.bookstore.testhelpers.InMemoryRecommendationRepository;

public class RecommendationServiceTest {

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        service = new RecommendationService(new InMemoryRecommendationRepository());
    }


    @Test
    public void testSeedAndFetch() {
        RecommendationRepository repo = new InMemoryRecommendationRepository();
        RecommendationService service = new RecommendationService(repo);

        String user = "alice";
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-1", "Book 1", "reason"),
                new Recommendation(2, "b-2", "Book 2", "reason")
        );

        service.seedIfEmpty(user, recs);
        List<Recommendation> fetched = service.getTop(user, 5);

        assertEquals(2, fetched.size());
        assertEquals("Book 1", fetched.get(0).getTitle());
    }

    // replace the existing inner class with this one
    static class InMemoryRecommendationRepository implements RecommendationRepository {
        private final java.util.Map<String, List<Recommendation>> store = new java.util.HashMap<>();

        @Override
        public void saveRecommendations(String userId, List<Recommendation> recs) {
            if (userId == null || recs == null) return;
            // store a defensive copy
            store.put(userId, new java.util.ArrayList<>(recs));
        }

        @Override
        public List<Recommendation> getTopRecommendations(String userId, int limit) {
            List<Recommendation> list = store.getOrDefault(userId, java.util.Collections.emptyList());
            if (limit <= 0) return java.util.Collections.emptyList();
            return list.stream()
                    .sorted(java.util.Comparator.comparingInt(Recommendation::getRank))
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public void deleteRecommendations(String userId) {
            store.remove(userId);
        }

        /**
         * Minimal implementation to satisfy the interface for tests.
         * Currently returns an empty list of Book objects. If you want tests
         * to verify Book-level results, I can convert Recommendation -> Book
         * here (need to know Book/Recommendation getters or allow reflection).
         */
        @Override
        public List<Book> findRecommendationsForUser(String userId, int limit) {
            return java.util.Collections.emptyList();
        }
    }
}
