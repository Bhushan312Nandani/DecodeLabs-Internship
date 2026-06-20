package com.decodelabs.atm.strategy.interest;

import com.decodelabs.atm.model.Account;


public class NoInterest implements InterestStrategy {

    @Override
    public double calculateInterest(Account account) {
        System.out.println("  [#] Interest Rate: 0.0% (Current Account)");
        System.out.println("     This account type does not earn interest.");
        System.out.println("     Benefit: Unlimited withdrawal access with no daily cap.");
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "No Interest (0%)";
    }
}
