package com.bookstore.spring;

import com.bookstore.Book;
import com.bookstore.RecommendationService;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Safe adapter that attempts to call a recommendation method on RecommendationService using reflection.
 */
public class RecommendationServiceAdapter {
    private final RecommendationService svc;

    public RecommendationServiceAdapter(RecommendationService svc) {
        this.svc = svc;
    }

    @SuppressWarnings("unchecked")
    public List<Book> recommendForUser(String user) {
        if (svc == null) return Collections.emptyList();

        String[] candidateNames = new String[] {
                "recommendForUser",
                "getRecommendationsForUser",
                "recommendationsFor",
                "getForUser",
                "recommend",
                "findRecommendations",
                "getRecommendations"
        };

        for (String name : candidateNames) {
            try {
                Method m = svc.getClass().getMethod(name, String.class);
                Object result = m.invoke(svc, user);
                if (result instanceof List) {
                    return (List<Book>) result;
                }
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                System.err.println("Recommendation adapter: invocation of " + name + " failed: " + e.getMessage());
            }
        }

        // Try zero-arg fallback
        try {
            Method m = svc.getClass().getMethod("getRecommendations");
            Object result = m.invoke(svc);
            if (result instanceof List) {
                return (List<Book>) result;
            }
        } catch (Exception ignored) {}

        return Collections.emptyList();
    }
}