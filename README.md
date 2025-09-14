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
  - [Environment Setup](docs/DAY3_ENV_SETUP.md)  
  - Project runs with `Hello Bookstore` message + `Book` & `User` classes.  

- **Day 4 — Implement Book Search** ✅  
  - [Book Search with DynamoDB Local](docs/DAY4_BOOK_SEARCH.md)  
  - Implemented `BookService` with CRUD methods, GSI query (by title), and scan-based search (by author/title).  
  - Seeded sample books (*Clean Code*, *Design Patterns*) idempotently.  

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
│   └── DAY4_BOOK_SEARCH.md          # Day 4: Book Search (DynamoDB Local)
├── src/                             # Java source code
│   └── main/java/com/bookstore/     # Book.java, User.java, BookService.java, Main.java
└── tests/                           # Test files (to be added from Day 5 onwards)
```

---

## ▶️ How to Run
- **Start DynamoDB Local (PowerShell, JAR mode):**
```text
 java "-Djava.library.path=.\DynamoDBLocal_lib" -jar .\DynamoDBLocal.jar -sharedDb -port 8000
 Keep this window open.
```

- **Compile & run the project (new PowerShell window, project root):**
```text
 mvn compile
 mvn exec:java
```

**Expected Output:**
```text
Seeded sample books.

All books:
Clean Code by Robert C. Martin (Programming) price=35.5 stock=10
Design Patterns by Erich Gamma, et al. (Programming) price=42 stock=5

Exact query by title (GSI):
Clean Code by Robert C. Martin (Programming) price=35.5 stock=10

Contains search 'Design':
Design Patterns by Erich Gamma, et al. (Programming) price=42 stock=5
```



---


