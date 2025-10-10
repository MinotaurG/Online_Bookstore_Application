package com.bookstore;

/**
 * Simple user representation used for authentication and personalization.
 */
public class User {
    private final String username;
    private final String email;
    private final String password;

    public User(String username, String email, String password) {
        this.username = username;
        this.email    = email;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }
}