package com.decodelabs.exchange.service;

import com.decodelabs.exchange.model.ConversionRecord;
import com.decodelabs.exchange.model.ConversionResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Maintains the live in-session conversion history.
 *
 * ── Project-3 Parallel ──────────────────────────────────────────
 * Project-3 used TransactionHistory.printMiniStatement() to print
 * the last N transactions to the console on demand.
 *
 * Here, ConversionHistoryService holds an ObservableList that the
 * TableView OBSERVES directly. Every time addRecord() is called,
 * JavaFX's binding system automatically refreshes the table row.
 * No manual refresh needed — it's reactive data binding.
 *
 * Singleton: one shared history for the entire session.
 * New records are prepended (index 0) so the latest appears at top.
 */
public class ConversionHistoryService {

    private static final ConversionHistoryService INSTANCE = new ConversionHistoryService();

    // ObservableList is watched by JavaFX TableView — changes propagate automatically
    private final ObservableList<ConversionRecord> history =
            FXCollections.observableArrayList();

    private ConversionHistoryService() {}

    public static ConversionHistoryService getInstance() {
        return INSTANCE;
    }

    /**
     * Prepends a new record to the top of the history list.
     * The TableView observing this list will refresh automatically.
     *
     * @param result the completed conversion to log
     */
    public void addRecord(ConversionResult result) {
        history.add(0, new ConversionRecord(result)); // newest at top
    }

    /**
     * Returns the live ObservableList that JavaFX TableView should bind to.
     * Call once in the controller: tableView.setItems(historyService.getHistory())
     */
    public ObservableList<ConversionRecord> getHistory() {
        return history;
    }

    public void clear() {
        history.clear();
    }

    public int getCount() {
        return history.size();
    }
}
