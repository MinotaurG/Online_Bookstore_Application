package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BrowsingHistoryServiceTest {

    private BrowsingHistoryService service;
    private Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        service = new BrowsingHistoryService(3); // capacity of 3
        book1 = new Book("1", "Book1", "Author1", "Genre1", new BigDecimal("10"), 5);
        book2 = new Book("2", "Book2", "Author2", "Genre2", new BigDecimal("20"), 5);
        book3 = new Book("3", "Book3", "Author3", "Genre3", new BigDecimal("30"), 5);
    }

    @Test
    void testView_addsBookToHistory() {
        service.view("user1", book1);

        List<Book> history = service.getRecent("user1");
        assertEquals(1, history.size());
        assertEquals(book1, history.get(0));
    }

    @Test
    void testView_maintainsMostRecentFirst() {
        service.view("user1", book1);
        service.view("user1", book2);
        service.view("user1", book3);

        List<Book> history = service.getRecent("user1");
        assertEquals(3, history.size());
        assertEquals(book3, history.get(0)); // most recent
        assertEquals(book2, history.get(1));
        assertEquals(book1, history.get(2));
    }

    @Test
    void testView_respectsCapacity() {
        Book book4 = new Book("4", "Book4", "Author4", "Genre4", new BigDecimal("40"), 5);

        service.view("user1", book1);
        service.view("user1", book2);
        service.view("user1", book3);
        service.view("user1", book4); // exceeds capacity

        List<Book> history = service.getRecent("user1");
        assertEquals(3, history.size());
        assertFalse(history.contains(book1), "Oldest item should be removed");
        assertTrue(history.contains(book4));
    }

    @Test
    void testView_movesReviewedBookToFront() {
        service.view("user1", book1);
        service.view("user1", book2);
        service.view("user1", book3);
        service.view("user1", book1); // view book1 again

        List<Book> history = service.getRecent("user1");
        assertEquals(3, history.size());
        assertEquals(book1, history.get(0), "Re-viewed book should move to front");
    }

    @Test
    void testView_doesNotDuplicateBooks() {
        service.view("user1", book1);
        service.view("user1", book1); // view same book twice

        List<Book> history = service.getRecent("user1");
        assertEquals(1, history.size());
    }

    @Test
    void testGetRecent_returnsEmptyForNewUser() {
        List<Book> history = service.getRecent("unknownUser");
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void testView_isolatesUserHistories() {
        service.view("user1", book1);
        service.view("user2", book2);

        List<Book> history1 = service.getRecent("user1");
        List<Book> history2 = service.getRecent("user2");

        assertEquals(1, history1.size());
        assertEquals(1, history2.size());
        assertEquals(book1, history1.get(0));
        assertEquals(book2, history2.get(0));
    }

    @Test
    void testConstructor_withDefaultCapacity() {
        BrowsingHistoryService defaultService = new BrowsingHistoryService(10);
        assertNotNull(defaultService);

        // Verify default capacity works
        for (int i = 0; i < 15; i++) {
            Book b = new Book("" + i, "Book" + i, "Author", "Genre", BigDecimal.TEN, 5);
            defaultService.view("user", b);
        }

        List<Book> history = defaultService.getRecent("user");
        assertEquals(10, history.size(), "Should respect capacity of 10");
    }
}