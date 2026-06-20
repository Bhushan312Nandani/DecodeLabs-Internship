package com.decodelabs.atm.model;

import com.decodelabs.atm.strategy.withdrawal.LimitedWithdrawal;
import com.decodelabs.atm.strategy.interest.SavingsInterest;
import com.decodelabs.atm.strategy.fee.NoFee;

public class SavingsAccount extends Account {

    private static final double DAILY_WITHDRAWAL_LIMIT = 25000.0;
    public SavingsAccount(String accountNumber, String accountHolderName, String pin, double initialBalance) {
        super(accountNumber, accountHolderName, pin, initialBalance);
        this.withdrawalStrategy = new LimitedWithdrawal(DAILY_WITHDRAWAL_LIMIT);
        this.interestStrategy = new SavingsInterest();
        this.feeStrategy = new NoFee();
    }
    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("  ┌──────────────────────────────────────────┐");
        System.out.println("  │        [#] SAVINGS ACCOUNT                │");
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.printf("  │  Account No : %-25s  │%n", getAccountNumber());
        System.out.printf("  │  Holder     : %-25s  │%n", getAccountHolderName());
        System.out.printf("  │  Balance    : Rs.%-24s │%n", String.format("%,.2f", getBalance()));
        System.out.printf("  │  Type       : %-25s  │%n", getAccountType());
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.printf("  │  Withdrawal : %-25s  │%n", withdrawalStrategy.getStrategyName());
        System.out.printf("  │  Interest   : %-25s  │%n", interestStrategy.getStrategyName());
        System.out.printf("  │  Fee        : %-25s  │%n", feeStrategy.getStrategyName());
        System.out.println("  └──────────────────────────────────────────┘");
    }
}
