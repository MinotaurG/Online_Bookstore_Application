package com.bookstore.api;

import com.bookstore.Book;
import com.bookstore.BulkDeleteRequest;
import com.bookstore.BulkUpdateRequest;
import com.bookstore.DeterministicId;
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

        // Generate deterministic ID if missing or empty
        if (book.getId() == null || book.getId().isBlank()) {
            book.setId(DeterministicId.forBook(book.getTitle(), book.getAuthor()));
        }

        // Ensure ASIN
        book.ensureAsin();

        adapter.save(book);
        return ResponseEntity.created(URI.create("/api/books/" + book.getId())).build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulkUpsert(HttpSession session, @RequestBody List<Book> books) {
        if (!sessionAuth.isAdmin(session)) return ResponseEntity.status(403).body("Forbidden: admin only");
        if (books == null || books.isEmpty()) return ResponseEntity.badRequest().body("No books provided");

        int count = 0;
        for (Book b : books) {
            if (b == null || b.getTitle() == null || b.getTitle().isBlank())
                return ResponseEntity.badRequest().body("Each book needs a non-empty title");

            // Generate deterministic ID if missing or empty
            if (b.getId() == null || b.getId().isBlank()) {
                b.setId(DeterministicId.forBook(b.getTitle(), b.getAuthor()));
            }

            // Ensure ASIN
            b.ensureAsin();

            adapter.save(b);
            count++;
        }
        return ResponseEntity.ok(Map.of("count", count));
    }

    // ADMIN: Migrate existing books to have ASINs (run once)
    @PostMapping("/migrate-asin")
    public ResponseEntity<?> migrateAsin(HttpSession session) {
        if (!sessionAuth.isAdmin(session))
            return ResponseEntity.status(403).body("Forbidden: admin only");

        int updated = adapter.migrateToAsin();
        return ResponseEntity.ok(Map.of(
                "message", "Migration complete",
                "booksUpdated", updated
        ));
    }

    // NEW: Get book by ASIN
    @GetMapping("/asin/{asin}")
    public ResponseEntity<Book> getByAsin(@PathVariable String asin) {
        Book book = adapter.findByAsin(asin);
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.notFound().build();
    }

    // ADMIN: Delete by ASIN (in addition to delete by ID)
    @DeleteMapping("/asin/{asin}")
    public ResponseEntity<?> deleteByAsin(HttpSession session, @PathVariable String asin) {
        if (!sessionAuth.isAdmin(session))
            return ResponseEntity.status(403).body("Forbidden: admin only");

        boolean deleted = adapter.deleteByAsin(asin);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
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

    // ADMIN: Bulk update books
    @PutMapping("/bulk")
    public ResponseEntity<?> bulkUpdate(HttpSession session, @RequestBody BulkUpdateRequest request) {
        if (!sessionAuth.isAdmin(session))
            return ResponseEntity.status(403).body("Forbidden: admin only");

        if (request.getUpdates() == null || request.getUpdates().isEmpty()) {
            return ResponseEntity.badRequest().body("No updates provided");
        }

        int updated = adapter.bulkUpdate(request.getUpdates());
        return ResponseEntity.ok(Map.of(
                "message", "Bulk update complete",
                "updated", updated,
                "requested", request.getUpdates().size()
        ));
    }

    // ADMIN: Bulk delete books
    @DeleteMapping("/bulk")
    public ResponseEntity<?> bulkDelete(HttpSession session, @RequestBody BulkDeleteRequest request) {
        if (!sessionAuth.isAdmin(session))
            return ResponseEntity.status(403).body("Forbidden: admin only");

        if ((request.getIds() == null || request.getIds().isEmpty()) &&
                (request.getAsins() == null || request.getAsins().isEmpty())) {
            return ResponseEntity.badRequest().body("No IDs or ASINs provided");
        }

        int deleted = adapter.bulkDelete(request.getIds(), request.getAsins());
        return ResponseEntity.ok(Map.of(
                "message", "Bulk delete complete",
                "deleted", deleted
        ));
    }

    // ADMIN: Download catalog with filters (JSON)
    @GetMapping("/export/json")
    public ResponseEntity<?> exportJson(
            HttpSession session,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock) {

        if (!sessionAuth.isAdmin(session))
            return ResponseEntity.status(403).body("Forbidden: admin only");

        List<Book> books = adapter.filterBooks(genre, author, minStock, maxStock);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=catalog.json")
                .header("Content-Type", "application/json")
                .body(books);
    }

    // ADMIN: Download catalog as CSV
    @GetMapping("/export/csv")
    public ResponseEntity<?> exportCsv(
            HttpSession session,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock) {

        if (!sessionAuth.isAdmin(session))
            return ResponseEntity.status(403).body("Forbidden: admin only");

        List<Book> books = adapter.filterBooks(genre, author, minStock, maxStock);

        // Convert to CSV
        StringBuilder csv = new StringBuilder();
        csv.append("ASIN,ID,Title,Author,Genre,Price,Stock,ISBN\n");

        for (Book book : books) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%d,\"%s\"\n",
                    book.getAsin() != null ? book.getAsin() : "",
                    book.getId() != null ? book.getId() : "",
                    book.getTitle() != null ? book.getTitle().replace("\"", "\"\"") : "",
                    book.getAuthor() != null ? book.getAuthor().replace("\"", "\"\"") : "",
                    book.getGenre() != null ? book.getGenre() : "",
                    book.getPrice() != null ? book.getPrice() : 0.0,
                    book.getStockQuantity() != null ? book.getStockQuantity() : 0,
                    book.getIsbn() != null ? book.getIsbn() : ""
            ));
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=catalog.csv")
                .header("Content-Type", "text/csv")
                .body(csv.toString());
    }

}