# 📘 Online Bookstore — Day 2 Deliverable
**Deliverable:** OOAD & Design (Class Diagram, Use Case Diagram, ER Diagram)  
**Date:** 9/12/2012

---

## 1. Class Diagram

Shows the main classes in the system, their attributes, and relationships.

```text
+----------------+        1        +----------------+
|     User       |-----------------|      Cart      |
|----------------|    owns / has   |----------------|
| - id: Long     |                 | - id: Long     |
| - username     |                 | - userId       |
| - email        |                 | - createdAt    |
+----------------+                 +----------------+
                                         |
                                         | contains *
                                         |
                                   +------------+
                                   | CartItem   |
                                   |------------|
                                   | - id       |
                                   | - cartId   |
                                   | - bookId   |
                                   | - quantity |
                                   +------------+
                                         |
                                         | references 1
                                         |
                                   +------------+
                                   |   Book     |
                                   |------------|
                                   | - id       |
                                   | - title    |
                                   | - author   |
                                   | - genre    |
                                   | - price    |
                                   | - stockQty |
                                   +------------+

+----------------+
|     Order      |
|----------------|
| - id           |
| - userId       |
| - total        |
| - status       |
| - createdAt    |
+----------------+
       |
       | contains *
       v
   +-----------+
   | OrderItem |
   |-----------|
   | - id      |
   | - orderId |
   | - bookId  |
   | - title_snapshot |
   | - price_snapshot |
   | - quantity |
   +-----------+


---

## 2. Use Case Diagram

- Shows how actors interact with the system.

### Actors

- User

- Admin (optional)


### User Use Cases

- Search Book

- View Book Details

- Add to Cart

- View Cart

- Checkout / Make Payment

- View Order History


### Admin Use Cases

- Add Book

- Update Book / Stock

- Remove Book


### Main Flow (Happy Path):

User → Search Book → View Details → Add to Cart → View Cart → Checkout → Payment Gateway → Order Confirmed

### Alternate Flows:

- Payment Failure → Show Error → Keep Cart Intact

- Out of Stock at Checkout → Notify User → Remove or Waitlist Item



---

## 3. ER Diagram

Maps entities to relational tables in MySQL.

TABLE: users
 - id PK
 - username
 - email
 - password_hash
 - created_at

TABLE: books
 - id PK
 - title
 - author
 - genre
 - isbn
 - price
 - stock_quantity
 - description

TABLE: carts
 - id PK
 - user_id FK -> users(id)
 - created_at

TABLE: cart_items
 - id PK
 - cart_id FK -> carts(id)
 - book_id FK -> books(id)
 - quantity

TABLE: orders
 - id PK
 - user_id FK -> users(id)
 - total
 - status
 - created_at

TABLE: order_items
 - id PK
 - order_id FK -> orders(id)
 - book_id FK -> books(id)   (optional snapshot link)
 - title_snapshot
 - price_snapshot
 - quantity

### Cardinalities:

- users 1 --- * carts

- carts 1 --- * cart_items

- books 1 --- * cart_items

- users 1 --- * orders

- orders 1 --- * order_items

- books 1 --- * order_items (snapshots stored for immutability)