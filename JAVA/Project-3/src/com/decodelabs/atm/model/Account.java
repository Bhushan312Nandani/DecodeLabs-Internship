package com.decodelabs.atm.model;

import com.decodelabs.atm.service.TransactionHistory;
import com.decodelabs.atm.strategy.withdrawal.WithdrawalStrategy;
import com.decodelabs.atm.strategy.interest.InterestStrategy;
import com.decodelabs.atm.strategy.fee.TransactionFeeStrategy;

public abstract class Account {
    private String accountNumber;
    private String accountHolderName;
    private String pin;                         
    private double balance;

    protected WithdrawalStrategy withdrawalStrategy;     // like flyBehavior
    protected InterestStrategy interestStrategy;          // like quackBehavior
    protected TransactionFeeStrategy feeStrategy;         // 3rd behavior family

    private TransactionHistory transactionHistory;
    /**
     * Creates a new Account. Subclasses MUST call this and then set
     * their own default strategies (just like Duck subclasses set
     * flyBehavior and quackBehavior in their constructors).
     *
     * @param accountNumber    unique account identifier
     * @param accountHolderName name of the account holder
     * @param pin              secret PIN for authentication
     * @param initialBalance   starting balance
     */
    protected Account(String accountNumber, String accountHolderName, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new TransactionHistory();
    }
    /**
     * Deposits money into the account. All account types deposit the same way.
     * (Like swim() in Duck — all ducks swim.)
     *
     * @param amount the amount to deposit (must be > 0)
     * @return true if deposit was successful
     */
    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("  [X] Invalid amount. Deposit must be greater than zero.");
            return false;
        }

        // Apply transaction fee
        double fee = feeStrategy.applyFee(amount);

        balance += amount;
        if (fee > 0) {
            balance -= fee;
            transactionHistory.addTransaction(new Transaction(
                    TransactionType.FEE, fee, balance, "Transaction fee for deposit"));
        }

        transactionHistory.addTransaction(new Transaction(
                TransactionType.DEPOSIT, amount, balance, "Cash deposit"));

        System.out.printf("  [OK] Successfully deposited Rs.%,.2f%n", amount);
        if (fee > 0) {
            System.out.printf("  [i] Fee deducted: Rs.%,.2f%n", fee);
        }
        System.out.printf("  [$] New balance: Rs.%,.2f%n", balance);
        return true;
    }

    /**
     * Credits the account (used internally by strategies like TransferStrategy).
     *
     * @param amount the amount to credit
     */
    public void credit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    /**
     * Debits the account (called by withdrawal strategies after validation).
     *
     * @param amount the amount to debit
     * @return true if sufficient funds were available
     */
    public boolean debit(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    /**
     * Validates the entered PIN against the stored PIN.
     * The PIN itself is NEVER exposed — only this validation method exists.
     *
     * @param inputPin the PIN entered by the user
     * @return true if the PIN matches
     */
    public boolean validatePin(String inputPin) {
        return this.pin != null && this.pin.equals(inputPin);
    }

    /**
     * Delegates withdrawal to the current WithdrawalStrategy.
     * Like duck.performFly() → calls flyBehavior.fly()
     *
     * @param amount the amount to withdraw
     * @return true if withdrawal was successful
     */
    public boolean performWithdrawal(double amount) {
        boolean success = withdrawalStrategy.withdraw(this, amount);
        if (success) {
            // Apply fee
            double fee = feeStrategy.applyFee(amount);
            if (fee > 0 && fee <= balance) {
                balance -= fee;
                transactionHistory.addTransaction(new Transaction(
                        TransactionType.FEE, fee, balance, "Transaction fee for withdrawal"));
            }
            transactionHistory.addTransaction(new Transaction(
                    TransactionType.WITHDRAWAL, amount, balance, "ATM withdrawal"));
        }
        return success;
    }

    /**
     * Delegates interest calculation to the current InterestStrategy.
     * Like duck.performQuack() → calls quackBehavior.quack()
     *
     * @return the calculated interest amount
     */
    public double performInterestCalculation() {
        return interestStrategy.calculateInterest(this);
    }

    /**
     * Delegates fee calculation to the current TransactionFeeStrategy.
     *
     * @param amount the transaction amount to calculate fee for
     * @return the fee amount
     */
    public double performFeeCalculation(double amount) {
        return feeStrategy.applyFee(amount);
    }
    public void setWithdrawalStrategy(WithdrawalStrategy withdrawalStrategy) {
        this.withdrawalStrategy = withdrawalStrategy;
        System.out.printf("  [~] Withdrawal policy changed to: %s%n", withdrawalStrategy.getStrategyName());
    }
    public void setInterestStrategy(InterestStrategy interestStrategy) {
        this.interestStrategy = interestStrategy;
        System.out.printf("  [~] Interest policy changed to: %s%n", interestStrategy.getStrategyName());
    }
    public void setFeeStrategy(TransactionFeeStrategy feeStrategy) {
        this.feeStrategy = feeStrategy;
        System.out.printf("  [~] Fee policy changed to: %s%n", feeStrategy.getStrategyName());
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public TransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    public WithdrawalStrategy getWithdrawalStrategy() {
        return withdrawalStrategy;
    }

    public InterestStrategy getInterestStrategy() {
        return interestStrategy;
    }

    public TransactionFeeStrategy getFeeStrategy() {
        return feeStrategy;
    }
    public abstract String getAccountType();
    public abstract void displayAccountInfo();
}
