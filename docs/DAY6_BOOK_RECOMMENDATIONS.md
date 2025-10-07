# Day 6 — Book Recommendations (DynamoDB, Option A)

**Deliverable:** Implement a recommendation feature that returns the **top 5 recommended books** for a user, backed by DynamoDB Local (Option A: single item per user).  
**Date:** (9/20/2025)

---

## Goals
- Store recommendations in DynamoDB Local and fetch top N (N = 5) for a user.  
- Seed recommendations idempotently for a demo user.  
- Provide `RecommendationService` + repository abstraction to make switching to Option B (one-item-per-recommendation) easy later.  
- Add a demo `RecommendationsDemo` that prints the top-5 recommendations.  
- Add unit tests for service logic.

---

## Files added / updated
```text
src/main/java/com/bookstore/
  Recommendation.java
  UserRecommendations.java 
  RecommendationRepository.java 
  DynamoDbSingleItemRecommendationRepository.java 
  RecommendationService.java

src/main/java/com/bookstore/demo/RecommendationsDemo.java
src/test/java/com/bookstore/RecommendationServiceTest.java
```
(Implementations use the AWS SDK v2 enhanced client and follow the pattern used in `BookService`.)

---

## Short description of each file

- **Recommendation.java** — POJO for a single recommendation (rank, bookId, title, reason).  
- **UserRecommendations.java** — DynamoDB-mapped bean representing the single item stored per user (`userId` + `List<Recommendation>`).  
- **RecommendationRepository.java** — Interface (save/get/delete) so storage strategy is pluggable.  
- **DynamoDbSingleItemRecommendationRepository.java** — Option A repository implementation using the enhanced client and `Recommendations` table (auto-creates the table if missing).  
- **RecommendationService.java** — Business logic: seed if empty, get top-N recommendations. Uses the repository interface.  
- **RecommendationsDemo.java** — Demo runner: seeds a deterministic list for `demoUser` (idempotent) and prints top 5. Closes the Dynamo client at the end.  
- **RecommendationServiceTest.java** — Unit test using an in-memory test repository to validate seed + fetch behavior.

---

## DynamoDB table design (Option A)
- **Table name:** `Recommendations`  
- **Partition key:** `userId` (String)  
- **Attributes:** `recommendations` (List\<Map\>) — each element contains `{rank, bookId, title, reason}`  
- Access pattern: `GetItem(userId)` then return ordered recommendations (top N). This is optimal for small per-user lists and very simple for demo.

---

## How to run (locally)

1. **Start DynamoDB Local** (PowerShell, from the folder containing `DynamoDBLocal.jar` and `DynamoDBLocal_lib`):
```powershell
java "-Djava.library.path=.\DynamoDBLocal_lib" -jar .\DynamoDBLocal.jar -inMemory -port 8000

Keep this window open. Use -inMemory if you want a fresh DB every time.
Compile & run demo (project root, new PowerShell window):
mvn clean compile
mvn "-Dexec.mainClass=com.bookstore.demo.RecommendationsDemo" exec:java
```

**Run unit tests:**
```
mvn -DskipTests=false test
```

**Expected output** (sample)
Seeded recommendations for demoUser
```
Top 5 recommendations for demoUser:
1. Effective Java (bookId=b-100) [classic]
2. The Pragmatic Programmer (bookId=b-200) [best practice]
3. Clean Architecture (bookId=b-300) [design]
4. Refactoring (bookId=b-400) [patterns]
5. Head First Design Patterns (bookId=b-500) [easy learning]
```

## Notes, gotchas & behavior
- **Idempotent seeding**: The demo seeds only if no recommendations exist for the user — safe to re-run.
Client cleanup: RecommendationsDemo closes the Dynamo client at the end to avoid lingering threads/warnings from exec:java.
- **Stock / Side-effects:** Recommendations are read-only for the demo. No side-effects on BookService data.
Why Option A: Simple single GetItem read returns top N in one operation — ideal for Day 6 demo. If lists grow large or you need fine-grained updates, migrate to Option B (per-item model).

## Tests
- **Unit test:** RecommendationServiceTest validates seedIfEmpty() and getTop() using an in-memory repository implementation.
- **Integration test** (recommended): you can add an IT that starts DynamoDB Local, creates the Recommendations table, seeds, calls getTopRecommendations, and then tears down the table.

## Switching to Option B later (short plan)
The code uses RecommendationRepository interface so you can add DynamoDbPerItemRecommendationRepository later.
Migration steps (later): backfill per-user lists into per-item table, add adapter that reads Option A then Option B, enable dual-write, validate, then switch reads to Option B and remove old table.

---
