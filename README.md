# PR_ERPMonolith_JavaAPi (Sales ERP Monolith)

A robust, enterprise-grade Spring Boot Monolithic ERP Application designed with a clean layered architecture, secure authentication, and integrated DevOps tooling.

## 🚀 Key Features

### 🌟 Core & Architecture

- **Layered Architecture**: Strictly separates `Controller` (API), `Service` (Business Logic), and `Repository` (Data Access via JOOQ).
- **Java 25 runtime & Spring Boot 3.4**: Runs on the newest JDK; code is compiled with release 23 for Spring Boot’s current bytecode support.
- **MySQL & JOOQ**: Type-safe SQL execution and efficient data handling.

### 🔐 Security & Authentication

- **JWT Authentication**: Stateless security using `Bearer` tokens.
- **Secure Endpoints**: `JwtAuthenticationFilter` enforces validation on all protected routes.
- **Public & Protected**: Explicit configuration for public endpoints (Swagger, Auth, Actuator) vs protected resources.

### 📚 API Documentation (Swagger UI)

- **URL**: [http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)
- **Dynamic Config**: API Title and Version are synced from environment variables (`.env`).
- **Features**:
  - **Bearer Auth**: "Authorize" button to test protected endpoints.
  - **Select Definition**: Dropdown to filter API groups (e.g., "0.ALL APIs").
  - **Clean View**: Operations are collapsed by default (`doc-expansion: none`).

### ⚙️ DevOps & Monitoring

- **Actuator Enabled**: Operational monitoring at `/actuator` and `/actuator/health`.
- **Docker Ready**: Includes `Dockerfile` and scripts to build lightweight images (`eclipse-temurin:25-jre`).
- **Build Automation**: Node.js scripts (`scripts/build.js`) manage cross-platform builds (Windows/Mac) without external dependencies.

### 🔔 Error Handling & Alerts

- **Global Exception Handler**: Centralized error management returning standardized `ApiError` responses.
- **Telegram Integration**: Critical exceptions trigger instant notifications to a configured Telegram Chat via `TelegramBotService`.

---

## 🛠️ Configuration

The application uses a centralized **`.env`** file for configuration. Create this file in the project root:

```env
# Application Metadata
APP_VERSION=1.0.0
PROJECT_NAME="Sales ERP Monolith"
APP_ENV=dev

# Server
SERVER_PORT=9090

# Database URLs (Based on active APP_ENV profile)
DB_URL_LOCAL=jdbc:mysql://localhost:3306/1074_NewLink_DBA_Local?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
DB_URL_DEV=jdbc:mysql://dev-server:3306/1074_NewLink_DBA_Dev?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
DB_URL_QA=jdbc:mysql://qa-server:3306/qa_db?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
DB_URL_PROD=jdbc:mysql://prod-server:3306/prod_db?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true

# Database Credentials
DB_USERNAME_LOCAL=root
DB_PASSWORD_LOCAL=root
DB_USERNAME_DEV=dev_user
DB_PASSWORD_DEV=dev_password
# ... QA & PROD ...

# JWT
JWT_SECRET=your_very_long_secret_key_needs_to_be_secure

# Telegram / Alerts
TELEGRAM_BOT_TOKEN=your_bot_token
TELEGRAM_CHAT_ID=your_chat_id
```

---

## � Getting Started

### Prerequisites

- **Java 25** (JDK)
- **Node.js** (for build scripts)
- **Docker** (optional, for containerization)
- **Maven** (optional, wrapper/embedded used)

### 1. Run Locally

To start the application in development mode:

```bash
# Run Maven Spring Boot target
npm run dev
```

_App will start on port `9090` taking database properties mapped to the value in `APP_ENV` ._

### 2. Build for Production (Artifact)

Generates a deployable ZIP file in `release/`:

```bash
# General Build (using current APP_ENV value to generate properties)
npm run build
```

### 3. Docker Build & Run

Build and run a multi-stage Docker container utilizing docker compose.

```bash
npm run docker
```

**Run Docker Container Manually:**
_Note: `COMPOSE_PROJECT_NAME` derives the container name flexibly._

```bash
COMPOSE_PROJECT_NAME=erp-monolith docker compose up -d --build
```

---

## 📂 Project Structure

```
src/main/java/com/udaya
├── config          # Configuration (App, Security, Swagger, CORS)
├── controller      # REST Controllers (API Layer)
├── exception       # Global Exception Handling & ApiError
├── model           # Data Models / DTOs
├── repository      # Data Access Layer (JOOQ)
├── service         # Business Logic Layer
└── util            # Utilities (JwtUtil, TelegramUtil)

scripts/            # Build & Ops Scripts (Node.js)
```

## 📬 API Usage

### Authentication

**Endpoint**: `POST /auth/login`
**Body**:

```json
{
  "username": "admin",
  "password": "password"
}
```

**Response**:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

_Use this token in Swagger's "Authorize" button._
