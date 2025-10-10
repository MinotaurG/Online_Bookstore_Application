# Online Bookstore Application

## Overview

The Online Bookstore Application is a Java-based web application that simulates a simple e-commerce bookstore. It allows users to search for books, view detailed information, add books to a shopping cart, and proceed through a checkout to place an order. The system also provides personalized recommendations and tracks recently viewed books for each user. All data is persisted in a NoSQL database (Amazon DynamoDB Local), ensuring a flexible schema and easy local development. The application is built with a modern tech stack, focusing on clean design and a responsive user experience.

## Features

### Book Search & Browse
- Users can search for books by title or author keywords and browse a catalog of available books
- Search results are retrieved from a DynamoDB table of books, enabling quick lookups by key or secondary indexes

### Book Details
- Each book has a detail page showing information like title, author, description, price, etc.
- Users can view these details before deciding to purchase

### Shopping Cart
- Users can add books to a virtual cart (with specified quantities)
- The cart is managed in-memory for the session (using Java data structures) and displays all selected items with subtotals
- Users can update quantities or remove items from the cart

### Checkout & Order Placement
- At checkout, the application creates an order for the user
- Order information (including items and total price) is saved to the DynamoDB Orders table
- A confirmation of the successful order placement is provided (simulating a payment flow; actual payment processing is not integrated)

### User Accounts
- The system models user accounts with basic information (username, password, contact info)
- A user can register and log in to maintain a unique cart and order history
- For the demo, a guest user account can be used if registration is not explicitly provided through the UI
- All user data is stored in a DynamoDB Users table for persistence

### Personalized Recommendations
- The application demonstrates personalized book recommendations
- For each user, a set of recommended books (e.g. top 5 picks) is stored in a DynamoDB table and displayed on the user's home page or dashboard
- This feature illustrates integration with DynamoDB for fast retrieval of suggested items

### Recently Viewed Books
- The application keeps track of each user's recently viewed books
- As a user views book details, those titles are added to a "Recently Browsed" list (maintained using a LinkedList in memory)
- Users can easily see their last few viewed items, enhancing the browsing experience
- This feature is for demonstration and resets when the session ends (not persisted to the database)

### Responsive UI
- The web interface is designed with a responsive layout, making the bookstore accessible and user-friendly
- Pages adjust using standard HTML/CSS (and optionally Bootstrap) to ensure a good user experience

## Tech Stack

### Language & Framework
- **Java 17** with **Spring Boot** (Spring MVC) for building the web application and RESTful APIs
- Spring's inversion of control and MVC pattern simplify development and testing

### Database
- **Amazon DynamoDB (Local)** – a NoSQL key-value and document database
- All books, users, orders, and recommendations are stored in DynamoDB tables
- DynamoDB Local is used for development and testing, allowing the application to run offline while emulating the AWS DynamoDB environment

### AWS SDK
- **AWS Java SDK** is used to integrate with DynamoDB
- Domain model classes are annotated with DynamoDB Mapper annotations (e.g., `@DynamoDBTable`, `@DynamoDBHashKey`, etc.), enabling object-relational mapping to DynamoDB items
- Alternatively, the SDK's low-level client is used for certain queries and scans

### Testing
- **JUnit** and **Mockito** for unit tests, ensuring individual components (services, controllers) work correctly
- **Cucumber (BDD)** is used for high-level behavior testing; for example, a feature file defines scenarios for searching a book by title and verifying the results
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

### Prerequisites

1. **Node.js and nvm** installed on your development machine
2. **DynamoDB Local** set up

#### Setting up DynamoDB Local

**JAR Download**
```bash
java -jar DynamoDBLocal.jar -sharedDb
```

*No AWS cloud account is needed; all data will be stored in a local file.*

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd Online_Bookstore_Application
   ```

2. **Configure AWS SDK**
   - The application is configured by default to connect to a local DynamoDB on `http://localhost:8000`
   - In the Spring Boot `application.properties` (or YAML), the endpoint for DynamoDB is set accordingly
   - Set AWS access keys as environment variables (dummy values for local connection):
     ```bash
     export AWS_ACCESS_KEY_ID=dummy
     export AWS_SECRET_ACCESS_KEY=dummy
     ```

3. **Install Dependencies**
   ```bash
   npm install
   ```
   
   *This will install all required node modules and dependencies for the application.*

4. **Run the Application**
   
   **Development Mode:**
   ```bash
   npm run dev
   ```
   
   The application will start a development server on `http://localhost:5173/`. You should see console logs indicating the server has started and that it connected to DynamoDB Local successfully.

## Usage

### Getting Started

1. **Access the Web UI:** Open a web browser and navigate to `http://localhost:5173/`
2. You should see the homepage of the Online Bookstore Application

### Core Functionality

#### Searching for Books
- Use the search bar or menu to search by title or author
- Example: Enter "Clean Code" to find books matching that title
- The application will query the Books table in DynamoDB and return matching results
- Click on a book in the results to view its details

#### Viewing Book Details
- On a book's detail page, you will see:
  - Author information
  - Description
  - Price
  - Availability
- Click "Add to Cart" to purchase the book

#### Managing Your Cart
- **Adding items:** Click "Add to Cart" on any book page (specify quantity if prompted)
- **Viewing cart:** Click the cart icon/link to review selected items
- **Updating cart:** Change quantities or remove items entirely
- **Total calculation:** The total price is calculated automatically

#### Checkout Process
1. Go to the cart page and click "Checkout" or "Place Order"
2. Log in if required (or use guest user)
3. Confirm your order
4. The application creates an Order in the Orders table with:
   - Generated order ID
   - List of books in the order
   - Total amount
   - Link to your user account
5. View order confirmation page

#### User Account Management
- **Registration:** New users can register with username and password (saved in Users table)
- **Login:** Registered users can log in to track cart and orders
- **Default guest user**

#### Personalized Features

**Recommendations**
- After logging in, view "Recommended Books" section
- Retrieves personalized book list from Recommendations table
- Click on any recommended book to view details and add to cart

**Recently Viewed Books**
- Automatically tracks books you view
- Access via "Recently Viewed" link (sidebar or navigation)
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