package com.academy.studentmanager;

import com.academy.studentmanager.data.CsvStudentRepository;
import com.academy.studentmanager.data.InMemoryStudentRepository;
import com.academy.studentmanager.model.Student;
import com.academy.studentmanager.service.StudentService;
import com.academy.studentmanager.service.StudentService.StudentOperationException;
import com.academy.studentmanager.service.StudentValidator;
import com.academy.studentmanager.ui.DashboardPanel;
import com.academy.studentmanager.ui.AppTheme;
import com.academy.studentmanager.ui.LoginPanel;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicInteger;

/** Dependency-free acceptance checks for validation, CRUD, search, and CSV persistence. */
public final class ProjectAcceptanceCheck {
    private ProjectAcceptanceCheck() { }

    public static void main(String[] args) throws Exception {
        checkLogin();
        checkValidation();
        checkDarkMode();
        checkDashboardPanel();
        checkCrudSearchAndPersistence();
        System.out.println("PASS: 5 acceptance groups completed (login; validation; theme toggle; responsive panel/add action; CRUD/search/CSV persistence).");
    }

    private static void checkLogin() throws Exception {
        AtomicInteger successfulLogins = new AtomicInteger();
        SwingUtilities.invokeAndWait(() -> {
            LoginPanel panel = new LoginPanel();
            panel.setOnSuccess(successfulLogins::incrementAndGet);
            JTextField username = (JTextField) findByName(panel, "login.username");
            JPasswordField password = (JPasswordField) findByName(panel, "login.password");
            JButton submit = (JButton) findByName(panel, "login.submit");
            require(username != null && password != null && submit != null, "login controls should be available");
            username.setText("admin");
            password.setText("incorrect");
            submit.doClick();
            require(successfulLogins.get() == 0, "incorrect credentials should not sign in");
            password.setText("admin123");
            submit.doClick();
            require(successfulLogins.get() == 1, "demo credentials should sign in exactly once");
        });
        System.out.println("PASS: incorrect login rejected; demonstration login accepted");
    }

    private static void checkValidation() {
        StudentValidator validator = new StudentValidator();
        require(validator.validate(sample("CSE-101", "Demo Student One", "Computer Science")).isEmpty(),
                "valid student should be accepted");
        Student invalid = new Student("", "", "", "2026-2028", "bad-email", "12", "");
        List<String> errors = validator.validate(invalid);
        require(errors.size() >= 6, "incomplete and malformed values should produce field errors");
        System.out.println("PASS: required fields, year, email, phone, and gender validation");
    }

    private static void checkDashboardPanel() throws Exception {
        AppTheme.setDarkMode(false);
        AppTheme.install();
        StudentService service = new StudentService(new InMemoryStudentRepository(List.of()));
        SwingUtilities.invokeAndWait(() -> {
            DashboardPanel panel = new DashboardPanel(service);
            panel.setSize(1500, 920);
            panel.doLayout();
            JSplitPane split = (JSplitPane) findType(panel, JSplitPane.class);
            require(split != null && split.getOrientation() == JSplitPane.HORIZONTAL_SPLIT,
                    "wide dashboard should place form and directory side by side");
            panel.setSize(1024, 760);
            panel.doLayout();
            JTabbedPane compactTabs = (JTabbedPane) findByName(panel, "dashboard.compactTabs");
            require(compactTabs != null && compactTabs.getTabCount() == 2,
                    "narrow dashboard should provide full-height student and directory tabs");

            ((JTextField) findByName(panel, "student.roll")).setText("CSE-901");
            ((JTextField) findByName(panel, "student.name")).setText("Demo Learner");
            ((JTextField) findByName(panel, "student.course")).setText("Computer Science");
            ((JTextField) findByName(panel, "student.year")).setText("2026-2027");
            ((JTextField) findByName(panel, "student.email")).setText("demo@example.invalid");
            ((JTextField) findByName(panel, "student.phone")).setText("0000000000");
            ((JRadioButton) findByName(panel, "student.gender.female")).setSelected(true);
            ((JButton) findByName(panel, "student.add")).doClick();
            require(service.count() == 1, "Add student button should save a valid form");
            JLabel feedback = (JLabel) findByName(panel, "student.feedback");
            require(feedback.getText().contains("was added and saved"), "successful add should show confirmation");
        });
        System.out.println("PASS: dashboard switches to compact tabs on resize and Add student saves from the panel");
    }

