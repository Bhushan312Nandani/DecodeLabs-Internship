package com.decodelabs.exchange.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable record of a completed currency conversion.
 * Created by the ConversionStrategy and carried inside ConversionResultEvent.
 *
 * Fields:
 *  - inputAmount     : what the user entered
 *  - fromCurrency    : source currency code (e.g. "EUR")
 *  - toCurrency      : target currency code (e.g. "INR")
 *  - convertedAmount : calculated output
 *  - exchangeRate    : effective rate — 1 fromCurrency = exchangeRate toCurrency
 *  - strategyName    : "Direct Conversion" or "Cross-Rate (USD Pivot)"
 *  - usedPivot       : true if CrossRateStrategy was used (A→USD→B)
 *  - timestamp       : when this result was generated
 */
public class ConversionResult {

    private final double inputAmount;
    private final String fromCurrency;
    private final String toCurrency;
    private final double convertedAmount;
    private final double exchangeRate;
    private final String strategyName;
    private final boolean usedPivot;
    private final LocalDateTime timestamp;

    public ConversionResult(
            double inputAmount,
            String fromCurrency,
            String toCurrency,
            double convertedAmount,
            double exchangeRate,
            String strategyName,
            boolean usedPivot) {
        this.inputAmount      = inputAmount;
        this.fromCurrency     = fromCurrency;
        this.toCurrency       = toCurrency;
        this.convertedAmount  = convertedAmount;
        this.exchangeRate     = exchangeRate;
        this.strategyName     = strategyName;
        this.usedPivot        = usedPivot;
        this.timestamp        = LocalDateTime.now();
    }

    public double getInputAmount()      { return inputAmount; }
    public String getFromCurrency()     { return fromCurrency; }
    public String getToCurrency()       { return toCurrency; }
    public double getConvertedAmount()  { return convertedAmount; }
    public double getExchangeRate()     { return exchangeRate; }
    public String getStrategyName()     { return strategyName; }
    public boolean isUsedPivot()        { return usedPivot; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[%s] %.4f %s = %.4f %s (rate: %.6f) via %s",
                getFormattedTimestamp(), inputAmount, fromCurrency,
                convertedAmount, toCurrency, exchangeRate, strategyName);
    }
}
