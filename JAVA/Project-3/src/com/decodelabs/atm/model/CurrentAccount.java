package com.decodelabs.atm.model;

import com.decodelabs.atm.strategy.withdrawal.StandardWithdrawal;
import com.decodelabs.atm.strategy.interest.NoInterest;
import com.decodelabs.atm.strategy.fee.FlatFee;

public class CurrentAccount extends Account {

    private static final double FLAT_FEE_AMOUNT = 5.0;
    public CurrentAccount(String accountNumber, String accountHolderName, String pin, double initialBalance) {
        super(accountNumber, accountHolderName, pin, initialBalance);
        this.withdrawalStrategy = new StandardWithdrawal();
        this.interestStrategy = new NoInterest();
        this.feeStrategy = new FlatFee(FLAT_FEE_AMOUNT);
    }
    @Override
    public String getAccountType() {
        return "Current Account";
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("  ┌──────────────────────────────────────────┐");
        System.out.println("  │        [B] CURRENT ACCOUNT                │");
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
