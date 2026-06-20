package com.decodelabs.atm.strategy.interest;

import com.decodelabs.atm.model.Account;
public class SavingsInterest implements InterestStrategy {

    private static final double RATE = 0.04; // 4% per annum

    @Override
    public double calculateInterest(Account account) {
        double interest = account.getBalance() * RATE;
        System.out.printf("  [^] Interest Rate   : %.1f%% per annum%n", RATE * 100);
        System.out.printf("     Current Balance : Rs.%,.2f%n", account.getBalance());
        System.out.printf("     Annual Interest : Rs.%,.2f%n", interest);
        System.out.printf("     Monthly Approx  : Rs.%,.2f%n", interest / 12);
        return interest;
    }

    @Override
    public String getStrategyName() {
        return "Savings Interest (4% p.a.)";
    }
}
