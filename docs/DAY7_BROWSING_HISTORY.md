# Day 7 — Browsing History (LinkedList)

**Deliverable:** Implement browsing history using a LinkedList. When a user views a book, add it to the history. Provide a feature to display “Recently Browsed Books” in console/UI.  
**Date:** 9/22/2025

---

## Goals
- Use a **LinkedList** to track browsing history.  
- Ensure the list is **most-recent-first**.  
- Handle duplicates: if a book is viewed again, move it to the front.  
- Limit history size (e.g., 10 most recent).  
- Provide a demo that prints recent browsing history to console.  

---

## Files added
- `src/main/java/com/bookstore/BrowsingHistory.java`  
- `src/main/java/com/bookstore/BrowsingHistoryService.java`  
- `src/main/java/com/bookstore/demo/BrowsingHistoryDemo.java`  
- `src/test/java/com/bookstore/BrowsingHistoryTest.java`

---

## How to run
1. Start DynamoDB Local (only if BookService is used to seed books):
   ```bash
   java "-Djava.library.path=.\DynamoDBLocal_lib" -jar .\DynamoDBLocal.jar -inMemory -port 8000
    ```
2. Compile:
  ```bash
  mvn clean compile
  ```

3. Run demo:
```bash
mvn "-Dexec.mainClass=com.bookstore.demo.BrowsingHistoryDemo" exec:java
```



---

### Sample expected output
```text
Seeded demo books for browsing demo.
Recently browsed by demoUser:
Design Patterns by Erich Gamma, et al.
Effective Java by Joshua Bloch
Clean Code by Robert C. Martin
```

---

## Notes

Browsing history is in-memory only (non-persistent). Restarting the app clears history.

Data structure choice: LinkedList is efficient for add/remove at both ends and moving items.

For production, browsing history could be persisted (e.g., Redis, DynamoDB) for cross-session tracking.

This day’s focus is on data structures, not persistence.



---