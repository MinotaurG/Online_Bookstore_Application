package com.bookstore;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Handles checkout flow:
 * - validate cart not empty
 * - create Order
 * - persist Order to repository
 * - (optional) decrement stock in BookService (demo mode)
 */
public class CheckoutService {
    private final OrderRepository orderRepo;
    private final BookService bookService; // used for validation/stock optionally

    public CheckoutService(OrderRepository orderRepo, BookService bookService) {
        this.orderRepo = Objects.requireNonNull(orderRepo);
        this.bookService = bookService; // can be null if you don't need stock checks
    }

    /**
     * Checkout returns the created Order.
     * username can be null for anonymous orders in demo mode.
     */
    public Order checkout(String username, Cart cart) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // validate availability (optional)
        for (Map.Entry<String, CartItem> e : cart.getItems().entrySet()) {
            String bookId = e.getKey();
            CartItem item = e.getValue();

            if (bookService != null) {
                Book b = bookService.getBookById(bookId);
                if (b == null) {
                    throw new IllegalStateException("Book not found: " + bookId);
                }
                // simple stock check (if Book has stockQuantity)
                if (b.getStockQuantity() != null && b.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Not enough stock for: " + b.getTitle());
                }
            }
        }

        BigDecimal total = cart.calculateTotal();
        String orderId = OrderIdGenerator.nextId();
        Order order = new Order(orderId, username, cart.getItems(), total);

        // persist order
        orderRepo.save(order);

        // optionally decrement stock in BookService (demo, not transactional)
        if (bookService != null) {
            for (CartItem item : cart.getItems().values()) {
                Book b = bookService.getBookById(item.getBookId());
                if (b != null && b.getStockQuantity() != null) {
                    int newStock = b.getStockQuantity() - item.getQuantity();
                    if (newStock < 0) newStock = 0;
                    b.setStockQuantity(newStock);
                    bookService.saveOrUpdateBookByTitle(b); // overwrite in DynamoDB; BookService.saveBook exists
                }
            }
        }

        // clear cart after checkout
        cart.clear();

        return order;
    }
}
