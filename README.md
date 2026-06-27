# Portobello Hub

RESTful backend for a portobello electronics and clothing platform.

## Tech Stack

- Java 17, Spring Boot 3.3 (Web, Data JPA, Security)
- PostgreSQL
- JWT (Stateless)
- Cloudinary (Image upload)
- ExchangeRate-API (Currency conversion)
- GraphQL (Admin stats)

## Roles

| Role | Capabilities |
|---|---|
| ROLE_CUSTOMER | Buy, review, and list items. |
| ROLE_STAFF | Moderate inventory and categories. |
| ROLE_ADMIN | Manage staff and view statistics. |

## Quick Start

### 1. Prerequisites

- Java 17+
- Maven 3.9+
- Docker Desktop (for PostgreSQL)

### 2. Database

Start PostgreSQL via Docker:

docker compose up -d

### 3. Environment Variables

cp .env.example .env

Required fields:
- DB_URL, DB_USERNAME, DB_PASSWORD
- JWT_SECRET (min. 32 characters)
- CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET

> ExchangeRate-API works without an API key on the free tier.

### 4. Run Application

mvn spring-boot:run

Server URL: http://localhost:8080

### 5. Seed Data (Development)

Auto-generated on startup:
- Roles: CUSTOMER, STAFF, ADMIN
- Admin Account: admin@portobellohub.com / Admin123!
- Categories: Smartphone, Laptop, Jackets, Shoes

## Project Structure

src/main/java/com/university/portobellohub/
config/          # Security, Cloudinary, seed data
controller/      # REST APIs
dto/             # Request/Response DTOsentity/          # JPA entities (Item inheritance)
repository/      # JpaRepository & custom queries
service/         # Business logic
 security/        # JWT filters & providers
 exception/       # Global error handling
 graphql/         # Admin analytics queries

## Data Model (JOINED Inheritance)

Item (Base)
 ElectronicItem (brand, model, warrantyMonths, batteryHealth)
 ClothingItem (size, material, gender)

Tables: users, roles, user_roles, categories, items, electronic_items, clothing_items, orders, order_items, reviews.

## Main Endpoints

| Method | Endpoint | Access |
|---|---|---|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |
| GET | /api/users/me | Authenticated |
| POST | /api/users/me/avatar | Authenticated |
| GET | /api/items | Public |
| POST | /api/items/electronics | Authenticated |
| POST | /api/items/clothing | Authenticated |
| PATCH | /api/items/{id}/review | STAFF/ADMIN |
| GET | /api/items/{id}/price?currency=USD | Public |
| GET | /api/currency/convert | Public |
| POST | /api/orders | Authenticated |
| GET | /api/admin/stats | ADMIN |
| POST | /graphql | ADMIN (with JWT) |

## GraphQL

GraphiQL Interface: http://localhost:8080/graphiql
Required Header: Authorization: Bearer <admin-jwt-token>

Example Query:

query {
  adminStats {
    totalUsers
    publishedItems
    totalRevenue
    inventoryByType { type count }
  }
  topSellingItems(limit: 5) {
    name
    salesCount
  }
}

## Postman Testing

1. Import collection: postman/portobellohub.postman_collection.json
2. Collection variables:
   - baseUrl = http://localhost:8080
   - token = Auto-filled after login

## Business Flow

1. Customer registers and logs in.
2. Customer lists an item (PENDING_REVIEW).
3. Staff approves (PUBLISHED) or rejects (REJECTED) the item.
4. Another customer places an order (PENDING).
5. Staff confirms the order (CONFIRMED -> item status becomes SOLD).
6. Customer leaves a post-purchase review.

## Key Features

- API Security: Entities are never exposed (DTOs only).
- Authentication: Passwords encrypted with BCrypt.
- Authorization: RBAC with 3 distinct roles.
- Database: Derived, JPQL, and Native queries used.
- Data Delivery: Pagination implemented for lists.
- Reliability: Centralized error handling (GlobalExceptionHandler).
- Configuration: Credentials managed strictly via environment variables.
