# 🛠️ Day 3 — Environment Setup

**Deliverable:** Project runs with `Hello Bookstore` message and includes basic `Book` & `User` classes.  
**Date:** (replace with today's date)

---

## ✅ 1. Goals (Day 3)
- Install and use **Java 17** (project SDK).  
- Create a Maven project (`pom.xml`) at repo root.  
- Create standard Java project layout:
  - `src/main/java/com/bookstore/`
  - `src/test/java/`
- Implement simple classes: `Main`, `Book`, `User`.  
- Verify project runs and prints `Hello Bookstore`.

---

## 📂 2. Project Structure (expected)

Online_Book_Store/
├── docs/
│   ├── DAY1_REQUIREMENT_SPEC.md
│   ├── DAY2_DESIGN_DIAGRAMS.md
│   └── DAY3_ENV_SETUP.md
│
├── src/
│   ├── main/java/com/bookstore/
│   │   ├── Main.java
│   │   ├── Book.java
│   │   └── User.java
│   │
│   └── test/java/
│
├── pom.xml
├── .gitignore
├── README.md
└── Online Bookstore Application.pdf

---

## 💻 3. Files & Code

### `pom.xml` (minimal, Java 17)
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.bookstore</groupId>
  <artifactId>online-bookstore</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
  </properties>

  <dependencies>
    <!-- Add dependencies later (JUnit, JDBC, AWS SDK, etc.) -->
  </dependencies>
</project>


---

Main.java

package com.bookstore;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello Bookstore");
    }
}

Book.java

package com.bookstore;

import java.math.BigDecimal;

public class Book {
    private Long id;
    private String title;
    private String author;
    private String genre;
    private BigDecimal price;
    private int stockQuantity;

    public Book(String title, String author, String genre, BigDecimal price, int stockQuantity) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getters (examples)
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    public boolean isAvailable() {
        return stockQuantity > 0;
    }

    @Override
    public String toString() {
        return title + " by " + author + " (" + genre + ") - $" + price;
    }
}

User.java

package com.bookstore;

public class User {
    private Long id;
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return username + " <" + email + ">";
    }
}


---

🧭 4. How to run locally (commands)

Assuming you are in the repo root and have Maven + Java 17 installed:

# compile
mvn compile

# run (simple)
mvn exec:java -Dexec.mainClass="com.bookstore.Main"
# or run via IntelliJ by right-clicking Main.java -> Run 'Main.main()'

If you don't have the Maven exec plugin configured yet, you can simply:

# compile to target/classes then run with java
mvn compile
java -cp target/classes com.bookstore.Main

Expected console output:

Hello Bookstore


—
