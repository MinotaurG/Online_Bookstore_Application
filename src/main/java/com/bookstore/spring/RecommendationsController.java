package com.bookstore.spring;

import com.bookstore.Book;
import com.bookstore.RecommendationService;
import com.bookstore.BookService; // for fallback picks
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationsController {

    private final RecommendationService svc;
    private final BookService bookService;

    public RecommendationsController(RecommendationService svc, BookService bookService) {
        this.svc = svc;
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> recommendations(
            HttpSession session,
            @RequestParam(name = "user", required = false) String userParam,
            @RequestParam(name = "limit", required = false, defaultValue = "5") int limit) {

        String sessionUser = (String) session.getAttribute("username");
        String user = (sessionUser != null && !sessionUser.isBlank())
                ? sessionUser
                : (userParam != null && !userParam.isBlank() ? userParam : "demoUser");

        List<Book> recs = svc.recommendForUser(user, limit);
        // Optional: friendly fallback when no data present
        if (recs == null || recs.isEmpty()) {
            // pick top 'limit' books as a fallback (you can sort by stock, price, etc.)
            List<Book> all = bookService.listAllBooks();
            recs = all.stream().limit(limit).toList();
        }
        return ResponseEntity.ok(recs);
    }
}