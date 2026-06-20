package com.decodelabs.atm.strategy.interest;

import com.decodelabs.atm.model.Account;

public class FixedDepositInterest implements InterestStrategy {

    private static final double RATE = 0.07; // 7% per annum

    @Override
    public double calculateInterest(Account account) {
        double interest = account.getBalance() * RATE;
        System.out.printf("  [^] Interest Rate   : %.1f%% per annum (Fixed Deposit)%n", RATE * 100);
        System.out.printf("     Deposited Amount: Rs.%,.2f%n", account.getBalance());
        System.out.printf("     Annual Interest : Rs.%,.2f%n", interest);
        System.out.printf("     Maturity Value  : Rs.%,.2f%n", account.getBalance() + interest);
        return interest;
    }

    @Override
    public String getStrategyName() {
        return "Fixed Deposit Interest (7% p.a.)";
    }
}
