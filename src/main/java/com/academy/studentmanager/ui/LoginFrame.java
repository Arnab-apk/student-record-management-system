package com.academy.studentmanager.ui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;

public final class LoginFrame extends JFrame {
    public LoginFrame(Runnable onSuccess) {
        super("Student Record Management System | Sign in");
        LoginPanel panel = new LoginPanel();
        panel.setOnSuccess(onSuccess);
        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(560, 620));
        setSize(700, 720);
        setLocationRelativeTo(null);
    }
}
