# Asset Tracker Backend

A **Spring Boot** REST API for personal finance management — track your investment portfolio (crypto, stocks, gold) and daily expenses in one place, with a unified dashboard summary. Secured with **JWT authentication** and optimized with **Caffeine caching**.

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.2 |
| Language | Java 17 |
| Build Tool | Gradle 8.14 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Caching | Caffeine (in-memory, 10 min TTL) |
| API Docs | Swagger UI (SpringDoc OpenAPI 2.8.8) |
| Utilities | Lombok |
| Monitoring | Spring Actuator |
| Containerization | Docker |

## Project Structure

```
src/main/java/com/example/asset_tracker_backend/
├── AssetTrackerBackendApplication.java      # Entry point (@EnableCaching)
├── config/                                  # Security & JWT configuration
│   ├── SecurityConfig.java                  # Filter chain, CORS, password encoder
│   ├── JwtUtil.java                         # Token generation & validation
│   └── JwtAuthenticationFilter.java         # Extracts & validates Bearer token
├── auth/                                    # Authentication module
│   ├── controller/AuthController.java
│   ├── dto/RegisterRequest.java, AuthRequest.java, AuthResponse.java
│   ├── model/User.java
│   ├── repository/UserRepository.java
│   └── service/AuthService.java, UserDetailsServiceImpl.java
├── portfolio/                               # Investment assets module
│   ├── controller/AssetController.java
│   ├── model/Asset.java
│   ├── repository/AssetRepository.java
│   └── service/AssetService.java & AssetServiceImpl.java
├── expense/                                 # Expense tracking module
│   ├── controller/ExpenseController.java
│   ├── dto/CategoryExpenseDto.java
│   ├── model/Expense.java
│   ├── repository/ExpenseRepository.java
│   └── service/ExpenseService.java & ExpenseServiceImpl.java
└── dashboard/                               # Aggregated dashboard module
    ├── controller/DashboardController.java
    ├── dto/DashboardSummaryDto.java
    └── service/DashboardService.java & DashboardServiceImpl.java
```

Each module follows the **Controller → Service → Repository** layered architecture pattern.

## Authentication

### How It Works

1. **Register** a new user via `POST /api/auth/register`
2. **Login** via `POST /api/auth/login` — returns a JWT token
3. **Include the token** in all subsequent requests as a header:
   ```
   Authorization: Bearer <your-jwt-token>
   ```
4. The `JwtAuthenticationFilter` intercepts every request to `/api/**` (except `/api/auth/**`), validates the token, and sets the security context
5. If the token is missing, expired, or invalid → **401 Unauthorized**

### Auth Flow Diagram

```
┌────────┐    POST /api/auth/register     ┌────────────┐
│ Client │ ──────────────────────────────▶ │ AuthService │ ──▶ Save user (BCrypt hash)
└────────┘                                └────────────┘

┌────────┐    POST /api/auth/login        ┌────────────┐
│ Client │ ──────────────────────────────▶ │ AuthService │ ──▶ Verify credentials
└────────┘    ◀── { token, username }     └────────────┘      ──▶ Generate JWT

┌────────┐    GET /api/assets             ┌─────────────────────┐
│ Client │ ── Authorization: Bearer xxx ─▶│ JwtAuthFilter       │ ──▶ Validate token
└────────┘    ◀── 200 / 401               │ → SecurityContext   │     ──▶ Route to controller
                                          └─────────────────────┘
```

### Token Details

- **Algorithm:** HMAC-SHA256
- **Default expiration:** 24 hours (configurable via `JWT_EXPIRATION_MS`)
- **Payload:** username, roles, issued-at, expiration

## API Endpoints

### Auth — `/api/auth` (public, no token required)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT token |

<details>
<summary>Example: Register</summary>

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "secret123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john",
  "role": "USER"
}
```
</details>

<details>
<summary>Example: Login</summary>

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "secret123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john",
  "role": "USER"
}
```
</details>

### Portfolio — `/api/assets` 🔒

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/assets` | Add a new asset |
| `GET` | `/api/assets` | List all assets (cached) |

**Asset types:** `CRYPTO`, `STOCK`, `GOLD`

<details>
<summary>Example: Create an asset</summary>

```bash
curl -X POST http://localhost:8081/api/assets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Bitcoin",
    "type": "CRYPTO",
    "quantity": 0.5,
    "currentValue": 15000.00
  }'
```
</details>

### Expenses — `/api/expenses` 🔒

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/expenses` | Record a new expense |
| `GET` | `/api/expenses` | List all expenses (cached) |
| `DELETE` | `/api/expenses/{id}` | Delete an expense by ID |
| `GET` | `/api/expenses/monthly?year=&month=` | Get total spending for a specific month |
| `GET` | `/api/expenses/category-summary` | Get spending grouped by category (cached) |

<details>
<summary>Example: Create an expense</summary>

```bash
curl -X POST http://localhost:8081/api/expenses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "date": "2026-05-31",
    "category": "Food",
    "description": "Lunch",
    "amount": 12.50
  }'
```
</details>

