package com.bookstore.spring;

import com.bookstore.Book;
import com.bookstore.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationsController {

    private final RecommendationService svc;
    private final RecommendationServiceAdapter adapter;

    public RecommendationsController(RecommendationService svc) {
        this.svc = svc;
        this.adapter = new RecommendationServiceAdapter(svc);
    }

    @GetMapping
    public ResponseEntity<List<Book>> recommendations(@RequestParam(name = "user", required = false, defaultValue = "demoUser") String user) {
        List<Book> recs = adapter.recommendForUser(user);
        return ResponseEntity.ok(recs);
    }
}