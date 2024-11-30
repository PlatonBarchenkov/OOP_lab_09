// Файл: DataLoadResult.java
package org.example;

import java.util.List;

public class DataLoadResult {
    private List<String[]> teachers;
    private List<String[]> students;

    public DataLoadResult(List<String[]> teachers, List<String[]> students) {
        this.teachers = teachers;
        this.students = students;
    }

    public List<String[]> getTeachers() {
        return teachers;
    }

    public List<String[]> getStudents() {
        return students;
    }
}
