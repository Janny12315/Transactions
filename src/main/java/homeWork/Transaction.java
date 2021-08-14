package homeWork;

import java.util.Objects;

public class Transaction {
    private int transactionId;
    private int accountId;
    private int amount;

    public Transaction(int transactionId, int accountId, int amount) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId &&
                accountId == that.accountId &&
                amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, accountId, amount);
    }

    public String toString() {
        return this.transactionId + " " + this.accountId + " " + this.amount;
    }

}
