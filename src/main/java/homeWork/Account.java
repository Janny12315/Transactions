package homeWork;

import java.util.Objects;

public class Account {
    private int accountId;
    private int userId;
    private double balance;
    private String currency;

    public Account(int accountId, int userId, double balance, String currency) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                userId == account.userId &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(currency, account.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, balance, currency);
    }

    @Override
    public String toString() {
        return this.accountId + " " + this.userId + " " + this.balance + " " + this.currency;
    }
}
