package com.bookstore;

/**
 * Simple user representation used for authentication and personalization.
 */
public class User {
    private final String username;
    private final String email;
    private final String password;
    private final boolean isAdmin; // <-- new

    public User(String username, String email, String password, boolean isAdmin) {
        this.username = username;
        this.email    = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public boolean isAdmin()    { return isAdmin; }

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }
}