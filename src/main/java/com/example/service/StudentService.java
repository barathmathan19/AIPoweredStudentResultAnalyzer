package com.example.service;

import com.example.model.Student;
import com.example.model.StudentResult;
import com.example.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SubjectCatalogService subjectCatalogService;

    public StudentService(StudentRepository studentRepository, SubjectCatalogService subjectCatalogService) {
        this.studentRepository = studentRepository;
        this.subjectCatalogService = subjectCatalogService;
    }

    @Transactional
    public void saveResultsForStudent(Long studentId, String course, String syllabus, List<String> subjects, List<Integer> marks, List<String> remarks) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        String normalizedCourse = course == null ? "" : course.trim();
        String normalizedSyllabus = syllabus == null ? "" : syllabus.trim();

        if (!subjectCatalogService.isValidCourse(normalizedCourse)) {
            throw new IllegalArgumentException("Select a valid course for the student.");
        }
        if (!subjectCatalogService.isValidSyllabus(normalizedCourse, normalizedSyllabus)) {
            throw new IllegalArgumentException("Select a valid syllabus for the chosen course.");
        }

        student.setCourse(normalizedCourse);
        student.setSyllabus(normalizedSyllabus);
        student.clearResults();
        attachResults(student, normalizedCourse, subjects, marks, remarks);
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public Optional<Student> findByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Student> findAllStudents() {
        return studentRepository.findAllByOrderByNameAsc();
    }

    private void attachResults(Student student, String course, List<String> subjects, List<Integer> marks, List<String> remarks) {
        List<String> safeSubjects = subjects == null ? List.of() : subjects;
        List<Integer> safeMarks = marks == null ? List.of() : marks;
        List<String> safeRemarks = remarks == null ? List.of() : remarks;

        int count = Math.min(safeSubjects.size(), Math.min(safeMarks.size(), safeRemarks.size()));
        List<StudentResult> results = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String subject = safeSubjects.get(i) == null ? "" : safeSubjects.get(i).trim();
            Integer mark = safeMarks.get(i);
            String remark = safeRemarks.get(i) == null ? "" : safeRemarks.get(i).trim();

            if (subject.isEmpty() || mark == null) {
                continue;
            }

            if (!subjectCatalogService.isValidSubject(course, subject)) {
                throw new IllegalArgumentException("Invalid subject selected for the chosen course: " + subject);
            }

            int boundedMark = Math.max(0, Math.min(100, mark));
            StudentResult result = new StudentResult();
            result.setSubject(subject);
            result.setMarks(boundedMark);
            result.setRemark(remark);
            results.add(result);
        }

        if (results.isEmpty()) {
            throw new IllegalArgumentException("Add at least one valid subject mark");
        }

        for (StudentResult result : results) {
            student.addResult(result);
        }
    }
}
