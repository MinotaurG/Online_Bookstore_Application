# 📚 Online Bookstore Application

This repository contains the source code and documentation for an **Online Bookstore Application**, developed as a 10-day learning and training project.  

Each day focuses on one deliverable: requirements, design, implementation, testing, and deployment. Deliverables are stored in the [`docs/`](docs/) folder.

---

## 🚀 Project Overview

- **Core idea**: An online bookstore where users can search for books, view details, add them to a cart, and checkout.  
- **Tech Stack**:  
  - Java 17  
  - MySQL (relational persistence)  
  - AWS DynamoDB (for recommendations)  
  - Cucumber (BDD testing)  
  - Jenkins (CI/CD)  
- **Target Environment**: Linux VM  

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
  - Project runs with Hello Bookstore + Book & User classes. 

- **Day 4 — Implement Book Search**  
  - `BookService` with CRUD methods, SQL queries, and search functionality.  

*(See the full 10-day plan in [Online Bookstore Application.pdf](Online%20Bookstore%20Application.pdf)).*

---

## 📂 Repository Structure

```text
.
├── README.md                       # Project overview (this file)
├── Online Bookstore Application.pdf # Training handbook with 10-day plan
├── docs/                           # Daily deliverables
│   ├── DAY1_REQUIREMENT_SPEC.md    # Day 1: Requirement Specification
│   └── DAY2_DESIGN_DIAGRAMS.md     # Day 2: OOAD & Design (to be added)
├── src/                            # Java source code (added from Day 3 onwards)
└── tests/                          # Test files (added from Day 4 onwards)
