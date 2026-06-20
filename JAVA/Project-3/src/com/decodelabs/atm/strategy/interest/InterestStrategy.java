package com.decodelabs.atm.strategy.interest;

import com.decodelabs.atm.model.Account;

public interface InterestStrategy {

    /**
     * Calculates the annual interest for the given account.
     *
     * @param account the account to calculate interest for
     * @return the calculated interest amount
     */
    double calculateInterest(Account account);
    String getStrategyName();
}
