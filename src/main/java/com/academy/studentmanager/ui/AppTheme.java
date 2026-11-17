package com.academy.studentmanager.ui;

import javax.swing.BorderFactory;
import javax.swing.AbstractButton;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/** Shared colors and control styles for a clear, high-contrast desktop layout. */
public final class AppTheme {
    public static Color NAVY;
    public static Color BLUE;
    public static Color BLUE_DARK;
    public static Color GREEN;
    public static Color TEAL;
    public static Color CANVAS;
    public static Color SURFACE;
    public static Color TEXT;
    public static Color HEADING_TEXT;
    public static Color MUTED;
    public static Color HEADER_SUBTITLE;
    public static Color BORDER;
    public static Color ERROR;
    public static Color SUBTLE;
    public static Color BADGE;
    public static Color BADGE_TEXT;
    public static Color TABLE_HEADER;
    public static Color TABLE_SELECTION;
    public static Color TABLE_GRID;
    public static Color DANGER_SURFACE;
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font HEADING = new Font("Segoe UI", Font.BOLD, 24);
    private static boolean darkMode;

    static {
        setDarkMode(false);
    }

    private AppTheme() { }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
        if (enabled) {
            NAVY = new Color(8, 15, 28);
            BLUE = new Color(3, 105, 161);
            BLUE_DARK = new Color(7, 89, 133);
            GREEN = new Color(21, 128, 61);
            TEAL = new Color(15, 118, 110);
            CANVAS = new Color(11, 18, 32);
            SURFACE = new Color(17, 27, 43);
            TEXT = new Color(248, 250, 252);
            HEADING_TEXT = TEXT;
            MUTED = new Color(203, 213, 225);
            HEADER_SUBTITLE = new Color(203, 213, 225);
            BORDER = new Color(51, 65, 85);
            ERROR = new Color(252, 165, 165);
            SUBTLE = new Color(31, 41, 55);
            BADGE = new Color(30, 41, 59);
            BADGE_TEXT = new Color(241, 245, 249);
            TABLE_HEADER = new Color(30, 41, 59);
            TABLE_SELECTION = new Color(22, 78, 99);
            TABLE_GRID = new Color(51, 65, 85);
            DANGER_SURFACE = new Color(69, 26, 26);
        } else {
            NAVY = new Color(18, 44, 77);
            BLUE = new Color(2, 132, 199);
            BLUE_DARK = new Color(3, 105, 161);
            GREEN = new Color(22, 163, 74);
            TEAL = new Color(13, 148, 136);
            CANVAS = new Color(240, 249, 255);
            SURFACE = Color.WHITE;
            TEXT = new Color(15, 23, 42);
            HEADING_TEXT = NAVY;
            MUTED = new Color(71, 85, 105);
            HEADER_SUBTITLE = new Color(203, 213, 225);
            BORDER = new Color(203, 213, 225);
            ERROR = new Color(185, 28, 28);
            SUBTLE = new Color(241, 245, 249);
            BADGE = new Color(30, 64, 107);
            BADGE_TEXT = new Color(224, 242, 254);
            TABLE_HEADER = new Color(241, 245, 249);
            TABLE_SELECTION = new Color(219, 234, 254);
            TABLE_GRID = new Color(226, 232, 240);
            DANGER_SURFACE = new Color(254, 226, 226);
        }
    }

    public static void install() {
        UIManager.put("Panel.background", CANVAS);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.font", BODY);
        UIManager.put("TextField.font", BODY);
        UIManager.put("PasswordField.font", BODY);
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("Button.font", BODY_BOLD);
        UIManager.put("Table.font", BODY);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.background", SURFACE);
        UIManager.put("Table.selectionBackground", TABLE_SELECTION);
        UIManager.put("Table.selectionForeground", darkMode ? TEXT : NAVY);
        UIManager.put("Table.gridColor", TABLE_GRID);
        UIManager.put("TableHeader.font", BODY_BOLD);
        UIManager.put("TableHeader.background", TABLE_HEADER);
        UIManager.put("TableHeader.foreground", darkMode ? TEXT : MUTED);
        UIManager.put("TableHeader.focusCellBackground", TABLE_GRID);
        UIManager.put("Component.focusColor", BLUE);
    }

    public static void styleInput(javax.swing.JComponent component) {
        component.setFont(BODY);
        component.setForeground(TEXT);
        component.setBackground(SURFACE);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        component.setPreferredSize(new java.awt.Dimension(180, 38));
    }

    public static void styleButton(AbstractButton button, Color background, Color foreground) {
        button.setFont(BODY_BOLD);
        button.setForeground(foreground);
        button.setBackground(background);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker()),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        button.setFocusPainted(true);
        button.setFocusable(true);
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }
}
