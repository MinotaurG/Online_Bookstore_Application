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

    public void seedIfEmpty(String user, List<Recommendation> recs) {
    }
}
