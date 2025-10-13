# 📚 Online Bookstore

A full-stack online bookstore application with React frontend and Spring Boot backend.

## Features

- Shopping cart with real-time stock validation
- User authentication (Admin & Customer roles)
- Order management
- Browsing history
- Book recommendations
- Dark mode support
- Responsive design with Material-UI

## Tech Stack

**Frontend:**
- React 18
- Material-UI v5
- React Router v6
- Vite
- React Toastify

**Backend:**
- Spring Boot 3.2
- DynamoDB (AWS SDK v2)
- Java 17
- Maven

<<<<<<< HEAD
## Quick Start
=======
### AWS SDK
- **AWS Java SDK** is used to integrate with DynamoDB
- Domain model classes are annotated with DynamoDB Mapper annotations (e.g., `@DynamoDBTable`, `@DynamoDBHashKey`, etc.), enabling object-relational mapping to DynamoDB items
- Alternatively, the SDK's low-level client is used for certain queries and scans

### Testing
- **JUnit** and **Mockito** for unit tests, ensuring individual components (services, controllers) work correctly
- Test coverage is measured with **JaCoCo** (about 54% of instructions covered in tests), including both unit and integration tests

### Build & Tools
- **Maven** is used for project management and build
- The project follows standard Maven structure (`src/main/java` for source and `src/test/java` for tests)
- A continuous integration pipeline (**Jenkins**) can be configured to run tests and build the project on every commit
- The Spring Boot Maven plugin or JAR packaging allows easy deployment

### Deployment
- The application is packaged as an executable Spring Boot JAR
- It can be deployed on any machine with Java 17
- For demonstration, it was deployed on a Linux server with DynamoDB Local running
- In production, the DynamoDB endpoint can be switched to AWS DynamoDB service with minimal configuration changes

## Architecture

The Online Bookstore follows a simple **three-tier architecture** with a web-based client, a Java Spring Boot application server, and DynamoDB as the database.

