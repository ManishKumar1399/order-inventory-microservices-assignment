# Inventory Service

The Inventory Service is a Spring Boot microservice responsible for maintaining the inventory of materials/products. It supports managing product batches with different expiry dates and provides functionalities to retrieve inventory information and update stock levels.

## Features

*   Maintain inventory of materials/products with batch and expiry date details.
*   Provide inventory batches sorted by expiry date for a given product.
*   Update inventory levels, supporting both addition and reduction of stock.
*   Utilizes a Factory Pattern for extensible inventory handling logic.
*   Uses Spring Data JPA with an H2 in-memory database for persistence.

## How to Build and Run

To build and run the Inventory Service locally:

1.  **Navigate to the `inventory` directory:**
    ```bash
    cd order-inventory-microservices-assignment/inventory
    ```

2.  **Build the project:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The service will start on port `8080` (as configured in `application.properties`).

## API Endpoints

The Inventory Service exposes the following REST endpoints:

### 1. Get Inventory Batches by Product ID

*   **URL:** `/inventory/{productId}`
*   **Method:** `GET`
*   **Description:** Returns a list of inventory batches for a given product, sorted by expiry date in ascending order.
*   **Path Parameters:**
    *   `productId` (string, required): The ID of the product.
*   **Response (200 OK):** A JSON array of `InventoryResponse` objects.
    ```json
    [
        {
            "productId": "string",
            "batchId": "string",
            "quantity": 0,
            "expiryDate": "YYYY-MM-DD"
        }
    ]
    ```
*   **Response (404 Not Found):** If the product is not found.

### 2. Update Inventory

*   **URL:** `/inventory/update`
*   **Method:** `POST`
*   **Description:** Updates the quantity of a specific inventory batch. This can be used to add stock (positive quantity) or reduce stock (negative quantity). If a batch does not exist, a new inventory record will be created.
*   **Request Body (`UpdateRequest`):** A JSON object containing details of the inventory update.
    ```json
    {
        "productId": "string",
        "batchId": "string",
        "quantity": 0,             // Use negative for reduction, positive for addition
        "expiryDate": "YYYY-MM-DD" // Required for new batches or if existing batch's expiry needs update
    }
    ```
*   **Response (200 OK):** Inventory updated successfully.
*   **Response (400 Bad Request):** If the request body is invalid.

## Initial Data

The service is pre-configured with `schema.sql` and `data.sql` to initialize the database with some sample inventory records upon startup.
