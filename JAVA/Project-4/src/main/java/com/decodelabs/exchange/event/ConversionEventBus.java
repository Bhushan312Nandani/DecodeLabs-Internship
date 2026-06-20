package com.decodelabs.exchange.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Central event hub — decouples UI from business logic using
 * a publish/subscribe (Observer) pattern.
 *
 * ── Project-3 Parallel ──────────────────────────────────────────
 * In Project-3, ATMApplication directly called atm.handleWithdrawal().
 * That tightly couples the caller to the handler.
 *
 * Here, the UI publishes a ConversionRequestEvent and knows NOTHING
 * about how it will be processed. ConversionHandler subscribes and
 * handles it independently. This is the core of event-driven design.
 *
 * ── Pattern ──────────────────────────────────────────────────────
 * Singleton: one shared bus for the entire application.
 * Thread-safe: ConcurrentHashMap for concurrent access.
 */
public class ConversionEventBus {

    private static final ConversionEventBus INSTANCE = new ConversionEventBus();

    // Maps event type → list of handlers subscribed to that event
    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();

    private ConversionEventBus() {}

    public static ConversionEventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a handler for a specific event type.
     *
     * @param eventType the class of the event to listen for
     * @param handler   the consumer to invoke when the event is published
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>())
                   .add((Consumer<Object>) handler);
    }

    /**
     * Publishes an event to all registered subscribers.
     * All matching handlers are invoked synchronously on the calling thread.
     * UI subscribers should wrap their handler in Platform.runLater() if
     * they are called from a background thread.
     *
     * @param event the event object to broadcast
     */
    public <T> void publish(T event) {
        if (event == null) return;
        List<Consumer<Object>> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            // Create a copy to avoid ConcurrentModificationException
            new ArrayList<>(handlers).forEach(h -> h.accept(event));
        }
    }

    /**
     * Removes all subscribers (useful for cleanup/testing).
     */
    public void clearAll() {
        subscribers.clear();
    }
}
