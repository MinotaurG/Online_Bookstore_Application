package com.bookstore;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeterministicIdTest {

    @Test
    void testForBook_generatesStableId() {
        String id1 = DeterministicId.forBook("Clean Code", "Robert C. Martin");
        String id2 = DeterministicId.forBook("Clean Code", "Robert C. Martin");

        assertNotNull(id1);
        assertEquals(id1, id2, "Same inputs should generate same ID");
        assertTrue(id1.startsWith("b-"), "ID should start with 'b-'");
        assertEquals(14, id1.length(), "ID should be 'b-' + 12 chars = 14 total"); // ✅ Fixed: 14 not 15
    }

    @Test
    void testForBook_differentInputsGenerateDifferentIds() {
        String id1 = DeterministicId.forBook("Clean Code", "Robert C. Martin");
        String id2 = DeterministicId.forBook("Effective Java", "Joshua Bloch");

        assertNotEquals(id1, id2);
    }

    @Test
    void testForBook_handlesNullTitle() {
        String id = DeterministicId.forBook(null, "Author");
        assertNotNull(id);
        assertTrue(id.startsWith("b-"));
    }

    @Test
    void testForBook_handlesNullAuthor() {
        String id = DeterministicId.forBook("Title", null);
        assertNotNull(id);
        assertTrue(id.startsWith("b-"));
    }

    @Test
    void testForBook_handlesBothNull() {
        String id = DeterministicId.forBook(null, null);
        assertNotNull(id);
        assertTrue(id.startsWith("b-"));
    }

    @Test
    void testForBook_trimsWhitespace() {
        String id1 = DeterministicId.forBook("  Clean Code  ", "  Robert C. Martin  ");
        String id2 = DeterministicId.forBook("Clean Code", "Robert C. Martin");

        assertEquals(id1, id2, "Whitespace should be trimmed");
    }
}