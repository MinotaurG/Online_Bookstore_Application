# Online Bookstore Application

[![CI/CD Pipeline](https://github.com/MinotaurG/Online_Bookstore_Application/actions/workflows/ci.yml/badge.svg)](https://github.com/MinotaurG/Online_Bookstore_Application/actions)
[![Test Coverage](https://img.shields.io/badge/coverage-58%25-yellow.svg)](https://github.com/MinotaurG/Online_Bookstore_Application)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A production-ready full-stack online bookstore application built with React and Spring Boot, featuring Amazon-style ASIN inventory management, personalized recommendations, and comprehensive admin tools.

> **Developed as part of the Amazon ATLAS Training Program**


## Features

### Customer Features
- **Advanced Search** - Full-text search by title/author with filters and sorting
- **Smart Shopping Cart** - Real-time stock validation with quantity management
- **Seamless Checkout** - Order placement with validation and confirmation
- **Order History** - View past orders with detailed item information
- **Browsing History** - Recently viewed books (capacity: 10, FIFO ordering)
- **Personalized Recommendations** - DynamoDB-based suggestion engine
- **Responsive Design** - Mobile, tablet, and desktop optimized
- **Dark Mode** - Toggle between light and dark themes
- **Book Covers** - Automatic fetching via OpenLibrary API (ISBN-based)

### Admin Features
- **Inventory Management** - CRUD operations with Amazon-style ASIN system
- **Bulk Upload** - CSV/JSON import with preview (drag & drop)
- **Catalog Export** - Filtered export in JSON/CSV formats
- **Bulk Edit** - Update multiple books simultaneously
- **Bulk Delete** - Remove selected books in one operation
- **Analytics-Ready** - Data structure optimized for business insights

### Technical Features
- **Session-based Authentication** - Secure login with admin authorization
- **Material-UI Components** - Professional, polished interface
- **Pagination** - Handles 1000+ books efficiently (12/24/48/96 per page)
- **Deterministic IDs** - Hash-based IDs prevent duplicate books
- **ASIN Generation** - B0XXXXXXXX format (like Amazon)
- **134 Automated Tests** - 58% code coverage with JUnit & Mockito
- **CI/CD Pipeline** - GitHub Actions with parallel builds

---

## Tech Stack

### Backend
- **Java 17 (LTS)** - Core language
- **Spring Boot 3.2** - REST API framework
- **DynamoDB (AWS SDK v2)** - NoSQL database
- **Maven 3.9+** - Build tool
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework

### Frontend
- **React 18** - UI library
- **Material-UI v5** - Component library
- **React Router v6** - Client-side routing
- **Vite 5** - Build tool
- **React Toastify** - Notifications

### DevOps
- **CI/CD:** GitHub Actions
- **Version Control:** Git + GitHub
- **Containerization:** Docker (DynamoDB Local)
- **Testing:** JUnit, Mockito, Spring Test

### External APIs
- **OpenLibrary Covers API** - Book cover images by ISBN

### DevOps
- **CI/CD:** GitHub Actions
- **Version Control:** Git + GitHub
- **Containerization:** Docker (DynamoDB Local)
- **Testing:** JUnit, Mockito, Spring Test

### External APIs
- **OpenLibrary Covers API:** Book cover images by ISBN

---



### Database Schema

**DynamoDB Tables:**
- **Users** - Authentication and authorization
- **BrowsingHistory** - LinkedList-based recent views (max 10)
- **UserRecommendations** - Personalized book suggestions

**In-Memory Storage:**
- **Books** - Catalog with ASIN indexing
- **Orders** - Completed purchases

---

## Quick Start

### Prerequisites
- **Java:** 17 or higher ([Download](https://adoptium.net/))
- **Node.js:** 18+ ([Download](https://nodejs.org/))
- **Maven:** 3.9+ ([Download](https://maven.apache.org/))
- **DynamoDB Local:** Docker or standalone

### Backend Setup

```bash
# 1. Clone repository
git clone https://github.com/MinotaurG/Online_Bookstore_Application.git
cd Online_Bookstore_Application
```
### 2. Start DynamoDB Local (Option A: Docker)
```bash
docker run -p 8000:8000 amazon/dynamodb-local
```
### OR (Option B: Standalone)
```bash
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

### 3. Build and run Spring Boot (new terminal)
```bash
./mvnw clean install
./mvnw spring-boot:run
```

Backend runs on: http://localhost:8080

Verify backend:

```bash
curl http://localhost:8080/api/books
```
Frontend Setup

### 1. Navigate to frontend directory

```bash
cd bookstore-ui
```

### 2. Install dependencies

```bash
npm install
```

### 3. Start development server

```bash
npm run dev
```

Frontend runs on: http://localhost:5173
Build for production:

```bash
npm run build
```

**Output in:** bookstore-ui/dist/

Demo Credentials

**Admin Account**

Username: admin

Password: admin123

Capabilities: Full inventory management, bulk operations

**Customer Account**
Option 1: Register at /register

Option 2: Use any test account you create

### Key Highlights
1. **ASIN Inventory System:**
Amazon-style 10-character identifiers (B0XXXXXXXX)
Deterministic generation prevents duplicates
Enables bulk operations with confidence
2. **Deterministic ID Generation:**
SHA-256 hash of (title + author)
Same book = same ID across imports
Idempotent bulk uploads
3. **Bulk Operations:**
Upload: CSV/JSON with drag & drop preview
Update: Batch price/stock adjustments
Delete: Remove multiple books at once
Export: Filtered catalog download
4. **Browsing History:**
LinkedList implementation (FIFO)
Capacity limit: 10 books
O(1) add to front, O(1) remove from end
5. **OpenLibrary Integration:**
Automatic cover fetching via ISBN
Fallback to genre-based gradient backgrounds
Lazy loading for performance
6. **Responsive Design:**
CSS Grid for equal-width cards

---