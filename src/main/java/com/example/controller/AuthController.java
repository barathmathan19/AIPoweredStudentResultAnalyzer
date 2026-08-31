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

    // Signup now creates a STUDENT account by default (anyone can create an account)
    @PostMapping("/signup")
    public String signup(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            // always create STUDENT accounts here
            authService.signupStudent(fullName, email, password);

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

    // Login requires an existing account; redirect based on stored role but no separate teacher-login form
    @PostMapping("/login")
    public String login(
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

        // Always redirect to student dashboard for normal users; if a teacher account exists you can still navigate
        // (this preserves existing role behavior but the login form no longer asks for role)
        if (userRole == UserRole.STUDENT) {
            return "redirect:/student/dashboard";
        }

        return "redirect:/student/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}