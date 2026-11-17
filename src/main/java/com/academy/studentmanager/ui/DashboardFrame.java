package com.academy.studentmanager.ui;

import com.academy.studentmanager.service.StudentService;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;

public final class DashboardFrame extends JFrame {
    private final StudentService service;

    public DashboardFrame(StudentService service) {
        super("Student Record Management System");
        this.service = service;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 620));
        setSize(1440, 920);
        setLocationRelativeTo(null);
        showDashboard(false);
    }

    private void showDashboard(boolean darkMode) {
        AppTheme.setDarkMode(darkMode);
        AppTheme.install();
        setContentPane(new DashboardPanel(service, darkMode, this::showDashboard));
        revalidate();
        repaint();
    }
}
