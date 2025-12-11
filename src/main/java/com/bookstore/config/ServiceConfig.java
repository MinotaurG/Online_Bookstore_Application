package com.bookstore.config;

import com.bookstore.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public InMemoryOrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public CheckoutService checkoutService(InMemoryOrderRepository orderRepository, BookService bookService) {
        return new CheckoutService(orderRepository, bookService);
    }

    @Bean
    public RecommendationService recommendationService(RecommendationRepository repo) {
        return new RecommendationService(repo);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
