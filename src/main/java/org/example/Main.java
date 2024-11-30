// Файл: Main.java
package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Программа для управления данными учителей и учеников в системе управления школой.
 * Содержит функции добавления, удаления учителей и учеников, а также поиска, фильтрации,
 * загрузки и сохранения данных из/в текстовый файл.
 *
 * @author Барченков Платон 3312
 * @version 1.0
 */
public class Main {
    public JFrame frame;
    public JTable teacherTable, studentTable;
    public DefaultTableModel teacherTableModel, studentTableModel;
    private JPanel filterPanel;
    private JButton addTeacherButton, addStudentButton, deleteTeacherButton, deleteStudentButton, generateReportButton;
    private JButton searchButton, resetButton, loadButton, saveButton;
    private JComboBox<String> searchCriteria;
    private JTextField searchField;
    private JScrollPane teacherScrollPane, studentScrollPane;
    private JTabbedPane tabbedPane;
    private DataManager.TeacherManager teacherManager;
    private DataManager.StudentManager studentManager;
    TableRowSorter<DefaultTableModel> teacherSorter;
    TableRowSorter<DefaultTableModel> studentSorter;

    /**
     * Метод для создания и отображения основного окна программы.
     */
    public void SchoolManagementSystem() {
        // Инициализация менеджеров учителей и учеников
        teacherManager = new DataManager.TeacherManager();
        studentManager = new DataManager.StudentManager();

        // Создание главного окна программы
        frame = new JFrame("School Management System");
        frame.setSize(1000, 700); // Увеличиваем размер окна для двух таблиц
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие окна завершает программу
        frame.setLayout(new BorderLayout()); // Устанавливаем BorderLayout для главного окна

        // Создание панели инструментов с кнопками действий
        JToolBar actionPanel = new JToolBar("Toolbar");

        // Кнопки для учителей и учеников
        addTeacherButton = new JButton("Добавить учителя");
        addStudentButton = new JButton("Добавить ученика");
        deleteTeacherButton = new JButton("Уволить учителя");
        deleteStudentButton = new JButton("Удалить ученика");
        generateReportButton = new JButton("Создать отчет");

        // Кнопки загрузки и сохранения данных
        loadButton = new JButton("Загрузить данные");
        saveButton = new JButton("Сохранить данные");

        // Добавляем кнопки на панель инструментов слева
        actionPanel.add(addTeacherButton);
        actionPanel.add(addStudentButton);
        actionPanel.add(deleteTeacherButton);
        actionPanel.add(deleteStudentButton);
        actionPanel.add(generateReportButton);

        // Добавляем гибкое пространство, чтобы следующие кнопки были справа
        actionPanel.add(Box.createHorizontalGlue());

        // Добавляем кнопки загрузки и сохранения данных справа
        actionPanel.add(loadButton);
        actionPanel.add(saveButton);

        frame.add(actionPanel, BorderLayout.NORTH); // Размещаем панель инструментов сверху

        // Определяем столбцы таблицы учителей
        String[] teacherColumns = {"ФИО учителя", "Предмет", "Классы"};
        // Инициализация модели таблицы учителей
        teacherTableModel = new DefaultTableModel(teacherColumns, 0);
        for (String[] teacher : teacherManager.getTeachers()) {
            teacherTableModel.addRow(teacher);
        }
        teacherTable = new JTable(teacherTableModel);
        teacherScrollPane = new JScrollPane(teacherTable);
        teacherScrollPane.setBorder(BorderFactory.createTitledBorder("Учителя"));

        // Создание сортировщика для таблицы учителей
        teacherSorter = new TableRowSorter<>(teacherTableModel);
        teacherTable.setRowSorter(teacherSorter);

        // Определяем столбцы таблицы учеников
        String[] studentColumns = {"ФИО ученика", "Класс", "Успеваемость"};
        // Инициализация модели таблицы учеников
        studentTableModel = new DefaultTableModel(studentColumns, 0);
        for (String[] student : studentManager.getStudents()) {
            studentTableModel.addRow(student);
        }
        studentTable = new JTable(studentTableModel);
        studentScrollPane = new JScrollPane(studentTable);
        studentScrollPane.setBorder(BorderFactory.createTitledBorder("Ученики"));

        // Создание сортировщика для таблицы учеников
        studentSorter = new TableRowSorter<>(studentTableModel);
        studentTable.setRowSorter(studentSorter);

        // Создание вкладок для таблиц
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Учителя", teacherScrollPane);
        tabbedPane.addTab("Ученики", studentScrollPane);
        frame.add(tabbedPane, BorderLayout.CENTER); // Размещаем вкладки в центре

        // Создание компонентов для панели поиска и фильтрации данных
        searchCriteria = new JComboBox<>(new String[]{
                "ФИО учителя", "Предмет", "Классы",
                "ФИО ученика", "Класс ученика", "Успеваемость"
        });
        searchField = new JTextField(20);
        searchButton = new JButton("Поиск");
        resetButton = new JButton("Сбросить");

        // Панель фильтрации
        filterPanel = new JPanel();
        filterPanel.add(new JLabel("Критерий поиска: "));
        filterPanel.add(searchCriteria);
        filterPanel.add(new JLabel("Значение: "));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(resetButton);
        frame.add(filterPanel, BorderLayout.SOUTH); // Размещаем панель фильтрации снизу

        // Действие при переключении вкладок для обновления критериев поиска
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateSearchCriteria();
            }
        });

        // Инициализация критериев поиска по текущей вкладке
        updateSearchCriteria();

        // Действие при нажатии кнопки "Поиск"
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String criterion = (String) searchCriteria.getSelectedItem();
                String value = searchField.getText().trim();
                searchTable(criterion, value);
            }
        });

        // Действие при нажатии кнопки "Сбросить"
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetTable();
            }
        });

        // Действие при нажатии кнопки "Добавить учителя"
        addTeacherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Ввод и валидация ФИО учителя
                    String teacherName = promptForInput("Введите ФИО учителя:");
                    if (teacherName == null) return; // Пользователь отменил ввод

                    // Ввод и валидация предмета
                    String subject = promptForInput("Введите предмет:");
                    if (subject == null) return;

                    // Ввод и валидация классов
                    String classes = promptForInput("Введите классы (разделенные точкой с запятой ';'):");
                    if (classes == null) return;

                    // Добавление нового учителя в менеджер и таблицу
                    String[] newTeacher = {teacherName, subject, classes};
                    teacherManager.addTeacher(newTeacher);
                    teacherTableModel.addRow(newTeacher);

                } catch (InvalidInputException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Произошла непредвиденная ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Действие при нажатии кнопки "Удалить учителя"
        deleteTeacherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = teacherTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Преобразование индекса с учёта сортировки
                        selectedRow = teacherTable.convertRowIndexToModel(selectedRow);
                        teacherManager.removeTeacher(selectedRow);
                        teacherTableModel.removeRow(selectedRow);
                    } else {
                        throw new InvalidInputException("Пожалуйста, выберите учителя для удаления.");
                    }
                } catch (InvalidInputException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка удаления", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Произошла непредвиденная ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Действие при нажатии кнопки "Добавить ученика"
        addStudentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Ввод и валидация ФИО ученика
                    String studentName = promptForInput("Введите ФИО ученика:");
                    if (studentName == null) return; // Пользователь отменил ввод

                    // Ввод и валидация класса
                    String studentClass = promptForInput("Введите класс:");
                    if (studentClass == null) return;

                    // Ввод и валидация успеваемости
                    String performance = promptForInput("Введите успеваемость:");
                    if (performance == null) return;

                    // Добавление нового ученика в менеджер и таблицу
                    String[] newStudent = {studentName, studentClass, performance};
                    studentManager.addStudent(newStudent);
                    studentTableModel.addRow(newStudent);

                } catch (InvalidInputException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Произошла непредвиденная ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Действие при нажатии кнопки "Удалить ученика"
        deleteStudentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = studentTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Преобразование индекса с учёта сортировки
                        selectedRow = studentTable.convertRowIndexToModel(selectedRow);
                        studentManager.removeStudent(selectedRow);
                        studentTableModel.removeRow(selectedRow);
                    } else {
                        throw new InvalidInputException("Пожалуйста, выберите ученика для удаления.");
                    }
                } catch (InvalidInputException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка удаления", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Произошла непредвиденная ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Действие при нажатии кнопки "Загрузить данные"
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Выберите текстовый файл для загрузки данных");
                    int userSelection = fileChooser.showOpenDialog(frame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToLoad = fileChooser.getSelectedFile();
                        DataLoadResult result = DataManager.loadDataFromFile(fileToLoad);

                        // Обновляем менеджеры данных
                        teacherManager.resetTeachers(result.getTeachers());
                        studentManager.resetStudents(result.getStudents());

                        // Обновляем таблицы
                        refreshTeacherTable();
                        refreshStudentTable();

                        JOptionPane.showMessageDialog(frame, "Данные успешно загружены.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (DataLoadException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка загрузки", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Произошла непредвиденная ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Действие при нажатии кнопки "Сохранить данные"
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Сохраните данные в текстовый файл");
                    int userSelection = fileChooser.showSaveDialog(frame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        DataManager.saveDataToFile(fileToSave, teacherManager.getTeachers(), studentManager.getStudents());
                        JOptionPane.showMessageDialog(frame, "Данные успешно сохранены.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (DataSaveException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Ошибка сохранения", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Произошла непредвиденная ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Делаем главное окно видимым
        frame.setVisible(true);
    }

    /**
     * Обновляет критерии поиска в зависимости от выбранной вкладки.
     */
    private void updateSearchCriteria() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        searchCriteria.removeAllItems();

        if (selectedIndex == 0) { // Учителя
            searchCriteria.addItem("ФИО учителя");
            searchCriteria.addItem("Предмет");
            searchCriteria.addItem("Классы");
        } else if (selectedIndex == 1) { // Ученики
            searchCriteria.addItem("ФИО ученика");
            searchCriteria.addItem("Класс ученика");
            searchCriteria.addItem("Успеваемость");
        }
    }

    /**
     * Метод для фильтрации данных в таблице на основе критерия и значения поиска.
     *
     * @param criterion Критерий поиска.
     * @param value     Значение для поиска.
     */
    public void searchTable(String criterion, String value) {
        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Поле поиска не может быть пустым.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 0) { // Учителя
            int columnIndex = -1;
            switch (criterion) {
                case "ФИО учителя":
                    columnIndex = 0;
                    break;
                case "Предмет":
                    columnIndex = 1;
                    break;
                case "Классы":
                    columnIndex = 2;
                    break;
            }

            if (columnIndex != -1) {
                teacherSorter.setRowFilter(RowFilter.regexFilter("(?i)" + value, columnIndex));
            }
        } else if (selectedIndex == 1) { // Ученики
            int columnIndex = -1;
            switch (criterion) {
                case "ФИО ученика":
                    columnIndex = 0;
                    break;
                case "Класс ученика":
                    columnIndex = 1;
                    break;
                case "Успеваемость":
                    columnIndex = 2;
                    break;
            }

            if (columnIndex != -1) {
                studentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + value, columnIndex));
            }
        }
    }

    /**
     * Метод для сброса фильтров и восстановления исходных данных.
     */
    public void resetTable() {
        // Сброс фильтра для учителей и учеников
        teacherSorter.setRowFilter(null);
        studentSorter.setRowFilter(null);
        // Очистка поля поиска
        searchField.setText("");
    }

    /**
     * Метод для обновления таблицы учителей после загрузки данных.
     */
    private void refreshTeacherTable() {
        teacherTableModel.setRowCount(0);
        for (String[] teacher : teacherManager.getTeachers()) {
            teacherTableModel.addRow(teacher);
        }
    }

    /**
     * Метод для обновления таблицы учеников после загрузки данных.
     */
    private void refreshStudentTable() {
        studentTableModel.setRowCount(0);
        for (String[] student : studentManager.getStudents()) {
            studentTableModel.addRow(student);
        }
    }

    /**
     * Метод для запроса ввода у пользователя с возможностью повторного ввода при ошибке.
     *
     * @param message Сообщение для отображения в диалоговом окне.
     * @return Введенное пользователем значение, либо null, если пользователь отменил ввод.
     */
    private String promptForInput(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(frame, message);
            if (input == null) {
                // Пользователь отменил ввод
                return null;
            }
            input = input.trim();
            if (input.isEmpty()) {
                // Показываем сообщение об ошибке и предлагаем повторный ввод
                JOptionPane.showMessageDialog(frame, "Это поле не может быть пустым. Пожалуйста, введите значение.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            return input;
        }
    }

    /**
     * Метод для добавления учителя.
     *
     * @param name      ФИО учителя.
     * @param subject   Предмет.
     * @param classes   Классы.
     * @throws InvalidInputException Если данные некорректны.
     */
    public void addTeacher(String name, String subject, String classes) throws InvalidInputException {
        if (name != null && !name.isEmpty() && subject != null && !subject.isEmpty() && classes != null && !classes.isEmpty()) {
            teacherTableModel.addRow(new Object[]{name, subject, classes});
            teacherManager.addTeacher(new String[]{name, subject, classes});
        } else {
            throw new InvalidInputException("Все поля должны быть заполнены!");
        }
    }

    /**
     * Метод для редактирования учителя.
     *
     * @param row      Номер строки.
     * @param name     Новое ФИО.
     * @param subject  Новый предмет.
     * @param classes  Новые классы.
     * @throws InvalidInputException Если данные некорректны.
     */
    public void editTeacher(int row, String name, String subject, String classes) throws InvalidInputException {
        if (name != null && !name.isEmpty() && subject != null && !subject.isEmpty() && classes != null && !classes.isEmpty()) {
            teacherTableModel.setValueAt(name, row, 0);
            teacherTableModel.setValueAt(subject, row, 1);
            teacherTableModel.setValueAt(classes, row, 2);
            teacherManager.getTeachers().set(row, new String[]{name, subject, classes});
        } else {
            throw new InvalidInputException("Все поля должны быть заполнены!");
        }
    }

    /**
     * Метод для удаления учителя.
     *
     * @param selectedRow Номер выбранной строки.
     * @throws InvalidInputException Если строка не выбрана или индекс некорректен.
     */
    public void deleteTeacher(int selectedRow) throws InvalidInputException {
        if (selectedRow != -1) {
            teacherTableModel.removeRow(selectedRow);
            teacherManager.removeTeacher(selectedRow);
        } else {
            throw new InvalidInputException("Пожалуйста, выберите строку для удаления");
        }
    }

    /**
     * Метод для добавления ученика.
     *
     * @param name        ФИО ученика.
     * @param studentClass Класс ученика.
     * @param performance Успеваемость.
     * @throws InvalidInputException Если данные некорректны.
     */
    public void addStudent(String name, String studentClass, String performance) throws InvalidInputException {
        if (name != null && !name.isEmpty() && studentClass != null && !studentClass.isEmpty() && performance != null && !performance.isEmpty()) {
            studentTableModel.addRow(new Object[]{name, studentClass, performance});
            studentManager.addStudent(new String[]{name, studentClass, performance});
        } else {
            throw new InvalidInputException("Все поля должны быть заполнены!");
        }
    }

    /**
     * Метод для редактирования ученика.
     *
     * @param row          Номер строки.
     * @param name         Новое ФИО.
     * @param studentClass Новый класс.
     * @param performance  Новая успеваемость.
     * @throws InvalidInputException Если данные некорректны.
     */
    public void editStudent(int row, String name, String studentClass, String performance) throws InvalidInputException {
        if (name != null && !name.isEmpty() && studentClass != null && !studentClass.isEmpty() && performance != null && !performance.isEmpty()) {
            studentTableModel.setValueAt(name, row, 0);
            studentTableModel.setValueAt(studentClass, row, 1);
            studentTableModel.setValueAt(performance, row, 2);
            studentManager.getStudents().set(row, new String[]{name, studentClass, performance});
        } else {
            throw new InvalidInputException("Все поля должны быть заполнены!");
        }
    }

    /**
     * Метод для удаления ученика.
     *
     * @param selectedRow Номер выбранной строки.
     * @throws InvalidInputException Если строка не выбрана или индекс некорректен.
     */
    public void deleteStudent(int selectedRow) throws InvalidInputException {
        if (selectedRow != -1) {
            studentTableModel.removeRow(selectedRow);
            studentManager.removeStudent(selectedRow);
        } else {
            throw new InvalidInputException("Пожалуйста, выберите строку для удаления");
        }
    }

    /**
     * Метод для загрузки данных из файла.
     *
     * @param file Файл для загрузки данных.
     * @throws DataLoadException Если возникает ошибка при загрузке.
     */
    public void loadDataFromFile(File file) throws DataLoadException {
        DataLoadResult result = DataManager.loadDataFromFile(file);
        teacherManager.resetTeachers(result.getTeachers());
        studentManager.resetStudents(result.getStudents());
        refreshTeacherTable();
        refreshStudentTable();
    }

    /**
     * Метод для сохранения данных в файл.
     *
     * @param file Файл для сохранения данных.
     * @throws DataSaveException Если возникает ошибка при сохранении.
     */
    public void saveDataToFile(File file) throws DataSaveException {
        DataManager.saveDataToFile(file, teacherManager.getTeachers(), studentManager.getStudents());
    }

    /**
     * Точка входа в программу. Запуск приложения.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        // Запуск интерфейса в потоке обработки событий Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().SchoolManagementSystem();
            }
        });
    }
}
