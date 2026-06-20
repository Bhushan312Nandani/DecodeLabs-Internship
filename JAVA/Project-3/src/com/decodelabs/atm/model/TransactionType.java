package com.decodelabs.atm.model;

public enum TransactionType {

    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    BALANCE_INQUIRY("Balance Inquiry"),
    TRANSFER("Transfer"),
    INTEREST("Interest Credit"),
    FEE("Transaction Fee"),
    PENALTY("Penalty Deduction");

    private final String displayLabel;

    TransactionType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    @Override
    public String toString() {
        return displayLabel;
    }
}
