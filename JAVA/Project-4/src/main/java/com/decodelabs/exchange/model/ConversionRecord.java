package com.decodelabs.exchange.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * JavaFX-observable wrapper around ConversionResult.
 * Uses StringProperty fields so TableView columns can bind automatically.
 *
 * When a property changes, the TableView refreshes that cell instantly —
 * this is JavaFX's reactive data binding (replaces manual UI refresh calls).
 *
 * ── Project-3 Parallel ──────────────────────────────────────────
 * Project-3 had TransactionHistory.printMiniStatement() that printed to console.
 * Here, ConversionRecord feeds into an ObservableList that the TableView observes.
 * No manual printing — the table reacts to data changes automatically.
 */
public class ConversionRecord {

    private final StringProperty fromCurrency  = new SimpleStringProperty();
    private final StringProperty toCurrency    = new SimpleStringProperty();
    private final StringProperty amount        = new SimpleStringProperty();
    private final StringProperty result        = new SimpleStringProperty();
    private final StringProperty rate          = new SimpleStringProperty();
    private final StringProperty strategy      = new SimpleStringProperty();
    private final StringProperty time          = new SimpleStringProperty();

    public ConversionRecord(ConversionResult r) {
        fromCurrency.set(r.getFromCurrency());
        toCurrency.set(r.getToCurrency());
        amount.set(String.format("%,.2f %s", r.getInputAmount(), r.getFromCurrency()));
        result.set(String.format("%,.4f %s", r.getConvertedAmount(), r.getToCurrency()));
        rate.set(String.format("1 %s = %.6f %s",
                r.getFromCurrency(), r.getExchangeRate(), r.getToCurrency()));
        strategy.set(r.isUsedPivot() ? "⤷ Cross-Rate (USD Pivot)" : "→ Direct");
        time.set(r.getFormattedTimestamp());
    }

    // Property accessors for TableView cellValueFactory bindings
    public StringProperty fromCurrencyProperty() { return fromCurrency; }
    public StringProperty toCurrencyProperty()   { return toCurrency; }
    public StringProperty amountProperty()        { return amount; }
    public StringProperty resultProperty()        { return result; }
    public StringProperty rateProperty()          { return rate; }
    public StringProperty strategyProperty()      { return strategy; }
    public StringProperty timeProperty()          { return time; }

    // Plain getters (required for PropertyValueFactory fallback)
    public String getFromCurrency() { return fromCurrency.get(); }
    public String getToCurrency()   { return toCurrency.get(); }
    public String getAmount()       { return amount.get(); }
    public String getResult()       { return result.get(); }
    public String getRate()         { return rate.get(); }
    public String getStrategy()     { return strategy.get(); }
    public String getTime()         { return time.get(); }
}
