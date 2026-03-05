package com.example.controller;

import com.example.dto.ResultView;
import com.example.model.Student;
import com.example.model.UserRole;
import com.example.service.StudentService;
import com.example.service.SuggestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final SuggestionService suggestionService;

    public StudentController(StudentService studentService, SuggestionService suggestionService) {
        this.studentService = studentService;
        this.suggestionService = suggestionService;
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
}
