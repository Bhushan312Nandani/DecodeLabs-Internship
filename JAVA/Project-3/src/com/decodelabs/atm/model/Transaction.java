package com.decodelabs.atm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private static int idCounter = 1000;

    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    /**
     * Creates a new Transaction with auto-generated ID and current timestamp.
     *
     * @param type         the type of transaction (DEPOSIT, WITHDRAWAL, etc.)
     * @param amount       the monetary amount involved
     * @param balanceAfter the account balance after this transaction
     * @param description  a human-readable description of what happened
     */
    public Transaction(TransactionType type, double amount, double balanceAfter, String description) {
        this.transactionId = "TXN" + (++idCounter);
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    // ── Getters (immutable — no setters) ──

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
        String sign = (type == TransactionType.WITHDRAWAL || type == TransactionType.FEE || type == TransactionType.PENALTY) ? "-" : "+";
        return String.format("  %s | %s | %-16s | %sRs.%,.2f | Bal: Rs.%,.2f",
                transactionId,
                timestamp.format(fmt),
                type.getDisplayLabel(),
                sign,
                amount,
                balanceAfter);
    }
}
