package com.decodelabs.exchange.strategy;

import com.decodelabs.exchange.model.ConversionRequest;
import com.decodelabs.exchange.model.ConversionResult;

import java.util.Map;

/**
 * Strategy interface for currency conversion calculation.
 *
 * ── Project-3 Parallel ──────────────────────────────────────────
 *
 *   Project-3 WithdrawalStrategy  ←→  Project-4 ConversionStrategy
 *   ──────────────────────────────────────────────────────────────
 *   withdraw(Account, double)     │  convert(ConversionRequest, Map<String,Double>)
 *   getStrategyName()             │  getStrategyName()
 *   StandardWithdrawal            │  DirectConversionStrategy
 *   LimitedWithdrawal             │  CrossRateStrategy
 *   NoWithdrawal                  │  (error case — handled by event bus)
 *
 * "Program to an interface, not an implementation."
 * ConversionHandler holds a reference to ConversionStrategy — never to the
 * concrete class directly — allowing runtime swap (just like Account did).
 */
public interface ConversionStrategy {

    /**
     * Executes the conversion calculation.
     *
     * @param request the request object containing amount and currency pair
     * @param rates   all exchange rates relative to USD (USD = 1.0 by definition)
     * @return a ConversionResult with the calculated output and metadata
     */
    ConversionResult convert(ConversionRequest request, Map<String, Double> rates);

    /**
     * Human-readable name of this strategy.
     * Displayed in the UI result card and history table.
     * Mirrors getStrategyName() in Project-3's strategy interfaces.
     */
    String getStrategyName();
}
