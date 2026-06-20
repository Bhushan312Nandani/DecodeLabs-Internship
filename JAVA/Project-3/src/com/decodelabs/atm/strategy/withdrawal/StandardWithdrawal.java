package com.decodelabs.atm.strategy.withdrawal;

import com.decodelabs.atm.model.Account;

public class StandardWithdrawal implements WithdrawalStrategy {

    @Override
    public boolean withdraw(Account account, double amount) {
        if (amount <= 0) {
            System.out.println("  [X] Invalid amount. Please enter a positive value.");
            return false;
        }
        if (amount > account.getBalance()) {
            System.out.printf("  [X] Insufficient Funds! Available balance: Rs.%,.2f%n", account.getBalance());
            return false;
        }
        account.debit(amount);
        System.out.printf("  [OK] Successfully withdrawn Rs.%,.2f%n", amount);
        System.out.printf("  [$] Remaining balance: Rs.%,.2f%n", account.getBalance());
        return true;
    }

    @Override
    public String getStrategyName() {
        return "Standard Withdrawal (No Limit)";
    }
}
