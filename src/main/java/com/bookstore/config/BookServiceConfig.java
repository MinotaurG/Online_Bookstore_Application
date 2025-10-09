package com.bookstore.config;

import com.bookstore.BookService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookServiceConfig {

    @Bean(destroyMethod = "close")
    public BookService bookService() {
        return new BookService();
    }
}