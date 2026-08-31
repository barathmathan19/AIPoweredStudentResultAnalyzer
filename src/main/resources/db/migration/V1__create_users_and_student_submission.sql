-- Create users table (if you want Flyway to manage it; if your app already has the users table created via JPA, adapt or omit)
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(512) NOT NULL,
  role VARCHAR(50) NOT NULL
);

-- Student submission table
CREATE TABLE IF NOT EXISTS student_submission (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  subject VARCHAR(255),
  class_name VARCHAR(255),
  university VARCHAR(255),
  question_paper_path TEXT,
  answer_sheet_path TEXT,
  created_at TIMESTAMP DEFAULT now()
);

-- Add foreign key if you maintain relational integrity (optional)
ALTER TABLE IF EXISTS student_submission
  ADD CONSTRAINT fk_submission_user FOREIGN KEY (user_id) REFERENCES users(id);