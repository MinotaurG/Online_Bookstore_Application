package com.bookstore;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request for bulk update operations
 * Can update by IDs or ASINs
 */
public class BulkUpdateRequest {

    public static class BookUpdate {
        private String id;          // Optional: update by ID
        private String asin;        // Optional: update by ASIN
        private String title;       // Optional: new title
        private String author;      // Optional: new author
        private String genre;       // Optional: new genre
        private BigDecimal price;   // Optional: new price
        private Integer stockQuantity; // Optional: new stock
        private String isbn;        // Optional: new ISBN

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getAsin() { return asin; }
        public void setAsin(String asin) { this.asin = asin; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
    }

    private List<BookUpdate> updates;

    public List<BookUpdate> getUpdates() { return updates; }
    public void setUpdates(List<BookUpdate> updates) { this.updates = updates; }
}