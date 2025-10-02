package com.bookstore;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationServiceTest {

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

    // In-memory repo for unit tests
    static class InMemoryRecommendationRepository implements RecommendationRepository {
        private java.util.Map<String, List<Recommendation>> store = new java.util.HashMap<>();

        @Override
        public void saveRecommendations(String userId, List<Recommendation> recs) {
            store.put(userId, recs);
        }

        @Override
        public List<Recommendation> getTopRecommendations(String userId, int limit) {
            return store.getOrDefault(userId, java.util.Collections.emptyList());
        }

        @Override
        public void deleteRecommendations(String userId) {
            store.remove(userId);
        }
    }
}
