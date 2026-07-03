# CourseHelper

Students juggle courses, deadlines, and documents across a dozen tools. CourseHelper is a full-stack Java desktop application that centralizes it all, with an AI assistant that answers questions about their schedule and uploaded course materials.

---

## Screenshots

| Light Mode | Dark Mode |
|---|---|
| ![Login Light](assests/login-light.png) | ![Login Dark](assests/login-dark.png) |
| ![Home Light](assests/home-light.png) | ![Home Dark](assests/home-dark.png) |

**AI Chat**

![AI Chat](assests/chat-dark.png)

**Document Upload**

![Documents](assests/document-light.png)

**Settings**

![Settings](assests/settings-light.png)

---

## Features

**Secure login and registration**  
Accounts use JWT authentication — credentials are never stored client-side and sessions restore automatically on login.

**Course, assignment, and task management**  
Add courses for the semester, track assignments with due dates and completion status, and manage personal tasks alongside coursework — all in one place.

**Course calendar with color-coded events and recurring schedule support**  
Each course gets its own color across the day and week views. Recurring events repeat automatically so you only set them up once.

**PDF upload with semantic document search**  
Upload syllabi or notes and they're indexed for meaning, not just keywords. The AI can retrieve relevant sections when you ask questions about your materials.

**AI assistant with access to your schedule, assignments, and uploaded documents**  
Ask what's due this week, when your next class is, or what a document covers. The assistant queries your actual data before responding.

**Light / dark theme with live toggle**  
Switch themes from anywhere in the app. The change applies instantly across every page and open panel.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | JavaFX 17, CalendarFX, Maven |
| Backend | Spring Boot 4, Java 17, Maven |
| Database | PostgreSQL + pgvector |
| AI | OpenAI GPT-4o (chat + embeddings) |
| Auth | JWT (jjwt) |

---

## Architecture

### Backend — Feature-based Modularization

I went with a modular backend — each domain is self-contained to make a future microservices split straightforward.

```
backend/
└── src/main/java/com/coursehelper/backend/
    ├── auth/          # JWT filter, login, registration
    ├── user/          # User entity, profile, username/password change
    ├── course/        # Course CRUD
    ├── assignment/    # Assignment tracking
    ├── task/          # Task tracking
    ├── event/         # Calendar events
    ├── userSettings/  # Semester configuration
    ├── ai/            # Agent, RAG pipeline, tool definitions
    └── exceptions/    # Global exception handler
```

In each domain I enforced separation of concerns — controllers handle requests, services own the logic, and repositories talk to the database.

### Exception Handling

I used `@RestControllerAdvice` to centralize error handling — controllers just throw domain exceptions, and the global handler maps them to consistent HTTP responses.

### AI Agentic Loop

The AI assistant runs an agentic loop with GPT-4o — it keeps calling tools until it has enough context to answer, then responds. I defined five tools that give the model access to the user's data:

| Tool | Purpose |
|---|---|
| `search_resources` | Semantic search over uploaded documents |
| `get_schedule` | Retrieve course schedule for the semester |
| `get_assignments` | Fetch assignments filtered by status |
| `get_tasks` | Fetch tasks filtered by completion |
| `get_summary` | Combined overdue / due-today / upcoming summary |

All data fetching and classification happens server-side — the model only formats and presents the result.

### RAG Pipeline

1. User uploads a PDF
2. The backend extracts the text, splits it into overlapping chunks, embeds each chunk via OpenAI, and stores the vectors in PostgreSQL with pgvector
3. When the agent detects the user is asking about their documents, it runs a cosine similarity search and pulls the most relevant chunks into context

### Frontend — Theme System

The theme system uses CSS color tokens defined in a single file per theme. Toggling swaps that one file at the root and everything updates — pages, popups, and the access screen. Preference persists across sessions.

---

## Setup

### Prerequisites

- Java 17
- Maven
- PostgreSQL with the `pgvector` extension
- An OpenAI API key

### Database

```sql
CREATE DATABASE course_helper;
CREATE USER course_helper_user WITH PASSWORD 'your-password';
GRANT ALL PRIVILEGES ON DATABASE course_helper TO course_helper_user;
\c course_helper
CREATE EXTENSION IF NOT EXISTS vector;
```

Create the schema manually — JPA DDL is set to `none`.

### Environment Variables

The backend reads credentials from environment variables. Set the following before running:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/course_helper
export DB_USERNAME=course_helper_user
export DB_PASSWORD=your-db-password
export OPENAI_API_KEY=your-openai-api-key
export JWT_SECRET=your-jwt-secret-min-32-chars
```

### Run

```bash
# Terminal 1 — start the backend
cd backend && ./mvnw spring-boot:run

# Terminal 2 — start the frontend (requires backend running)
cd frontend && mvn javafx:run
```

---

## Project Structure

```
course-helper/
├── backend/    # Spring Boot REST API
├── frontend/   # JavaFX desktop client
└── assests/    # Screenshots
```

The frontend and backend are independent Maven projects — run both simultaneously, the frontend calls the backend at `http://localhost:8080/api`.

---

Developed by Maria Clement. Designed by Crystal Zelinske.
