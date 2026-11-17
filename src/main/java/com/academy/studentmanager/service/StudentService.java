package com.academy.studentmanager.service;

import com.academy.studentmanager.data.StudentRepository;
import com.academy.studentmanager.model.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Coordinates validation, uniqueness, search, and durable saves. */
public final class StudentService {
    private final StudentRepository repository;
    private final StudentValidator validator;
    private List<Student> students;

    public StudentService(StudentRepository repository) throws IOException {
        this(repository, new StudentValidator());
    }

    public StudentService(StudentRepository repository, StudentValidator validator) throws IOException {
        this.repository = repository;
        this.validator = validator;
        this.students = List.copyOf(repository.load());
    }

    public List<Student> all() {
        return students.stream().sorted(Comparator.comparing(Student::rollNumber, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    public List<Student> search(String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            return all();
        }
        return all().stream()
                .filter(student -> student.rollNumber().toLowerCase(Locale.ROOT).contains(needle)
                        || student.name().toLowerCase(Locale.ROOT).contains(needle)
                        || student.course().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }

    public List<String> validate(Student student) {
        return validator.validate(student);
    }

    public void add(Student student) throws IOException, StudentOperationException {
        validateOrThrow(student);
        if (findIndex(student.rollNumber()) >= 0) {
            throw new StudentOperationException("Roll number " + student.rollNumber() + " already exists.");
        }
        List<Student> next = new ArrayList<>(students);
        next.add(student);
        persist(next);
    }

    public void update(String originalRollNumber, Student replacement) throws IOException, StudentOperationException {
        validateOrThrow(replacement);
        int index = findIndex(originalRollNumber);
        if (index < 0) {
            throw new StudentOperationException("The selected student no longer exists. Refresh the list and try again.");
        }
        int duplicateIndex = findIndex(replacement.rollNumber());
        if (duplicateIndex >= 0 && duplicateIndex != index) {
            throw new StudentOperationException("Roll number " + replacement.rollNumber() + " already exists.");
        }
        List<Student> next = new ArrayList<>(students);
        next.set(index, replacement);
        persist(next);
    }

    public void delete(String rollNumber) throws IOException, StudentOperationException {
        int index = findIndex(rollNumber);
        if (index < 0) {
            throw new StudentOperationException("The selected student no longer exists. Refresh the list and try again.");
        }
        List<Student> next = new ArrayList<>(students);
        next.remove(index);
        persist(next);
    }

    public int count() {
        return students.size();
    }

    public int courseCount() {
        return (int) students.stream().map(s -> s.course().toLowerCase(Locale.ROOT)).distinct().count();
    }

    public int academicYearCount() {
        return (int) students.stream().map(Student::academicYear).distinct().count();
    }

    private void validateOrThrow(Student student) throws StudentOperationException {
        List<String> errors = validator.validate(student);
        if (!errors.isEmpty()) {
            throw new StudentOperationException(String.join("\n", errors));
        }
    }

    private int findIndex(String rollNumber) {
        String target = rollNumber == null ? "" : rollNumber.trim();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).rollNumber().equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    private void persist(List<Student> next) throws IOException {
        List<Student> immutable = List.copyOf(next);
        repository.save(immutable);
        students = immutable;
    }

    public static final class StudentOperationException extends Exception {
        public StudentOperationException(String message) {
            super(message);
        }
    }
}
