package com.bookstore;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem {
    private final String bookId;
    private final String title;
    private final String author;      // NEW
    private final String isbn;        // NEW
    private final BigDecimal unitPrice;
    private int quantity;

    // Constructor with all fields
    public CartItem(String bookId, String title, String author, String isbn, BigDecimal unitPrice, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.unitPrice = unitPrice;
        this.quantity = Math.max(0, quantity);
    }

    // Legacy constructor (for backward compatibility)
    public CartItem(String bookId, String title, BigDecimal unitPrice, int quantity) {
        this(bookId, title, null, null, unitPrice, quantity);
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }           // NEW
    public String getIsbn() { return isbn; }               // NEW
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public void increaseQuantity(int delta) {
        if (delta <= 0) return;
        this.quantity += delta;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem that = (CartItem) o;
        return Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    @Override
    public String toString() {
        return String.format("%s by %s x%d @ %s each -> %s",
                title, author != null ? author : "Unknown", quantity, unitPrice, totalPrice());
    }
}