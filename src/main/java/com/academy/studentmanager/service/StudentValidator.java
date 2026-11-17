package com.academy.studentmanager.service;

import com.academy.studentmanager.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Field validation shared by the UI and the service. */
public final class StudentValidator {
    private static final Set<String> GENDERS = Set.of("Female", "Male", "Other");
    private static final String EMAIL = "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@"
            + "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?"
            + "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$";

    public List<String> validate(Student student) {
        List<String> errors = new ArrayList<>();
        if (student.rollNumber().isBlank()) {
            errors.add("Roll number is required.");
        } else if (!student.rollNumber().matches("[A-Za-z0-9][A-Za-z0-9/_-]{0,19}")) {
            errors.add("Roll number must be 1-20 letters, digits, /, _ or -.");
        }
        if (student.name().isBlank()) {
            errors.add("Student name is required.");
        } else if (student.name().length() > 80) {
            errors.add("Student name must be 80 characters or fewer.");
        } else if (!student.name().matches("[\\p{L}][\\p{L} .'-]*")) {
            errors.add("Student name can contain letters, spaces, apostrophes, periods, and hyphens.");
        }
        if (student.course().isBlank()) {
            errors.add("Course is required.");
        } else if (student.course().length() > 80) {
            errors.add("Course must be 80 characters or fewer.");
        }
        if (!student.academicYear().matches("\\d{4}-\\d{4}")) {
            errors.add("Academic year must use the format YYYY-YYYY, such as 2026-2027.");
        } else {
            int start = Integer.parseInt(student.academicYear().substring(0, 4));
            int end = Integer.parseInt(student.academicYear().substring(5, 9));
            if (end != start + 1) {
                errors.add("Academic year must span two consecutive years.");
            }
        }
        if (!student.email().matches(EMAIL) || student.email().length() > 254) {
            errors.add("Enter a valid email address.");
        }
        String digits = student.phone().replaceAll("\\D", "");
        if (!student.phone().matches("\\+?[0-9() -]+") || digits.length() < 7 || digits.length() > 15) {
            errors.add("Phone must contain 7-15 digits and may include +, spaces, hyphens, or parentheses.");
        }
        if (!GENDERS.contains(student.gender())) {
            errors.add("Select a gender option.");
        }
        return List.copyOf(errors);
    }
}
