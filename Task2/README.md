# Personal Finance Tracker

## Introduction

The **Personal Finance Tracker** is a Java-based console application that helps users manage their finances effectively.
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
Navigate to project root folder and run command:
```bash
docker-compose up --build
```
Once docker is up and PostgreSQL database is ready run Gradle command:
```bash
./gradlew clean shadowJar 
```
Once the process is completed run Java command:
```bash
java -cp Task2/build/libs/* com.demo.finance.app.TaskTwoMain
```

---

## Configuration

The application uses **.env** file for database connection configuration. The credentials are:
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
Tests with coverage
```bash
./gradlew build jacocoTestReport 
```
**Known Issue:** First-time test failures <br>
- When running tests for the first time, all integration tests fail due to container initialization delay.<br> 
- When running tests again (2nd time, 3rd time, etc) - all test pass.

---

## Code Structure

The project follows Clean Architecture principles with key packages:
- `in.cli` – Handles user input (including controllers).
- `domain.model` – Contains entities.
- `domain.utils` – Utility classes for validation and date handling.
- `out.repository` – Manages database storage and retrieval.
- `out.service` – Implements core business logic and interacts with repositories.
- `app` - Has main class and application configs
- `resources.db.changelog` - Liquibase xml config files
---

## Technological Stack
- **Java Core**
- **Gradle**
- **PostgreSQL database**
- **Docker**
- **JUnit 5, Mockito, AssertJ, Testcontainers**

---