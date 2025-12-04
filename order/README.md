# Order Service

This is a Spring Boot microservice for managing orders. It communicates with an Inventory Service to check for product availability and update stock.

## Project Setup

To run this service, you will need:
- Java 17
- Maven

### Running the Application

1.  Clone the repository:
    ```bash
    git clone https://github.com/ManishKumar1399/order-inventory-microservices-assignment.git
    cd order-inventory-microservices-assignment/order
    ```

2.  Build the project using Maven:
    ```bash
    ./mvnw clean install
    ```

3.  Run the application:
    ```bash
    java -jar target/order-0.0.1-SNAPSHOT.jar
    ```

The service will start on port `8081`.

### Configuration

The following properties can be configured in `src/main/resources/application.properties`:

- `server.port`: The port on which the application will run (default: `8081`).
- `spring.h2.console.enabled`: Enables or disables the H2 database console (default: `true`).
- `spring.datasource.url`: The URL for the H2 database (default: `jdbc:h2:mem:order-db`).
- `inventory.service.url`: The base URL of the Inventory Service (default: `http://localhost:8081/api/inventory`).

## API Documentation

The API documentation is generated using Springdoc OpenAPI and is available at:

- [Swagger UI](http://localhost:8081/swagger-ui.html)
- [OpenAPI Spec](http://localhost:8081/v3/api-docs)

### Endpoints

#### Place an Order

- **URL:** `/api/order`
- **Method:** `POST`
- **Request Body:**

  ```json
  {
    "productId": 1,
    "quantity": 10
  }
  ```

- **Success Response (201 CREATED):**

  ```json
  {
    "orderId": 1,
    "productId": 1,
    "quantity": 10
  }
  ```

- **Error Response (400 BAD REQUEST):**

  If the product is out of stock:
  ```json
  {
    "timestamp": "2025-11-27T10:00:00.000000",
    "message": "Product out of stock"
  }
  ```

## Testing

To run the tests, execute the following Maven command:

```bash
./mvnw test
```

This will run both unit and integration tests.

### Unit Tests

Unit tests for the service layer are located in `src/test/java/com/service/order/OrderServiceTest.java`. These tests use JUnit 5 and Mockito to test the business logic in isolation.

### Integration Tests

Integration tests for the controller layer are located in `src/test/java/com/service/order/OrderControllerTest.java`. These tests use `@SpringBootTest` and `MockMvc` to test the full request-response cycle of the API endpoints.