### System Architecture Overview
- The **client** (user's web browser) interacts with the application through HTTP requests (for example, browsing the catalog or submitting an order)
- The **server** (Spring Boot backend) contains controllers to handle these requests, services implementing business logic (like search and checkout), and data access layers for DynamoDB
- The **database** is DynamoDB Local, which the server accesses via AWS SDK calls
- This design decouples the presentation, logic, and data storage layers, making the system modular and scalable

### Data Model

All data persistence is handled by DynamoDB. Each core domain object is stored in its own DynamoDB table:

#### Books Table
- Stores book records
- **Primary key:** Unique Book ID (for example, a UUID or ISBN)
- **Attributes:** title, author, price, genre, description, and inventory stock count
- Can be queried by primary key
- For search by title/author, the application either performs a scan (for demo scale) or utilizes a Global Secondary Index on those attributes

#### Users Table
- Stores user account information
- **Primary key:** Unique User ID or username
- **Attributes:** name, email, password hash
- Used to authenticate users (login) and to retrieve user details (e.g., when placing an order)

#### Orders Table
- Stores orders placed by users
- **Primary key:** Order ID (unique identifier for each order)
- **Attributes:** 
  - Associated User ID (to indicate who placed it)
  - List of purchased books (each with quantity)
  - Timestamp/date
  - Total amount
- A single item per order is used, with a list or map attribute to detail the items and quantities
- This eliminates the need for joins, since all relevant order info is encapsulated in one record

#### Recommendations Table
- Stores recommended books per user
- **Primary key:** User ID
- **Attributes:** List of Book IDs (or titles) recommended for that user
- On a user's home page or after login, the application fetches the recommendations from this table and then retrieves those books' details from the Books table to display
- This is a denormalized design aimed at quick lookup of personalized suggestions

#### Shopping Cart (In-Memory Only)
- The shopping cart is **not persisted** in the database
- Maintained in-memory for each user session (using a Java `HashMap<Book, Integer>` or similar)
- This decision simplifies the implementation: the cart exists only while the user is shopping
- Once the user checks out, an Order is created and saved

### Benefits of NoSQL Design
- **Flexibility:** New attributes can be added to Book or User without migrating schemas
- **Scalability:** DynamoDB can handle high volumes of reads/writes with proper key design
- **Trade-offs:** Complex queries (like filtering or joins) must be handled at the application level or by using indexes

## Installation & Setup
>>>>>>> main

### Prerequisites
- Node.js v18+
- Java 17+
- Maven
- DynamoDB Local (for development)

### Backend Setup

```bash
# Start DynamoDB Local (port 8000)
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb

# Run Spring Boot
cd Online_Book_Store
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`

### Frontend Setup

```bash
cd bookstore-ui
npm install
npm run dev
```

Frontend runs on: `http://localhost:5173`

## 👤 Demo Credentials

**Admin Account:**
- Username: `admin`
- Password: `admin123`

## 📂 Project Structure

```
Online_Book_Store/
├── src/main/java/com/bookstore/     # Backend Java code
│   ├── api/                          # REST controllers
│   ├── config/                       # Configuration
│   └── spring/                       # Spring components
├── bookstore-ui/                     # Frontend React app
│   └── src/
│       ├── components/               # React components
│       ├── pages/                    # Page components
│       └── api.js                    # API client
└── pom.xml                           # Maven configuration
```

## Current Status

- [X] User authentication and authorization
- [X] Book catalog with search
- [X] Shopping cart functionality
- [X] Order placement and history
- [X] Admin panel for book management
- [X] Bulk upload for books
- [X] Dark mode
- [X] Responsive UI

## Roadmap

- [ ] User profile management
- [ ] Book reviews and ratings
- [ ] Advanced search filters
- [ ] Payment integration
- [ ] Email notifications
- [ ] Wishlists

## Screenshots

![Catalog](./screenshots/catalog.png)
![Cart](./screenshots/cart.png)
![Admin Panel](./screenshots/admin.png)

## 📄 License

MIT License

<<<<<<< HEAD
## 👨‍💻 Author

Aditya Shubham
=======
**Recommendations**
- After logging in, view "Recommended Books" section
- Retrieves personalized book list from Recommendations table

**Recently Viewed Books**
- Automatically tracks books you view
- Shows last few books you looked at
- *Note: This list is maintained only in memory and resets when session ends*

#### Logging Out
- Use the logout option in the UI
- Clears session data including in-memory cart and browsing history
- Allows login as a different user

## Project Status and Future Improvements

This Online Bookstore Application is a **functional prototype** demonstrating core features. While fully working for demo purposes, several enhancements could be made:

### Planned Improvements

#### Payment Integration
- Integrate a payment gateway to make the checkout process realistic
- Handle real payment processing and transaction management

#### Enhanced Search
- Add more filters (by category, price range)
- Implement DynamoDB Global Secondary Indexes for efficient querying by non-key attributes
- Enable fast search by author without scanning the entire table

#### Dynamic Recommendations
- Expand the recommendation engine to generate suggestions based on user behavior
- Currently, recommendations are static or manually provided
- Could integrate AWS Machine Learning services or collaborative filtering algorithms

#### UI/UX Improvements
- Introduce a frontend framework (React, Angular, etc.) for a more dynamic single-page application
- Current implementation uses server-side rendering for simplicity
- Enhanced responsive design and user experience

#### AWS Deployment
- Deploy to AWS EC2 or AWS Elastic Beanstalk
- Switch from DynamoDB Local to actual AWS DynamoDB service
- Take advantage of AWS-managed infrastructure for scalability

#### Security Enhancements
- Implement proper authentication using Spring Security
- Use HTTPS and other production security best practices

#### Testing & Coverage
- Increase test coverage, especially integration tests covering full API-to-database flows
- Add more Cucumber scenarios for cart operations and checkout flow
- Currently at 54% test coverage with room for improvement

### Conclusion

This project provides a **solid foundation** for an online bookstore system with a fully NoSQL-based backend. It demonstrates how to map traditional e-commerce concepts (orders, carts, users) onto DynamoDB's schema-less paradigm. With the above improvements, it could be expanded into a production-ready application.

---
>>>>>>> main
