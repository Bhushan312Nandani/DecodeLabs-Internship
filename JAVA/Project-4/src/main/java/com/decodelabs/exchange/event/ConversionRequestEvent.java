package com.decodelabs.exchange.event;

/**
 * Event fired by the UI when the user submits a conversion request.
 *
 * ── Project-3 Parallel ──────────────────────────────────────────
 * In Project-3, scanner.nextLine() captured user input directly.
 * Here, the UI packages user input into this event and publishes it.
 * ConversionHandler subscribes and processes it — no direct coupling.
 *
 * Immutable: once created, fields cannot change (safe to share across threads).
 */
public class ConversionRequestEvent {

    private final double amount;
    private final String fromCurrency;
    private final String toCurrency;

    public ConversionRequestEvent(double amount, String fromCurrency, String toCurrency) {
        this.amount = amount;
        this.fromCurrency = fromCurrency.toUpperCase().trim();
        this.toCurrency = toCurrency.toUpperCase().trim();
    }

    public double getAmount()        { return amount; }
    public String getFromCurrency()  { return fromCurrency; }
    public String getToCurrency()    { return toCurrency; }

    @Override
    public String toString() {
        return String.format("ConversionRequestEvent{%.2f %s → %s}", amount, fromCurrency, toCurrency);
    }
}
