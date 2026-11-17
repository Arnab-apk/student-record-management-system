package com.academy.studentmanager;

import com.academy.studentmanager.data.InMemoryStudentRepository;
import com.academy.studentmanager.model.Student;
import com.academy.studentmanager.service.StudentService;
import com.academy.studentmanager.ui.AppTheme;
import com.academy.studentmanager.ui.DashboardPanel;
import com.academy.studentmanager.ui.LoginPanel;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Paints the actual Swing panels into PNGs for the technical report. */
public final class PreviewRenderer {
    private PreviewRenderer() { }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        AppTheme.setDarkMode(false);
        AppTheme.install();
        Path output = Path.of("screenshots");
        Files.createDirectories(output);
        SwingUtilities.invokeAndWait(() -> {
            try {
                render(new LoginPanel(), 1200, 820, output.resolve("login.png"));
                List<Student> demoStudents = List.of(
                        new Student("CSE-101", "Demo Student One", "Computer Science", "2026-2027", "student1@example.invalid", "0000000001", "Female"),
                        new Student("CSE-114", "Demo Student Two", "Computer Science", "2026-2027", "student2@example.invalid", "0000000002", "Male"),
                        new Student("ECE-205", "Demo Student Three", "Electronics", "2026-2027", "student3@example.invalid", "0000000003", "Female"),
                        new Student("BCA-033", "Demo Student Four", "Computer Applications", "2025-2026", "student4@example.invalid", "0000000004", "Other"));
                StudentService demoService = new StudentService(new InMemoryStudentRepository(demoStudents));
                render(new DashboardPanel(demoService, false, null), 1600, 1000, output.resolve("dashboard.png"));
                AppTheme.setDarkMode(false);
                AppTheme.install();
                DashboardPanel compactPanel = new DashboardPanel(demoService, false, null);
                render(compactPanel, 1024, 960, output.resolve("dashboard-compact.png"));
                ((JTabbedPane) findByName(compactPanel, "dashboard.compactTabs")).setSelectedIndex(1);
                render(compactPanel, 1024, 960, output.resolve("dashboard-compact-directory.png"));
                AppTheme.setDarkMode(true);
                AppTheme.install();
                render(new DashboardPanel(demoService, true, null), 1600, 1000, output.resolve("dashboard-dark.png"));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
        System.out.println("Rendered login, wide, compact, compact-directory, and dark dashboard screenshots.");
    }

    private static void render(JComponent component, int width, int height, Path output) throws Exception {
        component.setSize(width, height);
        layoutTree(component);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        component.paint(graphics);
        graphics.dispose();
        ImageIO.write(image, "png", output.toFile());
    }

    private static void layoutTree(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container nested) {
                layoutTree(nested);
            }
        }
    }

    private static Component findByName(Container parent, String name) {
        for (Component child : parent.getComponents()) {
            if (name.equals(child.getName())) {
                return child;
            }
            if (child instanceof Container nested) {
                Component found = findByName(nested, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
