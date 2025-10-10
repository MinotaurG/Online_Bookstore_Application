package com.bookstore;

import java.util.List;
import java.util.Objects;

public class RecommendationService {
    private final RecommendationRepository repository;

    public RecommendationService(RecommendationRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public List<Book> recommendForUser(String userId, int limit) {
        return repository.findRecommendationsForUser(userId, limit);
    }

    // convenience if used by demos/tests
    public List<Book> recommendForUser(String userId) {
        return recommendForUser(userId, 5);
    }

    // If there are no recommendations for the given user, write the provided ones.
    public void seedIfEmpty(String user, List<Recommendation> recs) {
        List<Recommendation> existing = repository.getTopRecommendations(user, 1);
        if (existing == null || existing.isEmpty()) {
            repository.saveRecommendations(user, recs);
        }
    }

    // Return top Recommendations (used by RecommendationsDemo)
    public List<Recommendation> getTop(String userId, int limit) {
        return repository.getTopRecommendations(userId, limit);
    }
}
