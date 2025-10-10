package com.bookstore.spring;

import com.bookstore.User;
import com.bookstore.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // DTOs for incoming requests
    public static record RegisterRequest(String username, String email, String password) {}
    public static record LoginRequest(String username, String password) {}

    /** Create a new user. */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request == null || request.username() == null || request.username().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required");
        }
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists: " + request.username());
        }
        String email = request.email() == null ? "" : request.email();
        User user = new User(request.username(), email, request.password(), false);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "isAdmin", user.isAdmin()
                ));
    }

    /** Log in a user. */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        if (request == null || request.username() == null || request.password() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        Optional<User> optUser = userRepository.findByUsername(request.username());
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        User user = optUser.get();
        if (user.getPassword() != null && user.getPassword().equals(request.password())) {
            session.setAttribute("username", user.getUsername());
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "isAdmin", user.isAdmin()));  // <-- include flag
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    /** Log out the current user. */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    /** Get the current user's profile. */
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is logged in");
        }
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "isAdmin", user.isAdmin()
        ));
    }
}