package com.decodelabs.atm.strategy.withdrawal;

import com.decodelabs.atm.model.Account;

public class PenaltyWithdrawal implements WithdrawalStrategy {

    private final double penaltyPercentage;

    /**
     * Creates a PenaltyWithdrawal strategy with the given penalty rate.
     *
     * @param penaltyPercentage the penalty as a percentage (e.g., 1.0 for 1%)
     */
    public PenaltyWithdrawal(double penaltyPercentage) {
        this.penaltyPercentage = penaltyPercentage;
    }

    @Override
    public boolean withdraw(Account account, double amount) {
        if (amount <= 0) {
            System.out.println("  [X] Invalid amount. Please enter a positive value.");
            return false;
        }
        double penalty = amount * (penaltyPercentage / 100.0);
        double totalDeduction = amount + penalty;
        if (totalDeduction > account.getBalance()) {
            System.out.printf("  [X] Insufficient Funds!%n");
            System.out.printf("     Withdrawal Amount : Rs.%,.2f%n", amount);
            System.out.printf("     Penalty (%.1f%%)    : Rs.%,.2f%n", penaltyPercentage, penalty);
            System.out.printf("     Total Required    : Rs.%,.2f%n", totalDeduction);
            System.out.printf("     Available Balance : Rs.%,.2f%n", account.getBalance());
            return false;
        }
        account.debit(totalDeduction);
        System.out.println("  [!!]  EARLY WITHDRAWAL — PENALTY APPLIED");
        System.out.printf("     Withdrawal Amount : Rs.%,.2f%n", amount);
        System.out.printf("     Penalty (%.1f%%)    : Rs.%,.2f%n", penaltyPercentage, penalty);
        System.out.printf("     Total Deducted    : Rs.%,.2f%n", totalDeduction);
        System.out.printf("  [$] Remaining balance : Rs.%,.2f%n", account.getBalance());
        return true;
    }

    @Override
    public String getStrategyName() {
        return String.format("Penalty Withdrawal (%.1f%% penalty)", penaltyPercentage);
    }
}
