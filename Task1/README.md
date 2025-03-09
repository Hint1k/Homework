# Personal Finance Tracker

## Introduction

The **Personal Finance Tracker** is a Java-based console application that helps users manage their finances effectively. 
It allows users to track transactions, set budgets, define financial goals, generate reports, and receive notifications—all. 
The system follows **Clean Architecture** and **SOLID principles**, ensuring a well-structured and maintainable codebase.

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

### Downloading the Application
Clone the repository:
```bash
git clone https://github.com/your-repo/personal-finance-tracker.git
cd personal-finance-tracker
```

### Building the Application
Using Gradle:
```bash
gradle build
```

### Running the Application
```bash
java -jar target/personal-finance-tracker.jar
```

---

## Configuration

The application uses a simple **application.properties** file for configuration. The default admin credentials are:
```
admin.email=admin@demo.com
admin.password=123
```

---

## Running Tests

The application includes unit tests with **JUnit 5, Mockito, and AssertJ**. To run tests:

Using Gradle:
```bash
gradle test
```

---

## Code Structure

The project follows Clean Architecture principles with key packages:
- `in.cli` – Handles user input (including controllers).
- `domain.model` – Contains entities.
- `domain.utils` – Utility classes for validation and date handling.
- `out.repository` – Manages in-memory data storage and retrieval.
- `out.service` – Implements core business logic and interacts with repositories.
- `app` - Has main class and application config

---

## Technological Stack
- **Java Core (No external frameworks)**
- **JUnit 5, Mockito, AssertJ** (for unit testing)
- **Gradle** (for build management)

---