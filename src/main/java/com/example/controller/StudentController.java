package com.example.controller;

import com.example.dto.ResultView;
import com.example.model.Student;
import com.example.model.StudentSubmission;
import com.example.model.UserRole;
import com.example.repository.StudentSubmissionRepository;
import com.example.service.StudentService;
import com.example.service.SuggestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final SuggestionService suggestionService;
    private final StudentSubmissionRepository submissionRepository;

    public StudentController(StudentService studentService,
                             SuggestionService suggestionService,
                             StudentSubmissionRepository submissionRepository) {
        this.studentService = studentService;
        this.suggestionService = suggestionService;
        this.submissionRepository = submissionRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isStudent(session)) {
            return "redirect:/";
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/";
        }

        Long userId = (Long) userIdObj;
        Optional<Student> studentOpt = studentService.findByUserId(userId);
        if (studentOpt.isEmpty()) {
            session.invalidate();
            return "redirect:/";
        }

        Student student = studentOpt.get();
        List<ResultView> results = suggestionService.buildResultViews(student);

        model.addAttribute("student", student);
        model.addAttribute("results", results);
        return "student-dashboard";
    }

    private boolean isStudent(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && UserRole.STUDENT.name().equals(role.toString());
    }

    // New: Accept question paper + answer sheet + subject/class/university text fields
    @PostMapping("/upload")
    public String uploadSubmission(HttpSession session,
                                   @RequestParam("questionPaper") MultipartFile questionPaper,
                                   @RequestParam("answerSheet") MultipartFile answerSheet,
                                   @RequestParam("subject") String subject,
                                   @RequestParam("className") String className,
                                   @RequestParam("university") String university,
                                   Model model) {
        if (!isStudent(session)) {
            return "redirect:/";
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/";
        }
        Long userId = (Long) userIdObj;

        try {
            Path uploadDir = Paths.get("uploads", String.valueOf(userId), String.valueOf(System.currentTimeMillis()));
            Files.createDirectories(uploadDir);

            String qFilename = "question_" + questionPaper.getOriginalFilename();
            String aFilename = "answer_" + answerSheet.getOriginalFilename();

            Path qPath = uploadDir.resolve(qFilename);
            Path aPath = uploadDir.resolve(aFilename);

            questionPaper.transferTo(qPath.toFile());
            answerSheet.transferTo(aPath.toFile());

            StudentSubmission submission = new StudentSubmission();
            submission.setUserId(userId);
            submission.setSubject(subject);
            submission.setClassName(className);
            submission.setUniversity(university);
            submission.setQuestionPaperPath(qPath.toString());
            submission.setAnswerSheetPath(aPath.toString());

            submissionRepository.save(submission);

            model.addAttribute("success", "Files uploaded successfully.");
        } catch (IOException ex) {
            model.addAttribute("error", "Failed to upload files: " + ex.getMessage());
        }

        return "redirect:/student/dashboard";
    }
}