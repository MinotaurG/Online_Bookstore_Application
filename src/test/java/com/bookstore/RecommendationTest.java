package com.bookstore;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecommendationTest {

    @Test
    void testNoArgsConstructor() {
        Recommendation rec = new Recommendation();
        assertNotNull(rec);
        assertEquals(0, rec.getRank());
        assertNull(rec.getBookId());
    }

    @Test
    void testAllArgsConstructor() {
        Recommendation rec = new Recommendation(1, "b-100", "Clean Code", "classic");

        assertEquals(1, rec.getRank());
        assertEquals("b-100", rec.getBookId());
        assertEquals("Clean Code", rec.getTitle());
        assertEquals("classic", rec.getReason());
    }

    @Test
    void testSettersAndGetters() {
        Recommendation rec = new Recommendation();

        rec.setRank(5);
        rec.setBookId("b-200");
        rec.setTitle("Effective Java");
        rec.setReason("best practice");

        assertEquals(5, rec.getRank());
        assertEquals("b-200", rec.getBookId());
        assertEquals("Effective Java", rec.getTitle());
        assertEquals("best practice", rec.getReason());
    }

    @Test
    void testToString_withAllFields() {
        Recommendation rec = new Recommendation(1, "b-100", "Clean Code", "classic");

        String str = rec.toString();
        assertNotNull(str);
        assertTrue(str.contains("1."));
        assertTrue(str.contains("Clean Code"));
        assertTrue(str.contains("b-100"));
        assertTrue(str.contains("classic"));
    }

    @Test
    void testToString_withoutReason() {
        Recommendation rec = new Recommendation(2, "b-200", "Design Patterns", null);

        String str = rec.toString();
        assertNotNull(str);
        assertTrue(str.contains("2."));
        assertTrue(str.contains("Design Patterns"));
        assertFalse(str.contains("[null]"));
    }

    @Test
    void testToString_handlesNullValues() {
        Recommendation rec = new Recommendation();
        assertDoesNotThrow(() -> rec.toString());
    }
}