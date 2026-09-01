# Product Catalog Service

The service provides product catalog management for the E-Commerce Microservices architecture, with public product browsing, paginated search, product management, and role-based authorization. It uses Spring Security for endpoint-level access control and communicates with the User & Auth Service for token validation, keeping authentication centralized while allowing the Product Catalog Service to enforce its own authorization rules.

> **Note:** This service is part of a larger E-Commerce Microservices project.

---

## Highlights

* **Public product browsing** — Product browsing, product details, and search are accessible without authentication, allowing users to explore the catalog before logging in
* **Paginated search results** — Search results are returned in pages to avoid unnecessarily loading the entire result set
* **Dual search interfaces** — Supports both `GET` search using request parameters and `POST` search using a dedicated request DTO
* **Role-based product management** — Add, update, and delete operations require authentication and are permitted only when the authenticated user's roles allow the requested operation
* **Spring Security filter chain** — Endpoint-level access control is enforced through Spring Security, with catalog read and search operations configured as public and product management operations protected
* **Centralized authentication** — Access tokens are validated by the User & Auth Service rather than duplicating token validation logic across the Product Catalog Service
* **Service-level authorization** — After authentication, the Product Catalog Service evaluates the user's roles before allowing protected product operations
* **Service-to-service communication** — Communication with the User & Auth Service uses Eureka-based service discovery and load-balanced RestTemplate
* **Clean layered architecture** — Controller, service, repository, security, and domain responsibilities are separated following clean code and Low-Level Design principles

---

## Architecture Overview

```text
                         Client
                           │
                           ▼
                 Product Catalog Service
                           │
                           ▼
              Spring Security Filter Chain
                           │
              ┌────────────┴────────────┐
              │                         │
        READ / SEARCH              WRITE OPERATIONS
        permitAll()               Authentication Required
              │                         │
              ▼                         ▼
       Product Service          User/Auth Service
              │                         │
              │                 /validate + Token
              │                         │
              │                         ▼
              │                  Validate Access Token
              │                         │
              │                         ▼
              │                  User Identity + Roles
              │                         │
              │                         ▼
              │                  Role Authorization
              │                    /          \
              │                Allowed       Denied
              │                   │             │
              │                   │             ▼
              │                   │         Exception
              │                   │
              └───────────────────┤
                                  ▼
                           Product Repository
                                  │
                                  ▼
                              MySQL DB
```


## Authentication & Authorization

The Product Catalog Service separates **authentication** from **authorization**.

### Authentication

The User & Auth Service is responsible for validating the access token.


### Authorization

Authentication establishes **who the user is**.

The Product Catalog Service is responsible for determining whether that authenticated user is allowed to perform a particular product operation.

---

## Public vs Protected Operations

### Public Operations

Product discovery is publicly accessible and does not require authentication.

This includes:

* Product browsing
* Product details
* Product search

Search supports both `GET` and `POST` requests:

```text
GET  /products/search
POST /products/search
```

Both endpoints are configured with `permitAll()` in the Spring Security filter chain.

The `GET` endpoint accepts search criteria through request parameters, while the `POST` endpoint accepts a dedicated search request DTO.

### Protected Operations

Product management operations require authentication and appropriate roles.

```text
POST   /products/
PUT    /products/{id}
DELETE /products/{id}
```

For these operations:

1. The request passes through the Spring Security filter chain.
2. The access token is sent to the User & Auth Service for validation.
3. The authenticated user's identity and roles are returned.
4. The Product Catalog Service checks whether the user's roles permit the requested operation.
5. The operation is either executed or rejected.

---

## Product Catalog Features

### Product Management

* Add products
* Retrieve products
* Update products
* Delete products
* Retrieve individual product details

### Product Browsing

* Browse available products
* Retrieve product details
* Browse catalog without authentication

### Search

* Search products using keywords
* `GET` search using request parameters
* `POST` search using a dedicated request DTO
* Paginated search results
* Public search without authentication

---

## Service-to-Service Communication

Communication between Product Catalog and User & Auth Service uses the service discovery infrastructure of the E-Commerce architecture.

