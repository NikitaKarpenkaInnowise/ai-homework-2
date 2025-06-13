# JSONPlaceholder Clone API

This is a Spring Boot implementation of the JSONPlaceholder API with added JWT authentication and PostgreSQL database support.

## Features

- Full REST API implementation matching JSONPlaceholder endpoints
- JWT-based authentication
- PostgreSQL database with proper schema
- Docker and Docker Compose support
- Secure password hashing
- Input validation

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose
- PostgreSQL (if running locally)

## Running with Docker

1. Build and start the containers:
```bash
docker-compose up --build
```

2. The API will be available at `http://localhost:8080`

## Running Locally

1. Start PostgreSQL database
2. Update `application.properties` with your database credentials
3. Build the project:
```bash
mvn clean package
```
4. Run the application:
```bash
java -jar target/jsonplaceholder-clone-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Authentication

- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login and get JWT token

### Users

- GET `/api/users` - Get all users
- GET `/api/users/{id}` - Get user by ID
- POST `/api/users` - Create new user
- PUT `/api/users/{id}` - Update user
- DELETE `/api/users/{id}` - Delete user

## Authentication

All endpoints except `/api/auth/**` require JWT authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Database Schema

The application uses the following schema:

- Users table with embedded Address and Company information
- Secure password storage with bcrypt hashing
- Proper indexing on email and username fields

## Development

To run tests:
```bash
mvn test
```

## Security

- Passwords are hashed using bcrypt
- JWT tokens are signed with a secure key
- Input validation on all endpoints
- CORS configuration
- SQL injection prevention through JPA 