package com.academy.studentmanager.data;

import com.academy.studentmanager.model.Student;

import java.io.IOException;
import java.util.List;

/** Storage boundary so the service can use CSV on disk or an in-memory source. */
public interface StudentRepository {
    List<Student> load() throws IOException;

    void save(List<Student> students) throws IOException;
}
