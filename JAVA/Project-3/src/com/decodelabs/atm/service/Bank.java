package com.decodelabs.atm.service;

import com.decodelabs.atm.model.Account;
import com.decodelabs.atm.model.SavingsAccount;
import com.decodelabs.atm.model.CurrentAccount;
import com.decodelabs.atm.model.FixedDepositAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class Bank {

    private final String bankName;
    private final Map<String, Account> accounts;

    /**
     * Creates a Bank and pre-loads demo accounts for testing.
     *
     * @param bankName the name of the bank
     */
    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
        loadDemoAccounts();
    }

    private void loadDemoAccounts() {
        accounts.put("SAV1001", new SavingsAccount(
                "SAV1001", "Aarav Sharma", "1234", 50000.0));
        accounts.put("SAV1002", new SavingsAccount(
                "SAV1002", "Priya Patel", "5678", 125000.0));
        accounts.put("CUR2001", new CurrentAccount(
                "CUR2001", "Vikram Industries", "4321", 500000.0));
        accounts.put("CUR2002", new CurrentAccount(
                "CUR2002", "Neha Enterprises", "8765", 750000.0));
        accounts.put("FD3001", new FixedDepositAccount(
                "FD3001", "Rohan Gupta", "1111", 200000.0, 12));
        accounts.put("FD3002", new FixedDepositAccount(
                "FD3002", "Ananya Singh", "2222", 500000.0, 24));
    }

    /**
     * Retrieves an account by its account number.
     *
     * @param accountNumber the account number to look up
     * @return the Account object, or null if not found
     */
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber.toUpperCase());
    }

    /**
     * Checks if an account exists in the bank.
     *
     * @param accountNumber the account number to check
     * @return true if the account exists
     */
    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber.toUpperCase());
    }
    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    public String getBankName() {
        return bankName;
    }
    public void displayAvailableAccounts() {
        System.out.println();
        System.out.println("  ┌──────────────────────────────────────────────────────────┐");
        System.out.println("  │              [i] AVAILABLE DEMO ACCOUNTS                  │");
        System.out.println("  ├──────────┬──────────────────────┬────────────┬────────────┤");
        System.out.println("  │ Acc No   │ Holder Name          │ Type       │ PIN        │");
        System.out.println("  ├──────────┼──────────────────────┼────────────┼────────────┤");
        System.out.printf("  │ SAV1001  │ Aarav Sharma         │ Savings    │ 1234       │%n");
        System.out.printf("  │ SAV1002  │ Priya Patel          │ Savings    │ 5678       │%n");
        System.out.printf("  │ CUR2001  │ Vikram Industries    │ Current    │ 4321       │%n");
        System.out.printf("  │ CUR2002  │ Neha Enterprises     │ Current    │ 8765       │%n");
        System.out.printf("  │ FD3001   │ Rohan Gupta          │ Fixed Dep  │ 1111       │%n");
        System.out.printf("  │ FD3002   │ Ananya Singh         │ Fixed Dep  │ 2222       │%n");
        System.out.println("  └──────────┴──────────────────────┴────────────┴────────────┘");
    }
}
