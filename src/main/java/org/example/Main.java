package org.example;

import org.example.dao.AccountDAO;
import org.example.dao.TransactionDAO;
import org.example.dao.UserDAO;
import org.example.model.Account;
import org.example.model.Transaction;
import org.example.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        new LoginScreen();
        // Create an instance of UserDAO
        UserDAO userDAO = new UserDAO();

        // Generate a unique username and email using a timestamp
        long timestamp = System.currentTimeMillis();
        String uniqueUsername = "jane_doe_" + timestamp;
        String uniqueEmail = "jane.doe." + timestamp + "@example.com";

        // Test creating a new user
        String password = "securepassword123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(0, uniqueUsername, hashedPassword, "Jane Doe", uniqueEmail);

        boolean isUserCreated = userDAO.createUser(newUser);
        if (isUserCreated) {
            System.out.println("User created successfully!");
        } else {
            System.out.println("Failed to create user.");
            return; // Exit if user creation fails
        }

        // Test retrieving the user by username
        User retrievedUser = userDAO.getUserByUsername(uniqueUsername);
        if (retrievedUser != null) {
            System.out.println("User retrieved: " + retrievedUser);
        } else {
            System.out.println("User not found.");
            return; // Exit if user retrieval fails
        }

        // Test validating user login credentials
        boolean isValid = userDAO.validateUser(uniqueUsername, "securepassword123");
        if (isValid) {
            System.out.println("Login credentials are valid!");
        } else {
            System.out.println("Invalid login credentials.");
            return; // Exit if login validation fails
        }

        // Create an instance of AccountDAO
        AccountDAO accountDAO = new AccountDAO();

        // Generate a unique account number using a timestamp
        String uniqueAccountNumber = "ACC" + timestamp;

        // Test creating a new account
        Account newAccount = new Account(0, retrievedUser.getUserId(), uniqueAccountNumber, 1000.00);
        boolean isAccountCreated = accountDAO.createAccount(newAccount);
        if (isAccountCreated) {
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Failed to create account.");
            return; // Exit if account creation fails
        }

        // Test retrieving the account by account number
        Account retrievedAccount = accountDAO.getAccountByAccountNumber(uniqueAccountNumber);
        if (retrievedAccount != null) {
            System.out.println("Account retrieved: " + retrievedAccount);
        } else {
            System.out.println("Account not found.");
            return; // Exit if account retrieval fails
        }

        // Test updating the account balance
        boolean isBalanceUpdated = accountDAO.updateAccountBalance(retrievedAccount.getAccountId(), 1500.00);
        if (isBalanceUpdated) {
            System.out.println("Account balance updated successfully!");
        } else {
            System.out.println("Failed to update account balance.");
        }

        // Test transferring funds
        // Create a second account for testing fund transfer
        String secondUniqueAccountNumber = "ACC" + (timestamp + 1); // Ensure a unique account number
        Account secondAccount = new Account(0, retrievedUser.getUserId(), secondUniqueAccountNumber, 500.00);
        boolean isSecondAccountCreated = accountDAO.createAccount(secondAccount);
        if (isSecondAccountCreated) {
            System.out.println("Second account created successfully!");
        } else {
            System.out.println("Failed to create second account.");
            return; // Exit if second account creation fails
        }

        // Retrieve the second account
        Account retrievedSecondAccount = accountDAO.getAccountByAccountNumber(secondUniqueAccountNumber);
        if (retrievedSecondAccount == null) {
            System.out.println("Second account not found.");
            return; // Exit if second account retrieval fails
        }

        // Transfer funds from the first account to the second account
        boolean isTransferSuccessful = accountDAO.transferFunds(retrievedAccount.getAccountId(), secondUniqueAccountNumber, 500.00);
        if (isTransferSuccessful) {
            System.out.println("Fund transfer successful!");
        } else {
            System.out.println("Fund transfer failed.");
        }

        // Test retrieving transaction history
        TransactionDAO transactionDAO = new TransactionDAO();
        List<Transaction> transactions = transactionDAO.getTransactionHistory(retrievedAccount.getAccountId());
        System.out.println("Transaction history for account " + retrievedAccount.getAccountId() + ":");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}