```text
Product Catalog
      │
      ▼
LoadBalanced RestTemplate
      │
      ▼
Eureka Service Discovery
      │
      ▼
USER-AUTH-SERVICE
      │
      ▼
/validate
```

The Product Catalog Service therefore does not rely on a hardcoded User/Auth Service host or port.

---

## API Endpoints

The service uses `/products` as its context path.

### Product APIs

| Method   | Endpoint         | Auth Required | Description              |
| -------- | ---------------- | ------------- | ------------------------ |
| `POST`   | `/products/`     | Yes           | Add a product            |
| `PUT`    | `/products/{id}` | Yes           | Update a product         |
| `DELETE` | `/products/{id}` | Yes           | Delete a product         |
| `GET`    | `/products/{id}` | No            | Retrieve product details |

### Search APIs

Search is publicly accessible and supports both request-parameter and request-body based searches.

| Method | Endpoint           | Auth Required | Description                                |
| ------ | ------------------ | ------------- | ------------------------------------------ |
| `GET`  | `/products/search` | No            | Search products using request parameters   |
| `POST` | `/products/search` | No            | Search products using a search request DTO |

Search responses are paginated.

---

## Design Decisions

### Why are product reads public?

-> Product discovery is a core e-commerce browsing experience and should not require users to authenticate before viewing or searching the catalog. Authentication is therefore required only for operations that modify product data.

### Why support both GET and POST search?

-> The `GET` search endpoint provides a simple query-parameter based interface for straightforward searches, while the `POST` endpoint accepts a dedicated request DTO for structured search criteria. Both provide the same public catalog search capability.

### Why centralize token validation?

-> Authentication is already owned by the User & Auth Service. Delegating token validation avoids duplicating JWT validation and session-related authentication logic across individual microservices.

### Why perform authorization inside Product Catalog?

-> Authentication and authorization are separate responsibilities. The User & Auth Service establishes the authenticated user's identity and roles, while the Product Catalog Service determines whether those roles are sufficient to perform product-specific operations.

### Why use Spring Security Filter Chain?

-> Spring Security provides a centralized mechanism for enforcing endpoint-level access rules. Public catalog and search endpoints can be explicitly permitted while product management endpoints require authentication before reaching the application logic.

### Why use service discovery for User/Auth communication?

-> The Product Catalog Service should not depend on a hardcoded physical location of the User & Auth Service. Service discovery allows it to communicate using the logical service name while the infrastructure resolves the actual service instance.

### Why paginated search?

-> Returning the entire search result set can become inefficient as the product catalog grows. Pagination limits the amount of data returned per request and provides a more scalable API response model.

---

## Tech Stack

| Layer                 | Technology                 |
| --------------------- | -------------------------- |
| Framework             | Spring Boot                |
| Security              | Spring Security            |
| Database              | MySQL                      |
| ORM                   | Spring Data JPA            |
| Service Discovery     | Netflix Eureka             |
| Service Communication | Load-balanced RestTemplate |
| Build Tool            | Maven                      |
| Language              | Java                       |

---

## Environment Variables

Sensitive database and service configuration should be externalized through environment variables rather than hardcoded.

| Variable              | Description         |
| --------------------- | ------------------- |
| `DATASOURCE_URL`      | JDBC connection URL |
| `DATASOURCE_USERNAME` | Database username   |
| `DATASOURCE_PASSWORD` | Database password   |
| `EUREKA_SERVER_URL`   | Eureka Server URL   |

---
<!--
## Getting Started

```bash
# Clone the repository
git clone https://github.com/your-username/product-catalog-service.git

# Navigate to the project
cd product-catalog-service

# Configure environment variables

# Run the service
./mvnw spring-boot:run
```
-->
The Product Catalog Service will register itself with the Eureka Service Discovery Server and can communicate with the User & Auth Service through service discovery.

---

## Known Gaps & Roadmap

* Advanced product filtering and sorting
* Product image storage/management
* Product inventory integration
* Event-driven product updates through Kafka
* Redis caching for frequently accessed catalog data
* Elasticsearch-based search
* Docker containerization
* Distributed tracing and observability
