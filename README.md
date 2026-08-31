# AI-Powered Student Result Analyzer

A full-stack academic performance platform built with Spring Boot and Google Gemini AI. Teachers can enter student marks, and students receive personalized, AI-generated improvement plans for each subject.

---

## Features

- **Role-based authentication** — Separate flows for Teacher and Student roles with BCrypt password hashing and session-based access control
- **Teacher dashboard** — Bulk marks entry with subject-wise remarks for multiple students
- **Student dashboard** — Subject-wise performance visualization with AI-generated insights
- **AI improvement plans** — Gemini 2.5 Flash generates a personalized 4-week study plan, concept gap analysis, and exam strategy per subject
- **Prompt engineering** — Structured prompt builder that sends student context (marks, remarks, course, syllabus) to Gemini for accurate and relevant output

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java, Spring Boot |
| Frontend | Thymeleaf, HTML5, CSS3 |
| Database | H2 (in-memory) |
| AI Integration | Google Gemini API (gemini-2.5-flash) |
| ORM | Spring Data JPA |
| Security | BCrypt password hashing, HTTP session |

---

## Project Structure

```
src/main/java/com/example/
├── controller/
│   ├── AuthController.java       # Login, signup routing
│   ├── TeacherController.java    # Marks entry, student management
│   └── StudentController.java    # Dashboard, AI suggestion fetch
├── service/
│   ├── AuthService.java          # User registration and login logic
│   ├── GeminiService.java        # Gemini API HTTP client
│   ├── PromptBuilder.java        # Structured prompt construction
│   ├── SuggestionService.java    # Orchestrates AI suggestion flow
│   └── StudentService.java       # Student data operations
├── model/
│   ├── UserAccount.java          # User entity with role
│   ├── Student.java              # Student profile entity
│   └── StudentResult.java        # Subject marks entity
└── repository/                   # Spring Data JPA repositories
```

---

## Getting Started

### Prerequisites
- Java 17+
- Maven
- Google Gemini API key (free at [aistudio.google.com](https://aistudio.google.com))

### Run Locally

```bash
git clone https://github.com/barathmathan19/AIPoweredStudentResultAnalyzer.git
cd AIPoweredStudentResultAnalyzer

# Recommended: create a .env file from the example and export it before running
# cp .env.example .env
# source .env   # (Linux / macOS)

# If using Docker for Postgres + Redis, start them first (see Docker commands below)

mvn spring-boot:run
```

Visit `http://localhost:8080`

Health check: GET /health returns a simple JSON payload {"status":"ok","timestamp":"..."}.

---

### Docker (Postgres + Redis) - quick start

Run Postgres and Redis locally using Docker (example):

```bash
# Start Postgres
docker run -d --name aipg_postgres \
  -e POSTGRES_USER=appuser \
  -e POSTGRES_PASSWORD=pass \
  -e POSTGRES_DB=studentdb \
  -p 5432:5432 postgres:15

# Start Redis
docker run -d --name aipg_redis -p 6379:6379 redis:7
```

Wait a few seconds for both containers to be healthy.

### Example .env file

Create a file named `.env` at the project root (do NOT commit your real .env to Git). Use `.env.example` as a template. Example values:

```env
# Postgres
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/studentdb
JDBC_DATABASE_USERNAME=appuser
JDBC_DATABASE_PASSWORD=pass
JDBC_DRIVER_CLASS=org.postgresql.Driver

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
# REDIS_PASSWORD=

# Gemini API Key (optional)
GEMINI_API_KEY=
```

Load the env (Linux/macOS): `source .env` then run `mvn spring-boot:run`.

On Windows PowerShell:

```powershell
Get-Content .env | ForEach-Object { $p = ($_ -split '=',2); if ($p.Length -eq 2) { Set-Item -Path Env:$($p[0]) -Value $p[1] } }
mvn spring-boot:run
```

---

If you'd like, I can add a small bash script to bring up Docker and start the app using the .env file.

---

## How It Works

1. Teacher signs up and logs in
2. Teacher enters marks and remarks for each student per subject
3. Student logs in and views their subject-wise results
4. Student clicks **"Get AI Suggestion"** on any subject
5. The app sends structured context (marks, remarks, course, syllabus) to Gemini API
6. Gemini returns a personalized plan — concept gaps, 4-week schedule, exam strategy
7. Student sees the formatted improvement plan on their dashboard

---

## API / Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET/POST | `/login` | Login page |
| GET/POST | `/signup` | Signup page |
| GET | `/teacher/dashboard` | Teacher marks entry dashboard |
| POST | `/teacher/marks` | Submit student marks |
| GET | `/student/dashboard` | Student results dashboard |
| GET | `/student/suggestion/{subjectId}` | Fetch AI suggestion for a subject |

---
