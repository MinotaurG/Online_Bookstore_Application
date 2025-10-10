package com.bookstore;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User("john", "john@example.com", "secret123", false);

        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    void testConstructorWithAdminUser() {
        User admin = new User("admin", "admin@example.com", "adminpass", true);

        assertTrue(admin.isAdmin());
        assertEquals("admin", admin.getUsername());
    }

    @Test
    void testToString() {
        User user = new User("alice", "alice@test.com", "pass", false);

        String str = user.toString();
        assertNotNull(str);
        assertTrue(str.contains("alice"));
        assertTrue(str.contains("alice@test.com"));
    }

    @Test
    void testToString_format() {
        User user = new User("bob", "bob@mail.com", "pwd", false);

        assertEquals("bob (bob@mail.com)", user.toString());
    }

    @Test
    void testUserWithNullValues() {
        // Test defensive programming - should handle nulls gracefully
        User user = new User(null, null, null, false);

        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    void testRegularUserIsNotAdmin() {
        User user = new User("user", "user@example.com", "pass", false);

        assertFalse(user.isAdmin());
    }

    @Test
    void testAdminUser() {
        User admin = new User("superuser", "super@example.com", "pass", true);

        assertTrue(admin.isAdmin());
    }
}