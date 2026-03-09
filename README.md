# 📚 BookShelf API

BookShelf is a backend system for managing books and borrowing operations.  
It provides authentication, role-based access control, book management, borrowing workflows, and user profile management.

---

## 🚀 Features

### Public Book Listing
- Browse books (latest first)
- Search by title or author
- Pagination support
- Book detail: cover image, description, author, created date
- Only authenticated users can borrow books

### Authentication
- User registration (email or phone)
- Login (email/phone + password)
- Email/phone verification
- Password reset functionality
- JWT-based authentication

### Book Management (ADMIN)
- Create, update, delete books
- View all books
- Regular users can only view and borrow books

### Profile Management
- View and update profile
- Change email (with verification)
- Upload profile photo
- Update password

### Borrowing System
- Borrow and return books
- Track borrow and return dates
- View personal borrowing history (USER)
- Admins can view all borrowing history

### Administration
- View all users
- Block/unblock users
- Delete users
- Manage roles and permissions

### Search & Filtering
- Search by title or author
- Filter by author, category, publication year
- Supports pagination

### API Documentation
- Interactive Swagger UI
- Test endpoints without frontend
- Clear request/response documentation

### Validation & Logging
- Request validation (required fields, description length)
- Clear error messages with HTTP status codes
- Logs for important actions (book updates, user management)

---

## 🛠 Tech Stack
- Java, Spring Boot
- Spring Security (JWT)
- PostgreSQL + Hibernate + JPA
- Maven
- Flyway (DB migrations)
- Email/SMS integration
- Swagger / OpenAPI
- i18n (internationalization)

---

## 📦 API Modules

**Auth** – Registration, login, password reset  
**Attach** – Upload/open images  
**Book** – CRUD, list, search, filtering  
**Borrowing** – Borrow/return books, history  
**Profile** – User details, update, block/unblock (admin)

---

## ▶️ Running Locally

1. Clone the repository
2. Configure PostgreSQL database
3. Update `application.properties`
4. Run with Maven:

```bash
./mvnw spring-boot:run
