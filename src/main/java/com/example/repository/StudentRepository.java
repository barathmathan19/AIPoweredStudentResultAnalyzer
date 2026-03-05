package com.example.repository;

import com.example.model.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = {"results", "user"})
    Optional<Student> findById(Long id);

    @EntityGraph(attributePaths = {"results", "user"})
    Optional<Student> findByUserEmail(String email);

    @EntityGraph(attributePaths = {"results", "user"})
    Optional<Student> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"results", "user"})
    List<Student> findAllByOrderByNameAsc();
}
