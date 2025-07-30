# Event Booking System

A comprehensive web-based platform for browsing, searching, and booking tickets for upcoming events.

[![GitHub](https://img.shields.io/badge/GitHub-Repository-blue?style=flat-square)](https://github.com/Elen17/event-booking-system)
[![Project Board](https://img.shields.io/badge/Project-Board-blueviolet?style=flat-square)](https://github.com/users/Elen17/projects/2)

## 🎯 Features

### User Authentication

- Secure login and registration
- Error handling for invalid credentials

### Event Management

- Browse top 10 upcoming events sorted by ratings
- Detailed event information pages
- Pagination for event listings

### Search Functionality

- Smart search with minimum 3 characters
- Advanced filters
- Dedicated search results page

### Booking System

- Interactive seat selection
- Real-time seat availability
- Dummy payment integration

## 🛠️ Tech Stack

### Backend

- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Spring JDBC

### Frontend

- Thymeleaf
- HTML5
- CSS3
- JavaScript
- Bootstrap 5

### Database

- PostgreSQL

### Build & Version Control

- Maven
- Git/GitHub

## 🚀 Getting Started

### Prerequisites

- Java 17
- Maven 3.6.3+
- PostgreSQL 13+
- Git

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Elen17/event-booking-system.git
   cd event-booking-system
   ```

2. **Database Setup**:
    - Create a new PostgreSQL database
    - Update `application.properties` with your database credentials

3. **Build and Run**:

   You can use the provided `start_postgres.sh` script under bin/ folder
   to start a local PostgreSQL instance and run the application. 
   Update the db properties in start_postgres.sh
   ```bash
   ./bin/start_postgres.sh
   ```

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   The application will be available at: http://localhost:8080

## 📂 Project Structure

```
event-booking-system/
├── src/main/java/com/event/booking/
│   ├── config/       # Spring configurations
│   ├── controller/   # MVC controllers
│   ├── model/        # Entity classes
│   ├── repository/   # Data access layer
│   ├── service/      # Business logic
│   └── security/     # Security configurations
├── src/main/resources/
│   ├── static/       # CSS, JS, images
│   ├── templates/    # Thymeleaf templates
│   └── application.properties
└── pom.xml
```

## ✉️ Contact

Elen Khachatryan - [GitHub Profile](https://github.com/Elen17)

Project Link: [https://github.com/Elen17/event-booking-system](https://github.com/Elen17/event-booking-system)
