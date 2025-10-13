package com.bookstore.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // ✅ Explicitly register cache names
        cacheManager.setCacheNames(java.util.Arrays.asList("booksAll", "booksById"));

        // ✅ Configure Caffeine cache settings (optional but recommended)
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)  // Max 1000 entries per cache
                .expireAfterWrite(10, TimeUnit.MINUTES)  // Auto-expire after 10 minutes
                .recordStats());  // Enable statistics for monitoring

        return cacheManager;
    }
}