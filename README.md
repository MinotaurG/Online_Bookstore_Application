# 📚 Online Bookstore Application

This repository contains the source code and documentation for an **Online Bookstore Application**, developed as a 10-day learning and training project.  

Each day focuses on one deliverable: requirements, design, implementation, testing, and deployment. Deliverables are stored in the [`docs/`](docs/) folder.

---

## ⚡ Quickstart (Full Demo in 3 Steps)

### 1. Build the fat JAR
```bash
mvn clean package
```
### 2. Start DynamoDB Local (inMemory)

Windows PowerShell
```
java "-Djava.library.path=.\DynamoDBLocal_lib" -jar .\DynamoDBLocal.jar -inMemory -port 8000
```
WSL / Linux
```
java -Djava.library.path=./DynamoDBLocal_lib -jar ./DynamoDBLocal.jar -inMemory -port 8000 &
```

Keep this running. DynamoDB resets every time you restart (fresh state each demo).

### 3. Run the full end-to-end demo
```
java -jar target/online-bookstore-0.0.1-SNAPSHOT-all.jar full
```
Expected: books are seeded → cart built → checkout success → order printed.


---

## 🚀 Project Overview

- **Core idea**: An online bookstore where users can search for books, view details, add them to a cart, and checkout.

- **Tech Stack**:

- Java 17

- Maven

- DynamoDB Local (NoSQL database, inMemory mode)

- Jenkins (for CI/CD pipeline)


- **Target Environment**: Local development (Windows + PowerShell) and Linux VM (WSL used for deployment simulation).



---

## 📅 Daily Deliverables

- **Day 1 — Requirement Gathering** ✅

  - [Requirement Specification Document](docs/DAY1_REQUIREMENT_SPEC.md)  


- **Day 2 — OOAD & Design** ✅

  - [Design Diagrams](docs/DAY2_DESIGN_DIAGRAMS.md)  


- **Day 3 — Environment Setup** ✅

  - [Environment Setup](docs/DAY3_ENV_SETUP.MD)


- **Day 4 — Implement Book Search** ✅

  - [Book Search with DynamoDB Local](docs/DAY4_BOOK_SEARCH.md)  


- **Day 5 — Cart & Checkout** ✅
  - [Cart & Checkout](docs/DAY5_CART_CHECKOUT.md)

- **Day 6 — DynamoDB Integration** ✅

  - [Recommended Books](docs/DAY6_BOOK_RECOMMENDATIONS.md)

- **Day 7 — Browsing History** ✅

  - [Browsing History](docs/DAY7_BROWSING_HISTORY.md)


- **Day 8 — Testing & Quality** ✅

  - [Tests & Coverage](docs/DAY8_TESTS.md)


- **Day 9 — CI/CD Pipeline** ✅
  - [CI_CD with Jenkins](docs/DAY9_CI_CD.md)

- **Day 10 — Final Deployment & Report** ✅

  - [Final Project Report](docs/PROJECT_REPORT.md)



*(See the full 10-day plan in [Online Bookstore Application.pdf](Online%20Bookstore%20Application.pdf)).*


---

📂 Repository Structure

.
├── README.md
├── Jenkinsfile
├── Online Bookstore Application.pdf        # Training handbook
├── docs/                                   # Daily deliverables
│   ├── DAY1_REQUIREMENT_SPEC.md
│   ├── DAY2_DESIGN_DIAGRAMS.md
│   ├── DAY3_ENV_SETUP.md
│   ├── DAY4_BOOK_SEARCH.md
│   ├── DAY5_CART_CHECKOUT.md
│   ├── DAY6_DYNAMODB_RECOMMENDATIONS.md
│   ├── DAY7_BROWSING_HISTORY.md
│   ├── DAY8_TESTING.md
│   ├── DAY9_CICD_SETUP.md
│   └── DAY10_DEPLOYMENT.md
│   └── PROJECT_REPORT.md
├── src/
│   └── main/java/com/bookstore/
│       ├── Book.java
│       ├── BookService.java                # Uses DynamoDB Local (idempotent seeding)
│       ├── Cart.java
│       ├── CartItem.java
│       ├── CheckoutService.java
│       ├── InMemoryOrderRepository.java
│       ├── Order.java
│       ├── OrderIdGenerator.java
│       ├── RecommendationService.java
│       ├── User.java
│       ├── Main.java                       # Book Search Demo
│       └── demo/
│           ├── CartDemo.java               # Cart & Checkout Demo
│           ├── RecommendationsDemo.java    # Book Recommendation Demo
│           ├── BrowsingHistoryDemo.java    # Browsing History Demo
│           └── DemoLauncher.java           # Unified entrypoint (full demo)
└── pom.xml



---

## ▶️ How to Run (Detailed)

**Build**
```
mvn clean package
```
**Start DynamoDB Local (inMemory)**

PowerShell:

```
java "-Djava.library.path=.\DynamoDBLocal_lib" -jar .\DynamoDBLocal.jar -inMemory -port 8000
```
**WSL / Linux:**

```
java -Djava.library.path=./DynamoDBLocal_lib -jar ./DynamoDBLocal.jar -inMemory -port 8000 &
```
### Run Demos

**Book Search demo (Day 4)**
```
java -jar target/online-bookstore-0.0.1-SNAPSHOT-all.jar search
```
**Cart & Checkout demo (Day 5)**
```
java -jar target/online-bookstore-0.0.1-SNAPSHOT-all.jar cart
```
**Recommendations demo (Day 6)**
```
java -jar target/online-bookstore-0.0.1-SNAPSHOT-all.jar recs
```
**Browsing History demo (Day 7)**
```
java -jar target/online-bookstore-0.0.1-SNAPSHOT-all.jar history
```
**Full end-to-end demo (Day 10)**
```
java -jar target/online-bookstore-0.0.1-SNAPSHOT-all.jar full
```

---

## ⚠️ Notes

DynamoDB is always fresh because of -inMemory. Every restart gives a clean DB.

Stock decrements after checkout — if you re-run Cart demo without restart, you may see “Not enough stock”. Restart DynamoDB Local for a fresh run.

DemoLauncher simplifies running different demos (search, cart, recs, history, full).


---