<details>
<summary>Example: Monthly total</summary>

```bash
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8081/api/expenses/monthly?year=2026&month=5"
```
</details>

### Dashboard — `/api/dashboard` 🔒

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/summary?year=&month=` | Aggregated financial summary |

Returns a combined view with:
- **Total portfolio value** — sum of all asset values
- **Monthly expenses** — total spending for the given month
- **Asset allocation** — portfolio value grouped by asset type
- **Expense by category** — spending grouped by category

<details>
<summary>Example response</summary>

```json
{
  "totalPortfolioValue": 45000.00,
  "monthlyExpenses": 1250.75,
  "assetAllocation": {
    "CRYPTO": 15000.00,
    "STOCK": 20000.00,
    "GOLD": 10000.00
  },
  "expenseByCategory": {
    "Food": 450.00,
    "Transport": 200.75,
    "Entertainment": 600.00
  }
}
```
</details>

### Actuator (public)

| Endpoint | Description |
|---|---|
| `GET /actuator/health` | Application health check |
| `GET /actuator/info` | Application info |

## Swagger UI (API Documentation)

Interactive API docs are available out of the box at:

| URL | Description |
|---|---|
| [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Swagger UI (interactive) |
| [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) | OpenAPI 3.0 JSON spec |

**Using JWT in Swagger:**
1. Call `POST /api/auth/login` (or `/register`) to get a token
2. Click the **Authorize 🔒** button at the top of Swagger UI
3. Enter your token (without the `Bearer ` prefix)
4. All subsequent "Try it out" requests will include the token automatically

> Swagger endpoints are **public** — no authentication required to view the docs.

## Caching

In-memory caching is powered by **Caffeine** to reduce database load on read-heavy endpoints.

| Cache Name | Used By | Evicted On |
|---|---|---|
| `assets` | `getAllAssets()` | `createAsset()` |
| `assetAllocation` | `getAllocationByType()` | `createAsset()` |
| `expenses` | `getAllExpenses()` | `createExpense()`, `deleteExpense()` |
| `categorySummary` | `getCategorySummary()` | `createExpense()`, `deleteExpense()` |
| `dashboard` | — (evicted cross-module) | `createAsset()`, `createExpense()`, `deleteExpense()` |

**Config:** max 500 entries, 10-minute TTL (`expireAfterWrite`). Configurable in `application.yml`.

## Getting Started

### Prerequisites

- **Java 17+**
- **PostgreSQL** running locally (or a remote instance)

### Database Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE asset_tracker;
   ```

2. Run the migration scripts in order:
   ```bash
   psql -U postgres -d asset_tracker -f migration/update-schema.sql
   psql -U postgres -d asset_tracker -f migration/create-users-table.sql
   ```

   The `assets` table schema (if not already created):
   ```sql
   CREATE TABLE assets (
       id            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
       name          VARCHAR(255),
       type          VARCHAR(255),
       quantity      DOUBLE PRECISION,
       current_value DOUBLE PRECISION,
       created_at    TIMESTAMP
   );
   ```

### Run Locally

```bash
# Build (skip tests if no DB available)
./gradlew build -x test

# Run the application
./gradlew bootRun
```

The server starts on **http://localhost:8081**.

### Environment Variables

All config has sensible local defaults — no env vars needed for local development. For production, override with:

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/asset_tracker` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | *(built-in dev key)* | Base64-encoded HMAC-SHA256 secret (min 256 bits) |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | Token expiration in milliseconds |

> ⚠️ **Always set a strong `JWT_SECRET` in production.** The default is for local development only.

### Docker

```bash
# Build the JAR first
./gradlew build -x test

# Build the Docker image
docker build -t asset-tracker-backend .

# Run the container
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/asset_tracker \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e JWT_SECRET=<your-base64-secret> \
  asset-tracker-backend
```

## Database Schema

### `users` table

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT (PK, auto) | Unique identifier |
| `username` | VARCHAR (unique) | Login username |
| `email` | VARCHAR (unique) | User email |
| `password` | VARCHAR | BCrypt-hashed password |
| `role` | VARCHAR | USER or ADMIN |

### `assets` table

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT (PK, auto) | Unique identifier |
| `name` | VARCHAR | Asset name (e.g. "Bitcoin") |
| `type` | VARCHAR | Asset type: CRYPTO, STOCK, GOLD |
| `quantity` | DOUBLE | Quantity held |
| `current_value` | DOUBLE | Current monetary value |
| `created_at` | TIMESTAMP | Creation timestamp |

### `expenses` table

| Column | Type | Description |
|---|---|---|
| `id` | INTEGER (PK, auto) | Unique identifier |
| `date` | DATE | Expense date |
| `category` | VARCHAR | Category (e.g. Food, Transport) |
| `description` | VARCHAR | Description of the expense |
| `amount` | DOUBLE | Amount spent |

## License

This project is for personal/demo use.

