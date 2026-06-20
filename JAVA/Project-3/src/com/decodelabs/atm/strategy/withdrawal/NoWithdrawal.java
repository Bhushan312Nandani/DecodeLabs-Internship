package com.decodelabs.atm.strategy.withdrawal;

import com.decodelabs.atm.model.Account;

public class NoWithdrawal implements WithdrawalStrategy {

    @Override
    public boolean withdraw(Account account, double amount) {
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║  [X] WITHDRAWAL NOT PERMITTED             ║");
        System.out.println("  ║                                          ║");
        System.out.println("  ║  This account does not allow withdrawals ║");
        System.out.println("  ║  at this time. Possible reasons:         ║");
        System.out.println("  ║                                          ║");
        System.out.println("  ║  • Fixed Deposit has not matured         ║");
        System.out.println("  ║  • Account is temporarily frozen         ║");
        System.out.println("  ║                                          ║");
        System.out.println("  ║  Please contact your bank branch for     ║");
        System.out.println("  ║  assistance.                             ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        return false;
    }

    @Override
    public String getStrategyName() {
        return "No Withdrawal (Account Locked)";
    }
}
