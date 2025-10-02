package com.bookstore;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BrowsingHistoryTest {

    @Test
    public void testViewAndRecentOrderAndCapacity() {
        // create some books
        Book b1 = new Book("id-1", "A", "Author A", "Genre", new BigDecimal("10.00"), 5);
        Book b2 = new Book("id-2", "B", "Author B", "Genre", new BigDecimal("12.00"), 5);
        Book b3 = new Book("id-3", "C", "Author C", "Genre", new BigDecimal("8.00"), 5);

        BrowsingHistory history = new BrowsingHistory(2); // small capacity to test eviction

        history.view(b1); // [A]
        history.view(b2); // [B, A]
        List<Book> recent = history.getRecent();
        assertEquals(2, recent.size());
        assertEquals("B", recent.get(0).getTitle());

        // viewing A again moves it to front [A, B]
        history.view(b1);
        recent = history.getRecent();
        assertEquals("A", recent.get(0).getTitle());
        assertEquals(2, recent.size());

        // adding c evicts oldest (B) -> [C, A]
        history.view(b3);
        recent = history.getRecent();
        assertEquals(2, recent.size());
        assertEquals("C", recent.get(0).getTitle());
        assertTrue(recent.stream().noneMatch(b -> b.getTitle().equals("B")));
    }
}
