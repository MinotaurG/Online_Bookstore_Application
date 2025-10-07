# Project Report — Online Bookstore Application

**Day 10 Deliverable:** Live demo + Final Project Report  
**Date:** 10/2/2025

---

## 1. Requirements
- Functional requirements: Book search, Cart & Checkout, Recommendations, Browsing History.  
- Non-functional: stock consistency, performance, maintainability.  
- User stories & acceptance criteria defined in Day 1 doc.

---

## 2. Design
- OOAD approach with UML diagrams (Day 2).  
- Class responsibilities:  
  - `BookService` → CRUD & search.  
  - `Cart` + `CheckoutService` → Order flow.  
  - `RecommendationService` → Suggested books.  
  - `BrowsingHistory` → LinkedList-based recent views.  
- ER diagram defined entities & relationships (Day 2).  

---

## 3. Implementation
- Built with **Java 17** + **Maven**.  
- Persistence: DynamoDB Local (option A schema).  
- Key classes: Book, User, Cart, Order, BookService, CheckoutService, RecommendationService, BrowsingHistory.  
- Demo entrypoints: `Main`, `CartDemo`, `RecommendationsDemo`, `BrowsingHistoryDemo`, `FullAppDemo`.  
- `DemoLauncher` multiplexer to run all demos from one JAR.

---

## 4. Testing
- **Unit tests:** BookService, CheckoutService, Cart, Recommendations, BrowsingHistory.  
- **Integration tests:** BookServiceIT, RecommendationServiceIT against DynamoDB Local.  
- **Coverage:** ~36% (JaCoCo). Plan to extend to >70%.  
- Automated pipeline runs tests with `mvn verify`.

---

## 5. Deployment
- **CI/CD (Day 9):** Jenkinsfile automates build → test → package → deploy.  
- **Target environment:** Linux VM (simulated with WSL).  
- **Deployment flow:**  
  1. Jenkins builds shaded JAR.  
  2. JAR copied to `/opt/bookstore/`.  
  3. Managed by systemd (`online-bookstore.service`).  
  4. Smoke test run (Add → Checkout → Order Success).  
- Logs available via `journalctl -u online-bookstore`.

---

## 6. Demo (Day 10)
- Running `FullAppDemo` shows:  
  1. Seed books.  
  2. Add to cart.  
  3. Checkout.  
  4. Order created successfully.  
- This proves the system is functional end-to-end.

---

## 7. Conclusion
The **Online Bookstore Application** project was completed in 10 days with progressive deliverables.  
Final outcome: a working bookstore system with end-to-end flow (Add → Checkout → Order Success), automated build & deployment pipeline, and structured documentation.


---