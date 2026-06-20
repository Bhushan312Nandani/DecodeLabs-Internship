package com.decodelabs.atm.strategy.fee;

public class FlatFee implements TransactionFeeStrategy {

    private final double feeAmount;

    /**
     * Creates a FlatFee strategy with the specified fixed charge.
     *
     * @param feeAmount the flat fee charged per transaction (e.g., 5.0 for Rs.5)
     */
    public FlatFee(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    @Override
    public double applyFee(double transactionAmount) {
        System.out.printf("  [i] Transaction Fee : Rs.%.2f (Flat Rate)%n", feeAmount);
        return feeAmount;
    }

    @Override
    public String getStrategyName() {
        return String.format("Flat Fee (Rs.%.2f per transaction)", feeAmount);
    }
}
