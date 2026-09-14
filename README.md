# Davexo

Davexo is a personal management web application that brings expense tracking, budgets, tasks, and inventory together in a single dashboard.

The project consists of two applications:

- a REST API built with Spring Boot;
- a web interface built with Angular.

> [!IMPORTANT]
> Davexo is currently under development.
> The backend provides most of the business features, while the frontend currently offers authentication and dashboard access.

## Features

| Area | Backend | Frontend |
|---|---:|---:|
| JWT authentication | ✅ | ✅ |
| Registration with an allowed email list | ✅ | 🚧 |
| Dashboard | ✅ | ✅ |
| Task management | ✅ | 🚧 |
| Expense management | ✅ | 🚧 |
| Expense categories | ✅ | 🚧 |
| Budget management | ✅ | 🚧 |
| Budget consumption tracking | ✅ | 🚧 |
| Inventory management | ✅ | 🚧 |
| Shopping list | ✅ | 🚧 |
| Statistics by period | ✅ | 🚧 |
| Administration statistics | ✅ | 🚧 |

The dashboard currently displays:

- total expenses for the current month;
- global budget consumption;
- the number of pending tasks;
- the number of items to buy;
- the five highest-priority tasks;
- the five highest-priority items to buy;
- the ten most recent expenses.

## Technology Stack

### Backend

- Java 25
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT with JJWT
- PostgreSQL 18
- Springdoc OpenAPI / Swagger UI
- Gradle 9.5.1
- JUnit, Mockito, and Testcontainers

### Frontend

- Angular 22
- TypeScript 6
- RxJS 7
- Angular Reactive Forms
- HTTP client generated with OpenAPI Generator 7.24
- Vitest
- npm 11.17

## Architecture

```text
davexo/
├── davexo-backend/
│   ├── compose.yml
│   ├── build.gradle
│   └── src/
│       ├── main/
│       │   ├── java/com/davexo/backend/
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── entity/
│       │   │   ├── exception/
│       │   │   ├── mapper/
│       │   │   ├── repository/
│       │   │   ├── security/
│       │   │   └── service/
│       │   └── resources/
│       └── test/
└── davexo-frontend/
    ├── package.json
    ├── angular.json
    └── src/
        ├── environments/
        └── app/
            ├── api/
            ├── core/
            │   ├── guards/
            │   ├── interceptors/
            │   └── services/
            └── features/
                ├── auth/
                └── dashboard/
```

The backend follows a layered architecture:

```text
Controller → Service → Repository → PostgreSQL
               ↓
           Mapper / DTO
```

Business resources are isolated by user. Except for the authentication routes and OpenAPI documentation, all endpoints require a valid JWT. Administration routes also require the `ADMIN` role.

## Prerequisites

Before starting the project, install:

- Git;
- Java 25;
- Docker with Docker Compose;
- Node.js `22.22.3+`, `24.15.0+`, or a later version compatible with Angular 22;
- npm 11, preferably the `11.17.0` version declared by the project.

You do not need to install Gradle globally because the Gradle Wrapper is included in the repository.

## Installation

Clone the repository:

```bash
git clone https://github.com/DVDCoder18/Davexo.git
cd Davexo
```

### 1. Configure the backend

Create a `davexo-backend/.env` file:

```dotenv
DB_NAME=db_name
DB_USERNAME=db_username
DB_PASSWORD=change-me

JWT_SECRET=replace-with-a-base64-encoded-secret
JWT_EXPIRATION=3600000

ALLOWED_EMAILS=user@example.com,another-user@example.com
CORS_ALLOWED_ORIGIN=http://localhost:4200
```

Environment variable reference:

| Variable | Description |
|---|---|
| `DB_NAME` | PostgreSQL database name used by Docker Compose and the backend JDBC connection. |
| `DB_USERNAME` | PostgreSQL username. |
| `DB_PASSWORD` | PostgreSQL password. |
| `JWT_SECRET` | Base64-encoded secret used to sign JWTs. It must represent a sufficiently long key of at least 256 bits. |
| `JWT_EXPIRATION` | JWT validity period in milliseconds. |
| `ALLOWED_EMAILS` | Comma-separated list of email addresses allowed to create an account. |
| `CORS_ALLOWED_ORIGIN` | Origin allowed to call the API from a browser. |

The `.env` file is ignored by Git and must never be committed to the repository.

### 2. Start PostgreSQL

From the backend directory:

```bash
cd davexo-backend
docker compose up -d
```

PostgreSQL is then available on `localhost:5432`.

Check the container status:

```bash
docker compose ps
```

### 3. Start the backend

On Linux or macOS:

