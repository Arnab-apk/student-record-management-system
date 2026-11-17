package com.academy.studentmanager;

import com.academy.studentmanager.data.CsvStudentRepository;
import com.academy.studentmanager.service.StudentService;
import com.academy.studentmanager.ui.AppTheme;
import com.academy.studentmanager.ui.DashboardFrame;
import com.academy.studentmanager.ui.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.IOException;
import java.nio.file.Path;

/** Application entry point. Run from the project root with {@code run.bat}. */
public final class StudentManagementApp {
    private StudentManagementApp() { }

    public static void main(String[] args) {
        Path dataFile = Path.of(System.getProperty("student.data.path", "data/students.csv"));
        final StudentService service;
        try {
            service = new StudentService(new CsvStudentRepository(dataFile));
        } catch (IOException exception) {
            System.err.println("Could not load student records: " + exception.getMessage());
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "Could not load student records. Check the CSV file and folder permissions.\n\n"
                            + exception.getMessage(),
                    "Student data could not be opened", JOptionPane.ERROR_MESSAGE));
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Swing's cross-platform look and feel is a safe fallback.
            }
            AppTheme.install();
            final LoginFrame[] loginHolder = new LoginFrame[1];
            LoginFrame login = new LoginFrame(() -> {
                new DashboardFrame(service).setVisible(true);
                loginHolder[0].dispose();
            });
            loginHolder[0] = login;
            login.setVisible(true);
        });
    }
}
