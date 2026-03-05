package com.example.controller;

import com.example.model.Student;
import com.example.model.UserRole;
import com.example.service.StudentService;
import com.example.service.SubjectCatalogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final StudentService studentService;
    private final SubjectCatalogService subjectCatalogService;

    public TeacherController(StudentService studentService, SubjectCatalogService subjectCatalogService) {
        this.studentService = studentService;
        this.subjectCatalogService = subjectCatalogService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isTeacher(session)) {
            return "redirect:/";
        }

        List<Student> students = studentService.findAllStudents();
        model.addAttribute("students", students);
        model.addAttribute("courses", subjectCatalogService.getCourses());
        model.addAttribute("courseSubjects", subjectCatalogService.getCourseSubjects());
        model.addAttribute("courseSyllabi", subjectCatalogService.getCourseSyllabi());

        if (!model.containsAttribute("success")) {
            model.addAttribute("success", "");
        }
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", "");
        }
        return "teacher-dashboard";
    }

    @PostMapping("/students/results")
    public String saveStudentResults(
            HttpSession session,
            @RequestParam Long studentId,
            @RequestParam String course,
            @RequestParam String syllabus,
            @RequestParam(name = "subjects", required = false) List<String> subjects,
            @RequestParam(name = "marks", required = false) List<String> markTexts,
            @RequestParam(name = "remarks", required = false) List<String> remarks,
            RedirectAttributes redirectAttributes
    ) {
        if (!isTeacher(session)) {
            return "redirect:/";
        }

        try {
            List<Integer> marks = parseMarks(markTexts);
            studentService.saveResultsForStudent(studentId, course, syllabus, subjects, marks, remarks);
            redirectAttributes.addFlashAttribute("success", "Student results saved successfully");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Unable to save results. Check input values.");
        }

        return "redirect:/teacher/dashboard";
    }

    private boolean isTeacher(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null && UserRole.TEACHER.name().equals(role.toString());
    }

    private List<Integer> parseMarks(List<String> markTexts) {
        List<Integer> marks = new ArrayList<>();
        if (markTexts == null) {
            return marks;
        }

        for (String markText : markTexts) {
            if (markText == null || markText.isBlank()) {
                marks.add(null);
                continue;
            }
            marks.add(Integer.parseInt(markText.trim()));
        }
        return marks;
    }
}
