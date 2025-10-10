package com.bookstore.spring;


import com.bookstore.Book;
import com.bookstore.BookService;
import com.bookstore.Cart;
import com.bookstore.CheckoutService;
import com.bookstore.InMemoryOrderRepository;
import com.bookstore.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpSession;   // <-- add this import


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

            // NEW: validate quantity
            if (it.quantity() <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be >= 1 for " + b.getTitle());
            }

            // NEW: enforce stock at preview time (fail fast)
            if (b.getStockQuantity() < it.quantity()) {
                return ResponseEntity.status(409).body("Insufficient stock for: " + b.getTitle());
            }

            cart.addBook(b, it.quantity());
        }


        Map<String, Object> resp = new HashMap<>();
        resp.put("items", cart.getItems().values());
        resp.put("total", cart.calculateTotal());
        return ResponseEntity.ok(resp);
    }


    /**
     * Checkout the cart.
     * Uses session user if present; falls back to ?user=... and then "anonymous".
     *
     * POST /api/cart/checkout
     * Body: CartRequest JSON
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(HttpSession session,
                                      @RequestBody CartRequest cartReq,
                                      @RequestParam(name = "user", required = false) String userOverride) {
        if (cartReq == null || cartReq.items() == null) {
            return ResponseEntity.badRequest().body("Invalid cart payload");
        }


        // Resolve username: prefer session, then explicit override, else "anonymous"
        String username = (String) session.getAttribute("username");
        if (username == null || username.isBlank()) {
            if (userOverride != null && !userOverride.isBlank()) {
                username = userOverride;
            } else {
                username = "anonymous";
            }
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
            Order order = checkoutService.checkout(username, cart);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException ise) {
            return ResponseEntity.status(409).body(ise.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Checkout failed: " + ex.getMessage());
        }
    }
}