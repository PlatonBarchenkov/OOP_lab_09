// Файл: DataManager.java
package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static void saveDataToFile(File file, List<String[]> teachers, List<String[]> students) throws DataSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Записываем учителей
            bw.write("# Teachers");
            bw.newLine();
            bw.write("ФИО учителя,Предмет,Классы");
            bw.newLine();
            for (String[] teacher : teachers) {
                String line = String.join(",", teacher);
                bw.write(line);
                bw.newLine();
            }

            bw.newLine(); // Пустая строка между разделами

            // Записываем учеников
            bw.write("# Students");
            bw.newLine();
            bw.write("ФИО ученика,Класс,Успеваемость");
            bw.newLine();
            for (String[] student : students) {
                String line = String.join(",", student);
                bw.write(line);
                bw.newLine();
            }

        } catch (IOException ex) {
            throw new DataSaveException("Ошибка при сохранении данных: " + ex.getMessage());
        }
    }

    public static DataLoadResult loadDataFromFile(File file) throws DataLoadException {
        List<String[]> loadedTeachers = new ArrayList<>();
        List<String[]> loadedStudents = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isTeacherSection = false;
            boolean isStudentSection = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase("# Teachers")) {
                    isTeacherSection = true;
                    isStudentSection = false;
                    br.readLine(); // Пропускаем заголовок столбцов
                    continue;
                } else if (line.equalsIgnoreCase("# Students")) {
                    isTeacherSection = false;
                    isStudentSection = true;
                    br.readLine(); // Пропускаем заголовок столбцов
                    continue;
                }

                if (isTeacherSection) {
                    String[] parts = line.split(",", 3);
                    if (parts.length == 3) {
                        loadedTeachers.add(new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()});
                    }
                } else if (isStudentSection) {
                    String[] parts = line.split(",", 3);
                    if (parts.length == 3) {
                        loadedStudents.add(new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()});
                    }
                }
            }

        } catch (IOException ex) {
            throw new DataLoadException("Ошибка при загрузке данных: " + ex.getMessage());
        }

        return new DataLoadResult(loadedTeachers, loadedStudents);
    }

    public static class StudentManager {
        private List<String[]> students;

        public StudentManager() {
            students = new ArrayList<>();
            // Инициализация исходных данных
            students.add(new String[]{"Смирнов Алексей Иванович", "5А", "Отлично"});
            students.add(new String[]{"Кузнецова Мария Петровна", "6Б", "Хорошо"});
            students.add(new String[]{"Новиков Дмитрий Сергеевич", "7В", "Удовлетворительно"});
        }

        public List<String[]> getStudents() {
            return students;
        }

        public void addStudent(String[] student) throws InvalidInputException {
            if (student == null || student.length != 3) {
                throw new InvalidInputException("Неверные данные ученика.");
            }
            for (String field : student) {
                if (field == null || field.trim().isEmpty()) {
                    throw new InvalidInputException("Поля ученика не могут быть пустыми.");
                }
            }
            students.add(student);
        }

        public void removeStudent(int index) throws InvalidInputException {
            if (index < 0 || index >= students.size()) {
                throw new InvalidInputException("Некорректный индекс ученика для удаления.");
            }
            students.remove(index);
        }

        public void resetStudents(List<String[]> originalData) {
            students.clear();
            students.addAll(originalData);
        }
    }

    public static class TeacherManager {
        private List<String[]> teachers;

        public TeacherManager() {
            teachers = new ArrayList<>();
            // Инициализация исходных данных
            teachers.add(new String[]{"Иванов Иван Иванович", "Математика", "5А;6Б"});
            teachers.add(new String[]{"Петрова Анна Сергеевна", "Русский язык", "7В;8Г"});
            teachers.add(new String[]{"Сидоров Петр Петрович", "История", "9А;10Б"});
        }

        public List<String[]> getTeachers() {
            return teachers;
        }

        public void addTeacher(String[] teacher) throws InvalidInputException {
            if (teacher == null || teacher.length != 3) {
                throw new InvalidInputException("Неверные данные учителя.");
            }
            for (String field : teacher) {
                if (field == null || field.trim().isEmpty()) {
                    throw new InvalidInputException("Поля учителя не могут быть пустыми.");
                }
            }
            teachers.add(teacher);
        }

        public void removeTeacher(int index) throws InvalidInputException {
            if (index < 0 || index >= teachers.size()) {
                throw new InvalidInputException("Некорректный индекс учителя для удаления.");
            }
            teachers.remove(index);
        }

        public void resetTeachers(List<String[]> originalData) {
            teachers.clear();
            teachers.addAll(originalData);
        }
    }
}
