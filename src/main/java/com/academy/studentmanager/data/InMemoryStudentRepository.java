package com.academy.studentmanager.data;

import com.academy.studentmanager.model.Student;

import java.util.List;

/** Small in-memory repository used by the acceptance check and screenshot renderer. */
public final class InMemoryStudentRepository implements StudentRepository {
    private List<Student> students;

    public InMemoryStudentRepository(List<Student> initialStudents) {
        students = List.copyOf(initialStudents);
    }

    @Override
    public List<Student> load() {
        return students;
    }

    @Override
    public void save(List<Student> students) {
        this.students = List.copyOf(students);
    }
}
