package com.decodelabs.atm.strategy.withdrawal;

import com.decodelabs.atm.model.Account;

public class LimitedWithdrawal implements WithdrawalStrategy {

    private final double dailyLimit;
    private double withdrawnToday;

    /**
     * Creates a LimitedWithdrawal strategy with the specified daily cap.
     *
     * @param dailyLimit maximum amount that can be withdrawn per day
     */
    public LimitedWithdrawal(double dailyLimit) {
        this.dailyLimit = dailyLimit;
        this.withdrawnToday = 0;
    }

    @Override
    public boolean withdraw(Account account, double amount) {
        if (amount <= 0) {
            System.out.println("  [X] Invalid amount. Please enter a positive value.");
            return false;
        }
        double remainingLimit = dailyLimit - withdrawnToday;
        if (amount > remainingLimit) {
            System.out.printf("  [X] Daily withdrawal limit exceeded!%n");
            System.out.printf("     Daily Limit   : Rs.%,.2f%n", dailyLimit);
            System.out.printf("     Already Used   : Rs.%,.2f%n", withdrawnToday);
            System.out.printf("     Remaining Today: Rs.%,.2f%n", remainingLimit);
            return false;
        }
        if (amount > account.getBalance()) {
            System.out.printf("  [X] Insufficient Funds! Available balance: Rs.%,.2f%n", account.getBalance());
            return false;
        }

        account.debit(amount);
        withdrawnToday += amount;
        System.out.printf("  [OK] Successfully withdrawn Rs.%,.2f%n", amount);
        System.out.printf("  [$] Remaining balance: Rs.%,.2f%n", account.getBalance());
        System.out.printf("  [#] Daily limit remaining: Rs.%,.2f%n", dailyLimit - withdrawnToday);
        return true;
    }

    @Override
    public String getStrategyName() {
        return String.format("Limited Withdrawal (Daily Cap: Rs.%,.0f)", dailyLimit);
    }
    public void resetDailyLimit() {
        this.withdrawnToday = 0;
    }

    public double getRemainingLimit() {
        return dailyLimit - withdrawnToday;
    }
}
