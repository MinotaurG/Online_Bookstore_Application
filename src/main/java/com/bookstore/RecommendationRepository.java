package com.bookstore;

import java.util.List;

public interface RecommendationRepository {
    void saveRecommendations(String userId, List<Recommendation> recs);
    List<Recommendation> getTopRecommendations(String userId, int limit);
    void deleteRecommendations(String userId);

    List<Book> findRecommendationsForUser(String userId, int limit);
}