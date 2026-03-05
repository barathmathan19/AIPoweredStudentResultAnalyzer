package com.example.service;

import com.example.dto.ResultView;
import com.example.model.Student;
import com.example.model.StudentResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {

    private final GeminiService geminiService;

    public SuggestionService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public List<ResultView> buildResultViews(Student student) {
        List<ResultView> views = new ArrayList<>();
        for (StudentResult result : student.getResults()) {
            String suggestion = generateSuggestion(student, result);
            views.add(new ResultView(
                    result.getSubject(),
                    result.getMarks(),
                    result.getRemark(),
                    suggestion
            ));
        }
        return views;
    }

    private String generateSuggestion(Student student, StudentResult result) {
        String prompt = PromptBuilder.buildDetailedSubjectPrompt(
                student.getName(),
                student.getCourse(),
                student.getSyllabus(),
                result.getSubject(),
                result.getMarks(),
                result.getRemark()
        );

        try {
            String json = geminiService.askAI(prompt);
            String ai = geminiService.extractText(json);
            if (ai == null || ai.isBlank()) {
                return fallbackSuggestion(student, result);
            }
            return ai.trim();
        } catch (Exception ignored) {
            return fallbackSuggestion(student, result);
        }
    }

    private String fallbackSuggestion(Student student, StudentResult result) {
        String subject = result.getSubject();
        int marks = result.getMarks() == null ? 0 : result.getMarks();
        String remark = result.getRemark() == null ? "No teacher remark provided" : result.getRemark();
        String course = student.getCourse() == null ? "your course" : student.getCourse();
        String syllabus = student.getSyllabus() == null ? "current syllabus" : student.getSyllabus();

        String level;
        if (marks < 40) {
            level = "Foundation is weak and immediate correction is needed.";
        } else if (marks < 60) {
            level = "Core understanding exists, but consistency and depth are missing.";
        } else if (marks < 80) {
            level = "Good base present; target high-scoring precision and advanced problem solving.";
        } else {
            level = "Strong performance; focus on retaining consistency and converting to top-grade answers.";
        }

        return """
                1) Diagnosis
                - Current level in %s: %s
                - Teacher input to address: %s
                - Academic context: %s (%s)

                2) Concept Gaps To Fix
                - Identify 3 weak subtopics from recent tests and class notes.
                - Build a one-page formula/concept sheet for those subtopics.
                - Re-solve previously wrong questions without seeing answers.

                3) Four-Week Improvement Plan
                - Week 1: Rebuild basics, 45-60 min/day, and complete one topic-wise quiz.
                - Week 2: Solve medium-level problems and summarize mistakes after each session.
                - Week 3: Attempt timed practice sets and improve speed with accuracy.
                - Week 4: Full revision + two mock tests + error log correction.

                4) Daily Micro-Habits
                - 20 min concept recap before class or study session.
                - 30-40 min focused practice on one subtopic only.
                - 10 min error journal: what went wrong and how to avoid repetition.

                5) Exam Strategy
                - Start with sure-shot questions and secure easy marks first.
                - Show clear steps, units, diagrams, and final answers neatly.
                - Keep last 10 minutes for verification and correction.

                6) Measurable Next-Test Target
                - Raise %s score from %d to at least %d.
                - Complete minimum 4 timed practices before the next internal exam.
                """.formatted(
                subject,
                level,
                remark,
                course,
                syllabus,
                subject,
                marks,
                Math.min(100, marks + 15)
        );
    }
}
