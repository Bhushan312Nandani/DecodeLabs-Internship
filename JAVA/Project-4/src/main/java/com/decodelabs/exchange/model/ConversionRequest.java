package com.decodelabs.exchange.model;

import com.decodelabs.exchange.strategy.ConversionStrategy;

/**
 * Encapsulates one conversion request — amount, currencies, and the strategy to apply.
 *
 * ── Project-3 Parallel (Core Design Mirror) ─────────────────────
 *
 *   Project-3 Account  ←→  Project-4 ConversionRequest
 *   ─────────────────────────────────────────────────────────────
 *   Account HAS-A WithdrawalStrategy    │  Request HAS-A ConversionStrategy
 *   account.setWithdrawalStrategy(...)  │  request.setStrategy(...)   ← runtime swap
 *   account.performWithdrawal(amount)   │  request.getStrategy().convert(request, rates)
 *
 * The strategy is ASSIGNED by ConversionHandler (like ATM assigned strategies),
 * NOT chosen by the request itself — consistent with "Program to an interface".
 */
public class ConversionRequest {

    private final double amount;
    private final String fromCurrency;
    private final String toCurrency;

    // HAS-A relationship — assigned at runtime by ConversionHandler
    // Like: account.setWithdrawalStrategy(new LimitedWithdrawal());
    private ConversionStrategy strategy;

    public ConversionRequest(double amount, String fromCurrency, String toCurrency) {
        this.amount = amount;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    /**
     * Assigns the conversion strategy at runtime.
     * Mirrors: account.setWithdrawalStrategy(new StandardWithdrawal())
     *
     * @param strategy DirectConversionStrategy or CrossRateStrategy
     */
    public void setStrategy(ConversionStrategy strategy) {
        this.strategy = strategy;
    }

    public ConversionStrategy getStrategy() { return strategy; }
    public double getAmount()               { return amount; }
    public String getFromCurrency()         { return fromCurrency; }
    public String getToCurrency()           { return toCurrency; }
}
