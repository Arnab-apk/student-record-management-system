package com.academy.studentmanager.ui;

import com.academy.studentmanager.model.Student;
import com.academy.studentmanager.service.StudentService;
import com.academy.studentmanager.service.StudentService.StudentOperationException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/** Main student management screen. Kept as a panel for app use and screenshot rendering. */
public final class DashboardPanel extends JPanel {
    private final StudentService service;
    private final StudentTableModel tableModel = new StudentTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField searchField = new JTextField();
    private final JTextField rollField = input("student.roll");
    private final JTextField nameField = input("student.name");
    private final JTextField courseField = input("student.course");
    private final JTextField yearField = input("student.year");
    private final JTextField emailField = input("student.email");
    private final JTextField phoneField = input("student.phone");
    private final JRadioButton female = radio("Female");
    private final JRadioButton male = radio("Male");
    private final JRadioButton other = radio("Other");
    private final JLabel totalValue = statValue();
    private final JLabel coursesValue = statValue();
    private final JLabel yearsValue = statValue();
    private final JLabel feedback = new JLabel("Ready. Select a record or enter student details.");
    private final JButton addButton = button("Add student", AppTheme.BLUE, Color.WHITE);
    private final JButton updateButton = button("Save changes", AppTheme.TEAL, Color.WHITE);
    private final JButton deleteButton = button("Delete", AppTheme.DANGER_SURFACE, AppTheme.ERROR);
    private final List<JTextField> formFields = List.of(rollField, nameField, courseField, yearField, emailField, phoneField);

    private List<Student> displayedStudents = List.of();
    private String selectedRollNumber;
    private JSplitPane workspaceSplit;
    private JPanel formCard;
    private JPanel recordsCard;
    private JPanel responsiveContent;
    private CardLayout responsiveLayout;
    private JTabbedPane compactTabs;
    private boolean compactMode;
    private final boolean darkMode;
    private final Consumer<Boolean> themeChange;

    public DashboardPanel(StudentService service) {
        this(service, AppTheme.isDarkMode(), null);
    }

