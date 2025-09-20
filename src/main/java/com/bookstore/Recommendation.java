package com.bookstore;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class Recommendation {
    private int rank;
    private String bookId;
    private String title;
    private String reason;

    public Recommendation() {}

    public Recommendation(int rank, String bookId, String title, String reason) {
        this.rank = rank;
        this.bookId = bookId;
        this.title = title;
        this.reason = reason;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return String.format("%d. %s (bookId=%s) %s",
                rank, title, bookId, reason != null ? "[" + reason + "]" : "");
    }
}
