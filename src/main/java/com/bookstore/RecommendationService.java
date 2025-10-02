package com.bookstore;

import java.util.List;

public class RecommendationService {
    private final RecommendationRepository repo;

    public RecommendationService(RecommendationRepository repo) {
        this.repo = repo;
    }

    public void seedIfEmpty(String userId, List<Recommendation> recs) {
        List<Recommendation> existing = repo.getTopRecommendations(userId, 1);
        if (existing.isEmpty()) {
            repo.saveRecommendations(userId, recs);
            System.out.println("Seeded recommendations for " + userId);
        }
    }

    public List<Recommendation> getTop(String userId, int n) {
        return repo.getTopRecommendations(userId, n);
    }

    public void delete(String userId) {
        repo.deleteRecommendations(userId);
    }
}

