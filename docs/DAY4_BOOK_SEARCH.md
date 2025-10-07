# 📚 Day 4 — Book Search (DynamoDB Local)

**Deliverable:** User can search books by title/author from the database   
**Date:** 9/14/2024

---

## ✅ 1. Goals (Day 4)
- Implement `BookService` with CRUD methods.  
- Enable book search by title/author.  
- Seed database with sample books: *Clean Code*, *Design Patterns*.  
- Run demo and verify correct search results.  

---

## 📂 2. Files Implemented / Updated
- `src/main/java/com/bookstore/Book.java`  
  - Annotated DynamoDB bean with `@DynamoDbSecondaryPartitionKey(indexNames = {"TitleIndex"})`.  

- `src/main/java/com/bookstore/BookService.java`  
  - CRUD operations (`saveBook`, `getBookById`, `deleteBook`, `listAllBooks`).  
  - GSI query `findByTitle`.  
  - Scan + filter `searchByTitleOrAuthorContains`.  
  - Auto-creates table `Books` with GSI `TitleIndex`.  

- `src/main/java/com/bookstore/Main.java`  
  - Seeds sample books **idempotently** (only if table empty).  
  - Prints:
    - All books  
    - Exact query by title (via GSI)  
    - Contains search  

- `pom.xml`  
  - Added AWS SDK v2 DynamoDB + Enhanced Client dependencies.  
  - Configured `exec-maven-plugin` with `mainClass=com.bookstore.Main`.  

---

## ▶️ 3. How to Run

1. **Start DynamoDB Local (PowerShell, JAR mode):**
   ```powershell
   java "-Djava.library.path=.\DynamoDBLocal_lib" -jar .\DynamoDBLocal.jar -sharedDb -port 8000

Keep this window open.

2. Compile & run demo (new PowerShell window, repo root):

mvn compile
mvn exec:java




---

🖥️ 4. Expected Console Output

Seeded sample books.

All books:
Clean Code by Robert C. Martin (Programming) price=35.5 stock=10
Design Patterns by Erich Gamma, et al. (Programming) price=42 stock=5

Exact query by title (GSI):
Clean Code by Robert C. Martin (Programming) price=35.5 stock=10

Contains search 'Design':
Design Patterns by Erich Gamma, et al. (Programming) price=42 stock=5

> Note: harmless SLF4J warnings may appear at startup — safe to ignore.
Order of listAllBooks() may vary because DynamoDB scan order is non-deterministic.
