# Online Bookstore - Requirement Specification (Day 1)

**Project:** Online Bookstore Application
**Deliverable:** Requirement Specification Document (Day 1)
**Date:** 9/12/2025

---

## 1. Purpose
This document captures the functional and non-functional requirements for an Online Bookstore whre users can search for books, view details, add to cart, and checkout. It will act as the single source of truth for Day 1 deliverable and will be referenced in design and implementation tasks.

---

## 2. Scope
The Minimum Viable Product (MVP) covers:
- Browsing and searching books by title/author/genre
- Viewing book details
- Adding books to a cart
- Checkout flow with order creation and persistence
- Basic user account (signup/login) for order association

Out of scope for Day 1: Payment gateway integration, advanced recommendation engines, admin analytics dashboards (can be planned for later days).

---

## 4. Functional Requirements (FR)
FR-1: The system shall allow users to search for books by title, author, or genre.
FR-2: The system shall display book details (title, author, genre, price, availability, description).
FR-3: The system shall allow users to add one or more books to a cart with specific quantities.
FR-4: The system shall allow users to view cart contents and calculate total price.
FR-5: The system shall allow users to checkout - creating an order record persisted in the database.
FR-6: The system shall store order history for each user.
FR-7: The system shall support guest and authenticated user workflows (MVP: authenticated user flow prioritized).

---

## 5. Non-Functional Requirements (NFR)
NFR-1 (Performance): Search results must be returned to the user within 5 seconds under normal load.
NFR-2 (Security): Passwords must be stored hashed; sensitive operations must validate authentication.
NFR-3 (Scalability): System architecture should allow horizontal scaling of the search service.
NFR-4 (Availability): Core shopping flows (search, add-to-cart,checkout) should be availble 99% of the time in a rolling 30-day window (long-term goal)
NFR-5 (Responsiveness): UI should be responsive across desktop and mobile viewports.

---

## User Stories & Acceptance Criteria
**US-1**: *As a user, I want to search books by title so I can find a specifc book.*
- Acceptance: Searching for "Clean Code" returns results that include "Clean Code" with author and price.
**US-2**: *As a user, I want to view book details so I can decide to buy it.*
- Acceptance: Book detail page shows title, author, price, genre, description, and stock availability.
**US-3**: *As a user, I want to add books to my cart with quantity so I can purchase multiple copies.*
- Acceptance: Adding 3 copies of a book increases cart quantity accordingly and 'Cart.calculateTotal()' reflects price x quantity.
**US-4**: *As a user, I want to checkout so that my order is recorded.*
- Acceptance: On successful checkout, an 'orders' record and corresponding 'order_items' are created and persisted; order status is 'CONFIRMED' (or 'PENDING' if payment integration is deferred).

---

## Data Entities (high-level)
- **User**: id, username, email, password_hash, create_at
- **Book**: id, title, author, genre, isbn, price, stock_quantity, description
- **Cart**: id, user_id, created_at
- **CartItem**: id, cart_id, book_id, quantity
- **Order**: id, user_id, toatl, status, created_at
- **OrderItem**: id, order_id, book_id, title_snapshot, price_snapshot, quantity

## 8. Assumptions & Constraints
- Assumption: Users will be authenticated for the purpose of associating orders with accounts (guest flow optional)
- Assumption: Payment gateway will be integrated in a later sprint; initially checkout will create orders without charging (or simulate payment).
- Constraint: MVP uses a relational database (MySQL) for core data persistence.
- Constraint: The service will be developed in Java (Java 17) and tested on a Linux VM as per project plan.

---

## 9. Priority (MoSCow)
- **Must have**: Search, View Details, Add to Cart, Checkout (order creation), basic authentication.
- **Should have**: Order history, stock availability checks at checkout.
- **Could have**: Guest checkout, wishlist.
- **Won't have (this phase)**: Real payment gateway integration, recommendation engine (Day 6 covers simple recommendations using DynamoDB).

---

## 10. Acceptance Tests (high-level)
- **AT-1 (Search)**: Given books exist, when the user searches with exact or partial title, then results must include matching books and return within 2s.  
- **AT-2 (Add to Cart)**: Given a logged-in user, when they add a book with quantity 2, then the cart must reflect quantity=2 and total price = 2 × book.price.  
- **AT-3 (Checkout)**: Given cart has items, when user checks out, then new `orders` and `order_items` are created and persisted; stock_quantity is decremented accordingly in transactional manner.

---

## 11. Traceability
- Each user story maps to one or more functional requirements:
  - US-1 → FR-1
  - US-2 → FR-2
  - US-3 → FR-3, FR-4
  - US-4 → FR-5, FR-6

---

## 12. Next Steps (for Day 2 & Day 3)
- Day 2: OOAD & Design — create class diagrams, use-case diagrams, ER diagrams.  
- Day 3: Environment setup — Java 17, MySQL, project skeleton, initial `Book` and `User` classes.

---

## 13. Document History
- Created by: Aditya Shubham
- Version: 1.0  
- Date: 9/12/2025

