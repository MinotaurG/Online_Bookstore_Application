# Day 8 — Tests & Quality

**Deliverable:** Add unit tests, integration tests, and quality checks.  
**Date:** 10/2/2025

---

## Goals
- Strengthen project with automated testing.  
- Add unit tests for BookService, CheckoutService, Cart, Recommendations, BrowsingHistory.  
- Add integration tests that run against DynamoDB Local.  
- Add JaCoCo plugin for coverage reports.  

---

## Files Added
- `src/test/java/com/bookstore/BookServiceTest.java`  
- `src/test/java/com/bookstore/CheckoutServiceTest.java`  
- `src/test/java/com/bookstore/CartCheckoutTest.java`  
- `src/test/java/com/bookstore/RecommendationServiceTest.java`  
- `src/test/java/com/bookstore/BrowsingHistoryTest.java`  
- `pom.xml` updated with `jacoco-maven-plugin`.

---

## How to Run
1. Start DynamoDB Local (if needed):
   ```bash
   java "-Djava.library.path=./DynamoDBLocal_lib" -jar ./DynamoDBLocal.jar -inMemory -port 8000```

2. Run tests:
```bash
mvn clean test
```

3. Generate coverage report:
```bash
mvn verify
```
Open target/site/jacoco/index.html.




---

## Sample Output

[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS

Coverage: ~36% lines, ~27% branches.
(Plan: increase >70% with more edge-case tests later).

---