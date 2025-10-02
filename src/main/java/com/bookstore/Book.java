package com.bookstore;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.math.BigDecimal;

@DynamoDbBean
public class Book {
    private String id;
    private String title;
    private String author;
    private String genre;
    private BigDecimal price;
    private Integer stockQuantity;

    public Book() {}

    public Book(String id, String title, String author, String genre, BigDecimal price, Integer stockQuantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // This annotation tells the Enhanced client that "title" is the partition key of the GSI named "TitleIndex"
    @DynamoDbAttribute("title")
    @DynamoDbSecondaryPartitionKey(indexNames = {"TitleIndex"})
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @DynamoDbAttribute("author")
    // optionally add @DynamoDbSecondaryPartitionKey(indexNames = {"AuthorIndex"}) if you create an AuthorIndex GSI
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    @DynamoDbAttribute("genre")
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    @DynamoDbAttribute("price")
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @DynamoDbAttribute("stockQuantity")
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    @Override
    public String toString() {
        return String.format("%s by %s (%s) price=%s stock=%d", title, author, genre, price, stockQuantity);
    }
}
