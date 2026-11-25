package com.cafemanagement.view;

import javax.swing.*;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Try setting FlatLaf Light, fallback to System Look and Feel
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}
            }

            // Set global font
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);

            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
