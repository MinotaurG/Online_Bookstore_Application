package com.bookstore.spring;

import com.bookstore.Book;
import com.bookstore.BookService;
import com.bookstore.Cart;
import com.bookstore.CartItem;
import com.bookstore.CheckoutService;
import com.bookstore.InMemoryOrderRepository;
import com.bookstore.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for cart preview and checkout.
 *
 * DTOs are declared as nested records to avoid separate files.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final BookService bookService;
    private final CheckoutService checkoutService;
    private final InMemoryOrderRepository orderRepo;

    public CartController(BookService bookService,
                          CheckoutService checkoutService,
                          InMemoryOrderRepository orderRepo) {
        this.bookService = bookService;
        this.checkoutService = checkoutService;
        this.orderRepo = orderRepo;
    }

    // Nested DTOs (keeps this file self-contained)
    public static record CartItemRequest(String bookId, int quantity) {}
    public static record CartRequest(List<CartItemRequest> items) {}
    public static record CheckoutResponse(String orderId, String message) {}

    /**
     * Preview the cart: returns items (CartItem list) and total.
     * POST /api/cart/preview
     */
    @PostMapping("/preview")
    public ResponseEntity<?> preview(@RequestBody CartRequest cartReq) {
        if (cartReq == null || cartReq.items() == null) {
            return ResponseEntity.badRequest().body("Invalid cart payload");
        }

        Cart cart = new Cart();
        for (CartItemRequest it : cartReq.items()) {
            if (it == null || it.bookId() == null) {
                return ResponseEntity.badRequest().body("Invalid cart item");
            }
            Book b = bookService.getBookById(it.bookId());
            if (b == null) return ResponseEntity.badRequest().body("Book not found: " + it.bookId());
            cart.addBook(b, it.quantity());
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("items", cart.getItems().values());
        resp.put("total", cart.calculateTotal());
        return ResponseEntity.ok(resp);
    }

    /**
     * Checkout the cart. POST /api/cart/checkout?user=demoUser
     * Body: CartRequest JSON
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam(name = "user", required = false, defaultValue = "anonymous") String user,
                                      @RequestBody CartRequest cartReq) {
        if (cartReq == null || cartReq.items() == null) {
            return ResponseEntity.badRequest().body("Invalid cart payload");
        }

        Cart cart = new Cart();
        for (CartItemRequest it : cartReq.items()) {
            if (it == null || it.bookId() == null) {
                return ResponseEntity.badRequest().body("Invalid cart item");
            }
            Book b = bookService.getBookById(it.bookId());
            if (b == null) return ResponseEntity.badRequest().body("Book not found: " + it.bookId());
            cart.addBook(b, it.quantity());
        }

        try {
            Order order = checkoutService.checkout(user, cart);
            // Return the entire Order object (no need to call getId() here).
            return ResponseEntity.ok(order);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(409).body(ise.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Checkout failed: " + ex.getMessage());
        }
    }
}