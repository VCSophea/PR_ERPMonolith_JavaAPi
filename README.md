# PR_ERPMonolith_JavaAPi (Sales ERP Monolith)

A robust, enterprise-grade Spring Boot Monolithic ERP Application designed with a clean layered architecture, secure authentication, and integrated DevOps tooling.

## 🚀 Key Features

### 🌟 Core & Architecture

- **Layered Architecture**: Strictly separates `Controller` (API), `Service` (Business Logic), and `Repository` (Data Access via JOOQ).
- **Java 25 & Spring Boot 3+**: Built on the latest cutting-edge Java ecosystem.
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

# Server
SERVER_PORT=9090

# Database
DB_URL=jdbc:mysql://localhost:3306/1074_NewLink_DBA?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
DB_USERNAME=root
DB_PASSWORD=your_password

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
# Install dependencies (mvn) and run
yarn start
# OR
npm run start
```

_App will start on port `9090`._

### 2. Build for Production (Artifact)

Generates a deployable ZIP file in `release/`:

```bash
# QA / Testing Build
yarn build:qa

# Production Build
yarn build:prod
```

### 3. Docker Build & Run

Build a Docker image using the integrated script:

```bash
# Build Local Image (Tag: latest)
yarn build:docker:local

# Build Prod Image (Tag: <version>)
yarn build:docker:prod
```

**Run Docker Container:**
_Note: `host.docker.internal` allows the container to access your host's MySQL._

```bash
docker run -d \
  -p 9091:9090 \
  --name erp-monolith \
  --env-file .env \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/1074_NewLink_DBA?..." \
  erp-monolith:latest
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

**Endpoint**: `POST /api/v1/auth/login`
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
