package com.decodelabs.atm.strategy.fee;


public interface TransactionFeeStrategy {

    /**
     * Calculates the fee for a transaction of the given amount.
     *
     * @param transactionAmount the amount of the transaction
     * @return the fee to be charged (0.0 if no fee)
     */
    double applyFee(double transactionAmount);
    String getStrategyName();
}
