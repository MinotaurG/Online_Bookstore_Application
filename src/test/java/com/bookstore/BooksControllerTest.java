package com.bookstore.api;

import com.bookstore.Book;
import com.bookstore.spring.BookServiceAdapter;
import com.bookstore.spring.SessionAuth;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BooksController.class)
class BooksControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BookServiceAdapter adapter;

    @MockBean
    SessionAuth sessionAuth;

    @Test
    void listAll_ok() throws Exception {
        when(adapter.listAll()).thenReturn(List.of(
                new Book("b-1", "T1", "A1", "G1", BigDecimal.valueOf(10.0), 5)
        ));

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("b-1"))
                .andExpect(jsonPath("$[0].title").value("T1"));
    }

    @Test
    void getById_found() throws Exception {
        when(adapter.findById("b-1")).thenReturn(Optional.of(
                new Book("b-1", "T1", "A1", "G1", BigDecimal.valueOf(10.0), 5)
        ));

        mvc.perform(get("/api/books/b-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("b-1"));
    }

    @Test
    void getById_notFound() throws Exception {
        when(adapter.findById("nope")).thenReturn(Optional.empty());

        mvc.perform(get("/api/books/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_or_update_forbidden_if_not_admin() throws Exception {
        when(sessionAuth.isAdmin(any())).thenReturn(false);

        String bookJson = """
            {
                "title": "T1",
                "author": "A1",
                "genre": "G1",
                "price": 10,
                "stockQuantity": 2
            }
            """;

        mvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_or_update_created_if_admin() throws Exception {
        when(sessionAuth.isAdmin(any())).thenReturn(true);

        String bookJson = """
            {
                "title": "T1",
                "author": "A1",
                "genre": "G1",
                "price": 10,
                "stockQuantity": 2
            }
            """;

        mvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/api/books"));

        Mockito.verify(adapter).save(any(Book.class));
    }

    @Test
    void search_returns_matching_books() throws Exception {
        when(adapter.search("Harry")).thenReturn(List.of(
                new Book("b-1", "Harry Potter", "J.K. Rowling", "Fantasy", BigDecimal.valueOf(500.0), 10)
        ));

        mvc.perform(get("/api/books/search").param("q", "Harry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Harry Potter"));
    }

    @Test
    void deleteById_forbidden_if_not_admin() throws Exception {
        when(sessionAuth.isAdmin(any())).thenReturn(false);

        mvc.perform(delete("/api/books/b-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteById_success_if_admin() throws Exception {
        when(sessionAuth.isAdmin(any())).thenReturn(true);

        mvc.perform(delete("/api/books/b-1"))
                .andExpect(status().isNoContent());

        Mockito.verify(adapter).deleteById("b-1");
    }
}