package com.decodelabs.atm.service;

import com.decodelabs.atm.model.Account;
import com.decodelabs.atm.model.Transaction;
import com.decodelabs.atm.model.TransactionType;
import com.decodelabs.atm.strategy.withdrawal.NoWithdrawal;
import com.decodelabs.atm.strategy.withdrawal.PenaltyWithdrawal;

import java.util.Scanner;


public class ATM {

    private static final int MAX_PIN_ATTEMPTS = 3;
    private static final int MINI_STATEMENT_ENTRIES = 10;

    private final Bank bank;
    private final String atmId;
    private final String location;

    /**
     * Creates an ATM machine connected to a bank.
     *
     * @param bank     the bank this ATM belongs to
     * @param atmId    unique ATM identifier
     * @param location physical location of the ATM
     */
    public ATM(Bank bank, String atmId, String location) {
        this.bank = bank;
        this.atmId = atmId;
        this.location = location;
    }

    /**
     * Authenticates a user by account number and PIN.
     * Allows up to 3 attempts (like real ATMs) before locking out.
     *
     * @param scanner input scanner
     * @return the authenticated Account, or null if authentication failed
     */
    public Account authenticateUser(Scanner scanner) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║         [*] AUTHENTICATION               ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();

        System.out.print("  Enter Account Number: ");
        String accountNumber = scanner.nextLine().trim().toUpperCase();

        if (!bank.accountExists(accountNumber)) {
            System.out.println();
            System.out.println("  [X] Account not found. Please check your account number.");
            return null;
        }

        Account account = bank.getAccount(accountNumber);

