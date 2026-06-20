package com.decodelabs.atm.app;

import com.decodelabs.atm.model.Account;
import com.decodelabs.atm.service.ATM;
import com.decodelabs.atm.service.Bank;

import java.util.Scanner;


public class ATMApplication {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ── Initialize Bank and ATM ──
        Bank bank = new Bank("DecodeLabs National Bank");
        ATM atm = new ATM(bank, "ATM-DL-001", "Greater Lucknow, India");

        // ── Display Welcome Banner ──
        atm.displayWelcomeBanner();

        // ── Main ATM Loop ──
        boolean systemRunning = true;

        while (systemRunning) {
            System.out.println();
            System.out.println("  ╔══════════════════════════════════════════╗");
            System.out.println("  ║          [ATM] ATM HOME SCREEN             ║");
            System.out.println("  ╠══════════════════════════════════════════╣");
            System.out.println("  ║                                          ║");
            System.out.println("  ║   [1]  [*]  Insert Card (Login)           ║");
            System.out.println("  ║   [2]  [i]  View Demo Accounts           ║");
            System.out.println("  ║   [3]  [X]  Shutdown ATM                  ║");
            System.out.println("  ║                                          ║");
            System.out.println("  ╚══════════════════════════════════════════╝");
            System.out.println();
            System.out.print("  Enter your choice (1-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // ── Authenticate User ──
                    Account account = atm.authenticateUser(scanner);

                    if (account != null) {
                        // ── Show Main Menu (all operations delegate to strategies) ──
                        atm.showMainMenu(account, scanner);
                    }
                    break;

                case "2":
                    // ── Show available demo accounts for testing ──
                    bank.displayAvailableAccounts();
                    break;

                case "3":
                    systemRunning = false;
                    System.out.println();
                    System.out.println("  ╔══════════════════════════════════════════════════════════╗");
                    System.out.println("  ║                                                          ║");
                    System.out.println("  ║            [>] ATM SYSTEM SHUTTING DOWN...                ║");
                    System.out.println("  ║                                                          ║");
                    System.out.println("  ║        Thank you for using DecodeLabs ATM.               ║");
                    System.out.println("  ║        Strategy Pattern Implementation                   ║");
                    System.out.println("  ║        Java Project 3 — Batch 2026                       ║");
                    System.out.println("  ║                                                          ║");
                    System.out.println("  ╚══════════════════════════════════════════════════════════╝");
                    System.out.println();
                    break;

                default:
                    System.out.println("  [X] Invalid choice. Please select 1-3.");
            }
        }

        scanner.close();
    }
}
