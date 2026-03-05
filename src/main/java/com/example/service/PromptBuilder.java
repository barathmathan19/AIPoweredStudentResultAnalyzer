package com.example.service;

public class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildDetailedSubjectPrompt(
            String studentName,
            String course,
            String syllabus,
            String subject,
            int marks,
            String remark
    ) {
        String teacherRemark = (remark == null || remark.isBlank()) ? "No teacher remark provided" : remark;
        String studentCourse = (course == null || course.isBlank()) ? "Not specified" : course;
        String studentSyllabus = (syllabus == null || syllabus.isBlank()) ? "Not specified" : syllabus;

        return """
                You are an expert academic mentor for engineering students.
                Create a detailed, actionable improvement plan.

                Student Name: %s
                Course/Department: %s
                Syllabus: %s
                Subject: %s
                Marks: %d/100
                Teacher Remark: %s

                Requirements:
                1) Start with a concise diagnosis (strengths + weaknesses).
                2) Give concept-level gaps inferred from mark and remark.
                3) Provide a 4-week study plan with weekly targets.
                4) Provide daily micro-habits and revision strategy.
                5) Give exam-writing strategy specific to this subject.
                6) End with measurable target for the next test.

                Output format:
                - Use sections with headings.
                - Use bullets under each heading.
                - Be specific, practical, and personalized.
                """.formatted(studentName, studentCourse, studentSyllabus, subject, marks, teacherRemark);
    }
}
