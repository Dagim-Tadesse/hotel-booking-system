package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    public LoginPanel(CardLayout layout, JPanel container) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Notice at the top
        JLabel noticeLabel = new JLabel("<html><div style='text-align: center;'>"
                + "The full system is only allowed to the hotel administrators and managers.<br>"
                + "Login functionality is mock/hardcoded for demonstration purposes.<br>"
                + "</div></html>");
        noticeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(noticeLabel, gbc);

        // Title
        JLabel titleLabel = new JLabel("Hotel Management System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridy = 1;
        add(titleLabel, gbc);

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(userLabel, gbc);

        // Username Field
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        add(userField, gbc);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passLabel, gbc);

        // Password Field
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passField, gbc);

        // Hint
        JLabel hintLabel = new JLabel("Hint: Username: admin | Password: 123456 ");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        hintLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(hintLabel, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        gbc.gridy = 5;
        add(loginButton, gbc);

        // Status Message
        JLabel statusLabel = new JLabel();
        gbc.gridy = 6;
        add(statusLabel, gbc);

        // Guest Chat Button
        JButton guestChatButton = new JButton("Continue as Guest (Chat)");
        gbc.gridy = 7;
        add(guestChatButton, gbc);

        // Action: Login
        ActionListener loginAction = e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.equals("admin") && password.equals("123456")) {
                layout.show(container, "MainMenu");
            } else {
                statusLabel.setText("Invalid username or password");
                statusLabel.setForeground(Color.RED);
            }
        };

        loginButton.addActionListener(loginAction);
        passField.addActionListener(loginAction); // Enter key submits form

        // Action: Guest Chat
        guestChatButton.addActionListener(e -> {
            JFrame chatFrame = new JFrame("Guest Chat");
            chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chatFrame.add(new ChatPanel());
            chatFrame.pack();
            chatFrame.setSize(700, 400);
            chatFrame.setLocationRelativeTo(null);
            chatFrame.setVisible(true);
        });
    }
}