    public DashboardPanel(StudentService service, boolean darkMode, Consumer<Boolean> themeChange) {
        super(new BorderLayout(0, 18));
        this.service = service;
        this.darkMode = darkMode;
        this.themeChange = themeChange;
        setBackground(AppTheme.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        add(createHeader(), BorderLayout.NORTH);
        add(createWorkspace(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        configureTable();
        configureActions();
        refreshTable("");
        setFeedback("Ready. Select a record or enter student details.", false);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBackground(AppTheme.NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(17, 20, 17, 20));
        JLabel mark = new JLabel("SR", SwingConstants.CENTER);
        mark.setOpaque(true);
        mark.setBackground(AppTheme.BLUE);
        mark.setForeground(Color.WHITE);
        mark.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        mark.setPreferredSize(new Dimension(48, 48));
        JLabel title = new JLabel("Student Record Management");
        title.setForeground(Color.WHITE);
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        JLabel subtitle = new JLabel("Academic records, organized in one place");
        subtitle.setForeground(AppTheme.HEADER_SUBTITLE);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);
        JLabel admin = new JLabel("ADMINISTRATOR");
        admin.setOpaque(true);
        admin.setBackground(AppTheme.BADGE);
        admin.setForeground(AppTheme.BADGE_TEXT);
        admin.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
        admin.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        header.add(mark, BorderLayout.WEST);
        header.add(titles, BorderLayout.CENTER);
        JButton themeToggle = button(darkMode ? "Dark mode: on" : "Dark mode: off", AppTheme.BADGE, AppTheme.BADGE_TEXT);
        themeToggle.setName("theme.darkMode");
        themeToggle.setToolTipText("Switch between light and dark appearance");
        themeToggle.getAccessibleContext().setAccessibleName(
                darkMode ? "Dark mode is on. Activate to use light mode." : "Dark mode is off. Activate to use dark mode.");
        themeToggle.addActionListener(event -> {
            if (this.themeChange != null) {
                this.themeChange.accept(!this.darkMode);
            }
        });
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.TRAILING, 8, 0));
        headerActions.setOpaque(false);
        headerActions.add(themeToggle);
        headerActions.add(admin);
        header.add(headerActions, BorderLayout.EAST);
        return header;
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(0, 16));
        workspace.setOpaque(false);
        workspace.add(createStats(), BorderLayout.NORTH);
        formCard = createFormCard();
        recordsCard = createRecordsCard();
        workspaceSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formCard, recordsCard);
        workspaceSplit.setResizeWeight(0.32);
        workspaceSplit.setDividerSize(12);
        workspaceSplit.setBorder(null);
        workspaceSplit.setOpaque(false);
        workspaceSplit.setContinuousLayout(true);
        compactTabs = new JTabbedPane();
        compactTabs.setName("dashboard.compactTabs");
        compactTabs.setFont(AppTheme.BODY_BOLD);
        compactTabs.setBackground(AppTheme.SURFACE);
        compactTabs.setForeground(AppTheme.TEXT);
        responsiveLayout = new CardLayout();
        responsiveContent = new JPanel(responsiveLayout);
        responsiveContent.setOpaque(false);
        responsiveContent.add(workspaceSplit, "wide");
        responsiveContent.add(compactTabs, "compact");
        workspace.add(responsiveContent, BorderLayout.CENTER);
        return workspace;
    }

    @Override
    public void doLayout() {
        boolean compact = getWidth() < 1180;
        boolean layoutChanged = compact != compactMode;
        if (layoutChanged) {
            compactMode = compact;
            if (compact) {
                workspaceSplit.setLeftComponent(null);
                workspaceSplit.setRightComponent(null);
                compactTabs.addTab("Student details", formCard);
                compactTabs.addTab("Directory", recordsCard);
                responsiveLayout.show(responsiveContent, "compact");
            } else {
                compactTabs.removeAll();
                workspaceSplit.setLeftComponent(formCard);
                workspaceSplit.setRightComponent(recordsCard);
                responsiveLayout.show(responsiveContent, "wide");
            }
        }
        super.doLayout();
    }

    private JPanel createStats() {
        JPanel row = new JPanel(new GridLayout(1, 3, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(200, 82));
        row.add(statCard("STUDENTS", totalValue, AppTheme.BLUE));
        row.add(statCard("COURSES", coursesValue, AppTheme.TEAL));
        row.add(statCard("ACADEMIC YEARS", yearsValue, AppTheme.GREEN));
        return row;
    }

    private JPanel statCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(11, 16, 11, 16)));
        JPanel marker = new JPanel();
        marker.setBackground(accent);
        marker.setPreferredSize(new Dimension(4, 42));
        JLabel caption = new JLabel(title);
        caption.setForeground(AppTheme.MUTED);
        caption.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 4));
        texts.setOpaque(false);
        texts.add(caption);
        texts.add(value);
        card.add(marker, BorderLayout.WEST);
        card.add(texts, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFormCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 10));
        JPanel content = new JPanel(new GridLayout(0, 1, 0, 7));
        content.setOpaque(false);
        content.add(sectionHeading("Student details", "All 7 fields are required. Scroll to reach lower fields."));
        content.add(formRow("Roll number", rollField));
        content.add(formRow("Full name", nameField));
        content.add(formRow("Course", courseField));
        content.add(formRow("Academic year", yearField));
        content.add(formRow("Email address", emailField));
        content.add(formRow("Phone number", phoneField));
        content.add(genderRow());

        JPanel actions = new JPanel(new GridLayout(0, 1, 0, 8));
        actions.setOpaque(false);
        addButton.setName("student.add");
        updateButton.setName("student.update");
        deleteButton.setName("student.delete");
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        actions.add(addButton);
        actions.add(updateButton);
        JPanel lower = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        lower.setOpaque(false);
        lower.add(deleteButton);
        JButton clear = button("Clear form", AppTheme.SUBTLE, AppTheme.TEXT);
        clear.setName("student.clear");
        clear.addActionListener(event -> clearForm());
        lower.add(clear);
        actions.add(lower);

        JScrollPane formScroll = new JScrollPane(content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        card.setMinimumSize(new Dimension(340, 420));
        return card;
    }

    private JPanel createRecordsCard() {
        JPanel card = card();
        card.setLayout(new BorderLayout(0, 12));
        card.add(sectionHeading("Student directory", "Search by roll number, name, or course."), BorderLayout.NORTH);

        JPanel search = new JPanel(new BorderLayout(8, 0));
        search.setOpaque(false);
        AppTheme.styleInput(searchField);
        searchField.setName("student.search");
        searchField.setToolTipText("Search by roll number, name, or course");
        searchField.getAccessibleContext().setAccessibleName("Search students by roll number, name, or course");
        JButton clearSearch = button("Clear", AppTheme.SUBTLE, AppTheme.TEXT);
        clearSearch.addActionListener(event -> searchField.setText(""));
        search.add(searchField, BorderLayout.CENTER);
        search.add(clearSearch, BorderLayout.EAST);

        JPanel tableArea = new JPanel(new BorderLayout(0, 8));
        tableArea.setOpaque(false);
        tableArea.add(search, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.getViewport().setBackground(AppTheme.SURFACE);
        scroll.setColumnHeaderView(table.getTableHeader());
        tableArea.add(scroll, BorderLayout.CENTER);
        JLabel tableHint = new JLabel("Select a row to edit or delete a student.");
        tableHint.setForeground(AppTheme.MUTED);
        tableHint.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        tableArea.add(tableHint, BorderLayout.SOUTH);
        card.add(tableArea, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        feedback.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        feedback.setName("student.feedback");
        footer.add(feedback, BorderLayout.WEST);
        JLabel pathNote = new JLabel("Local CSV storage");
        pathNote.setForeground(AppTheme.MUTED);
        pathNote.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        footer.add(pathNote, BorderLayout.EAST);
        return footer;
    }

    private JPanel formRow(String title, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        label.setLabelFor(field);
        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel genderRow() {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        JLabel label = new JLabel("Gender");
        label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 1));
        options.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(female);
        group.add(male);
        group.add(other);
        options.add(female);
        options.add(male);
        options.add(other);
        row.add(label, BorderLayout.NORTH);
        row.add(options, BorderLayout.CENTER);
        return row;
    }

    private JPanel sectionHeading(String title, String subtitle) {
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 3));
        heading.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        titleLabel.setForeground(AppTheme.HEADING_TEXT);
        JLabel description = new JLabel(subtitle);
        description.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        description.setForeground(AppTheme.MUTED);
        heading.add(titleLabel);
        heading.add(description);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return heading;
    }

    private JPanel card() {
        JPanel card = new JPanel();
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(15, 16, 15, 16)));
        return card;
    }

    private void configureTable() {
        table.setName("student.table");
        table.getAccessibleContext().setAccessibleName("Student records table");
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(32);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setBackground(AppTheme.SURFACE);
        table.setForeground(AppTheme.TEXT);
        table.setGridColor(AppTheme.TABLE_GRID);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(AppTheme.TABLE_SELECTION);
        table.setSelectionForeground(darkMode ? AppTheme.TEXT : AppTheme.NAVY);
        table.getTableHeader().setPreferredSize(new Dimension(100, 38));
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedRow();
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(94);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(105);
        table.getColumnModel().getColumn(4).setPreferredWidth(170);
        table.getColumnModel().getColumn(5).setPreferredWidth(115);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
    }

    private void configureActions() {
        addButton.addActionListener(event -> addStudent());
        updateButton.addActionListener(event -> updateStudent());
        deleteButton.addActionListener(event -> deleteStudent());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { applySearch(); }
            @Override public void removeUpdate(DocumentEvent event) { applySearch(); }
            @Override public void changedUpdate(DocumentEvent event) { applySearch(); }
        });
    }

    private void applySearch() {
        String query = searchField.getText();
        refreshTable(query);
        if (query.isBlank()) {
            setFeedback("Showing all students.", false);
        } else {
            setFeedback(displayedStudents.size() + " matching record(s).", false);
        }
    }

    private void refreshTable(String query) {
        displayedStudents = service.search(query);
        tableModel.setStudents(displayedStudents);
        totalValue.setText(Integer.toString(service.count()));
        coursesValue.setText(Integer.toString(service.courseCount()));
        yearsValue.setText(Integer.toString(service.academicYearCount()));
        if (service.count() == 0) {
            tableModel.setEmptyMessage("No students yet. Add a record to get started.");
        } else if (displayedStudents.isEmpty()) {
            tableModel.setEmptyMessage("No matching students found.");
        } else {
            tableModel.setEmptyMessage("");
        }
    }

    private void addStudent() {
        Student student = readForm();
        List<String> errors = service.validate(student);
        if (!errors.isEmpty()) {
            setFeedback(errors.get(0), true);
            return;
        }
        try {
            service.add(student);
            refreshTable(searchField.getText());
            clearForm();
            setFeedback("Student " + student.rollNumber() + " was added and saved.", false);
        } catch (StudentOperationException | IOException exception) {
            setFeedback(exception.getMessage(), true);
        }
    }

    private void updateStudent() {
        if (selectedRollNumber == null) {
            setFeedback("Select a student row before saving changes.", true);
            return;
        }
        Student replacement = readForm();
        List<String> errors = service.validate(replacement);
        if (!errors.isEmpty()) {
            setFeedback(errors.get(0), true);
            return;
        }
        try {
            service.update(selectedRollNumber, replacement);
            refreshTable(searchField.getText());
            clearForm();
            setFeedback("Student " + replacement.rollNumber() + " was updated and saved.", false);
        } catch (StudentOperationException | IOException exception) {
            setFeedback(exception.getMessage(), true);
        }
    }

    private void deleteStudent() {
        if (selectedRollNumber == null) {
            setFeedback("Select a student row before deleting.", true);
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete student " + selectedRollNumber + "? This action cannot be undone.",
                "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            String deleted = selectedRollNumber;
            service.delete(deleted);
            refreshTable(searchField.getText());
            clearForm();
            setFeedback("Student " + deleted + " was deleted.", false);
        } catch (StudentOperationException | IOException exception) {
            setFeedback(exception.getMessage(), true);
        }
    }

    private Student readForm() {
        String gender = female.isSelected() ? "Female" : male.isSelected() ? "Male" : other.isSelected() ? "Other" : "";
        return new Student(rollField.getText(), nameField.getText(), courseField.getText(), yearField.getText(),
                emailField.getText(), phoneField.getText(), gender);
    }

    private void loadSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= displayedStudents.size()) {
            return;
        }
        Student student = displayedStudents.get(modelRow);
        selectedRollNumber = student.rollNumber();
        rollField.setText(student.rollNumber());
        nameField.setText(student.name());
        courseField.setText(student.course());
        yearField.setText(student.academicYear());
        emailField.setText(student.email());
        phoneField.setText(student.phone());
        female.setSelected("Female".equals(student.gender()));
        male.setSelected("Male".equals(student.gender()));
        other.setSelected("Other".equals(student.gender()));
        addButton.setEnabled(false);
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
        setFeedback("Editing " + student.rollNumber() + ". Save changes or clear the form.", false);
    }

    private void clearForm() {
        selectedRollNumber = null;
        for (JTextField field : formFields) {
            field.setText("");
        }
        female.setSelected(false);
        male.setSelected(false);
        other.setSelected(false);
        table.clearSelection();
        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        setFeedback("Form cleared.", false);
        rollField.requestFocusInWindow();
    }

    private void setFeedback(String text, boolean isError) {
        feedback.setText(text == null || text.isBlank() ? " " : text);
        feedback.setForeground(isError ? AppTheme.ERROR : AppTheme.MUTED);
        feedback.setToolTipText(text);
    }

    private static JTextField input(String name) {
        JTextField field = new JTextField();
        field.setName(name);
        AppTheme.styleInput(field);
        return field;
    }

    private static JRadioButton radio(String text) {
        JRadioButton radio = new JRadioButton(text);
        radio.setName("student.gender." + text.toLowerCase(java.util.Locale.ROOT));
        radio.setOpaque(false);
        radio.setForeground(AppTheme.TEXT);
        radio.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        return radio;
    }

    private static JLabel statValue() {
        JLabel value = new JLabel("0");
        value.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        value.setForeground(AppTheme.HEADING_TEXT);
        return value;
    }

    private static JButton button(String title, Color background, Color foreground) {
        JButton button = new JButton(title);
        AppTheme.styleButton(button, background, foreground);
        return button;
    }

    private static final class StudentTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {"Roll number", "Name", "Course", "Academic year", "Email", "Phone", "Gender"};
        private List<Student> students = List.of();
        private String emptyMessage = "";

        void setStudents(List<Student> students) {
            this.students = List.copyOf(students);
            fireTableDataChanged();
        }

        void setEmptyMessage(String emptyMessage) {
            this.emptyMessage = emptyMessage;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return students.isEmpty() && !emptyMessage.isEmpty() ? 1 : students.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int column) { return COLUMNS[column]; }
        @Override public boolean isCellEditable(int row, int column) { return false; }
        @Override public Object getValueAt(int row, int column) {
            if (students.isEmpty() && !emptyMessage.isEmpty()) {
                return column == 0 ? emptyMessage : "";
            }
            Student student = students.get(row);
            return switch (column) {
                case 0 -> student.rollNumber();
                case 1 -> student.name();
                case 2 -> student.course();
                case 3 -> student.academicYear();
                case 4 -> student.email();
                case 5 -> student.phone();
                case 6 -> student.gender();
                default -> "";
            };
        }
    }
}
