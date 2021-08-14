package homeWork;

import java.sql.*;
import java.util.ArrayList;

public class HomeWork {
    static final String DB = "jdbc:postgresql://127.0.0.1:5432/ForTransactions";
    static final String LOG = "postgres";
    static final String PASS = "postgres";

    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<Account> accounts = new ArrayList<>();
    static ArrayList<Transaction> transactions = new ArrayList<>();


    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(DB, LOG, PASS);
             Statement statement = connection.createStatement()) {

            getData(statement);

            printData();

            //userRegistration(statement, "Никита Щербаков");

            //addAccount(statement, 4, Currency.BYN.toString());

            //refill(connection, 1, 1000000);

            withdrawalFromAnAccount(connection, 1, 20000);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    //метод для получения базы данных
    static void getData(Statement statement) throws SQLException {

        ResultSet resultSetUsers = statement.executeQuery("SELECT * FROM users");
        while (resultSetUsers.next()) {
            int userId = resultSetUsers.getInt("userId");
            String name = resultSetUsers.getString("name");
            String address = resultSetUsers.getString("address");
            User user = new User(userId, name, address);
            users.add(user);
        }

        ResultSet resultSetAccounts = statement.executeQuery("SELECT * FROM accounts");
        while (resultSetAccounts.next()) {
            int accountId = resultSetAccounts.getInt("accountId");
            int userId = resultSetAccounts.getInt("userId");
            double balance = resultSetAccounts.getDouble("balance");
            String currency = resultSetAccounts.getString("currency");
            Account account = new Account(accountId, userId, balance, currency);
            accounts.add(account);
        }

        ResultSet resultSetTransactions = statement.executeQuery("SELECT * FROM transactions");
        while (resultSetTransactions.next()) {
            int transactionId = resultSetTransactions.getInt("transactionId");
            int accountId = resultSetTransactions.getInt("accountId");
            int amount = resultSetTransactions.getInt("amount");
            Transaction transaction = new Transaction(transactionId, accountId, amount);
            transactions.add(transaction);
        }
    }


    // метод для печати базы данных
    static void printData() {
        System.out.println("----Пользователи----");
        for (User user : users) {
            System.out.println(user);
        }
        System.out.println("----Аккаунты пользователей----");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.print(account);
            User thisUser = users.get(account.getUserId() - 1);
            System.out.printf(" (%s)\n", thisUser.getName());
        }
        System.out.println("----Транзакции----");
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    // метод для регистрации пользователя
    static void userRegistration(Statement statement, String... userParam) {
        try {
            System.out.print("Add USER...");
            String name = userParam[0];
            String address = "";
            if (userParam.length > 1) {
                address = userParam[1];
            }
            String request = String.format("INSERT INTO Users (name, address) VALUES ('%s', '%s')", name, address);
            statement.executeUpdate(request);
            System.out.println("done successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // метод для добавления аккаунта пользователю
    static void addAccount(Statement statement, int userId, String currency) {
        System.out.print("Add ACCOUNT...");
        String require = String.format("INSERT INTO Accounts (userId, currency) VALUES (%d, '%s');", userId, currency);

        try {
            if (!isAccountAlreadyExist(userId, currency)) {
                statement.executeUpdate(require);
                System.out.println("done successfully");
            }

        } catch (SQLException e) {
            if (userId > users.size() || userId < 0) {
                System.out.println("Пользователя с таким userId нет в базе");
                return;
            }
        }
    }


    //проверяем что такого аккаунта еще нет в базе данных
    static boolean isAccountAlreadyExist(int userId, String currency) {
        for (Account a : accounts) {
            if (a.getUserId() == userId && a.getCurrency().equals(currency)) {
                System.out.println("У пользователя с userId=" + userId + " уже есть аккаунт в валюте " + currency);
                return true;
            }
        }
        return false;
    }


    // метод для пополнения баланса
    static void refill(Connection connection, int accountId, int amount) {
        if (amount > 100_000_000) {
            System.out.println("Превышение суммы транзакции");
            return;
        }
        Account account = findAccount(accountId);
        if (account == null) {
            System.out.println("Аккаунт не найден");
            return;
        }

        double initiallyAmount = account.getBalance();
        double finalAmount = initiallyAmount + amount;

        if (finalAmount > 2_000_000_000) {
            System.out.println("Невозможно выполнить операцию: переполнение счета");
            return;
        }

        String require1 = String.format("INSERT INTO Transactions (accountId, amount) Values (%d, %d);", accountId, amount);
        String require2 = String.format("UPDATE accounts SET balance=%.3f WHERE accountId=%d;", finalAmount, accountId).replace(',', '.');

        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            System.out.print("Add Transaction...");
            statement.executeUpdate(require1);
            statement.executeUpdate(require2);
            connection.commit();
            System.out.println("done successfully");
        } catch (SQLException e) {
            System.out.println("чтото пошло не так в транзакции");
            e.printStackTrace();
        }
    }

    // инициализируем аккаунт для получение информации о балансе
    static Account findAccount(int accountId) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accountId == accounts.get(i).getAccountId()) {
                return accounts.get(i);
            }
        }
        return null;
    }


    // метод для снятия средств со счета
    static void withdrawalFromAnAccount(Connection connection, int accountId, int amount) {
        if (amount > 100_000_000) {
            System.out.println("Превышение суммы транзакции");
            return;
        }

        Account account = findAccount(accountId);
        if (account == null) {
            System.out.println("Аккаунт не найден");
            return;
        }

        double initiallyAmount = account.getBalance();
        double finalAmount = initiallyAmount - amount;

        if (finalAmount < 0) {
            System.out.println("Недостаточно средств");
            return;
        }

        String require1 = String.format("INSERT INTO Transactions (accountId, amount) Values (%d, -%d);", accountId, amount);
        String require2 = String.format("UPDATE accounts SET balance=%.3f  WHERE accountId=%d;", finalAmount, accountId).replace(',', '.');

        try (Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);
            System.out.print("Add Transaction...");
            statement.executeUpdate(require1);
            statement.executeUpdate(require2);
            connection.commit();
            System.out.println("done successfully");
        } catch (SQLException e) {
            System.out.println("что-то пошло не так в транзакции");
        }
    }
}

