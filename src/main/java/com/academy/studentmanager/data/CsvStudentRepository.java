package com.academy.studentmanager.data;

import com.academy.studentmanager.model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** UTF-8 CSV persistence with escaped fields and an atomic replacement on save. */
public final class CsvStudentRepository implements StudentRepository {
    public static final String HEADER = "Roll Number,Name,Course,Academic Year,Email,Phone,Gender";

    private final Path file;

    public CsvStudentRepository(Path file) {
        this.file = file.toAbsolutePath().normalize();
    }

    public Path file() {
        return file;
    }

    @Override
    public List<Student> load() throws IOException {
        if (Files.notExists(file)) {
            save(List.of());
            return List.of();
        }

        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null || !stripBom(line).equals(HEADER)) {
                throw new IOException("The student data file has an unexpected header: " + file);
            }
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                List<String> fields;
                try {
                    fields = parseCsvLine(line);
                } catch (IllegalArgumentException exception) {
                    throw new IOException("Could not read student data on line " + lineNumber + ".", exception);
                }
                if (fields.size() != 7) {
                    throw new IOException("Expected 7 fields on line " + lineNumber + " of " + file + ".");
                }
                students.add(new Student(fields.get(0), fields.get(1), fields.get(2),
                        fields.get(3), fields.get(4), fields.get(5), fields.get(6)));
            }
        }
        return List.copyOf(students);
    }

    @Override
    public void save(List<Student> students) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                writer.write(HEADER);
                writer.newLine();
                for (Student student : students) {
                    writer.write(csv(student.rollNumber()));
                    writer.write(',');
                    writer.write(csv(student.name()));
                    writer.write(',');
                    writer.write(csv(student.course()));
                    writer.write(',');
                    writer.write(csv(student.academicYear()));
                    writer.write(',');
                    writer.write(csv(student.email()));
                    writer.write(',');
                    writer.write(csv(student.phone()));
                    writer.write(',');
                    writer.write(csv(student.gender()));
                    writer.newLine();
                }
            }
            try {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static String stripBom(String value) {
        return value.startsWith("\uFEFF") ? value.substring(1) : value;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (quoted) {
                if (current == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    field.append(current);
                }
            } else if (current == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else if (current == '"' && field.isEmpty()) {
                quoted = true;
            } else {
                field.append(current);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("A quoted field was not closed.");
        }
        fields.add(field.toString());
        return fields;
    }
}
