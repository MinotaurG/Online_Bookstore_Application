# Day 5 — Cart & Checkout

**Deliverable:** Implement shopping cart and checkout flow.  
**Date:** 9/18/2025

---

## Goals
- Implement `Cart` with `addBook`, `removeBook`, and `calculateTotal`.  
- Implement `CheckoutService` that converts a cart into an `Order`.  
- Persist orders in memory using a simple repository.  
- Add a unit test demonstrating checkout flow.  
- Provide a demo run (`CartDemo.java`) showing add → view cart → checkout → order summary.  
- Enforce **stock consistency**: checkout will fail if requested quantity > available stock.

---

## Files added
- `src/main/java/com/bookstore/CartItem.java`  
- `src/main/java/com/bookstore/Cart.java`  
- `src/main/java/com/bookstore/Order.java`  
- `src/main/java/com/bookstore/OrderRepository.java`  
- `src/main/java/com/bookstore/InMemoryOrderRepository.java`  
- `src/main/java/com/bookstore/CheckoutService.java`  
- `src/main/java/com/bookstore/OrderIdGenerator.java`  
- `src/main/java/com/bookstore/demo/CartDemo.java`  
- `src/test/java/com/bookstore/CartCheckoutTest.java`  

---

## How to run

1. **Run tests** (JUnit 5):
   ```bash
   mvn clean test
   ```
2. **Run Cart demo** (requires DynamoDB Local running in-memory):
   ```bash
   mvn "-Dexec.mainClass=com.bookstore.demo.CartDemo" exec:java
   ```

### Sample expected output
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
(UUID and timestamp will differ per run)

### Stock consistency behavior
After a successful checkout, stock quantities in DynamoDB are decremented.
If you run CartDemo again without restarting DynamoDB Local, the checkout may throw an error such as:
```text
 java.lang.IllegalStateException: Not enough stock for: Design Patterns
```

This is intentional: it demonstrates the system prevents overselling when stock runs out.
To rerun with fresh stock, restart DynamoDB Local with -inMemory option so the database resets.

---