package com.decodelabs.atm.model;

import com.decodelabs.atm.strategy.withdrawal.NoWithdrawal;
import com.decodelabs.atm.strategy.interest.FixedDepositInterest;
import com.decodelabs.atm.strategy.fee.NoFee;

public class FixedDepositAccount extends Account {

    private final int tenureMonths;

    /**
     * Creates a FixedDepositAccount with locked withdrawal.
     *
     * @param accountNumber    unique account identifier
     * @param accountHolderName name of the account holder
     * @param pin              secret PIN
     * @param depositAmount    the fixed deposit amount
     * @param tenureMonths     lock-in period in months
     */
    public FixedDepositAccount(String accountNumber, String accountHolderName,
                                String pin, double depositAmount, int tenureMonths) {
        super(accountNumber, accountHolderName, pin, depositAmount);
        this.tenureMonths = tenureMonths;
        this.withdrawalStrategy = new NoWithdrawal();           // Can't fly! (locked)
        this.interestStrategy = new FixedDepositInterest();      // Squeaks! (high interest)
        this.feeStrategy = new NoFee();                          // No fees
    }
    @Override
    public String getAccountType() {
        return "Fixed Deposit Account";
    }

    @Override
    public void displayAccountInfo() {
        System.out.println("  ┌──────────────────────────────────────────┐");
        System.out.println("  │        [L] FIXED DEPOSIT ACCOUNT          │");
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.printf("  │  Account No : %-25s  │%n", getAccountNumber());
        System.out.printf("  │  Holder     : %-25s  │%n", getAccountHolderName());
        System.out.printf("  │  Deposit    : Rs.%-24s │%n", String.format("%,.2f", getBalance()));
        System.out.printf("  │  Type       : %-25s  │%n", getAccountType());
        System.out.printf("  │  Tenure     : %-25s  │%n", tenureMonths + " months");
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.printf("  │  Withdrawal : %-25s  │%n", withdrawalStrategy.getStrategyName());
        System.out.printf("  │  Interest   : %-25s  │%n", interestStrategy.getStrategyName());
        System.out.printf("  │  Fee        : %-25s  │%n", feeStrategy.getStrategyName());
        System.out.println("  └──────────────────────────────────────────┘");
    }

    public int getTenureMonths() {
        return tenureMonths;
    }
}
