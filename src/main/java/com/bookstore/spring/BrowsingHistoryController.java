package com.bookstore.spring;

import com.bookstore.BrowsingHistoryService;
import com.bookstore.Book;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class BrowsingHistoryController {
    private final BrowsingHistoryService historyService;

    public BrowsingHistoryController(BrowsingHistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/view")
    public void view(@RequestParam("user") String user, @RequestBody Book book) {
        historyService.view(user, book);
    }

    @GetMapping
    public List<Book> recent(@RequestParam("user") String user) {
        return historyService.getRecent(user);
    }
}