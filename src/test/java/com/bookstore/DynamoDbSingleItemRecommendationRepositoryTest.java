package com.bookstore;

import org.junit.jupiter.api.*;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DynamoDbSingleItemRecommendationRepository.
 * Requires DynamoDB Local running on http://localhost:8000
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DynamoDbSingleItemRecommendationRepositoryTest {

    private static DynamoDbSingleItemRecommendationRepository repository;
    private static final String TEST_USER_PREFIX = "test-user-";

    @BeforeAll
    static void setUpClass() {
        repository = new DynamoDbSingleItemRecommendationRepository();
    }

    @AfterAll
    static void tearDownClass() {
        cleanupTestData();
        if (repository != null) {
            repository.close();
        }
    }

    @BeforeEach
    void setUp() {
        cleanupTestData();
    }

    private static void cleanupTestData() {
        try {
            for (int i = 0; i < 10; i++) {
                repository.deleteRecommendations(TEST_USER_PREFIX + i);
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testSaveRecommendations() {
        String userId = TEST_USER_PREFIX + "1";
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Clean Code", "classic"),
                new Recommendation(2, "b-200", "Effective Java", "essential")
        );

        repository.saveRecommendations(userId, recs);

        List<Recommendation> retrieved = repository.getTopRecommendations(userId, 5);
        assertEquals(2, retrieved.size());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetTopRecommendations_sortedByRank() {
        String userId = TEST_USER_PREFIX + "2";
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(3, "b-300", "Book C", "reason"),
                new Recommendation(1, "b-100", "Book A", "reason"),
                new Recommendation(2, "b-200", "Book B", "reason")
        );

        repository.saveRecommendations(userId, recs);

        List<Recommendation> retrieved = repository.getTopRecommendations(userId, 5);
        assertEquals(3, retrieved.size());
        assertEquals(1, retrieved.get(0).getRank());
        assertEquals(2, retrieved.get(1).getRank());
        assertEquals(3, retrieved.get(2).getRank());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testGetTopRecommendations_respectsLimit() {
        String userId = TEST_USER_PREFIX + "3";
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Book 1", "reason"),
                new Recommendation(2, "b-200", "Book 2", "reason"),
                new Recommendation(3, "b-300", "Book 3", "reason"),
                new Recommendation(4, "b-400", "Book 4", "reason")
        );

        repository.saveRecommendations(userId, recs);

        List<Recommendation> retrieved = repository.getTopRecommendations(userId, 2);
        assertEquals(2, retrieved.size());
        assertEquals(1, retrieved.get(0).getRank());
        assertEquals(2, retrieved.get(1).getRank());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testGetTopRecommendations_returnsEmptyForNonexistentUser() {
        List<Recommendation> retrieved = repository.getTopRecommendations("nonexistent-user", 5);

        assertTrue(retrieved.isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testDeleteRecommendations() {
        String userId = TEST_USER_PREFIX + "5";
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Book", "reason")
        );

        repository.saveRecommendations(userId, recs);
        repository.deleteRecommendations(userId);

        List<Recommendation> retrieved = repository.getTopRecommendations(userId, 5);
        assertTrue(retrieved.isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testFindRecommendationsForUser_returnsBooks() {
        String userId = TEST_USER_PREFIX + "6";
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Clean Code", "classic"),
                new Recommendation(2, "b-200", "Effective Java", "essential")
        );

        repository.saveRecommendations(userId, recs);

        List<Book> books = repository.findRecommendationsForUser(userId, 5);
        assertFalse(books.isEmpty());
        // Note: books might be constructed from recommendation data
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testFindRecommendationsForUser_returnsEmptyForNullUser() {
        List<Book> books = repository.findRecommendationsForUser(null, 5);

        assertTrue(books.isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testFindRecommendationsForUser_returnsEmptyForZeroLimit() {
        String userId = TEST_USER_PREFIX + "8";
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Book", "reason")
        );
        repository.saveRecommendations(userId, recs);

        List<Book> books = repository.findRecommendationsForUser(userId, 0);

        assertTrue(books.isEmpty());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testSaveRecommendations_overwritesExisting() {
        String userId = TEST_USER_PREFIX + "9";
        List<Recommendation> recs1 = Arrays.asList(
                new Recommendation(1, "b-100", "Old Book", "old")
        );
        repository.saveRecommendations(userId, recs1);

        List<Recommendation> recs2 = Arrays.asList(
                new Recommendation(1, "b-200", "New Book", "new")
        );
        repository.saveRecommendations(userId, recs2);

        List<Recommendation> retrieved = repository.getTopRecommendations(userId, 5);
        assertEquals(1, retrieved.size());
        assertEquals("New Book", retrieved.get(0).getTitle());
    }
}