package com.bookstore.demo;

import com.bookstore.Book;
import com.bookstore.BookService;
import com.bookstore.Cart;
import com.bookstore.CheckoutService;
import com.bookstore.InMemoryOrderRepository;
import com.bookstore.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Robust Cart demo:
 *  - idempotent seed (checks if book title exists before creating)
 *  - tolerant lookup (case-insensitive)
 *  - prints helpful messages instead of throwing NoSuchElementException
 *  - closes BookService when done to avoid lingering threads
 */
public class CartDemo {
    public static void main(String[] args) {
        BookService bookService = new BookService();

        try {
            // Example demo books (use exact titles you expect elsewhere)
            seedIfMissing(bookService, "Clean Code", "Robert C. Martin", "Programming", new BigDecimal("35.50"), 10);
            seedIfMissing(bookService, "Design Patterns", "Erich Gamma, et al.", "Programming", new BigDecimal("42.00"), 5);

            List<Book> books = bookService.listAllBooks();

            // Defensive lookup helper: case-insensitive match by title
            Book cleanCode = findByTitleInsensitive(books, "Clean Code")
                    .orElseGet(() -> {
                        System.err.println("Warning: 'Clean Code' not found after seeding — proceeding with first available book as fallback.");
                        return books.isEmpty() ? null : books.get(0);
                    });

            Book designPatterns = findByTitleInsensitive(books, "Design Patterns")
                    .orElseGet(() -> {
                        System.err.println("Warning: 'Design Patterns' not found after seeding — proceeding with first available book as fallback.");
                        return books.size() > 1 ? books.get(1) : (books.isEmpty() ? null : books.get(0));
                    });

            if (cleanCode == null || designPatterns == null) {
                System.err.println("Error: Not enough books in database to run cart demo. Seed failed or DynamoDB Local not running.");
                return;
            }

            // create cart and add books
            Cart cart = new Cart();
            cart.addBook(cleanCode, 1);
            cart.addBook(designPatterns, 2);

            System.out.println("\n🛒 Cart contents:");
            cart.getItems().values().forEach(System.out::println);
            System.out.println("Cart total: " + cart.calculateTotal());

            // checkout
            InMemoryOrderRepository repo = new InMemoryOrderRepository();
            CheckoutService checkoutService = new CheckoutService(repo, bookService);

            try {
                Order order = checkoutService.checkout("demoUser", cart);
                System.out.println("\n✅ Checkout complete. Order details:");
                System.out.println(order);
                System.out.println("\n📦 Orders in repository: " + repo.findAll().size());
            } catch (IllegalStateException ise) {
                System.err.println("Checkout failed: " + ise.getMessage());
            }

        } finally {
            // ensure AWS SDK / clients are closed to avoid lingering threads
            try {
                bookService.close();
            } catch (Exception ignored) {}
        }
    }

    private static Optional<Book> findByTitleInsensitive(List<Book> books, String title) {
        return books.stream()
                .filter(b -> b.getTitle() != null && b.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    private static void seedIfMissing(BookService bookService, String title, String author, String genre, BigDecimal price, int stock) {
        if (bookService.findOneByTitleIgnoreCase(title).isEmpty()) {
            Book b = new Book(UUID.randomUUID().toString(), title, author, genre, price, stock);
            bookService.saveOrUpdateBookByTitle(b);
            System.out.println("Seeded: " + title);
        } else {
            System.out.println("Seed skipped (exists): " + title);
        }
    }
}
