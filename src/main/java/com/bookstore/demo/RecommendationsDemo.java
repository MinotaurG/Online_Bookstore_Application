package com.bookstore.demo;

import com.bookstore.*;

import java.util.Arrays;
import java.util.List;

public class RecommendationsDemo {
    public static void main(String[] args) {
        DynamoDbSingleItemRecommendationRepository repo = new DynamoDbSingleItemRecommendationRepository();
        RecommendationService service = new RecommendationService(repo);

        String user = "demoUser";

        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Effective Java", "classic"),
                new Recommendation(2, "b-200", "The Pragmatic Programmer", "best practice"),
                new Recommendation(3, "b-300", "Clean Architecture", "design"),
                new Recommendation(4, "b-400", "Refactoring", "patterns"),
                new Recommendation(5, "b-500", "Head First Design Patterns", "easy learning")
        );

        service.seedIfEmpty(user, recs);

        System.out.println("\nTop 5 recommendations for " + user + ":");
        service.getTop(user, 5).forEach(System.out::println);

        repo.close();
    }
}
