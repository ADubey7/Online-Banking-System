# Online Banking System

## Overview
The Online Banking System is a Java-based application that simulates the core functionalities of a banking system. It allows users to register, log in, manage their accounts, transfer funds, and view transaction history. The system is built with a focus on security, user-friendliness, and scalability.

## Features
### 🔒 User Authentication:
- Secure user registration and login.
- Password hashing using BCrypt for enhanced security.

### 🏦 Account Management:
- Create and manage bank accounts.
- View account details (account number, balance, etc.).
- Update account balance.

### 💰 Fund Transfers:
- Transfer funds between accounts.
- Record transactions for audit purposes.

### 📜 Transaction History:
- View a detailed history of all transactions (deposits, withdrawals, transfers).

### 🗄️ Database Integration:
- Uses MySQL to store user information, account details, and transaction records.
- Secure database connection with proper error handling.

### 🖥️ User Interface:
- Simple and intuitive Java Swing interface for user interaction.

## Technologies Used
- **🖥 Programming Language:** Java
- **🗄 Database:** MySQL
- **🔑 Password Hashing:** BCrypt
- **🎨 User Interface:** Java Swing
- **🔧 Build Tool:** Maven
- **🔗 Database Connectivity:** JDBC (Java Database Connectivity)

## Prerequisites
Before running the project, ensure you have the following installed:
- **☕ Java Development Kit (JDK):** Version 17 or higher.
- **🗄 MySQL:** A running MySQL server.
- **🔗 MySQL Connector/J:** For Java-MySQL connectivity.
- **⚙️ Maven:** For dependency management and building the project.

## Setup Instructions
### 1️⃣ Clone the Repository
Clone the project repository to your local machine:
```bash
git clone https://github.com/ADubey7/online-banking-system.git
cd online-banking-system
```

### 2️⃣ Set Up the Database
Create a new database in MySQL:
```sql
CREATE DATABASE online_banking;
USE online_banking;
```
Create the required tables:
```sql
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    from_account_id INT,
    to_account_number VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_account_id) REFERENCES accounts(account_id)
);
```
Update the `database.properties` file with your MySQL credentials:
```properties
db.url=jdbc:mysql://localhost:3306/online_banking
db.username=your_mysql_username
db.password=your_mysql_password
```

### 3️⃣ Build the Project
Use Maven to build the project:
```bash
mvn clean install
```

### 4️⃣ Run the Application
Run the Main class to start the application:
```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## 📂 Project Structure
```
online-banking-system/
├── .gitignore
├── pom.xml
├── structure.txt
├── .idea/
│   ├── .gitignore
│   ├── compiler.xml
│   ├── encodings.xml
│   ├── jarRepositories.xml
│   ├── misc.xml
│   ├── workspace.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── org/example/
│   │   │   │   ├── LoginScreen.java
│   │   │   │   ├── Main.java
│   │   │   │   ├── MainScreen.java
│   │   │   │   ├── RegistrationScreen.java
│   │   │   │   ├── dao/
│   │   │   │   │   ├── AccountDAO.java
│   │   │   │   │   ├── TransactionDAO.java
│   │   │   │   │   ├── UserDAO.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Account.java
│   │   │   │   │   ├── Transaction.java
│   │   │   │   │   ├── User.java
│   │   │   │   ├── util/
│   │   │   │       ├── DatabaseConnection.java
│   │   ├── resources/
│   │       ├── database.properties
│   ├── test/
│       ├── java/
├── target/
│   ├── classes/
│   │   ├── database.properties
│   │   ├── org/example/
│   │   │   ├── LoginScreen.class
│   │   │   ├── Main.class
│   │   │   ├── MainScreen.class
│   │   │   ├── RegistrationScreen.class
│   │   │   ├── dao/
│   │   │   │   ├── AccountDAO.class
│   │   │   │   ├── TransactionDAO.class
│   │   │   │   ├── UserDAO.class
│   │   │   ├── model/
│   │   │   │   ├── Account.class
│   │   │   │   ├── Transaction.class
│   │   │   │   ├── User.class
│   │   │   ├── util/
│   │   │       ├── DatabaseConnection.class
│   ├── generated-sources/
│   │   ├── annotations/
│   ├── generated-test-sources/
│   │   ├── test-annotations/
│   ├── test-classes/
```

## 🚀 Usage
### 📝 Registration:
- Register a new user with a unique username and email.
- Passwords are securely hashed before storage.

### 🔑 Login:
- Log in using your registered credentials.

### 🏦 Account Management:
- Create a new bank account.
- View account details and balance.

### 💳 Fund Transfers:
- Transfer funds to another account using the recipient’s account number.

### 📜 Transaction History:
- View a detailed history of all transactions.

## 📜 License
This project is licensed under the MIT License. See the LICENSE file for details.

## 📩 Contact
For any questions or feedback, feel free to reach out:
- **👤 Name:** Ashish Raj
- **✉️ Email:** ashishdubey7224@gmail.com
- **🐙 GitHub:** https://github.com/ADubey7

Thank you for checking out the Online Banking System! 🚀

