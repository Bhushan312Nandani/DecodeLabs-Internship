package com.decodelabs.exchange.event;

import com.decodelabs.exchange.model.ConversionResult;

/**
 * Event fired by ConversionHandler after a successful conversion.
 * MainController subscribes to update the result panel.
 *
 * Wraps ConversionResult — the calculated output from the strategy.
 */
public class ConversionResultEvent {

    private final ConversionResult result;

    public ConversionResultEvent(ConversionResult result) {
        this.result = result;
    }

    public ConversionResult getResult() { return result; }
}
