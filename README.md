# Dog Adoption Microservices Platform

A microservices-based dog adoption platform built with Spring Boot, featuring four decoupled services communicating via REST APIs through an API Gateway.

## Features

- **Adoption Service**: Manages adoption applications and documentation
- **Adopter Service**: Handles adopter profiles and information
- **Dog & Location Service**: Manages dog profiles and shelter locations
- **Volunteer Service**: Handles volunteer management and scheduling
- **API Gateway**: Single entry point for all client requests

## Technologies

- **Backend**: Java 17, Spring Boot 3.x
- **API**: RESTful services with HATEOAS
- **Databases**:
  - MySQL (Adopter & Dog Services)
  - PostgreSQL (Volunteer Service)
  - MongoDB (Adoption Paper Service)
- **ORM**: JPA/Hibernate for SQL databases, Spring Data MongoDB
- **Containerization**: Docker with Docker Compose
- **Testing**: JUnit 5, MockMVC, 90%+ test coverage with JaCoCo
- **API Documentation**: SpringDoc OpenAPI (Swagger)

## Project Structure

```
dogadoptionmicroservice/
├── adopter-service/         # Manages adopter information
├── adoptionpaper-service/   # Handles adoption applications
├── api-gateway/             # API Gateway service
├── dogandlocation-service/  # Manages dogs and their locations
└── volunteer-service/       # Handles volunteer management
```

## Getting Started

### Prerequisites

- Java 17+
- Docker (includes Docker Compose)
- Gradle 8.13 (included via Gradle Wrapper)


### Database Management

#### MySQL Databases
- **Adopter Service DB**: MySQL (port 3308)
  - **phpMyAdmin**: http://localhost:5014
  - Username: `user`
  - Password: `pwd`

- **Dog & Location Service DB**: MySQL (port 3307)
  - **phpMyAdmin**: http://localhost:5013
  - Username: `user`
  - Password: `pwd`

#### PostgreSQL Database
- **Volunteer Service DB**: PostgreSQL (port 5432)
  - **pgAdmin**: http://localhost:9000
  - Email: `admin@jeremy.com`
  - Password: `admin`

#### MongoDB
- **Adoption Paper Service DB**: MongoDB (port 27017)
  - **Mongo Express**: http://localhost:8086
  - Username: `user`
  - Password: `pwd`

### Running Locally

1. Start all services and databases:
   ```bash
   docker-compose up -d
   ```

2. Build and run individual services:
   ```bash
   ./gradlew :<service-name>:bootRun
   ```
   Example: `./gradlew :adoptionpaper-service:bootRun`

## Documentation

- **API Documentation**: Available via Swagger UI at `http://localhost:8080/swagger-ui.html` when services are running
- **OpenAPI Specification**: Available at `http://localhost:8080/v3/api-docs`
- **Architecture**: See `Diagrams/` for C4 model and DDD diagrams showing the system context and container architecture

## Testing

Run tests for all services:
```bash
./gradlew clean build test
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
