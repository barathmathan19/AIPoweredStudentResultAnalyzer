package com.example.controller;

import com.example.model.UserAccount;
import com.example.model.UserRole;
import com.example.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String loginPage(Model model) {
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", "");
        }
        if (!model.containsAttribute("success")) {
            model.addAttribute("success", "");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        if (!model.containsAttribute("error")) {
            model.addAttribute("error", "");
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String role,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            UserRole userRole = parseRole(role);
            if (userRole == UserRole.TEACHER) {
                authService.signupTeacher(fullName, email, password);
            } else {
                authService.signupStudent(fullName, email, password);
            }
            redirectAttributes.addFlashAttribute("success", "Signup successful. Please login.");
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "signup";
        } catch (Exception ex) {
            model.addAttribute("error", "Signup failed due to existing incompatible data. Please reset old DB once.");
            return "signup";
        }
    }

    @PostMapping("/login")
    public String login(
            @RequestParam(required = false) String role,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Optional<UserAccount> user = authService.login(email.trim(), password);
        if (user.isEmpty()) {
            model.addAttribute("error", "Invalid login credentials");
            return "login";
        }

        UserRole userRole = user.get().getRole();
        session.setAttribute("userId", user.get().getId());
        session.setAttribute("role", userRole.name());

        if (userRole == UserRole.STUDENT) {
            return "redirect:/student/dashboard";
        }

        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private UserRole parseRole(String role) {
        if ("teacher".equalsIgnoreCase(role)) {
            return UserRole.TEACHER;
        }
        if ("student".equalsIgnoreCase(role)) {
            return UserRole.STUDENT;
        }
        throw new IllegalArgumentException("Invalid role");
    }
}
