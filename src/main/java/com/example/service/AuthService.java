package com.example.service;

import com.example.model.Student;
import com.example.model.UserAccount;
import com.example.model.UserRole;
import com.example.repository.StudentRepository;
import com.example.repository.UserAccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserAccountRepository userAccountRepository, StudentRepository studentRepository) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public void signupTeacher(String fullName, String email, String rawPassword) {
        registerUser(fullName, email, rawPassword, UserRole.TEACHER);
    }

    @Transactional
    public void signupStudent(String fullName, String email, String rawPassword) {
        UserAccount user = registerUser(fullName, email, rawPassword, UserRole.STUDENT);

        Student student = new Student();
        student.setName(fullName.trim());
        student.setLegacyEmail(user.getEmail());
        student.setLegacyPassword(user.getPasswordHash());
        student.setUser(user);
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> login(String email, String rawPassword, UserRole role) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        return userAccountRepository.findByEmail(normalizedEmail)
                .filter(user -> user.getRole() == role)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> login(String email, String rawPassword) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        return userAccountRepository.findByEmail(normalizedEmail)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    private UserAccount registerUser(String fullName, String email, String rawPassword, UserRole role) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (userAccountRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        UserAccount user = new UserAccount();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userAccountRepository.save(user);
    }
}
