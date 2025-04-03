# Personal Finance Tracker

## Introduction

The **Personal Finance Tracker** is a Java-based web application that helps users manage their finances effectively.
It allows users to track transactions, set budgets, define financial goals, generate reports, and receive notifications.

This README provides installation and usage instructions for the application.

---

## Features

### User Management
- User registration and authentication.
- CRUD operations on user accounts.
- Admin functionality to manage users and view their transactions.

### Transactions
- CRUD operations on transactions.
- Filter transactions by date, category, or type (income/expense).

### Budgeting
- Set budgets for different expense categories.
- View budget.
- Receive notifications when spending exceeds budget limits.

### Financial Goals
- CRUD operations on goals.
- Track progress towards goals.

### Reporting
- Generate full financial reports.
- Generate reports for specific date ranges.
- Analyze expenses by category.

### Notifications
- Receive budget limit notifications.
- Receive goal progress notifications.

---

## Installation & Running

### Prerequisites
- **Java 17+** installed.
- **Gradle** for dependency and build management.
- **Docker** to run PostgreSQL database.

### Downloading the Application
Clone or download the application via link:
```
https://github.com/Hint1k/homework
```

### Building and Running the Application
Navigate to project root folder and run Gradle command:
```bash
./gradlew clean installDist
```
Then run Docker command:
```bash
docker-compose up --build
```

---

## Configuration

The application uses **.env** and **application.yml** files for credentials and configuration. The credentials are:
```
Application admin's email = admin@demo.com
Application admin's password = 123
Database username = user
Database password = 123
Database name = financedb
Database schema = finance
Table names: users, transactions, goals, reports, budgets
```

---

## Running Tests

The application includes unit tests with **JUnit 5, Mockito, AssertJ** and integration test with testcontainers. 

To run tests using Gradle:
```bash
./gradlew test
```
To run tests with coverage using Jacoco 
```bash
./gradlew jacocoTestReport  
```

---

## Code Structure

The project follows Clean Architecture principles with key packages:
- `in.controllers` – Rest Controllers.
- `domain.model` – Contains entities.
- `domain.utils` – Utility classes for validation and date handling.
- `out.repository` – Manages database storage and retrieval.
- `out.service` – Implements core business logic and interacts with repositories.
- `app` - Has main class and application config classes
- `resources.db.changelog` - Liquibase xml config files
---

## Technological Stack
- **Java**
- **Spring (Boot, Web, AOP)**
- **Gradle**
- **PostgreSQL**
- **Swagger**
- **Docker**
- **JUnit 5, Mockito, AssertJ, Testcontainers**

---

## Application endpoints

### Swagger:
Swagger UI and API Docs are accessible via links in a browser:
``` 
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

### User account actions:
POST http://localhost:8080/api/users/registration 
```json 
{
"name": "jay",
"email": "jay@demo.com",
"password": "123"
}
```
POST http://localhost:8080/api/users/authenticate
```json 
{
"email": "jay@demo.com",
"password": "123"
}
```
POST http://localhost:8080/api/users/logout
```json 
{ }
```
PUT http://localhost:8080/api/users
```json 
{
  "name": "jay2",
  "email": "jay2@demo.com",
  "password": "123"
}
```
GET http://localhost:8080/api/users/me
```json 
{ }
```
DELETE http://localhost:8080/api/users
```json 
{ }
```

### Admin actions:
GET http://localhost:8080/api/admin/users?page=1&size=10
```json 
{ }
```
GET http://localhost:8080/api/admin/users/2
```json 
{ }
```
GET http://localhost:8080/api/admin/users/transactions/2?page=1&size=10
```json 
{ }
```
PATCH http://localhost:8080/api/admin/users/role/2
```json 
{
"role": "user"
}
```
PATCH http://localhost:8080/api/admin/users/block/2
```json 
{
  "blocked": true
}
```
DELETE http://localhost:8080/api/admin/users/2
```json 
{ }
```

### Transaction actions:
GET http://localhost:8080/api/transactions?page=1&size=10
```json 
{ }
```
GET http://localhost:8080/api/transactions/1
```json 
{ }
```
POST http://localhost:8080/api/transactions
```json 
{
  "amount": "500",
  "category": "1",
  "date": "2025-03-23",
  "description": "1",
  "type": "EXPENSE"
}
```
PUT http://localhost:8080/api/transactions/1
```json 
{
  "amount": "750",
  "category": "2",
  "description": "2"
}
```
DELETE http://localhost:8080/api/transactions/1
```json 
{ }
```

### Goal actions: 
GET http://localhost:8080/api/goals?page=1&size=10
```json 
{ }
```
GET http://localhost:8080/api/goals/1
```json 
{ }
```
POST http://localhost:8080/api/goals
```json 
{
  "goalName": "1",
  "targetAmount": "1500",
  "duration": "3",
  "startTime": "2025-01-01"
}
```
PUT http://localhost:8080/api/goals/1
```json 
{
  "goalName": "2",
  "targetAmount": "1500",
  "duration": "3"
}
```
DELETE http://localhost:8080/api/goals/1
```json 
{ }
```

### Budget actions:
GET http://localhost:8080/api/budgets/budget
```json 
{ }
```
POST http://localhost:8080/api/budgets
```json 
{
  "monthlyLimit": "1500"
}
```

### Report actions 
GET http://localhost:8080/api/reports/report
```json 
{ }
```
GET http://localhost:8080/api/reports/expenses-by-category
```json 
{
  "fromDate": "2025-01-01",
  "toDate": "2025-06-30"
}
```
POST http://localhost:8080/api/reports/by-date
```json 
{
  "fromDate": "2025-01-01",
  "toDate": "2025-06-30"
}
```

### Notifications actions
GET http://localhost:8080/api/notifications/budget
```json 
{ }
```
GET http://localhost:8080/api/notifications/goal
```json 
{ }
```