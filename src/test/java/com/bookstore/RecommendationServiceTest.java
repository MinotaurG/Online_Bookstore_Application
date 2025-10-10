package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    @Mock
    private RecommendationRepository repository;

    private RecommendationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RecommendationService(repository);
    }

    @Test
    void testConstructor_throwsExceptionForNullRepository() {
        assertThrows(NullPointerException.class, () -> {
            new RecommendationService(null);
        });
    }

    @Test
    void testRecommendForUser_withLimit() {
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Book1", "Author1", "Genre1", null, null),
                new Book("2", "Book2", "Author2", "Genre2", null, null)
        );
        when(repository.findRecommendationsForUser("user1", 2)).thenReturn(mockBooks);

        List<Book> result = service.recommendForUser("user1", 2);

        assertEquals(2, result.size());
        verify(repository, times(1)).findRecommendationsForUser("user1", 2);
    }

    @Test
    void testRecommendForUser_defaultLimit() {
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Book1", "Author1", "Genre1", null, null)
        );
        when(repository.findRecommendationsForUser("user1", 5)).thenReturn(mockBooks);

        List<Book> result = service.recommendForUser("user1");

        assertEquals(1, result.size());
        verify(repository, times(1)).findRecommendationsForUser("user1", 5);
    }

    @Test
    void testRecommendForUser_returnsEmptyWhenNoneAvailable() {
        when(repository.findRecommendationsForUser("user1", 5)).thenReturn(Collections.emptyList());

        List<Book> result = service.recommendForUser("user1");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSeedIfEmpty_seedsWhenEmpty() {
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Book1", "classic"),
                new Recommendation(2, "b-200", "Book2", "new")
        );
        when(repository.getTopRecommendations("user1", 1)).thenReturn(Collections.emptyList());

        service.seedIfEmpty("user1", recs);

        verify(repository, times(1)).getTopRecommendations("user1", 1);
        verify(repository, times(1)).saveRecommendations("user1", recs);
    }

    @Test
    void testSeedIfEmpty_doesNotSeedWhenAlreadyPresent() {
        List<Recommendation> existing = Arrays.asList(
                new Recommendation(1, "b-100", "Book1", "classic")
        );
        List<Recommendation> newRecs = Arrays.asList(
                new Recommendation(1, "b-200", "Book2", "new")
        );
        when(repository.getTopRecommendations("user1", 1)).thenReturn(existing);

        service.seedIfEmpty("user1", newRecs);

        verify(repository, times(1)).getTopRecommendations("user1", 1);
        verify(repository, never()).saveRecommendations(anyString(), anyList());
    }

    @Test
    void testSeedIfEmpty_handlesNullResult() {
        List<Recommendation> recs = Arrays.asList(
                new Recommendation(1, "b-100", "Book1", "classic")
        );
        when(repository.getTopRecommendations("user1", 1)).thenReturn(null);

        service.seedIfEmpty("user1", recs);

        verify(repository, times(1)).saveRecommendations("user1", recs);
    }

    @Test
    void testGetTop_returnsTopRecommendations() {
        List<Recommendation> mockRecs = Arrays.asList(
                new Recommendation(1, "b-100", "Book1", "classic"),
                new Recommendation(2, "b-200", "Book2", "new")
        );
        when(repository.getTopRecommendations("user1", 2)).thenReturn(mockRecs);

        List<Recommendation> result = service.getTop("user1", 2);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRank());
        verify(repository, times(1)).getTopRecommendations("user1", 2);
    }

    @Test
    void testGetTop_returnsEmptyWhenNoneAvailable() {
        when(repository.getTopRecommendations("user1", 5)).thenReturn(Collections.emptyList());

        List<Recommendation> result = service.getTop("user1", 5);

        assertTrue(result.isEmpty());
    }
}