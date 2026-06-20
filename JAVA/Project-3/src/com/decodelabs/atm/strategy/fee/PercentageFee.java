package com.decodelabs.atm.strategy.fee;

public class PercentageFee implements TransactionFeeStrategy {

    private final double percentage;

    /**
     * Creates a PercentageFee strategy with the specified rate.
     *
     * @param percentage the fee as a percentage (e.g., 0.1 for 0.1%)
     */
    public PercentageFee(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double applyFee(double transactionAmount) {
        double fee = transactionAmount * (percentage / 100.0);
        System.out.printf("  [i] Transaction Fee : Rs.%.2f (%.2f%% of Rs.%,.2f)%n",
                fee, percentage, transactionAmount);
        return fee;
    }

    @Override
    public String getStrategyName() {
        return String.format("Percentage Fee (%.2f%% per transaction)", percentage);
    }
}
