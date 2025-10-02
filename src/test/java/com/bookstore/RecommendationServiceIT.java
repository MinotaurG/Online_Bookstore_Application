package com.bookstore;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
public class RecommendationServiceIT {

    private static DynamoDbSingleItemRecommendationRepository repo;
    private static RecommendationService service;

    @BeforeAll
    static void setup() {
        repo = new DynamoDbSingleItemRecommendationRepository();
        service = new RecommendationService(repo);
    }

    @AfterAll
    static void teardown() {
        repo.close();
    }

    @Test
    void testSeedAndFetchTop5() {
        String user = "itUser";
        repo.deleteRecommendations(user);

        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-1", "Book 1", "reason"),
                new Recommendation(2, "b-2", "Book 2", "reason"),
                new Recommendation(3, "b-3", "Book 3", "reason"),
                new Recommendation(4, "b-4", "Book 4", "reason"),
                new Recommendation(5, "b-5", "Book 5", "reason")
        );

        service.seedIfEmpty(user, recs);

        List<Recommendation> fetched = service.getTop(user, 5);
        assertEquals(5, fetched.size());
        assertEquals("Book 1", fetched.get(0).getTitle());
    }
}