```bash
./gradlew bootRun
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

The API is available at:

```text
http://localhost:8081
```

### 4. Start the frontend

In a second terminal:

```bash
cd davexo-frontend
npm ci
npm start
```

The application is available at:

```text
http://localhost:4200
```

The Angular development server automatically uses:

```text
http://localhost:8081
```

as the API URL.

## Development Profile and Demo Data

The Spring `dev` profile automatically initializes demo users and data.

On Linux or macOS:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

On Windows:

```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

Main user account:

```text
Email:    dev@davexo.local
Password: Dev1234!
```

Administrator account:

```text
Email:    admin@davexo.local
Password: Admin1234!
```

These credentials are intended exclusively for local development.

> [!CAUTION]
> Hibernate is currently configured with `spring.jpa.hibernate.ddl-auto=create`. The database schema and its data are recreated every time the backend starts. This configuration is not suitable for a production environment.

## API Documentation

When the backend is running, Swagger UI is available at:

```text
http://localhost:8081/swagger-ui/index.html
```

The OpenAPI contract in JSON format is available at:

```text
http://localhost:8081/v3/api-docs
```

### Main Resources

| Resource | Prefix | Access |
|---|---|---|
| Authentication | `/api/auth` | Public |
| Tasks | `/api/tasks` | Authenticated user |
| Budgets | `/api/budgets` | Authenticated user |
| Expenses | `/api/expenses` | Authenticated user |
| Expense categories | `/api/expense-categories` | Authenticated user |
| Inventory items | `/api/inventory-items` | Authenticated user |
| Inventory categories | `/api/inventory-categories` | Authenticated user |
| Statistics | `/api/statistics` | Authenticated user |
| Dashboard | `/api/dashboard` | Authenticated user |
| Administration | `/api/admin` | `ADMIN` role |

The main business resources provide CRUD operations. Additional endpoints are available for:

- budget consumption;
- the shopping list;
- expense and task statistics;
- dashboard data aggregation.

### Authentication

After a successful login, the API returns a JWT. Protected requests must provide this token in the HTTP header:

```http
Authorization: Bearer <token>
```

The frontend currently stores the token in `localStorage` under the `davexo_token` key. An interceptor automatically adds it to protected requests. If the API returns a `401` response, the token is removed and the user is redirected to the login page.

## OpenAPI-Generated Angular Client

The following directory contains the TypeScript client generated from the backend contract:

```text
davexo-frontend/src/app/api
```

It includes the HTTP services, DTOs, and configuration required by the Angular application.

To regenerate this client, start the backend and then run the following command from `davexo-frontend`:

```bash
npx openapi-generator-cli generate \
  -i http://localhost:8081/v3/api-docs \
  -g typescript-angular \
  -o src/app/api \
  --additional-properties=ngVersion=22.1.0
```

> [!WARNING]
> This command replaces the generated files in `src/app/api`. Any manual changes made in this directory may therefore be lost.

## Tests

### Backend

The backend test suite includes:

- service unit tests;
- statistics tests;
- repository integration tests;
- authentication and dashboard endpoint integration tests.

Run the tests on Linux or macOS:

```bash
cd davexo-backend
./gradlew test
```

Run the tests on Windows:

```powershell
cd davexo-backend
.\gradlew.bat test
```

Docker must be running because the integration tests use Testcontainers with PostgreSQL 18.

### Frontend

```bash
cd davexo-frontend
npm test
```

Frontend tests are executed with Vitest.

## Production Build

### Backend

On Linux or macOS:

```bash
cd davexo-backend
./gradlew build
```

On Windows:

```powershell
cd davexo-backend
.\gradlew.bat build
```

The artifact is generated in:

```text
davexo-backend/build/libs/
```

### Frontend

```bash
cd davexo-frontend
npm run build
```

The compiled files are generated in:

```text
davexo-frontend/dist/
```

In production, `environment.ts` defines an empty API URL. The frontend therefore assumes that the API is exposed on the same origin or configured by the deployment infrastructure.

## Stopping the Local Environment

From `davexo-backend`:

```bash
docker compose down
```

This command does not remove the PostgreSQL volume.

## Current Status and Known Limitations

- only the login and dashboard screens are available;
- the registration API exists, but no registration screen or associated Angular route has been implemented yet;
- the registration link displayed on the login page is not active;
- logout logic exists, but no logout control is currently displayed on the dashboard;
- task, expense, budget, and inventory management screens remain to be developed;
- statistics and administration features do not have a user interface yet;
- the Angular guard checks for the presence of a token, but does not directly check its expiration date;
- frontend tests primarily cover component and service instantiation;
- no end-to-end testing framework is configured;
- no application Dockerfile or production deployment configuration is currently provided;
- no continuous integration pipeline is configured.

## License

No license is currently defined for this project. Without a license, the code remains subject to its owner's copyright.

---

_This README was generated by artificial intelligence._
