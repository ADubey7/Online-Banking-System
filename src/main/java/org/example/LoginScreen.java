package org.example;

import org.example.dao.AccountDAO;
import org.example.dao.UserDAO;
import org.example.model.Account;
import org.example.model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginScreen() {
        setTitle("Online Banking System - Login");
        setSize(300, 250);  // Increased height to accommodate the new button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 20, 80, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 100, 80, 25);
        panel.add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setBounds(100, 140, 80, 25);
        panel.add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                UserDAO userDAO = new UserDAO();
                boolean isValid = userDAO.validateUser(username, password);
                if (isValid) {
                    JOptionPane.showMessageDialog(LoginScreen.this, "Login successful!");
                    User user = userDAO.getUserByUsername(username);
                    AccountDAO accountDAO = new AccountDAO();
                    Account account = accountDAO.getAccountByUserId(user.getUserId());
                    if (account != null) {
                        new MainScreen(user.getUserId(), account.getAccountId()).setVisible(true);
                        dispose(); // Close the login screen
                    } else {
                        JOptionPane.showMessageDialog(LoginScreen.this, "Account not found.");
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginScreen.this, "Invalid username or password.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegistrationScreen().setVisible(true);
                dispose(); // Close the login screen
            }
        });

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}