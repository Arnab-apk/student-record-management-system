package com.academy.studentmanager.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Login form separated from its window so it can also be rendered for the report. */
public final class LoginPanel extends JPanel {
    public static final String DEMO_USERNAME = "admin";
    public static final String DEMO_PASSWORD = "admin123";

    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JLabel message = new JLabel(" ");
    private Runnable onSuccess = () -> { };

    public LoginPanel() {
        super(new GridBagLayout());
        setBackground(AppTheme.CANVAS);
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 234, 254)),
                BorderFactory.createEmptyBorder(32, 36, 30, 36)));
        card.setPreferredSize(new Dimension(460, 500));
        card.add(createBrand(), BorderLayout.NORTH);
        card.add(createForm(), BorderLayout.CENTER);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(20, 20, 20, 20);
        add(card, constraints);
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess == null ? () -> { } : onSuccess;
    }

    private JPanel createBrand() {
        JPanel brand = new JPanel(new BorderLayout(0, 8));
        brand.setBackground(AppTheme.SURFACE);
        JLabel mark = new JLabel("SR", SwingConstants.CENTER);
        mark.setOpaque(true);
        mark.setBackground(AppTheme.BLUE);
        mark.setForeground(Color.WHITE);
        mark.setFont(AppTheme.HEADING);
        mark.setPreferredSize(new Dimension(58, 58));
        JLabel title = new JLabel("Student Records");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 25));
        title.setForeground(AppTheme.NAVY);
        JLabel subtitle = new JLabel("Administrator sign in");
        subtitle.setForeground(AppTheme.MUTED);
        JPanel titleBlock = new JPanel(new BorderLayout(0, 4));
        titleBlock.setBackground(AppTheme.SURFACE);
        titleBlock.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.CENTER);
        brand.add(mark, BorderLayout.WEST);
        brand.add(titleBlock, BorderLayout.CENTER);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        return brand;
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.SURFACE);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 7, 0);
        form.add(label("Username"), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 18, 0);
        AppTheme.styleInput(username);
        username.setName("login.username");
        form.add(username, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 7, 0);
        form.add(label("Password"), c);
        c.gridy++;
        c.insets = new Insets(0, 0, 10, 0);
        AppTheme.styleInput(password);
        password.setName("login.password");
        form.add(password, c);

        message.setForeground(AppTheme.ERROR);
        message.setFont(AppTheme.BODY);
        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);
        form.add(message, c);

        JButton signIn = new JButton("Sign in");
        AppTheme.styleButton(signIn, AppTheme.BLUE, Color.WHITE);
        signIn.setName("login.submit");
        signIn.setPreferredSize(new Dimension(140, 42));
        signIn.addActionListener(event -> signIn());
        password.addActionListener(event -> signIn());
        c.gridy++;
        c.insets = new Insets(0, 0, 18, 0);
        form.add(signIn, c);

        JLabel hint = new JLabel("Demo access: admin  /  admin123");
        hint.setForeground(AppTheme.MUTED);
        hint.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        c.gridy++;
        c.insets = InsetsDialog.NONE;
        form.add(hint, c);
        return form;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.BODY_BOLD);
        return label;
    }

    private void signIn() {
        String enteredUsername = username.getText().trim();
        String enteredPassword = new String(password.getPassword());
        if (DEMO_USERNAME.equals(enteredUsername) && DEMO_PASSWORD.equals(enteredPassword)) {
            message.setText(" ");
            onSuccess.run();
        } else {
            message.setText("Username or password is incorrect.");
            password.selectAll();
            password.requestFocusInWindow();
        }
    }

    private static final class InsetsDialog {
        private static final Insets NONE = new Insets(0, 0, 0, 0);
    }
}
