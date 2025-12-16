# Online Bookstore Application

[![CI/CD Pipeline](https://github.com/MinotaurG/Online_Bookstore_Application/actions/workflows/ci.yml/badge.svg)](https://github.com/MinotaurG/Online_Bookstore_Application/actions)
[![Test Coverage](https://img.shields.io/badge/coverage-58%25-yellow.svg)](https://github.com/MinotaurG/Online_Bookstore_Application)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A production-ready full-stack online bookstore application built with React and Spring Boot, featuring Amazon-style ASIN inventory management, personalized recommendations, and comprehensive admin tools.

> **Developed as part of the Amazon ATLAS Training Program**

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
  - [Docker Setup (Recommended)](#option-1-docker-recommended-)
  - [Manual Setup](#option-2-manual-setup)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Demo Credentials](#demo-credentials)
- [Key Highlights](#key-highlights)
- [API Endpoints](#api-endpoints)
- [Screenshots](#screenshots)
- [Contributing](#contributing)
- [License](#license)

---

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
- **Docker Support** - One-command deployment with Docker Compose
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
| Technology | Purpose |
|------------|---------|
| Java 17 (LTS) | Core language |
| Spring Boot 3.2 | REST API framework |
| DynamoDB (AWS SDK v2) | NoSQL database |
| Maven 3.9+ | Build tool |
| JUnit 5 & Mockito | Testing |

### Frontend
| Technology | Purpose |
|------------|---------|
| React 18 | UI library |
| Material-UI v5 | Component library |
| React Router v6 | Client-side routing |
| Vite 5 | Build tool |
| Nginx | Production server (Docker) |

### DevOps
| Technology | Purpose |
|------------|---------|
| Docker & Docker Compose | Containerization |
| GitHub Actions | CI/CD pipeline |
| Git | Version control |

### External APIs
- **OpenLibrary Covers API** - Book cover images by ISBN

---

## Quick Start

### Option 1: Docker (Recommended) 🐳

The easiest way to run the entire application with one command.

#### Prerequisites
- **Docker:** [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose:** Included with Docker Desktop

#### Run with Docker Compose

```bash
# 1. Clone repository
git clone https://github.com/MinotaurG/Online_Bookstore_Application.git
cd Online_Bookstore_Application

# 2. Start all services
docker compose up -d

# 3. Check status
docker compose ps

# 4. View logs (optional)
docker compose logs -f
```

#### Access the Application

| Service | URL |
|---------|-----|
| **Frontend** | http://localhost:3000 |
| **Backend API** | http://localhost:8080/api/books |
| **Health Check** | http://localhost:8080/actuator/health |
| **DynamoDB Local** | http://localhost:8000 |

#### Docker Commands Reference

```bash
# Start all services (detached)
docker compose up -d

# Stop all services
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v

# View all logs
docker compose logs -f

# View specific service logs
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f dynamodb

# Rebuild after code changes
docker compose build backend
docker compose up -d

# Full rebuild (no cache)
docker compose down
docker compose build --no-cache
docker compose up -d

# Check container status
docker compose ps

# Enter container shell
docker exec -it bookstore-backend sh
docker exec -it bookstore-frontend sh
```

---

### Option 2: Manual Setup

For development with hot-reload capabilities.

#### Prerequisites
- **Java:** 17 or higher ([Download](https://adoptium.net/))
- **Node.js:** 18+ ([Download](https://nodejs.org/))
- **Maven:** 3.9+ ([Download](https://maven.apache.org/))

#### Step 1: Start DynamoDB Local

```bash
# Using Docker (recommended)
docker run -p 8000:8000 amazon/dynamodb-local

# OR using standalone JAR
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

#### Step 2: Start Backend

```bash
# Clone repository
git clone https://github.com/MinotaurG/Online_Bookstore_Application.git
cd Online_Bookstore_Application

# Build and run
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

Backend runs on: http://localhost:8080

#### Step 3: Start Frontend

```bash
# Navigate to frontend directory
cd bookstore-ui

# Install dependencies
npm install

# Start development server
npm run dev
```

Frontend runs on: http://localhost:5173

#### Build for Production

```bash
cd bookstore-ui
npm run build
```

Output in: `bookstore-ui/dist/`

---

## Project Structure

```
Online_Bookstore_Application/
├── Dockerfile                    # Backend multi-stage build
├── docker-compose.yml            # Service orchestration
├── .dockerignore                 # Docker build exclusions
├── pom.xml                       # Maven configuration
├── mvnw                          # Maven wrapper
│
├── src/
│   ├── main/
│   │   ├── java/com/bookstore/
│   │   │   ├── Application.java          # Spring Boot entry
│   │   │   ├── Book.java                 # Book entity
│   │   │   ├── BookService.java          # Book business logic
│   │   │   ├── AsinGenerator.java        # ASIN generation
│   │   │   ├── DeterministicId.java      # Hash-based IDs
│   │   │   ├── config/                   # Configuration classes
│   │   │   ├── spring/                   # Controllers & adapters
│   │   │   └── ...
│   │   └── resources/
│   │       ├── application.properties           # Default config
│   │       └── application-docker.properties    # Docker config
│   └── test/                             # Test files
│
├── bookstore-ui/
│   ├── Dockerfile                # Frontend multi-stage build
│   ├── nginx.conf                # Nginx reverse proxy config
│   ├── .dockerignore
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── App.jsx               # Root component
│       ├── api.js                # API client
│       ├── components/           # React components
│       ├── pages/                # Page components
│       └── services/             # Service utilities
│
├── docs/                         # Documentation
│   ├── DAY1_REQUIREMENT_SPEC.md
│   ├── DAY2_DESIGN_DIAGRAMS.md
│   └── ...
│
└── screenshots/                  # Application screenshots
```

---

## Architecture

### Docker Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Docker Network                           │
│                  (bookstore-network)                         │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────────┐  ┌────────────────┐  ┌───────────────┐  │
│  │    Frontend    │  │    Backend     │  │   DynamoDB    │  │
│  │    (Nginx)     │  │ (Spring Boot)  │  │    (Local)    │  │
│  │                │  │                │  │               │  │
│  │  Port: 3000    │  │  Port: 8080    │  │  Port: 8000   │  │
│  │                │  │                │  │               │  │
│  │  - React App   │  │  - REST API    │  │  - Users      │  │
│  │  - Static      │  │  - Business    │  │  - Books      │  │
│  │    Assets      │  │    Logic       │  │  - History    │  │
│  │  - API Proxy   │  │  - Auth        │  │  - Recs       │  │
│  └───────┬────────┘  └───────┬────────┘  └───────────────┘  │
│          │                   │                               │
│          │   /api/* ────────▶│                               │
│          │   (proxy)         │                               │
│                                                              │
└─────────────────────────────────────────────────────────────┘

External Access:
  • http://localhost:3000 → Frontend (Nginx)
  • http://localhost:8080 → Backend API (Spring Boot)
  • http://localhost:8000 → DynamoDB Local
```

### Data Flow

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  Browser │────▶│  Nginx   │────▶│  Spring  │────▶│ DynamoDB │
│          │◀────│  :3000   │◀────│  :8080   │◀────│  :8000   │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
                      │
                      │ Static files served directly
                      │ /api/* proxied to backend
```

### Database Schema

**DynamoDB Tables:**
| Table | Purpose | Key |
|-------|---------|-----|
| Users | Authentication | username (PK) |
| BrowsingHistory | Recent views | username (PK) |
| Recommendations | Suggestions | userId (PK) |

**In-Memory Storage:**
| Store | Purpose |
|-------|---------|
| Books | Catalog with ASIN indexing |
| Orders | Completed purchases |

---

## Demo Credentials

| Role | Username | Password | Capabilities |
|------|----------|----------|--------------|
| **Admin** | admin | admin123 | Full inventory management, bulk operations |
| **Customer** | (Register) | - | Browse, cart, checkout, history |

**Register a new account:** Navigate to `/register`

---

## Key Highlights

### 1. ASIN Inventory System
```
Format: B0XXXXXXXX (10 characters)
Example: B0K7M2N9PQ

Features:
- Amazon-style identifiers
- Deterministic generation (same book = same ASIN)
- Prevents duplicate imports
- Human-readable format
```

### 2. Deterministic ID Generation
```java
// Same book always gets same ID
String id = DeterministicId.forBook("Clean Code", "Robert Martin");
// Result: "b-7a3f2e1d9c8b" (consistent across imports)
```

### 3. Bulk Operations
| Operation | Description |
|-----------|-------------|
| **Upload** | CSV/JSON with drag & drop preview |
| **Update** | Batch price/stock adjustments by ASIN |
| **Delete** | Remove multiple books at once |
| **Export** | Filtered catalog download (JSON/CSV) |

### 4. Browsing History (LinkedList)
```
Capacity: 10 books (FIFO)
Operations:
- Add to front: O(1)
- Remove from end: O(1)
- Duplicate handling: Move to front
```

### 5. OpenLibrary Integration
```
Cover URL: https://covers.openlibrary.org/b/isbn/{ISBN}-L.jpg
Fallback: Genre-based gradient backgrounds
Loading: Lazy loading for performance
```

---

## API Endpoints

### Books
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | List all books |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/asin/{asin}` | Get book by ASIN |
| GET | `/api/books/search?q={query}` | Search books |
| POST | `/api/books` | Create book (admin) |
| PUT | `/api/books/{id}` | Update book (admin) |
| DELETE | `/api/books/{id}` | Delete book (admin) |
| POST | `/api/books/bulk` | Bulk upload (admin) |
| PUT | `/api/books/bulk` | Bulk update (admin) |
| DELETE | `/api/books/bulk` | Bulk delete (admin) |

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Login |
| POST | `/api/users/logout` | Logout |
| GET | `/api/users/me` | Get current user |

### Cart & Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cart/preview` | Calculate cart total |
| POST | `/api/cart/checkout` | Place order |
| GET | `/api/orders/me` | Get user's orders |

### History & Recommendations
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/history/view` | Record book view |
| GET | `/api/history` | Get browsing history |
| GET | `/api/recommendations` | Get recommendations |

### Export
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books/export/json` | Export as JSON |
| GET | `/api/books/export/csv` | Export as CSV |

---

## Screenshots

### Catalog View
![Catalog](screenshots/catalog.png)

### Admin Panel
![Admin](screenshots/admin.png)

### Shopping Cart
![Cart](screenshots/cart.png)

---

## Environment Variables

### Docker (docker-compose.yml)
```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - DDB_ENDPOINT=http://dynamodb:8000
  - AWS_REGION=us-east-1
  - AWS_ACCESS_KEY_ID=dummy
  - AWS_SECRET_ACCESS_KEY=dummy
```

### Local Development (application.properties)
```properties
dynamodb.endpoint=${DDB_ENDPOINT:http://localhost:8000}
dynamodb.region=us-east-1
dynamodb.accessKey=dummy
dynamodb.secretKey=dummy
```

---

## Testing

### Run Tests
```bash
# All tests
./mvnw test

# With coverage report
./mvnw verify

# Skip tests
./mvnw install -DskipTests
```

### Test Coverage
- **Overall:** 58%
- **Unit Tests:** 92 tests
- **Integration Tests:** 28 tests
- **Controller Tests:** 14 tests

---

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration:

```yaml
Triggers:
  - Push to main
  - Pull requests to main

Jobs:
  1. Backend Build
     - Setup JDK 17
     - Run tests
     - Build JAR
     
  2. Frontend Build
     - Setup Node 18
     - Install dependencies
     - Build production bundle
```

---

## Contributing

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit** your changes
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push** to the branch
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open** a Pull Request

### Development Guidelines
- Follow existing code style
- Write tests for new features
- Update documentation as needed
- Keep commits atomic and well-described

---

## Troubleshooting

### Docker Issues

**Container won't start:**
```bash
docker compose logs backend
```

**Port already in use:**
```bash
# Find process using port
lsof -i :8080
# Kill it
kill -9 <PID>
```

**Clean restart:**
```bash
docker compose down -v
docker compose build --no-cache
docker compose up -d
```

### Backend Issues

**DynamoDB connection refused:**
- Ensure DynamoDB is running on port 8000
- Check `application.properties` endpoint

**Build failures:**
```bash
./mvnw clean install -DskipTests
```

### Frontend Issues

**Dependencies issues:**
```bash
rm -rf node_modules
npm install
```

**Build issues:**
```bash
npm run build -- --debug
```

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- **Amazon ATLAS Program** - Training and mentorship
- **OpenLibrary** - Book cover API
- **Material-UI** - React component library
- **Spring Boot** - Backend framework
- **DynamoDB Local** - Local database for development

---

## Author

**Aditya Shubham**

- GitHub: [@MinotaurG](https://github.com/MinotaurG)
- Project Link: [Online Bookstore Application](https://github.com/MinotaurG/Online_Bookstore_Application)

---

**Built as part of the Amazon ATLAS Training Program**