    private static void checkDarkMode() throws Exception {
        StudentService service = new StudentService(new InMemoryStudentRepository(List.of()));
        AppTheme.setDarkMode(false);
        AppTheme.install();
        SwingUtilities.invokeAndWait(() -> {
            ThemeHarness harness = new ThemeHarness(service);
            JButton toggle = (JButton) findByName(harness.current, "theme.darkMode");
            require(toggle != null && toggle.getText().contains("off"), "light appearance should be the default");
            toggle.doClick();
            require(AppTheme.isDarkMode() && harness.current.getBackground().equals(AppTheme.CANVAS),
                    "dark toggle should rebuild the dashboard with the dark palette");
            JButton darkToggle = (JButton) findByName(harness.current, "theme.darkMode");
            darkToggle.doClick();
            require(!AppTheme.isDarkMode() && harness.current.getBackground().equals(AppTheme.CANVAS),
                    "dark toggle should switch back to the light palette");
        });
        AppTheme.setDarkMode(false);
        AppTheme.install();
        System.out.println("PASS: dark mode switches to dark palette and back");
    }

    private static void checkCrudSearchAndPersistence() throws Exception {
        Path directory = Files.createTempDirectory("student-record-check-");
        Path file = directory.resolve("students.csv");
        try {
            StudentService service = new StudentService(new CsvStudentRepository(file));
            Student first = sample("CSE-101", "Demo Student One", "Computer Science, AI");
            Student second = sample("ECE-102", "Demo Student Two", "Electronics");

            service.add(first);
            service.add(second);
            require(service.count() == 2, "both records should be added");
            require(service.search("cse-101").size() == 1, "search by roll number should match");
            require(service.search("DEMO STUDENT TWO").size() == 1, "case-insensitive search by name should match");
            require(service.search("electronics").size() == 1, "search by course should match");

            boolean duplicateRejected = false;
            try {
                service.add(sample("cse-101", "Another Student", "Computer Science"));
            } catch (StudentOperationException expected) {
                duplicateRejected = expected.getMessage().contains("already exists");
            }
            require(duplicateRejected && service.count() == 2, "duplicate roll number must be rejected without changing data");

            Student replacement = new Student("CSE-101", "Demo Student One", "Computer Science, AI",
                    "2026-2027", "student1@example.invalid", "0000000001", "Female");
            service.update("CSE-101", replacement);
            StudentService reloaded = new StudentService(new CsvStudentRepository(file));
            require(reloaded.count() == 2, "CSV reload should restore saved records");
            require(reloaded.search("Demo Student One").get(0).course().equals("Computer Science, AI"),
                    "quoted CSV field should round-trip commas");

            reloaded.delete("ECE-102");
            StudentService afterDelete = new StudentService(new CsvStudentRepository(file));
            require(afterDelete.count() == 1 && afterDelete.search("ECE-102").isEmpty(),
                    "delete should be durable after reload");
            System.out.println("PASS: add, list, update, delete, three search fields, duplicate guard, and CSV reload");
        } finally {
            Files.deleteIfExists(file.resolveSibling(file.getFileName() + ".tmp"));
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }

    private static Student sample(String roll, String name, String course) {
        return new Student(roll, name, course, "2026-2027", "student@example.invalid", "0000000000", "Female");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
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

    private static Component findType(Container parent, Class<?> type) {
        for (Component child : parent.getComponents()) {
            if (type.isInstance(child)) {
                return child;
            }
            if (child instanceof Container nested) {
                Component found = findType(nested, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static final class ThemeHarness implements Consumer<Boolean> {
        private final StudentService service;
        private DashboardPanel current;

        private ThemeHarness(StudentService service) {
            this.service = service;
            current = new DashboardPanel(service, false, this);
        }

        @Override
        public void accept(Boolean darkMode) {
            AppTheme.setDarkMode(darkMode);
            AppTheme.install();
            current = new DashboardPanel(service, darkMode, this);
        }
    }
}
