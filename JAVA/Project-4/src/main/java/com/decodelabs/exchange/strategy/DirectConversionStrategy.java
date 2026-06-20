package com.decodelabs.exchange.strategy;

import com.decodelabs.exchange.model.ConversionRequest;
import com.decodelabs.exchange.model.ConversionResult;

import java.util.Map;

/**
 * Direct Conversion Strategy — one hop, used when either currency is USD.
 *
 * ── Project-3 Parallel ──────────────────────────────────────────
 * Mirrors StandardWithdrawal: the straightforward, no-frills approach.
 * StandardWithdrawal.withdraw() simply debited the account.
 * DirectConversionStrategy.convert() simply multiplies by rate.
 *
 * ── Algorithm ────────────────────────────────────────────────────
 * All Frankfurter rates are relative to USD (USD = 1.0).
 *
 * Unified formula: convertedAmount = amount × (toRate / fromRate)
 *   where: rate of USD = 1.0 always
 *
 * Examples:
 *   USD → INR:  rates["USD"]=1.0, rates["INR"]=83.45
 *               result = 100 × (83.45 / 1.0) = 8,345.00 INR
 *
 *   EUR → USD:  rates["EUR"]=0.92, rates["USD"]=1.0
 *               result = 100 × (1.0 / 0.92)  = 108.70 USD
 *
 * This strategy is selected by ConversionHandler when from==USD or to==USD.
 */
public class DirectConversionStrategy implements ConversionStrategy {

    @Override
    public ConversionResult convert(ConversionRequest request, Map<String, Double> rates) {
        String from   = request.getFromCurrency();
        String to     = request.getToCurrency();
        double amount = request.getAmount();

        // Get rates (USD defaults to 1.0, as it's the base currency)
        double fromRate = rates.getOrDefault(from, 1.0);
        double toRate   = rates.getOrDefault(to,   1.0);

        // Core formula: works for USD→X, X→USD, and same currencies
        double convertedAmount = amount * (toRate / fromRate);
        double exchangeRate    = toRate / fromRate; // 1 from = X to

        return new ConversionResult(
                amount, from, to,
                convertedAmount, exchangeRate,
                getStrategyName(),
                false   // usedPivot = false — single hop
        );
    }

    @Override
    public String getStrategyName() {
        return "Direct Conversion";
    }
}
