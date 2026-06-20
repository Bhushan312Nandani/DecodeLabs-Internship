package com.decodelabs.atm.strategy.fee;

public class NoFee implements TransactionFeeStrategy {

    @Override
    public double applyFee(double transactionAmount) {
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "No Transaction Fee";
    }
}
