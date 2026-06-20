package com.decodelabs.exchange.event;

import java.util.Collections;
import java.util.Map;

/**
 * Event fired when the RateService successfully fetches/refreshes exchange rates.
 * The UI subscribes to update the live ticker bar with new rate data.
 */
public class RateRefreshEvent {

    private final Map<String, Double> rates;

    public RateRefreshEvent(Map<String, Double> rates) {
        this.rates = Collections.unmodifiableMap(rates);
    }

    /** Returns an unmodifiable snapshot of the refreshed rates (relative to USD = 1.0). */
    public Map<String, Double> getRates() { return rates; }
}
