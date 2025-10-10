package com.bookstore.api;

import com.bookstore.Book;
import com.bookstore.spring.BookServiceAdapter;
import com.bookstore.spring.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    private final BookServiceAdapter adapter;
    private final SessionAuth sessionAuth;

    public BooksController(BookServiceAdapter adapter, SessionAuth sessionAuth) {
        this.adapter = adapter;
        this.sessionAuth = sessionAuth;
    }

    @GetMapping
    public List<Book> all() { return adapter.listAll(); }

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

    // ADMIN: single upsert
    @PostMapping
    public ResponseEntity<?> createOrUpdate(HttpSession session, @RequestBody Book book) {
        if (!sessionAuth.isAdmin(session)) return ResponseEntity.status(403).body("Forbidden: admin only");
        if (book == null || book.getTitle() == null || book.getTitle().isBlank())
            return ResponseEntity.badRequest().body("Book requires a non-empty title");
        adapter.save(book);
        return ResponseEntity.created(URI.create("/api/books")).build();
    }

    // ADMIN: bulk upsert
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkUpsert(HttpSession session, @RequestBody List<Book> books) {
        if (!sessionAuth.isAdmin(session)) return ResponseEntity.status(403).body("Forbidden: admin only");
        if (books == null || books.isEmpty()) return ResponseEntity.badRequest().body("No books provided");
        int count = 0;
        for (Book b : books) {
            if (b == null || b.getTitle() == null || b.getTitle().isBlank())
                return ResponseEntity.badRequest().body("Each book needs a non-empty title");
            adapter.save(b);
            count++;
        }
        return ResponseEntity.ok(Map.of("count", count));
    }

    // ADMIN: delete by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(HttpSession session, @PathVariable String id) {
        if (!sessionAuth.isAdmin(session)) return ResponseEntity.status(403).body("Forbidden: admin only");
        adapter.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // (Optional) ADMIN: delete by title
    @DeleteMapping("/by-title")
    public ResponseEntity<?> deleteByTitle(HttpSession session, @RequestParam("title") String title) {
        if (!sessionAuth.isAdmin(session)) return ResponseEntity.status(403).body("Forbidden: admin only");
        int deleted = adapter.deleteByTitle(title); // or 0/1 if your impl is simple
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }
}