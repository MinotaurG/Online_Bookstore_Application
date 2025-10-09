package com.bookstore.demo;

import com.bookstore.Book;
import com.bookstore.BookService;

import java.util.*;
import java.util.stream.Collectors;

public class CleanupDemo {
    public static void main(String[] args) {
        BookService svc = new BookService();
        try {
            List<Book> all = svc.listAllBooks();

            // group by normalized title
            Map<String, List<Book>> byTitle = all.stream()
                    .collect(Collectors.groupingBy(b -> normalize(b.getTitle())));

            int deleted = 0;
            for (Map.Entry<String, List<Book>> e : byTitle.entrySet()) {
                List<Book> list = e.getValue();
                if (list.size() <= 1) continue;

                // pick first to keep, delete the rest
                list.sort(Comparator.comparing(Book::getId));
                List<Book> toDelete = list.subList(1, list.size());
                for (Book d : toDelete) {
                    System.out.println("Deleting duplicate: " + d.getTitle() + " id=" + d.getId());
                    svc.deleteBook(d.getId());
                    deleted++;
                }
            }
            System.out.println("Cleanup done. Deleted duplicates: " + deleted);
        } finally {
            svc.close();
        }
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}