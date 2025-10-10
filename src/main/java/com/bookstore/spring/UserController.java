package com.bookstore.spring;

import com.bookstore.User;
import com.bookstore.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserController(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    // --- DTOs with validation ---
    public static record RegisterRequest(
            @NotBlank @Size(min = 3, max = 40) String username,
            @Email(message = "Invalid email") String email,
            @NotBlank @Size(min = 6, max = 72) String password
    ) {}

    public static record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    /** Create a new user (password is stored as BCrypt hash). */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email() == null ? "" : request.email().trim();

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists: " + username);
        }

        String hash = encoder.encode(request.password());
        User user = new User(username, email, hash, false);
        userRepository.save(user);

        // 201 + Location for good REST hygiene
        return ResponseEntity.created(URI.create("/api/users/" + username))
                .body(Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "isAdmin", user.isAdmin()
                ));
    }

    /**
     * Log in a user.
     * Backward-compatible: if stored password is plaintext and equals input,
     * we re-hash and save transparently.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        String username = request.username().trim();
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        User user = optUser.get();
        String stored = user.getPassword();

        boolean ok = false;
        if (stored != null && stored.startsWith("$2a$") || (stored != null && stored.startsWith("$2b$"))) {
            // BCrypt hash path
            ok = encoder.matches(request.password(), stored);
        } else {
            // Legacy plaintext path
            ok = stored != null && stored.equals(request.password());
            if (ok) {
                // Upgrade to BCrypt transparently
                String newHash = encoder.encode(request.password());
                userRepository.save(new User(user.getUsername(), user.getEmail(), newHash, user.isAdmin()));
            }
        }

        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        session.setAttribute("username", user.getUsername());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "username", user.getUsername(),
                "email", user.getEmail(),
                "isAdmin", user.isAdmin()
        ));
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
        if (username == null || username.isBlank()) {
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
