# OnlineCourseManagement

A Spring Boot backend application for managing online courses with authentication, payments, and quizzes.
This project provides role-based access for students and instructors, enabling a complete e-learning flow:

Instructors create courses and upload video lectures.

Students enroll in courses via Razorpay payment integration.

Students can watch lectures, attempt quizzes, and track progress.

Notifications are handled asynchronously via RabbitMQ.

🚀 Features
👨‍🏫 Instructor

Create and manage courses.

Upload video lectures.

Create quizzes for each course.

👨‍🎓 Student

Register and login (email as primary key).

Browse and search courses.

Add courses to cart and checkout.

Pay securely using Razorpay.

Access only enrolled courses.

Watch video lectures (progress tracking).

Attempt quizzes (only after completing all lectures).

⚙️ System Features

Authentication & Role-based Authorization (Spring Security + JWT).

DTO-based clean API responses.

Exception handling with global error handler.

Event-driven architecture using RabbitMQ for notifications.

Logging with daily rolling logs.

Payment verification & enrollment transaction support.

🛠️ Tech Stack

Backend: Spring Boot 3, Spring Security, Spring Data JPA, Hibernate

Database: MySQL / PostgreSQL

Payment: Razorpay Integration

Messaging: RabbitMQ

Build Tool: Maven

Language: Java 17

📂 Project Structure
OnlineCourseManagement/
├── src/main/java/com/project/onlinecoursemanagement/
│   ├── controller/        # REST controllers
│   ├── dto/               # Data Transfer Objects
│   ├── model/             # Entities
│   ├── repository/        # JPA repositories
│   ├── service/           # Service interfaces
│   ├── service/impl/      # Service implementations
│   ├── mapper/            # Entity-DTO mappers
│   ├── exception/         # Custom exceptions & handlers
│   └── config/            # Security, JWT, RabbitMQ, etc.
└── src/main/resources/
├── application.properties
└── static / templates (if any)

⚡ Getting Started
Prerequisites

Java 17+

Maven 3+

MySQL/PostgreSQL

RabbitMQ installed & running

Razorpay account for API keys

Setup

Clone the repo

git clone https://github.com/aryan130903/OnlineCourseManagement.git
cd OnlineCourseManagement


Configure Database & Keys in application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/online_course
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

# JWT Secret
jwt.secret=your_jwt_secret_key

# Razorpay
razorpay.key=your_razorpay_key
razorpay.secret=your_razorpay_secret


Run the application

mvn spring-boot:run

🔗 API Endpoints (Sample)
Auth

POST /auth/signup – Register user

POST /auth/signin – Login and get JWT

Course

POST /courses – Create course (Instructor only)

GET /courses – List all courses

GET /courses/{id} – Course details

Cart & Enrollment

POST /cart/add – Add course to cart

POST /checkout – Generate Razorpay link

POST /payment/webhook – Verify payment & enroll

Video Lectures

POST /courses/{id}/lectures – Upload lecture

GET /courses/{id}/lectures – View lectures

Quizzes

POST /courses/{id}/quiz – Create quiz

GET /courses/{id}/quiz – Attempt quiz (only if lectures completed)

📩 Notifications

All user notifications (enrollment confirmation, payment success, etc.) are handled via a separate Notification Service connected with RabbitMQ.