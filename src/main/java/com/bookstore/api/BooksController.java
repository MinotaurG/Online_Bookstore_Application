package com.bookstore.api;

import com.bookstore.Book;
import com.bookstore.spring.BookServiceAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Minimal REST API for books.
 *
 * - GET  /api/books         -> list all
 * - GET  /api/books/{id}    -> get by id
 * - GET  /api/books/search?q= -> search by title or author contains
 * - POST /api/books         -> add / upsert book
 */
@RestController
@RequestMapping("/api/books")
public class BooksController {

    private final BookServiceAdapter adapter;

    public BooksController(BookServiceAdapter adapter) {
        this.adapter = adapter;
    }

    @GetMapping
    public List<Book> all() {
        return adapter.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable String id) {
        return adapter.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Book> search(@RequestParam(value = "q", required = false, defaultValue = "") String q) {
        return adapter.search(q);
    }

    @GetMapping("/by-title")
    public List<Book> byTitle(@RequestParam("title") String title) {
        return adapter.findByTitle(title);
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdate(@RequestBody Book book) {
        adapter.save(book);
        // return 201 Created with no specific location (we don't have stable id guarantees)
        return ResponseEntity.created(URI.create("/api/books")).build();
    }
}