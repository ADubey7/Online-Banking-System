package org.example.dao;

import org.example.model.Transaction;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Method to record a new transaction within an existing connection/transaction
    public boolean recordTransaction(Transaction transaction, Connection connection) {
        String sql = "INSERT INTO transactions (from_account_id, to_account_number, amount, transaction_type, transaction_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, transaction.getFromAccountId());
            statement.setString(2, transaction.getToAccountNumber());
            statement.setDouble(3, transaction.getAmount());
            statement.setString(4, transaction.getTransactionType());
            statement.setTimestamp(5, transaction.getTransactionDate());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // The original method for backward compatibility
    public boolean recordTransaction(Transaction transaction) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            boolean result = recordTransaction(transaction, connection);
            if (result) {
                connection.commit();
            } else {
                connection.rollback();
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add a convenience method that takes individual parameters
    public boolean recordTransaction(int accountId, String transactionType, double amount, String description) {
        // For simple transactions like deposits, the to_account_number is the same as the from_account
        // For other transaction types, this method might need to be expanded
        String accountNumber = getAccountNumberById(accountId);

        if (accountNumber != null) {
            Transaction transaction = new Transaction(
                    0, // transactionId will be auto-generated
                    accountId,
                    accountNumber, // Using same account for to_account_number
                    amount,
                    transactionType,
                    new Timestamp(System.currentTimeMillis())
            );

            return recordTransaction(transaction);
        }
        return false;
    }

    // Helper method to get account number by ID
    private String getAccountNumberById(int accountId) {
        String sql = "SELECT account_number FROM accounts WHERE account_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("account_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Method to retrieve transaction history for an account
    public List<Transaction> getTransactionHistory(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account_id = ? OR to_account_number = (SELECT account_number FROM accounts WHERE account_id = ?) ORDER BY transaction_date DESC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);
            statement.setInt(2, accountId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                        resultSet.getInt("transaction_id"),
                        resultSet.getInt("from_account_id"),
                        resultSet.getString("to_account_number"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("transaction_type"),
                        resultSet.getTimestamp("transaction_date")
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}