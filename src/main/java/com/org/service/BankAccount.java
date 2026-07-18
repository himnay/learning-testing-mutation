package com.org.service;

// GoF: Builder — BankAccount has several optional fields; Builder avoids telescoping constructors.
public class BankAccount {

    private final String accountId;
    private double balance;
    private final double overdraftLimit;

    private BankAccount(Builder builder) {
        this.accountId = builder.accountId;
        this.balance = builder.initialBalance;
        this.overdraftLimit = builder.overdraftLimit;
    }

    /** Handles deposit. */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive, got: " + amount);
        }
        balance += amount;
    }

    /** Handles withdraw. */
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive, got: " + amount);
        }
        if (!canWithdraw(amount)) {
            throw new IllegalStateException(
                "Insufficient funds. Available: " + getAvailableBalance() + ", Requested: " + amount);
        }
        balance -= amount;
    }

    /** Transfers. */
    public void transfer(BankAccount target, double amount) {
        if (target == null) {
            throw new IllegalArgumentException("Target account must not be null");
        }
        withdraw(amount);
        target.deposit(amount);
    }

    /** Applies interest. */
    public double applyInterest(double annualRate) {
        if (annualRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative, got: " + annualRate);
        }
        double interest = balance * annualRate / 100;
        balance += interest;
        return interest;
    }

    // Boundary: balance + overdraftLimit >= amount (not just >)
    /** Returns whether withdraw. */
    public boolean canWithdraw(double amount) {
        return getAvailableBalance() >= amount;
    }

    public double getBalance() {
        return balance;
    }

    public double getAvailableBalance() {
        return balance + overdraftLimit;
    }

    public String getAccountId() {
        return accountId;
    }

    /** Returns the builder. */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String accountId;
        private double initialBalance = 0.0;
        private double overdraftLimit = 0.0;

        /** Returns the account id. */
        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        /** Returns the initial balance. */
        public Builder initialBalance(double initialBalance) {
            if (initialBalance < 0) {
                throw new IllegalArgumentException(
                    "Initial balance cannot be negative, got: " + initialBalance);
            }
            this.initialBalance = initialBalance;
            return this;
        }

        /** Returns the overdraft limit. */
        public Builder overdraftLimit(double overdraftLimit) {
            if (overdraftLimit < 0) {
                throw new IllegalArgumentException(
                    "Overdraft limit cannot be negative, got: " + overdraftLimit);
            }
            this.overdraftLimit = overdraftLimit;
            return this;
        }

        /** Builds. */
        public BankAccount build() {
            if (accountId == null || accountId.isBlank()) {
                throw new IllegalStateException("Account ID is required");
            }
            return new BankAccount(this);
        }
    }
}
