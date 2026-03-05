# AI Powered Student Result Analyzer

A Spring Boot web application for colleges to manage student performance and generate AI-driven improvement plans.

The system supports:
- Role-based signup/login (`TEACHER` and `STUDENT`)
- Teacher-only result entry
- Course + syllabus aware subject selection (dropdown-based)
- Student dashboard with marks, teacher remarks, and detailed AI suggestions
- SQL persistence using H2 file database

## Tech Stack
- Java 17
- Spring Boot 3.3.5
- Spring Web + Thymeleaf
- Spring Data JPA
- H2 Database (file mode)
- BCrypt password hashing (`spring-security-crypto`)
- OkHttp + Gson for Gemini API calls

## Features

### Authentication
- Separate signup and login
- Accounts stored in `users` table
- Passwords hashed using BCrypt
- Session-based access control

### Teacher Workflow
- Login as teacher
- Select a registered student from dropdown
- Select course/department
- Select syllabus mapped to selected course
- Select subjects from a predefined course-specific subject list (no manual typing)
- Add marks and remarks for each subject

### Student Workflow
- Login as student
- View assigned course and syllabus
- View subject-wise marks and teacher remarks
- View detailed AI-generated improvement plan for each subject

### AI Suggestions
- Detailed prompts include:
  - student name
  - course/department
  - syllabus
  - subject
  - marks
  - teacher remarks
- If Gemini API key is missing or call fails, a detailed fallback plan is generated.

## Current Subject Catalog
The teacher UI currently supports:
- BE Electronics and Instrumentation Engineering
- BE Electrical and Electronics Engineering
- BE Computer Science and Engineering

You can extend subject/syllabus mappings in:
- `src/main/java/com/example/service/SubjectCatalogService.java`

## Project Structure
```text
src/main/java/com/example
  controller/
    AuthController.java
    TeacherController.java
    StudentController.java
  model/
    UserAccount.java
    UserRole.java
    Student.java
    StudentResult.java
  repository/
    UserAccountRepository.java
    StudentRepository.java
  service/
    AuthService.java
    StudentService.java
    SubjectCatalogService.java
    SuggestionService.java
    GeminiService.java
    PromptBuilder.java
src/main/resources
  templates/
    login.html
    signup.html
    teacher-dashboard.html
    student-dashboard.html
  application.properties
```

## Database
Configured in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./data/studentdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Persistent DB file is created at:
- `data/studentdb.mv.db`

## Gemini API Setup (Optional but Recommended)
Set your API key in:

```properties
gemini.api.key=YOUR_GEMINI_API_KEY
```

Without this key, fallback suggestions are shown.

## Run Locally

### Prerequisites
- Java 17+
- Maven 3.9+

### Commands
```bash
mvn clean spring-boot:run
```

App URL:
- `http://localhost:8080`

H2 Console:
- `http://localhost:8080/h2-console`

Use JDBC URL:
- `jdbc:h2:file:./data/studentdb`

## Usage Flow
1. Open `/signup` and create teacher and student accounts.
2. Login as teacher.
3. Select a registered student and publish marks/remarks.
4. Login as student.
5. View detailed AI improvement plan in dashboard.

## Troubleshooting

### Whitelabel 500 / old schema conflicts
If you upgraded from an older project version and get DB schema errors:
1. Stop the app.
2. Delete `data/studentdb.mv.db`.
3. Start the app again.
4. Recreate accounts via signup.

### Student cannot login
- Confirm student account exists in signup.
- Ensure correct email/password.
- Role dropdown in login is ignored for auth routing; account role is taken from DB.

## Security Notes
- Current implementation uses session auth and hashed passwords.
- For production, add Spring Security full config, CSRF protection review, and robust authorization middleware.

## Roadmap
- Replace H2 with MySQL/PostgreSQL for deployment
- Add admin panel for managing course catalogs
- Add edit/delete marks history
- Export reports (PDF/Excel)

## License
This project is for educational use. Add a proper LICENSE file before production/public distribution.
