package com.bookstore.spring;

import com.bookstore.User;
import com.bookstore.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionAuth {
    private final UserRepository repo;
    public SessionAuth(UserRepository repo) { this.repo = repo; }

    public Optional<User> currentUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return (username == null || username.isBlank()) ? Optional.empty() : repo.findByUsername(username);
    }

    public boolean isAdmin(HttpSession session) {
        return currentUser(session).map(User::isAdmin).orElse(false);
    }
}