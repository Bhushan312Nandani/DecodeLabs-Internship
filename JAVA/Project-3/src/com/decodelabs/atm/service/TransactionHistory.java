package com.decodelabs.atm.service;

import com.decodelabs.atm.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionHistory {

    private final List<Transaction> transactions;

    public TransactionHistory() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Records a new transaction in the history.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Returns the last N transactions (most recent first).
     *
     * @param n number of recent transactions to retrieve
     * @return list of last N transactions
     */
    public List<Transaction> getLastN(int n) {
        int size = transactions.size();
        if (n >= size) {
            return new ArrayList<>(transactions);
        }
        return new ArrayList<>(transactions.subList(size - n, size));
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    /**
     * Prints a formatted mini-statement showing recent transactions.
     * Mimics the mini-statement printed by real ATMs.
     *
     * @param maxEntries maximum number of entries to display
     */
    public void printMiniStatement(int maxEntries) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                              [=] MINI STATEMENT                                 ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════════════════════╣");

        if (transactions.isEmpty()) {
            System.out.println("  ║                         No transactions found.                                 ║");
        } else {
            List<Transaction> recent = getLastN(maxEntries);
            System.out.println("  ║  ID       | Date & Time       | Type             | Amount         | Balance    ║");
            System.out.println("  ║──────────────────────────────────────────────────────────────────────────────── ║");
            for (Transaction txn : recent) {
                System.out.println("  ║" + txn.toString() + " ║");
            }
            System.out.println("  ╠══════════════════════════════════════════════════════════════════════════════════╣");
            System.out.printf("  ║  Showing last %d of %d total transactions                                      ║%n",
                    Math.min(maxEntries, transactions.size()), transactions.size());
        }
        System.out.println("  ╚══════════════════════════════════════════════════════════════════════════════════╝");
    }
    public void printFullStatement() {
        printMiniStatement(transactions.size());
    }
}
