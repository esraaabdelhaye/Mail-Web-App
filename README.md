# 📧 Mail Web Application

A full-stack email management system built with **Angular** and **Spring Boot**, featuring advanced email organization, intelligent search capabilities, and AI-enhanced features.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)

---
## 📋 Table of Contents

- [Features](#-features)
- [Design Patterns](#-design-patterns)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [API Documentation](#-api-documentation)
- [Screenshots](#-screenshots)
- [Team](#-team)

---

## ✨ Features

### Core Email Functionality
- **Email Management**: Compose, send, receive, and organize emails with attachment support
- **Smart Folders**: System folders (Inbox, Sent, Trash, Drafts) + custom user-created folders
- **Priority Inbox**: Intelligent email prioritization with 4 importance levels (LOW, NORMAL, HIGH, CRITICAL)
- **Draft Auto-save**: Automatic draft saving as you compose messages
- **Pagination**: Efficient email loading with configurable page sizes

### Advanced Search & Filtering
- **Quick Search**: Fast search across sender, subject, body, and date fields
- **Advanced Search**: Multi-criteria filtering (sender, subject, priority, attachments, date range, folder)
- **Auto-Routing Filters**: Create custom rules to automatically organize incoming emails into specific folders

### Contact Management
- Store and manage contacts with multiple email addresses
- Quick access to frequently used contacts during email composition

### Automation & Intelligence
- **Automated Trash Cleanup**: Emails in trash are permanently deleted after 30 days
- **AI Email Summarization**: Automatically generates concise summaries of long emails
- **Voice-Based Search**: Search emails using spoken commands for hands-free operation

### Sorting & Organization
- Sort by: Date, Subject, Sender, Priority, Read/Unread status
- Folder-specific sorting strategies (e.g., Drafts sorted by last updated date)
- Mark emails as read/unread, archive, or move between folders

---

## 🎨 Design Patterns

This application showcases industry-standard design patterns for maintainable, scalable code:

### Chain of Responsibility
**Used in**: Authentication & Email Search
- **Authentication**: Validates user credentials through a chain of validators (email uniqueness, password length, email existence, password correctness)
- **Email Search**: Processes search criteria through specialized handlers (subject, sender, date range, attachments, priority)
- **Benefits**: Single responsibility, easy extensibility, early termination on validation failure

### Strategy Pattern
**Used in**: Email Sorting & Sending
- **Sorting**: Dynamic selection of sorting algorithms based on field (date, priority, sender, subject, unread status)
- **Email Sending**: Different strategies for single vs. multiple recipients
- **Benefits**: Open/closed principle, eliminates conditional complexity, easy to add new strategies

### Factory Pattern
**Used in**: Chain Creation
- Centralizes the creation of validation chains for different authentication flows (Login vs. Register)
- **Benefits**: Encapsulates chain construction logic, easy to add new authentication flows

### Filter Pattern
**Used in**: Email Search
- Builds dynamic database queries using predicates
- Combines with Chain of Responsibility for flexible search criteria
- **Benefits**: Clean separation of filtering logic, prevents SQL injection

### Builder Pattern
**Used in**: Entity Construction
- Simplifies creation of complex domain objects (Mail, UserMail, MailReceiver)
- Implemented using Lombok's `@Builder` annotation
- **Benefits**: Readable code, prevents construction errors, handles optional fields elegantly

---

## 🏗️ Architecture

### Full-Stack Separation
```
┌─────────────────┐         REST API        ┌─────────────────┐
│  Angular        │ ◄─────────────────────► │  Spring Boot    │
│  Frontend       │    HTTP Requests        │  Backend        │
│  (Port 4200)    │                         │  (Port 8080)    │
└─────────────────┘                         └─────────────────┘
                                                     │
                                                     ▼
                                            ┌─────────────────┐
                                            │  MySQL Database │
                                            └─────────────────┘
```

### Backend Layered Architecture
```
Controller Layer  → Handles HTTP requests/responses
      ↓
Service Layer     → Contains business logic
      ↓
Repository Layer  → Manages data persistence
      ↓
Entity Layer      → Represents database structure
```

### Key Design Decisions
- **Independent Scaling**: Frontend and backend can be deployed and scaled separately
- **Type Safety**: Extensive use of Java enums (Priority, ReceiverType, ChainType)
- **Lazy Loading**: JPA entities use `FetchType.LAZY` to optimize performance
- **Normalized Database**: Proper schema design with junction tables for many-to-many relationships
- **CORS Configuration**: Secure cross-origin resource sharing between frontend and backend

---

## 🔧 Prerequisites

| Component | Technology | Version |
|-----------|-----------|---------|
| **Backend** | Java Development Kit (JDK) | 11+ |
| | Maven | 3.6+ |
| **Frontend** | Node.js | 14+ |
| | npm | 6+ |
| | Angular CLI | 12+ |
| **Database** | MySQL Server | 8.0+ |
| | MySQL Workbench | (Optional) |

---

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/mail-web-app.git
cd mail-web-app
```

### 2. Database Setup
```sql
-- Create database
CREATE DATABASE mail_db;

-- Update application.properties with your MySQL credentials
spring.datasource.url=jdbc:mysql://localhost:3306/mail_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Start the Backend (Spring Boot)
```bash
cd MailBackend
mvn clean install
mvn spring-boot:run
```
✅ Backend will run on `http://localhost:8080`

### 4. Start the Frontend (Angular)
Open a new terminal window:
```bash
cd MailFrontend
npm install
ng serve --open
```
✅ Frontend will open automatically at `http://localhost:4200`

---

## 📸 Screenshots

### Inbox View
Default view with pagination and sorting options
<img width="3820" height="1890" alt="image" src="https://github.com/user-attachments/assets/d4755133-e460-49fd-9ee5-62d3f7fb6ae2" />

### Advanced Search
Multi-criteria search interface
<img width="3818" height="1887" alt="image" src="https://github.com/user-attachments/assets/12f46366-87ff-4880-948d-4e92594e525e" />

### Email Composition
<img width="3818" height="1882" alt="image" src="https://github.com/user-attachments/assets/86fb95c1-898b-4ebd-bc8a-dcbf03456201" />

### Contacts
<img width="3813" height="1887" alt="image" src="https://github.com/user-attachments/assets/84503e87-6498-4edb-9c59-e319c5ed4315" />

---

## 👥 Team

**Faculty of Engineering, Alexandria University - 2025**

| Name | ID |
|------|-----|
| Esraa Abdelhay Mohamed | 23010276 |
| Nour Ahmed Mahmoud | 23010913 |
| Karim Mohamed Basim | 23010673 |
| Ahmed Hassan Etman | 23010235 |

**Course**: Programming 2

---

## 📄 License

This project is an academic assignment for Alexandria University.

---

