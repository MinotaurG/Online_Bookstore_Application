package com.bookstore.config;

import com.bookstore.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central place to declare beans that complement existing BookServiceConfig
 * (which already defines the BookService bean). Do NOT define another
 * BookService() bean here.
 */
@Configuration
public class ServiceConfig {

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public CheckoutService checkoutService(OrderRepository orderRepository, BookService bookService) {
        // bookService is auto-provided by com.bookstore.config.BookServiceConfig
        return new CheckoutService(orderRepository, bookService);
    }

    @Bean
    public RecommendationRepository recommendationRepository() {
        // If you have DynamoDb implementation, return new DynamoDbSingleItemRecommendationRepository(...)
        return new DynamoDbSingleItemRecommendationRepository();
    }


    @Bean
    public RecommendationService
            recommendationService(RecommendationRepository repo) {
        // you may already have a DynamoDb-backed implementation (DynamoDbSingleItemRecommendationRepository).
        // If so, keep using it. For a simple in-memory fallback:
        return new RecommendationService(repo);
    }
}