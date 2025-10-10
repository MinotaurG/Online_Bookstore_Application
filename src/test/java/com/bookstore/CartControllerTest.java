package com.bookstore.spring;

import com.bookstore.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CartController.class)
class CartControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    CheckoutService checkoutService;

    @MockBean
    InMemoryOrderRepository orderRepo;

    @Test
    void preview_ok() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 5));

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": 2
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/preview")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(20.0));
    }

    @Test
    void preview_409_when_insufficient_stock() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 1));

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": 2
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/preview")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isConflict());
    }

    @Test
    void checkout_ok_uses_username_if_present() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 5));

        // Create mock order with proper constructor
        Map<String, CartItem> items = new HashMap<>();
        Order mockOrder = new Order("order-123", "adi", items, BigDecimal.valueOf(20.0));

        when(checkoutService.checkout(eq("adi"), any(Cart.class)))
                .thenReturn(mockOrder);

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": 2
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/checkout")
                        .contentType(APPLICATION_JSON)
                        .sessionAttr("username", "adi")
                        .content(cartJson))
                .andExpect(status().isOk());

        Mockito.verify(checkoutService).checkout(eq("adi"), any(Cart.class));
    }

    @Test
    void preview_badRequest_when_book_not_found() throws Exception {
        when(bookService.getBookById("invalid-id")).thenReturn(null);

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "invalid-id",
                        "quantity": 1
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/preview")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void preview_badRequest_when_quantity_is_zero() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 5));

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": 0
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/preview")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void preview_badRequest_when_quantity_is_negative() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 5));

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": -1
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/preview")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkout_uses_anonymous_when_no_session() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 5));

        Map<String, CartItem> items = new HashMap<>();
        Order mockOrder = new Order("order-456", "anonymous", items, BigDecimal.valueOf(10.0));

        when(checkoutService.checkout(eq("anonymous"), any(Cart.class)))
                .thenReturn(mockOrder);

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": 1
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/checkout")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isOk());

        Mockito.verify(checkoutService).checkout(eq("anonymous"), any(Cart.class));
    }

    @Test
    void checkout_uses_query_param_when_provided() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 5));

        Map<String, CartItem> items = new HashMap<>();
        Order mockOrder = new Order("order-789", "guest-user", items, BigDecimal.valueOf(10.0));

        when(checkoutService.checkout(eq("guest-user"), any(Cart.class)))
                .thenReturn(mockOrder);

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": 1
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/checkout")
                        .param("user", "guest-user")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isOk());

        Mockito.verify(checkoutService).checkout(eq("guest-user"), any(Cart.class));
    }

    @Test
    void checkout_handles_insufficient_stock_error() throws Exception {
        when(bookService.getBookById("b-1"))
                .thenReturn(new Book("b-1", "T", "A", "G", BigDecimal.valueOf(10.0), 5));

        when(checkoutService.checkout(anyString(), any(Cart.class)))
                .thenThrow(new IllegalStateException("Insufficient stock for: T"));

        String cartJson = """
            {
                "items": [
                    {
                        "bookId": "b-1",
                        "quantity": 1
                    }
                ]
            }
            """;

        mvc.perform(post("/api/cart/checkout")
                        .contentType(APPLICATION_JSON)
                        .content(cartJson))
                .andExpect(status().isConflict());
    }
}