package com.bookstore.spring;

import com.bookstore.Book;
import com.bookstore.BookService;
import com.bookstore.BrowsingHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class BrowsingHistoryController {

    private final BrowsingHistoryService historyService;
    private final BookService bookService;

    public BrowsingHistoryController(BrowsingHistoryService historyService,
                                     BookService bookService) {
        this.historyService = historyService;
        this.bookService = bookService;
    }

    // accept only a bookId to avoid fragile JSON mapping issues
    public static record ViewRequest(String bookId) {
    }

    @PostMapping("/view")
    public ResponseEntity<?> view(HttpSession session, @RequestBody ViewRequest req) {
        if (req == null || req.bookId() == null || req.bookId().isBlank()) {
            return ResponseEntity.badRequest().body("Missing bookId");
        }
        Book b = bookService.getBookById(req.bookId());
        if (b == null) return ResponseEntity.badRequest().body("Book not found: " + req.bookId());

        // use real username if logged in; else per-session guest key
        String username = (String) session.getAttribute("username");
        String key = (username != null && !username.isBlank()) ? username : "guest:" + session.getId();

        historyService.view(key, b);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> recent(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String key = (username != null && !username.isBlank()) ? username : "guest:" + session.getId();
        return ResponseEntity.ok(historyService.getRecent(key));
    }
}