package com.bookstore.spring;

import com.bookstore.Book;
import com.bookstore.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService svc;

    public BookController(BookService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Book> listAll() {
        return svc.listAllBooks();
    }

    // exact title query via GSI
    @GetMapping("/title/{title}")
    public List<Book> findByTitle(@PathVariable String title) {
        return svc.findByTitle(title);
    }

    // search q param: /api/books/search?q=Design
    @GetMapping("/search")
    public List<Book> search(@RequestParam("q") String q) {
        return svc.searchByTitleOrAuthorContains(q);
    }
}