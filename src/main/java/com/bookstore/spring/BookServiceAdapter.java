package com.bookstore.spring;

import com.bookstore.Book;
import com.bookstore.BookService;
import com.bookstore.BulkUpdateRequest;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring-managed adapter that wraps the legacy BookService (which creates its own DynamoDB client).
 * Keeps a single BookService instance for the Spring app and ensures it is closed on shutdown.
 */
@Service("bookServiceAdapter")
public class BookServiceAdapter {

    private final BookService bookService;

    public BookServiceAdapter() {
        this.bookService = new BookService();
    }

    // reads
    @org.springframework.cache.annotation.Cacheable("booksAll")
    public List<Book> listAll() {
        return bookService.listAllBooks();
    }

    @org.springframework.cache.annotation.Cacheable(value = "booksById", key = "#id")
    public Optional<Book> findById(String id) {
        return Optional.ofNullable(bookService.getBookById(id));
    }

    public List<Book> findByTitle(String title) {
        return bookService.findByTitle(title);
    }

    public List<Book> search(String q) {
        return bookService.searchByTitleOrAuthorContains(q);
    }

    public int migrateToAsin() {
        return bookService.migrateExistingBooksToAsin();
    }

    public Book findByAsin(String asin) {
        return bookService.getBookByAsin(asin);
    }

    public boolean deleteByAsin(String asin) {
        return bookService.deleteByAsin(asin);
    }

    // writes (admin add/update/delete/bulk) → evict caches
    @org.springframework.cache.annotation.CacheEvict(value={"booksAll","booksById"}, allEntries=true)
    public void save(Book book) {
        // choose idempotent upsert to avoid duplicates when called from web
        bookService.saveOrUpdateBookByTitle(book);
    }

    @org.springframework.cache.annotation.CacheEvict(value={"booksAll","booksById"}, allEntries=true)
    public void deleteById(String id) {
        bookService.deleteBook(id);
    }

    public int deleteByTitle(String title) {
        return bookService.deleteByTitle(title);
    }

    // Bulk operations
    public int bulkUpdate(List<BulkUpdateRequest.BookUpdate> updates) {
        return bookService.bulkUpdateBooks(updates);
    }

    public int bulkDelete(List<String> ids, List<String> asins) {
        return bookService.bulkDeleteBooks(ids, asins);
    }

    public List<Book> filterBooks(String genre, String author, Integer minStock, Integer maxStock) {
        return bookService.filterBooks(genre, author, minStock, maxStock);
    }

    @PreDestroy
    public void shutdown() {
        try {
            bookService.close();
        } catch (Exception ignored) {}
    }
}