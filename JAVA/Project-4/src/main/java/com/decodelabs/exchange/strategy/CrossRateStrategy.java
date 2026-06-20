package com.decodelabs.exchange.strategy;

import com.decodelabs.exchange.model.ConversionRequest;
import com.decodelabs.exchange.model.ConversionResult;

import java.util.Map;

/**
 * Cross-Rate Strategy — converts via USD as an intermediate pivot currency.
 * Used when NEITHER the source NOR the target is USD.
 *
 * ── Project-3 Parallel ──────────────────────────────────────────
 * Mirrors LimitedWithdrawal: additional rules and intermediate steps apply.
 * LimitedWithdrawal checked daily limits before allowing withdrawal.
 * CrossRateStrategy routes through USD before reaching the target currency.
 *
 * ── Why USD as Pivot? (Requirement 1 — Cross-Rate Routing) ───────
 * To avoid storing every possible currency pair (32 currencies = 992 pairs!),
 * we only store rates from USD. Any A→B conversion is decomposed into:
 *   A → USD → B
 *
 * This keeps the system scalable — adding one new currency only adds ONE rate.
 *
 * ── Algorithm (Step by Step) ─────────────────────────────────────
 * All rates in the map mean: 1 USD = X units of that currency.
 *   rates["EUR"] = 0.92  →  1 USD = 0.92 EUR  →  1 EUR = (1/0.92) USD
 *   rates["INR"] = 83.45 →  1 USD = 83.45 INR
 *
 * Step 1 — Source → USD:
 *   usdValue = amount / rates[from]
 *   e.g. 100 EUR / 0.92 = 108.70 USD
 *
 * Step 2 — USD → Target:
 *   result = usdValue × rates[to]
 *   e.g. 108.70 USD × 83.45 = 9,070.5 INR
 *
 * Combined (simplified):
 *   result = amount × (rates[to] / rates[from])
 *   exchangeRate = rates[to] / rates[from]
 *
 * Example: 100 EUR → INR
 *   usdStep   : 100 / 0.92   = 108.70 USD
 *   inrStep   : 108.70 × 83.45 = 9,070.5 INR
 *   effectiveRate: 83.45 / 0.92 = 90.70 INR per EUR
 */
public class CrossRateStrategy implements ConversionStrategy {

    @Override
    public ConversionResult convert(ConversionRequest request, Map<String, Double> rates) {
        String from   = request.getFromCurrency();
        String to     = request.getToCurrency();
        double amount = request.getAmount();

        if (!rates.containsKey(from)) {
            throw new IllegalArgumentException("Unsupported source currency: " + from);
        }
        if (!rates.containsKey(to)) {
            throw new IllegalArgumentException("Unsupported target currency: " + to);
        }

        double fromRate = rates.get(from); // 1 USD = fromRate of 'from' currency
        double toRate   = rates.get(to);   // 1 USD = toRate of 'to' currency

        // ── Step 1: from → USD ─────────────────────────────────────────────
        double usdEquivalent = amount / fromRate;

        // ── Step 2: USD → to ──────────────────────────────────────────────
        double convertedAmount = usdEquivalent * toRate;

        // Effective rate: 1 unit of 'from' = how many units of 'to'
        double exchangeRate = toRate / fromRate;

        return new ConversionResult(
                amount, from, to,
                convertedAmount, exchangeRate,
                getStrategyName(),
                true  // usedPivot = true — routed through USD
        );
    }

    @Override
    public String getStrategyName() {
        return "Cross-Rate (USD Pivot)";
    }
}
