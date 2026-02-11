# PR_ERPMonolith_JavaAPi

A Spring Boot Monolithic ERP Application with a refined layered architecture and secure authentication.

## 🚀 Key Features

### Architecture & Design

- **Layered Structure**: Controller, Service, Repository (JOOQ), Model.
- **Security**: JWT-based Authentication (Bearer Token) with `AuthController` and strict `JwtAuthenticationFilter`.
- **Error Handling**: Centralized `GlobalExceptionHandler` returning structured `ApiError` responses with Telegram alerts.
- **Database**: configured to use `1074_NewLink_DBA` with JOOQ support.

### API & Documentation

- **Clean REST API**: Controllers return direct `ResponseEntity` (no wrapper).
- **Swagger UI**: Integrated with Bearer Auth support (`/swagger-ui.html`).

### Build & Dev Ops

- **Environment Config**: Centralized `.env` file for configuration.
- **Automated Build**: `yarn build:qa` (Maven build + Version Sync + Artifact Packaging).
- **Standard Commands**: `package.json` for consistent run scripts (`start`, `build`, `clean`).

## 🛠️ Getting Started

### Prerequisites

- JDK 21+
- Node.js (for build scripts)

### Commands

```bash
# Build for QA
yarn build:qa

# Start Application
yarn start
```

### Authentication

Login via `POST /api/v1/auth/login` to receive a JWT token.