        for (int attempt = 1; attempt <= MAX_PIN_ATTEMPTS; attempt++) {
            System.out.printf("  Enter PIN (Attempt %d/%d): ", attempt, MAX_PIN_ATTEMPTS);
            String pin = scanner.nextLine().trim();

            if (account.validatePin(pin)) {
                System.out.println();
                System.out.println("  [OK] Authentication successful!");
                System.out.printf("  Welcome, %s!%n", account.getAccountHolderName());
                return account;
            } else {
                int remaining = MAX_PIN_ATTEMPTS - attempt;
                if (remaining > 0) {
                    System.out.printf("  [X] Incorrect PIN. %d attempt(s) remaining.%n", remaining);
                } else {
                    System.out.println();
                    System.out.println("  ╔══════════════════════════════════════════╗");
                    System.out.println("  ║  [!] ACCOUNT LOCKED                      ║");
                    System.out.println("  ║                                          ║");
                    System.out.println("  ║  Too many failed attempts.               ║");
                    System.out.println("  ║  Your account has been temporarily       ║");
                    System.out.println("  ║  frozen for security.                    ║");
                    System.out.println("  ║                                          ║");
                    System.out.println("  ║  Please visit your nearest branch.       ║");
                    System.out.println("  ╚══════════════════════════════════════════╝");
                    account.setWithdrawalStrategy(new NoWithdrawal());
                }
            }
        }
        return null;
    }

    /**
     * Displays the main ATM menu and handles user selections.
     * Routes operations to the Account, which delegates to its strategies.
     *
     * @param account the authenticated account
     * @param scanner input scanner
     */
    public void showMainMenu(Account account, Scanner scanner) {
        boolean running = true;

        while (running) {
            displayMenu(account);

            System.out.print("  Enter your choice (1-8): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleDeposit(account, scanner);
                    break;
                case "2":
                    handleWithdrawal(account, scanner);
                    break;
                case "3":
                    handleBalanceInquiry(account);
                    break;
                case "4":
                    handleTransfer(account, scanner);
                    break;
                case "5":
                    handleMiniStatement(account);
                    break;
                case "6":
                    handleInterestInfo(account);
                    break;
                case "7":
                    handleAccountInfo(account);
                    break;
                case "8":
                    running = false;
                    printGoodbye(account);
                    break;
                default:
                    System.out.println("  [X] Invalid choice. Please select 1-8.");
            }

            if (running) {
                System.out.println();
                System.out.print("  Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void handleDeposit(Account account, Scanner scanner) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║            [$] DEPOSIT                    ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();

        System.out.print("  Enter deposit amount: Rs.");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            account.deposit(amount);
        } catch (NumberFormatException e) {
            System.out.println("  [X] Invalid amount. Please enter a valid number.");
        }
    }
    private void handleWithdrawal(Account account, Scanner scanner) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║            [$] WITHDRAWAL                 ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.printf("  Current Strategy: %s%n", account.getWithdrawalStrategy().getStrategyName());
        System.out.println();

        System.out.print("  Enter withdrawal amount: Rs.");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            account.performWithdrawal(amount);
        } catch (NumberFormatException e) {
            System.out.println("  [X] Invalid amount. Please enter a valid number.");
        }
    }

    private void handleBalanceInquiry(Account account) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║            [$] BALANCE INQUIRY            ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.printf("  Account Number : %s%n", account.getAccountNumber());
        System.out.printf("  Account Type   : %s%n", account.getAccountType());
        System.out.printf("  Account Holder : %s%n", account.getAccountHolderName());
        System.out.println("  ─────────────────────────────────────────");
        System.out.printf("  Available Balance: Rs.%,.2f%n", account.getBalance());
        System.out.println("  ─────────────────────────────────────────");

        // Record balance inquiry in history
        account.getTransactionHistory().addTransaction(new Transaction(
                TransactionType.BALANCE_INQUIRY, 0, account.getBalance(), "Balance inquiry at ATM"));
    }

    private void handleTransfer(Account account, Scanner scanner) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║            [~] FUND TRANSFER              ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();

        System.out.print("  Enter recipient account number: ");
        String targetAccNum = scanner.nextLine().trim().toUpperCase();

        if (!bank.accountExists(targetAccNum)) {
            System.out.println("  [X] Recipient account not found.");
            return;
        }

        if (targetAccNum.equals(account.getAccountNumber())) {
            System.out.println("  [X] Cannot transfer to the same account.");
            return;
        }

        Account targetAccount = bank.getAccount(targetAccNum);
        System.out.printf("  Recipient: %s (%s)%n", targetAccount.getAccountHolderName(), targetAccount.getAccountType());
        System.out.println();

        System.out.print("  Enter transfer amount: Rs.");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            if (amount <= 0) {
                System.out.println("  [X] Invalid amount. Transfer must be greater than zero.");
                return;
            }

            if (amount > account.getBalance()) {
                System.out.printf("  [X] Insufficient Funds! Available: Rs.%,.2f%n", account.getBalance());
                return;
            }
            double fee = account.getFeeStrategy().applyFee(amount);
            double totalDeduction = amount + fee;

            if (totalDeduction > account.getBalance()) {
                System.out.printf("  [X] Insufficient funds after fee. Need: Rs.%,.2f, Available: Rs.%,.2f%n",
                        totalDeduction, account.getBalance());
                return;
            }
            account.debit(totalDeduction);
            targetAccount.credit(amount);
            account.getTransactionHistory().addTransaction(new Transaction(
                    TransactionType.TRANSFER, amount, account.getBalance(),
                    "Transfer to " + targetAccNum + " (" + targetAccount.getAccountHolderName() + ")"));

            if (fee > 0) {
                account.getTransactionHistory().addTransaction(new Transaction(
                        TransactionType.FEE, fee, account.getBalance(), "Transfer fee"));
            }

            targetAccount.getTransactionHistory().addTransaction(new Transaction(
                    TransactionType.TRANSFER, amount, targetAccount.getBalance(),
                    "Transfer from " + account.getAccountNumber() + " (" + account.getAccountHolderName() + ")"));

            System.out.println();
            System.out.println("  [OK] Transfer successful!");
            System.out.printf("     Amount Sent    : Rs.%,.2f%n", amount);
            if (fee > 0) {
                System.out.printf("     Transaction Fee: Rs.%,.2f%n", fee);
            }
            System.out.printf("     Your Balance   : Rs.%,.2f%n", account.getBalance());

        } catch (NumberFormatException e) {
            System.out.println("  [X] Invalid amount. Please enter a valid number.");
        }
    }

    private void handleMiniStatement(Account account) {
        account.getTransactionHistory().printMiniStatement(MINI_STATEMENT_ENTRIES);
    }
    private void handleInterestInfo(Account account) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║          [^] INTEREST INFORMATION         ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
        System.out.printf("  Account Type: %s%n", account.getAccountType());
        System.out.printf("  Strategy    : %s%n", account.getInterestStrategy().getStrategyName());
        System.out.println();
        account.performInterestCalculation();
    }

    private void handleAccountInfo(Account account) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║          [>] ACCOUNT INFORMATION          ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
        account.displayAccountInfo();
    }

    private void displayMenu(Account account) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║            [ATM] ATM MAIN MENU             ║");
        System.out.println("  ╠══════════════════════════════════════════╣");
        System.out.printf("  ║  Account: %-30s ║%n", account.getAccountNumber() + " | " + account.getAccountType());
        System.out.printf("  ║  Balance: Rs.%-29s ║%n", String.format("%,.2f", account.getBalance()));
        System.out.println("  ╠══════════════════════════════════════════╣");
        System.out.println("  ║                                          ║");
        System.out.println("  ║   [1]  [$]  Deposit                       ║");
        System.out.println("  ║   [2]  [$]  Withdraw                      ║");
        System.out.println("  ║   [3]  [$]  Check Balance                 ║");
        System.out.println("  ║   [4]  [~]  Fund Transfer                 ║");
        System.out.println("  ║   [5]  [=]  Mini Statement                ║");
        System.out.println("  ║   [6]  [^]  Interest Information          ║");
        System.out.println("  ║   [7]  [>]  Account Information           ║");
        System.out.println("  ║   [8]  [>]  Exit                          ║");
        System.out.println("  ║                                          ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }

    public void displayWelcomeBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                          ║");
        System.out.println("  ║       ██████╗ ███████╗ ██████╗ ██████╗ ██████╗ ███████╗  ║");
        System.out.println("  ║       ██╔══██╗██╔════╝██╔════╝██╔═══██╗██╔══██╗██╔════╝  ║");
        System.out.println("  ║       ██║  ██║█████╗  ██║     ██║   ██║██║  ██║█████╗    ║");
        System.out.println("  ║       ██║  ██║██╔══╝  ██║     ██║   ██║██║  ██║██╔══╝    ║");
        System.out.println("  ║       ██████╔╝███████╗╚██████╗╚██████╔╝██████╔╝███████╗  ║");
        System.out.println("  ║       ╚═════╝ ╚══════╝ ╚═════╝ ╚═════╝ ╚═════╝ ╚══════╝  ║");
        System.out.println("  ║                                                          ║");
        System.out.println("  ║               [ATM]  ATM BANKING SYSTEM  [ATM]                 ║");
        System.out.println("  ║                                                          ║");
        System.out.printf("  ║    Bank     : %-40s ║%n", bank.getBankName());
        System.out.printf("  ║    ATM ID   : %-40s ║%n", atmId);
        System.out.printf("  ║    Location : %-40s ║%n", location);
        System.out.println("  ║                                                          ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
    }

    private void printGoodbye(Account account) {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║                                          ║");
        System.out.println("  ║    Thank you for banking with us! **     ║");
        System.out.println("  ║                                          ║");
        System.out.printf("  ║    Final Balance: Rs.%-20s ║%n", String.format("%,.2f", account.getBalance()));
        System.out.println("  ║                                          ║");
        System.out.println("  ║    Please take your card.                ║");
        System.out.println("  ║    Have a great day!                     ║");
        System.out.println("  ║                                          ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }
}
