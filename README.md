# Project Name: Bookstore Application

## Description
This project is a Bookstore application that allows users to browse books, add them to a cart, and place orders. It includes functionalities for user authentication, cart management, and order processing.

## Technologies Used
- Java
- Spring Boot
- Maven
- SQL

## Prerequisites
- Java 17 or higher
- Maven 3.6.0 or higher
- A SQL database (e.g., MySQL, PostgreSQL)

## Setup Instructions

### 1. Clone the Repository
```sh
git clone https://github.com/peterthomas-soprasteria/kata.git
cd kata
```

### 2. Configure the Database
Update the `application.properties` file located in `src/main/resources` with your database configuration:
```properties
spring.datasource.url=jdbc:db://localhost:3306/bookstore
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build the Project
Run the following command to build the project:
```sh
mvn clean install
```

### 4. Run the Application
Use the following command to run the application:
```sh
mvn spring-boot:run
```

### 5. Access the Application
Once the application is running, you can access it at:
```
http://localhost:8080
```

## Endpoints

### Authentication
- `POST /auth/login` - Login endpoint

### Books
- `GET /books` - Retrieve all books (authenticated)

### Cart
- `POST /cart/add` - Add item to cart (authenticated)
- `PUT /cart/update` - Update item in cart (authenticated)
- `DELETE /cart/remove` - Remove item from cart (authenticated)
- `GET /cart` - Get cart details (authenticated)
- `POST /cart/checkout` - Checkout cart (authenticated)

### Orders
- `GET /orders` - Retrieve user orders (authenticated)

## Testing
Run the following command to execute tests:
```sh
mvn test
```

## Notes
- This is for demonstration purposes only and should not be used in production.
- Ensure that the database is running and accessible.
- Review the security configurations in `SecurityConfig` to match your requirements.
