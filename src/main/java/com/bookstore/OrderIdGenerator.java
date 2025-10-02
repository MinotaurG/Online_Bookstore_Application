package com.bookstore;

import java.util.UUID;

public class OrderIdGenerator {
    public static String nextId() {
        return UUID.randomUUID().toString();
    }
}
