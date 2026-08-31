package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "student_submission")
public class StudentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // store user id reference; keep simple to avoid cascading changes
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 255)
    private String subject;

    @Column(name = "class_name", length = 255)
    private String className;

    @Column(length = 255)
    private String university;

    @Column(name = "question_paper_path", columnDefinition = "text")
    private String questionPaperPath;

    @Column(name = "answer_sheet_path", columnDefinition = "text")
    private String answerSheetPath;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // getters & setters

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getQuestionPaperPath() {
        return questionPaperPath;
    }

    public void setQuestionPaperPath(String questionPaperPath) {
        this.questionPaperPath = questionPaperPath;
    }

    public String getAnswerSheetPath() {
        return answerSheetPath;
    }

    public void setAnswerSheetPath(String answerSheetPath) {
        this.answerSheetPath = answerSheetPath;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}