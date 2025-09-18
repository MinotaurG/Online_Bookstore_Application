package com.bookstore.demo;

import com.bookstore.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartDemo {
    public static void main(String[] args) {
        // Use BookService (already backed by DynamoDB Local for persistence)
        BookService bookService = new BookService();

        // Seed 2 demo books if they don't already exist
        if (bookService.listAllBooks().isEmpty()) {
            Book b1 = new Book(UUID.randomUUID().toString(), "Clean Code", "Robert C. Martin",
                    "Programming", new BigDecimal("35.50"), 10);
            Book b2 = new Book(UUID.randomUUID().toString(), "Design Patterns", "Erich Gamma, et al.",
                    "Programming", new BigDecimal("42.00"), 5);
            bookService.saveBook(b1);
            bookService.saveBook(b2);
            System.out.println("📚 Seeded demo books into DynamoDB Local.");
        }

        // Retrieve books from DB
        List<Book> books = bookService.listAllBooks();
        Book cleanCode = books.stream().filter(b -> b.getTitle().equals("Clean Code")).findFirst().orElseThrow();
        Book designPatterns = books.stream().filter(b -> b.getTitle().equals("Design Patterns")).findFirst().orElseThrow();

        // Create cart
        Cart cart = new Cart();
        cart.addBook(cleanCode, 1);
        cart.addBook(designPatterns, 2);

        System.out.println("\nCart contents:");
        cart.getItems().values().forEach(System.out::println);
        System.out.println("Cart total: " + cart.calculateTotal());

        // Checkout
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        CheckoutService checkoutService = new CheckoutService(repo, bookService);

        Order order = checkoutService.checkout("demoUser", cart);

        System.out.println("\nCheckout complete. Order details:");
        System.out.println(order);

        // Verify order repository
        System.out.println("\nOrders in repository: " + repo.findAll().size());
        bookService.close(); // ensures DynamoDB client shuts down
    }

}
