# OrderTrack

OrderTrack is a REST API for managing customers, products and orders. It was built as a backend learning project focused on layered architecture, database persistence, validation, JWT authentication, role-based authorization and Docker.

## Technologies

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL 8
- Flyway
- JWT with JJWT
- Maven Wrapper
- Docker and Docker Compose
- JUnit 5
- Mockito
- Lombok

## Main features

- User registration and login
- Password hashing with BCrypt
- Stateless authentication with JWT
- Role-based authorization with `ADMIN`, `STAFF` and `CUSTOMER`
- Public product catalogue
- Customer management
- Product management
- Order management
- Order-line management
- Order confirmation and cancellation
- Request validation
- Global exception handling
- Custom `401 Unauthorized` and `403 Forbidden` responses
- Database versioning with Flyway
- Dockerized Spring Boot application and MySQL database
- Persistent MySQL data through a Docker volume

## Architecture

The application follows a layered structure:

```text
Controller -> Service -> Repository -> Database
                |
              Mapper
                |
               DTO
```

Main packages:

```text
api/auth       Authentication and JWT generation
api/security   JWT filter and Spring Security handlers
api/user       Application users and roles
api/customer   Customer management
api/product    Product management
api/order      Order management
api/orderline  Order-line management
common         Shared exception handling
config         Application security configuration
```

## Security

The API uses stateless JWT authentication.

```text
Login or registration
        |
        v
JWT issued by the API
        |
        v
Authorization: Bearer <token>
        |
        v
JwtAuthenticationFilter
        |
        v
Authenticated user stored in SecurityContext
        |
        v
Role authorization applied to the requested endpoint
```

Access rules:

| Resource | Access |
|---|---|
| `/api/auth/**` | Public |
| `GET /api/products/**` | Public |
| `/api/admin/**` | `ADMIN` or `STAFF` |
| Any undeclared route | Denied |

Authentication errors return `401 Unauthorized`. Authenticated users without the required role receive `403 Forbidden`.

CORS is not enabled because this project is designed as a standalone backend API without an associated browser frontend.

## Requirements

To run the complete project with Docker:

- Docker
- Docker Compose

To run the application directly from the IDE or terminal:

- Java 17
- MySQL 8

## Environment variables

Create a `.env` file in the project root using `.env.example` as a reference:

```env
JWT_SECRET=replace-with-a-base64-secret
```

The JWT secret must be a sufficiently long Base64-encoded value. The real `.env` file must not be committed to Git.

The application also supports these optional environment variables:

| Variable | Default value | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3307/ordertrack` | Database connection URL |
| `DB_USERNAME` | `ordertrack` | Database username |
| `DB_PASSWORD` | `ordertrack` | Database password |
| `JWT_EXPIRATION` | `86400000` | Token lifetime in milliseconds |

## Run with Docker

Build and start the application and MySQL:

```bash
docker compose up --build
```

To run them in the background:

```bash
docker compose up -d --build
```

The API will be available at:

```text
http://localhost:8080
```

MySQL will be exposed locally at:

```text
localhost:3307
```

Check the running containers:

```bash
docker compose ps
```

View the application logs:

```bash
docker compose logs -f app
```

Stop the containers while keeping the database volume:

```bash
docker compose down
```

To also delete the persistent MySQL volume:

```bash
docker compose down -v
```

> `docker compose down -v` permanently removes the local database data stored in the Docker volume.

## Run locally

Start only MySQL with Docker:

```bash
docker compose up -d mysql
```

Set `JWT_SECRET` in the environment or in the IDE run configuration, then run the Spring Boot application.

On Windows PowerShell:

```powershell
$env:JWT_SECRET="replace-with-a-base64-secret"
.\mvnw.cmd spring-boot:run
```

## Main endpoints

### Authentication

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a user and issue a JWT |
| `POST` | `/api/auth/login` | Public | Authenticate a user and issue a JWT |

### Public products

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/products` | Public | List public products |
| `GET` | `/api/products/{id}` | Public | Get a public product by ID |

### Admin products

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/products` | `ADMIN`, `STAFF` | List all products |
| `GET` | `/api/admin/products/{id}` | `ADMIN`, `STAFF` | Get a product by ID |
| `POST` | `/api/admin/products` | `ADMIN`, `STAFF` | Create a product |
| `PUT` | `/api/admin/products/{id}` | `ADMIN`, `STAFF` | Update a product |
| `PATCH` | `/api/admin/products/{id}/activate` | `ADMIN`, `STAFF` | Activate a product |
| `PATCH` | `/api/admin/products/{id}/deactivate` | `ADMIN`, `STAFF` | Deactivate a product |

### Customers

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/customers` | `ADMIN`, `STAFF` | List customers |
| `GET` | `/api/admin/customers/{id}` | `ADMIN`, `STAFF` | Get a customer by ID |
| `POST` | `/api/admin/customers` | `ADMIN`, `STAFF` | Create a customer |
| `PUT` | `/api/admin/customers/{id}` | `ADMIN`, `STAFF` | Update a customer |
| `DELETE` | `/api/admin/customers/{id}` | `ADMIN`, `STAFF` | Delete a customer |

### Orders

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/orders` | `ADMIN`, `STAFF` | List orders |
| `GET` | `/api/admin/orders/{id}` | `ADMIN`, `STAFF` | Get an order by ID |
| `GET` | `/api/admin/orders/customer/{customerId}` | `ADMIN`, `STAFF` | List orders for a customer |
| `POST` | `/api/admin/orders` | `ADMIN`, `STAFF` | Create an order |
| `PATCH` | `/api/admin/orders/{id}/confirm` | `ADMIN`, `STAFF` | Confirm an order |
| `PATCH` | `/api/admin/orders/{id}/cancel` | `ADMIN`, `STAFF` | Cancel an order |

### Order lines

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/admin/orders/{orderId}/lines` | `ADMIN`, `STAFF` | Add a line to an order |
| `PUT` | `/api/admin/order-lines/{lineId}` | `ADMIN`, `STAFF` | Update an order line |
| `DELETE` | `/api/admin/order-lines/{lineId}` | `ADMIN`, `STAFF` | Delete an order line |

## Authentication example

Register a user:

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

Use the returned token in protected requests:

```http
Authorization: Bearer <your-jwt-token>
```

## Database migrations

Flyway applies the migrations located in:

```text
src/main/resources/db/migration
```

Hibernate uses schema validation, so database changes should be introduced through new Flyway migrations rather than by modifying the schema automatically.

## Tests

Run the test suite on Windows:

```bash
mvnw.cmd clean test
```

On Linux or macOS:

```bash
./mvnw clean test
```

Build the executable JAR without running tests:

```bash
mvnw.cmd clean package -DskipTests
```

The generated artifact is stored in:

```text
target/
```

## Suggested verification

Before considering the project ready, verify these cases:

| Scenario | Expected result |
|---|---|
| Register a new user | Successful response with JWT |
| Register an existing email | `409 Conflict` |
| Correct login | Successful response with JWT |
| Incorrect password | `401 Unauthorized` |
| Public products without token | `200 OK` |
| Admin endpoint without token | `401 Unauthorized` |
| Admin endpoint with `CUSTOMER` token | `403 Forbidden` |
| Admin endpoint with `ADMIN` or `STAFF` token | Successful response |
| Manipulated or malformed token | `401 Unauthorized` |
| Restart containers and log in again | Data remains available |

## Project status

The project is complete as a standalone backend portfolio application. It does not include a frontend and is not intended to be deployed as a production service without additional production-specific configuration.
