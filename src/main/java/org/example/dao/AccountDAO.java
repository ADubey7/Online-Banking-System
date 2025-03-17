package org.example.dao;

import org.example.model.Account;
import org.example.model.Transaction;
import org.example.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

public class AccountDAO {

    // Method to create a new account using an Account object
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, account.getUserId());
            statement.setString(2, account.getAccountNumber());
            statement.setDouble(3, account.getBalance());

            int rowsInserted = statement.executeUpdate();

            // If account creation is successful, record the initial deposit transaction
            if (rowsInserted > 0) {
                int accountId = getAccountIdByNumber(account.getAccountNumber());
                if (accountId > 0 && account.getBalance() > 0) {
                    TransactionDAO transactionDAO = new TransactionDAO();

                    // Create Transaction object
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    Transaction transaction = new Transaction(
                            0,
                            accountId,
                            account.getAccountNumber(),
                            account.getBalance(),
                            "DEPOSIT",
                            currentTimestamp
                    );

                    transactionDAO.recordTransaction(transaction);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to create a new account using userId and initialDeposit
    public boolean createAccount(int userId, double initialDeposit) {
        String sql = "INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Generate a random account number
            String accountNumber = generateAccountNumber();

            statement.setInt(1, userId);
            statement.setString(2, accountNumber);
            statement.setDouble(3, initialDeposit);

            int rowsInserted = statement.executeUpdate();

            // If account creation is successful, record the initial deposit transaction
            if (rowsInserted > 0) {
                int accountId = getAccountIdByNumber(accountNumber);
                if (accountId > 0 && initialDeposit > 0) {
                    TransactionDAO transactionDAO = new TransactionDAO();

                    // Create Transaction object
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    Transaction transaction = new Transaction(
                            0,
                            accountId,
                            accountNumber,
                            initialDeposit,
                            "DEPOSIT",
                            currentTimestamp
                    );

                    transactionDAO.recordTransaction(transaction);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to get account by account number
    public Account getAccountByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Account account = new Account(
                        resultSet.getInt("account_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("account_number"),
                        resultSet.getDouble("balance")
                );
                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Method to update account balance
    public boolean updateAccountBalance(int accountId, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, newBalance);
            statement.setInt(2, accountId);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to transfer funds
    public boolean transferFunds(int fromAccountId, String toAccountNumber, double amount) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Get account info for fromAccount
            String sqlFromAccount = "SELECT * FROM accounts WHERE account_id = ?";
            PreparedStatement fromAccountStmt = connection.prepareStatement(sqlFromAccount);
            fromAccountStmt.setInt(1, fromAccountId);
            ResultSet fromAccountRS = fromAccountStmt.executeQuery();

            if (!fromAccountRS.next()) {
                connection.rollback();
                return false; // From account not found
            }

            double fromAccountBalance = fromAccountRS.getDouble("balance");
            String fromAccountNumber = fromAccountRS.getString("account_number");

            if (fromAccountBalance < amount) {
                connection.rollback();
                return false; // Insufficient balance
            }

            // Get account info for toAccount
            String sqlToAccount = "SELECT * FROM accounts WHERE account_number = ?";
            PreparedStatement toAccountStmt = connection.prepareStatement(sqlToAccount);
            toAccountStmt.setString(1, toAccountNumber);
            ResultSet toAccountRS = toAccountStmt.executeQuery();

            if (!toAccountRS.next()) {
                connection.rollback();
                return false; // To account not found
            }

            int toAccountId = toAccountRS.getInt("account_id");
            double toAccountBalance = toAccountRS.getDouble("balance");

            // Update fromAccount balance
            String updateFromSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            PreparedStatement updateFromStmt = connection.prepareStatement(updateFromSql);
            updateFromStmt.setDouble(1, fromAccountBalance - amount);
            updateFromStmt.setInt(2, fromAccountId);
            updateFromStmt.executeUpdate();

            // Update toAccount balance
            String updateToSql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            PreparedStatement updateToStmt = connection.prepareStatement(updateToSql);
            updateToStmt.setDouble(1, toAccountBalance + amount);
            updateToStmt.setInt(2, toAccountId);
            updateToStmt.executeUpdate();

            // Record transactions
            TransactionDAO transactionDAO = new TransactionDAO();

            // Withdrawal transaction from fromAccount - CHANGED FROM "TRANSFER_OUT" to "TRANSFER"
            Transaction withdrawalTransaction = new Transaction(
                    0,
                    fromAccountId,
                    toAccountNumber,
                    amount,
                    "TRANSFER",
                    new Timestamp(System.currentTimeMillis())
            );
            transactionDAO.recordTransaction(withdrawalTransaction, connection);

            // Deposit transaction to toAccount - CHANGED FROM "TRANSFER_IN" to "TRANSFER"
            Transaction depositTransaction = new Transaction(
                    0,
                    toAccountId,
                    fromAccountNumber,
                    amount,
                    "TRANSFER",
                    new Timestamp(System.currentTimeMillis())
            );
            transactionDAO.recordTransaction(depositTransaction, connection);

            connection.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to generate a random account number
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Generate a 10-digit account number
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    // Helper method to get account ID by account number
    private int getAccountIdByNumber(String accountNumber) {
        String sql = "SELECT account_id FROM accounts WHERE account_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("account_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Method to get account by user ID
    public Account getAccountByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Account account = new Account(
                        resultSet.getInt("account_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("account_number"),
                        resultSet.getDouble("balance")
                );
                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}