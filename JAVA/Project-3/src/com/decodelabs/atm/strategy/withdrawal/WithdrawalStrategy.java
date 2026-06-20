package com.decodelabs.atm.strategy.withdrawal;

import com.decodelabs.atm.model.Account;

public interface WithdrawalStrategy {

    /**
     * Attempts to withdraw the specified amount from the given account.
     *
     * @param account the account to withdraw from
     * @param amount  the amount to withdraw (must be > 0)
     * @return true if the withdrawal was successful, false otherwise
     */
    boolean withdraw(Account account, double amount);
    String getStrategyName();
}
