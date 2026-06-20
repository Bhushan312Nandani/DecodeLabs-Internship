package com.decodelabs.exchange.service;

import com.decodelabs.exchange.event.*;
import com.decodelabs.exchange.model.ConversionRequest;
import com.decodelabs.exchange.model.ConversionResult;
import com.decodelabs.exchange.strategy.ConversionStrategy;
import com.decodelabs.exchange.strategy.CrossRateStrategy;
import com.decodelabs.exchange.strategy.DirectConversionStrategy;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The core processing engine — subscribes to ConversionRequestEvent,
 * validates the request, selects the right strategy, executes it,
 * and publishes the result or error back on the event bus.
 *
 * ── Project-3 Parallel (ATM class) ──────────────────────────────
 *
 *   Project-3 ATM              ←→  Project-4 ConversionHandler
 *   ────────────────────────────────────────────────────────────
 *   showMainMenu() loop         │  handleRequest() (event-triggered)
 *   authenticateUser()          │  validateRequest() (amount checks)
 *   handleWithdrawal()          │  executeConversion()
 *   account.performWithdrawal() │  request.getStrategy().convert()
 *   printing to console         │  publishing ConversionResultEvent
 *
 * ── Threading Model ──────────────────────────────────────────────
 * ConversionHandler runs the strategy on a CompletableFuture background thread
 * to keep the JavaFX UI thread responsive during API calls and computation.
 *
 * UI updates happen via Platform.runLater() inside MainController's subscribers.
 *
 * ── Requirement Coverage ─────────────────────────────────────────
 * Req 1: Direct + Cross-Rate routing — selectStrategy() method
 * Req 2: Negative amount rejection   — validation block in handleRequest()
 * Req 3: Exception handling           — try-catch in CompletableFuture supplier
 *         (NumberFormatException = InputMismatchException equivalent in GUI)
 */
public class ConversionHandler {

    private final RateService              rateService;
    private final ConversionHistoryService historyService;
    private final ConversionEventBus       bus;

    // Strategy instances — reused (stateless), like Project-3's strategy objects
    private final DirectConversionStrategy directStrategy    = new DirectConversionStrategy();
    private final CrossRateStrategy        crossRateStrategy = new CrossRateStrategy();

    public ConversionHandler(RateService rateService) {
        this.rateService    = rateService;
        this.historyService = ConversionHistoryService.getInstance();
        this.bus            = ConversionEventBus.getInstance();

        // Subscribe to incoming conversion requests from the UI
        bus.subscribe(ConversionRequestEvent.class, this::handleRequest);

        System.out.println("[ConversionHandler] Subscribed to ConversionRequestEvent — ready.");
    }

    // ── Main Handler ──────────────────────────────────────────────────────────

    /**
     * Invoked when the UI publishes a ConversionRequestEvent.
     * Validates, selects strategy, executes, then publishes result or error.
     */
    private void handleRequest(ConversionRequestEvent event) {
        System.out.printf("[ConversionHandler] Received: %.2f %s → %s%n",
                event.getAmount(), event.getFromCurrency(), event.getToCurrency());

        // ── REQUIREMENT 2: Reject Negative Amounts ────────────────────────
        // "The system must anticipate user error by immediately rejecting
        //  negative currency inputs."
        // This mirrors Project-3's Account.deposit() which checked: if (amount <= 0)
        if (event.getAmount() < 0) {
            bus.publish(new ConversionErrorEvent(
                "⚠  Negative amounts are not allowed.\n" +
                "Currency values must be a positive number (e.g. 100, 1500.50).",
                ConversionErrorEvent.ErrorType.NEGATIVE_AMOUNT
            ));
            return;
        }

        if (event.getAmount() == 0) {
            bus.publish(new ConversionErrorEvent(
                "⚠  Amount must be greater than zero.",
                ConversionErrorEvent.ErrorType.NEGATIVE_AMOUNT
            ));
            return;
        }

        if (event.getFromCurrency().equals(event.getToCurrency())) {
            bus.publish(new ConversionErrorEvent(
                "ℹ  Source and target currencies are the same — no conversion needed.",
                ConversionErrorEvent.ErrorType.SAME_CURRENCY
            ));
            return;
        }

        // ── Run computation on background thread (non-blocking) ───────────
        CompletableFuture.supplyAsync(() -> {
            try {
                // Fetch rates (cached or live from Frankfurter API)
                Map<String, Double> rates = rateService.getRates();

                // Build request object — HAS-A strategy (like Account HAS-A WithdrawalStrategy)
                ConversionRequest request = new ConversionRequest(
                        event.getAmount(),
                        event.getFromCurrency(),
                        event.getToCurrency()
                );

                // ── REQUIREMENT 1: Strategy Selection ─────────────────────
                // "Use USD as a central pivot currency."
                // Direct: if either currency IS USD (no pivot needed)
                // Cross-Rate: all other pairs route through USD as intermediate
                ConversionStrategy strategy = selectStrategy(
                        request.getFromCurrency(), request.getToCurrency());

                // Assign to request — mirrors: account.setWithdrawalStrategy(new LimitedWithdrawal())
                request.setStrategy(strategy);

                // Delegate to strategy — mirrors: account.performWithdrawal(amount)
                return request.getStrategy().convert(request, rates);

            } catch (NumberFormatException e) {
                // ── REQUIREMENT 3: Handle InputMismatchException equivalent ──
                // "The program must account for cases where Scanner.nextDouble()
                //  throws InputMismatchException."
                // In a GUI, NumberFormatException is equivalent.
                // "You are required to wrap user input logic in a try-catch block."
                throw new RuntimeException(
                    "⚠  Invalid number format detected in processing: " + e.getMessage(), e);

            } catch (IllegalArgumentException e) {
                throw new RuntimeException("⚠  " + e.getMessage(), e);

            } catch (Exception e) {
                throw new RuntimeException(
                    "⚠  Conversion failed: " + e.getMessage(), e);
            }

        }).thenAccept(result -> {
            // Success — log to history and broadcast result
            historyService.addRecord(result);
            bus.publish(new ConversionResultEvent(result));
            System.out.println("[ConversionHandler] ✓ " + result);

        }).exceptionally(ex -> {
            // ── REQUIREMENT 3: Graceful Recovery ─────────────────────────
            // "Instead of crashing, display an error message and route user back."
            // "Prevent infinite loops" — CompletableFuture.exceptionally() handles
            //  all exceptions without looping back into the same broken state.
            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            bus.publish(new ConversionErrorEvent(
                msg != null ? msg : "An unexpected error occurred.",
                ConversionErrorEvent.ErrorType.API_ERROR
            ));
            System.err.println("[ConversionHandler] ✗ Error: " + msg);
            return null;
        });
    }

    // ── Strategy Selector ─────────────────────────────────────────────────────

    /**
     * Selects the conversion strategy based on the currency pair.
     *
     * ── Requirement 1 Logic ───────────────────────────────────────────────
     * "To make the application scalable without hardcoding every currency pair,
     *  use USD as a central pivot currency."
     *
     * from==USD  →  USD → X (direct, toRate = rates[X])
     * to==USD    →  X → USD (direct, 1/fromRate)
     * else       →  A → USD → B (cross-rate pivot)
     *
     * Mirrors ATM.selectAccount() or the strategy assigned in Account constructors.
     */
    private ConversionStrategy selectStrategy(String from, String to) {
        if ("USD".equals(from) || "USD".equals(to)) {
            // Direct — one hop, no intermediate pivot needed
            return directStrategy;
        }
        // Cross-Rate — two hops via USD pivot
        return crossRateStrategy;
    }
}
