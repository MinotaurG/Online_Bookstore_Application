# 📚 Online Bookstore Application


This repository contains the source code and documentation for an **Online Bookstore Application**, developed as a 10-day learning and training project.  


Each day focuses on one deliverable: requirements, design, implementation, testing, and deployment. Deliverables are stored in the [`docs/`](docs/) folder.


---


## 🚀 Project Overview


- **Core idea**: An online bookstore where users can search for books, view details, add them to a cart, and checkout.  
- **Tech Stack**:  
  - Java 17  
  - Maven  
  - DynamoDB Local (NoSQL database for books and search)  
  - Jenkins (for CI/CD in later stages)  
- **Target Environment**: Local development (Windows + PowerShell) and Linux VM  


---


## 📅 Daily Deliverables


- **Day 1 — Requirement Gathering** ✅  
  - [Requirement Specification Document](docs/DAY1_REQUIREMENT_SPEC.md)  
  - Defines functional and non-functional requirements, user stories, and acceptance criteria.  


- **Day 2 — OOAD & Design** ✅  
  - [Design Diagrams](docs/DAY2_DESIGN_DIAGRAMS.md)  
  - Includes Class, Use Case, and ER diagrams.  


- **Day 3 — Environment Setup** ✅  
  - [Environment Setup](docs/DAY3_ENV_SETUP.MD)  
  - Project runs with `Hello Bookstore` message + `Book` & `User` classes.  


- **Day 4 — Implement Book Search** ✅  
  - [Book Search with DynamoDB Local](docs/DAY4_BOOK_SEARCH.md)  
  - Implemented `BookService` with CRUD methods, GSI query (by title), and scan-based search (by author/title).  
  - Seeded sample books (*Clean Code*, *Design Patterns*) idempotently.  


- **Day 5 — Cart & Checkout** ✅  
  - [Cart & Checkout](docs/DAY5_CART_CHECKOUT.md)  
  - Implemented `Cart`, `CartItem`, and `CheckoutService` classes.  
  - Orders are persisted in-memory using `InMemoryOrderRepository`.  
  - Added `CartDemo` to showcase add → view cart → checkout flow.  
  - Unit test (`CartCheckoutTest`) added for checkout logic.
  - Stock decrements after checkout; running the demo twice without restarting DynamoDB Local will throw a **"Not enough stock"** exception (intentional behaviour to prevent overselling)


*(See the full 10-day plan in [Online Bookstore Application.pdf](Online%20Bookstore%20Application.pdf)).*


---


## 📂 Repository Structure


```text
.
├── README.md                        # Project overview (this file)
├── Online Bookstore Application.pdf # Training handbook with 10-day plan
├── docs/                            # Daily deliverables
│   ├── DAY1_REQUIREMENT_SPEC.md     # Day 1: Requirement Specification
│   ├── DAY2_DESIGN_DIAGRAMS.md      # Day 2: OOAD & Design Diagrams
│   ├── DAY3_ENV_SETUP.md            # Day 3: Environment Setup
│   ├── DAY4_BOOK_SEARCH.md          # Day 4: Book Search (DynamoDB Local)
│   └── DAY5_CART_CHECKOUT.md        # Day 5: Cart & Checkout
├── src/                             # Java source code
│   └── main/java/com/bookstore/    
│       ├── Book.java
│       ├── User.java
│       ├── BookService.java
│       ├── CheckoutService.java
│       ├── Cart.java
│       ├── CartItem.java
│       ├── Order.java
│       ├── OrderRepository.java
│       ├── InMemoryOrderRepository.java
│       ├── OrderIdGenerator.java
│       ├── Main.java                # Day 4 demo entry point
│       └── demo/
│           └── CartDemo.java        # Day 5 demo entry point
└── src/test/java/com/bookstore/
    └── CartCheckoutTest.java        # Unit test for Day 5
```


## ▶️ How to Run
**1. Start DynamoDB Local** (PowerShell, JAR mode)
```bash
java "-Djava.library.path=.\DynamoDBLocal_lib" -jar .\DynamoDBLocal.jar -sharedDb -port 8000
```
Keep this window open while running the app.


**2. Compile the project**
```bash
mvn clean compile
```


**3. Run Book Search Demo (Day 4)**
```bash
mvn "-Dexec.mainClass=com.bookstore.Main" exec:java
```
- **Expected Output** (Day 4):
```text
All books:
Clean Code by Robert C. Martin (Programming) price=35.5 stock=10
Design Patterns by Erich Gamma, et al. (Programming) price=42 stock=5


Exact query by title (GSI):
Clean Code by Robert C. Martin (Programming) price=35.5 stock=10


Contains search 'Design':
Design Patterns by Erich Gamma, et al. (Programming) price=42 stock=5
```


**4. Run Cart & Checkout Demo** (Day 5)
```text
mvn "-Dexec.mainClass=com.bookstore.demo.CartDemo" exec:java
```
- **Expected Output** (Day 5):
```text
Cart contents:
Clean Code x1 @ 35.50 each -> 35.50
Design Patterns x2 @ 42.00 each -> 84.00
Cart total: 119.50


Checkout complete. Order details:
Order 123e4567-... for demoUser at 2025-09-15T12:34:56Z
 - Clean Code x1 @ 35.50 each -> 35.50
 - Design Patterns x2 @ 42.00 each -> 84.00
Total: 119.50


Orders in repository: 1
```
- **⚠️ Note:** If you run the demo again without restarting DynamoDB Local, you may see:
```text
java.lang.IllegalStateException: Not enough stock for: Design Patterns
```
This is intentional — it demonstrates stock consistency (no overselling). Restart DynamoDB Local with -inMemory for a fresh run  

---

