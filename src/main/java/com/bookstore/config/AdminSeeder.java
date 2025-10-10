package com.bookstore.config;

import com.bookstore.User;
import com.bookstore.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Optional: enable BCrypt later
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminSeeder {
    @Bean
    public ApplicationRunner seedAdmin(UserRepository users /*, BCryptPasswordEncoder bcrypt */) {
        return args -> {
            String admin = "admin";
            if (!users.existsByUsername(admin)) {
                // String hash = bcrypt.encode("admin123"); // when you enable hashing
                users.save(new User(admin, "admin@example.com", /*hash*/ "admin123", true));
                System.out.println("[SEED] Admin user created: admin / admin123");
            }
        };
    }

    // Uncomment to use BCrypt now:
    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
