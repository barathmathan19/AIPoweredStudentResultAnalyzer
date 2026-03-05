package com.example.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubjectCatalogService {

    private final Map<String, List<String>> courseSubjects;
    private final Map<String, List<String>> courseSyllabi;

    public SubjectCatalogService() {
        Map<String, List<String>> subjects = new LinkedHashMap<>();
        subjects.put("BE Electronics and Instrumentation Engineering", List.of(
                "Signals and Systems",
                "Electronic Devices and Circuits",
                "Control Systems Engineering",
                "Sensors and Transducers",
                "Industrial Instrumentation",
                "Process Control",
                "Microprocessors and Microcontrollers",
                "Embedded Systems",
                "PLC and SCADA",
                "Digital Signal Processing",
                "Biomedical Instrumentation",
                "Measurement and Calibration"
        ));
        subjects.put("BE Electrical and Electronics Engineering", List.of(
                "Circuit Theory",
                "Electrical Machines",
                "Power Systems",
                "Power Electronics",
                "Digital Electronics",
                "Control Engineering",
                "Renewable Energy Systems",
                "High Voltage Engineering"
        ));
        subjects.put("BE Computer Science and Engineering", List.of(
                "Programming in C",
                "Data Structures",
                "Database Management Systems",
                "Operating Systems",
                "Computer Networks",
                "Design and Analysis of Algorithms",
                "Software Engineering",
                "Machine Learning"
        ));

        Map<String, List<String>> syllabi = new LinkedHashMap<>();
        syllabi.put("BE Electronics and Instrumentation Engineering", List.of("R2018", "R2021", "Autonomous 2023"));
        syllabi.put("BE Electrical and Electronics Engineering", List.of("R2018", "R2021", "Autonomous 2023"));
        syllabi.put("BE Computer Science and Engineering", List.of("R2018", "R2021", "Autonomous 2023"));

        this.courseSubjects = subjects;
        this.courseSyllabi = syllabi;
    }

    public List<String> getCourses() {
        return new ArrayList<>(courseSubjects.keySet());
    }

    public Map<String, List<String>> getCourseSubjects() {
        return courseSubjects;
    }

    public Map<String, List<String>> getCourseSyllabi() {
        return courseSyllabi;
    }

    public boolean isValidCourse(String course) {
        return course != null && courseSubjects.containsKey(course);
    }

    public boolean isValidSyllabus(String course, String syllabus) {
        if (course == null || syllabus == null) {
            return false;
        }
        List<String> syllabi = courseSyllabi.get(course);
        return syllabi != null && syllabi.contains(syllabus);
    }

    public boolean isValidSubject(String course, String subject) {
        if (course == null || subject == null) {
            return false;
        }
        List<String> subjects = courseSubjects.get(course);
        return subjects != null && subjects.contains(subject);
    }
}
