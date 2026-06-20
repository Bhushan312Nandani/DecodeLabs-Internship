package com.decodelabs.exchange.event;

/**
 * Event fired when a validation or processing error occurs.
 *
 * ── Project-3 Parallel (Key Requirement) ──────────────────────────
 * In Project-3's handleDeposit():
 *   } catch (NumberFormatException e) {
 *       System.out.println("Invalid amount.");
 *   }
 *
 * Here, instead of printing directly, ConversionHandler publishes this event.
 * MainController subscribes and decides HOW to display the error (shake animation,
 * red label, etc.) — complete separation of concern.
 *
 * "Instead of crashing or outputting nonsensical data, the application must
 *  display an error message and route the user back to the start." — Requirement 2
 */
public class ConversionErrorEvent {

    public enum ErrorType {
        NEGATIVE_AMOUNT,   // User entered a negative number
        INVALID_FORMAT,    // User entered text instead of a number (InputMismatchException equiv.)
        SAME_CURRENCY,     // From and To are identical
        API_ERROR,         // Frankfurter API or network failure
        UNSUPPORTED_CURRENCY
    }

    private final String message;
    private final ErrorType type;

    public ConversionErrorEvent(String message, ErrorType type) {
        this.message = message;
        this.type = type;
    }

    /** Convenience constructor — defaults to API_ERROR type */
    public ConversionErrorEvent(String message) {
        this(message, ErrorType.API_ERROR);
    }

    public String getMessage() { return message; }
    public ErrorType getType() { return type; }
}
