package org.example;

import org.example.dao.AccountDAO;
import org.example.dao.TransactionDAO;
import org.example.model.Account;
import org.example.model.Transaction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainScreen extends JFrame {
    private int loggedInUserId;
    private int loggedInAccountId;
    private JButton viewAccountButton;
    private JButton transferFundsButton;
    private JButton viewTransactionsButton;
    private JButton logoutButton;

    public MainScreen(int loggedInUserId, int loggedInAccountId) {
        this.loggedInUserId = loggedInUserId;
        this.loggedInAccountId = loggedInAccountId;
        setTitle("Online Banking System - Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        viewAccountButton = new JButton("View Account Details");
        viewAccountButton.setBounds(50, 30, 300, 30);
        panel.add(viewAccountButton);

        transferFundsButton = new JButton("Transfer Funds");
        transferFundsButton.setBounds(50, 80, 300, 30);
        panel.add(transferFundsButton);

        viewTransactionsButton = new JButton("View Transaction History");
        viewTransactionsButton.setBounds(50, 130, 300, 30);
        panel.add(viewTransactionsButton);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(50, 180, 300, 30);
        panel.add(logoutButton);

        // Add action listeners
        viewAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the logged-in user's account details
                AccountDAO accountDAO = new AccountDAO();
                Account account = accountDAO.getAccountByUserId(loggedInUserId);
                if (account != null) {
                    JOptionPane.showMessageDialog(MainScreen.this,
                            "Account Number: " + account.getAccountNumber() + "\n" +
                                    "Balance: " + account.getBalance());
                } else {
                    JOptionPane.showMessageDialog(MainScreen.this, "Account not found.");
                }
            }
        });

        transferFundsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a transfer funds dialog
                String toAccountNumber = JOptionPane.showInputDialog(MainScreen.this, "Enter the recipient's account number:");
                String amountStr = JOptionPane.showInputDialog(MainScreen.this, "Enter the amount to transfer:");
                if (toAccountNumber != null && amountStr != null) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        AccountDAO accountDAO = new AccountDAO();
                        boolean isTransferSuccessful = accountDAO.transferFunds(loggedInAccountId, toAccountNumber, amount);
                        if (isTransferSuccessful) {
                            JOptionPane.showMessageDialog(MainScreen.this, "Fund transfer successful!");
                        } else {
                            JOptionPane.showMessageDialog(MainScreen.this, "Fund transfer failed.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainScreen.this, "Invalid amount.");
                    }
                }
            }
        });

        viewTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the transaction history
                TransactionDAO transactionDAO = new TransactionDAO();
                List<Transaction> transactions = transactionDAO.getTransactionHistory(loggedInAccountId);
                StringBuilder history = new StringBuilder("Transaction History:\n");
                for (Transaction transaction : transactions) {
                    history.append("Type: ").append(transaction.getTransactionType()).append(", ")
                            .append("Amount: ").append(transaction.getAmount()).append(", ")
                            .append("Date: ").append(transaction.getTransactionDate()).append("\n");
                }
                JOptionPane.showMessageDialog(MainScreen.this, history.toString());
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logout and return to the login screen
                new LoginScreen().setVisible(true);
                dispose(); // Close the main screen
            }
        });

        add(panel);
        setVisible(true);
    }
}