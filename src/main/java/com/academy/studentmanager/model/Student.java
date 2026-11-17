package com.academy.studentmanager.model;

import java.util.Objects;

/** Immutable student details managed by the application. */
public record Student(
        String rollNumber,
        String name,
        String course,
        String academicYear,
        String email,
        String phone,
        String gender) {

    public Student {
        rollNumber = clean(rollNumber);
        name = clean(name);
        course = clean(course);
        academicYear = clean(academicYear);
        email = clean(email);
        phone = clean(phone);
        gender = clean(gender);
    }

    private static String clean(String value) {
        return Objects.requireNonNullElse(value, "").trim();
    }
}
