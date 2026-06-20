package com.decodelabs.exchange.ui;

import com.decodelabs.exchange.event.*;
import com.decodelabs.exchange.model.ConversionRecord;
import com.decodelabs.exchange.model.ConversionResult;
import com.decodelabs.exchange.service.ConversionHistoryService;
import com.decodelabs.exchange.service.RateService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * JavaFX UI Controller — builds the full interface programmatically and
 * wires all user interactions to the event bus.
 *
 * ── Project-3 Parallel (ATMApplication) ──────────────────────────
 *
 *   Project-3 ATMApplication      ←→  Project-4 MainController
 *   ────────────────────────────────────────────────────────────────
 *   while (systemRunning)          │  JavaFX event loop (always running)
 *   scanner.nextLine()             │  TextField.getText()
 *   switch(choice) { case "1":... }│  button.setOnAction(e -> ...)
 *   System.out.println(result)     │  resultLabel.setText() + animation
 *   try-catch (NumberFormat...)    │  validateInput() + shake animation
 *   scanner.nextLine() after catch │  amountField.clear() after error
 *   "route back to main menu"      │  re-enable Convert button + clear error
 *
 * ── Architecture Role ─────────────────────────────────────────────
 * MainController is PURELY the view layer. It:
 *   1. Publishes events to the bus (ConversionRequestEvent)
 *   2. Subscribes to result/error events from the bus
 *   3. Updates the UI in response — nothing else
 *
 * ALL business logic lives in ConversionHandler + strategies.
 */
public class MainController {

    // ── Currency Registry ─────────────────────────────────────────────────────
    private static final String[] CURRENCIES = {
        "USD", "EUR", "GBP", "INR", "JPY", "AUD", "CAD", "CHF", "CNY", "SGD",
        "AED", "SAR", "MXN", "BRL", "KRW", "HKD", "NOK", "SEK", "DKK", "NZD",
        "ZAR", "THB", "MYR", "IDR", "PHP", "TRY", "PLN", "CZK", "HUF", "BGN",
        "RON", "ISK", "ILS"
    };

    private static final Map<String, String> FLAGS = new HashMap<>();
    static {
        FLAGS.put("USD","🇺🇸"); FLAGS.put("EUR","🇪🇺"); FLAGS.put("GBP","🇬🇧");
        FLAGS.put("INR","🇮🇳"); FLAGS.put("JPY","🇯🇵"); FLAGS.put("AUD","🇦🇺");
        FLAGS.put("CAD","🇨🇦"); FLAGS.put("CHF","🇨🇭"); FLAGS.put("CNY","🇨🇳");
        FLAGS.put("SGD","🇸🇬"); FLAGS.put("AED","🇦🇪"); FLAGS.put("SAR","🇸🇦");
        FLAGS.put("MXN","🇲🇽"); FLAGS.put("BRL","🇧🇷"); FLAGS.put("KRW","🇰🇷");
        FLAGS.put("HKD","🇭🇰"); FLAGS.put("NOK","🇳🇴"); FLAGS.put("SEK","🇸🇪");
        FLAGS.put("DKK","🇩🇰"); FLAGS.put("NZD","🇳🇿"); FLAGS.put("ZAR","🇿🇦");
        FLAGS.put("THB","🇹🇭"); FLAGS.put("MYR","🇲🇾"); FLAGS.put("IDR","🇮🇩");
        FLAGS.put("PHP","🇵🇭"); FLAGS.put("TRY","🇹🇷"); FLAGS.put("PLN","🇵🇱");
        FLAGS.put("CZK","🇨🇿"); FLAGS.put("HUF","🇭🇺"); FLAGS.put("BGN","🇧🇬");
        FLAGS.put("RON","🇷🇴"); FLAGS.put("ISK","🇮🇸"); FLAGS.put("ILS","🇮🇱");
    }

    private static final String[] TICKER_CURRENCIES = {
        "EUR","GBP","INR","JPY","AUD","CAD","CHF","CNY","SGD","AED","KRW","BRL"
    };

    // ── Dependencies ──────────────────────────────────────────────────────────
    private final RateService              rateService;
    private final ConversionEventBus       bus            = ConversionEventBus.getInstance();
    private final ConversionHistoryService historyService = ConversionHistoryService.getInstance();

    // ── Input Components ──────────────────────────────────────────────────────
    private TextField        amountField;
    private ComboBox<String> fromCombo;
    private ComboBox<String> toCombo;
    private Button           convertBtn;
    private Button           swapBtn;
    private Button           refreshBtn;

    // ── Result Panel Components ───────────────────────────────────────────────
    private VBox  resultDisplayBox;
    private VBox  placeholderBox;
    private Label resultAmountLabel;
    private Label resultCurrencyLabel;
    private Label rateInfoLabel;
    private Label strategyLabel;
    private Label pivotStepsLabel;
    private Label timestampLabel;

    // ── Status / Feedback Components ──────────────────────────────────────────
    private Label errorLabel;
    private Label statusLabel;
    private Label tickerLabel;
    private Label rateSourceBadge;

    // ── Layout References ─────────────────────────────────────────────────────
    private VBox converterCard;

    // ── Pulse animation handle (so we can stop it) ────────────────────────────
    private Timeline pulseTimeline;

    public MainController(RateService rateService) {
        this.rateService = rateService;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PUBLIC API — called by ExchangeApplication
    // ══════════════════════════════════════════════════════════════════════════

    /** Builds the complete scene graph. Called before scene is shown. */
    public Parent buildUI() {
        VBox root = new VBox(0);
        root.getStyleClass().add("root-container");
        root.setFillWidth(true);

        root.getChildren().addAll(
            buildHeader(),
            buildTickerBar(),
            buildMainContent(),
            buildHistoryPanel()
        );
        return root;
    }

    /** Wires event subscriptions + starts background rate fetch. Called after stage.show(). */
    public void initialize() {
        // Subscribe: result from bus → update result panel on FX thread
        bus.subscribe(ConversionResultEvent.class, event ->
            Platform.runLater(() -> handleResult(event.getResult()))
        );

        // Subscribe: error from bus → show error + shake on FX thread
        bus.subscribe(ConversionErrorEvent.class, event ->
            Platform.runLater(() -> handleError(event.getMessage(), event.getType()))
        );

        // Subscribe: rate refresh complete → update ticker
        bus.subscribe(RateRefreshEvent.class, event ->
            Platform.runLater(() -> updateTickerDisplay(event.getRates()))
        );

        // Fetch rates on startup (background thread)
        loadRatesAsync(false);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI BUILDERS
    // ══════════════════════════════════════════════════════════════════════════

    private HBox buildHeader() {
        HBox header = new HBox(20);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 28, 14, 28));

        // ── Logo ──
        VBox logoBox = new VBox(3);
        Label appTitle = new Label("DecodeLabs Exchange");
        appTitle.getStyleClass().add("header-title");
        Label appSub = new Label("Java Project 4  •  Event-Driven Architecture  •  Frankfurter API");
        appSub.getStyleClass().add("header-subtitle");
        logoBox.getChildren().addAll(appTitle, appSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ── Status block ──
        VBox statusBox = new VBox(4);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        statusLabel = new Label("⟳  Fetching rates...");
        statusLabel.getStyleClass().add("status-label");
        rateSourceBadge = new Label("ECB via Frankfurter API");
        rateSourceBadge.getStyleClass().add("badge-api");
        statusBox.getChildren().addAll(statusLabel, rateSourceBadge);

        // ── Refresh button ──
        refreshBtn = new Button("⟳  Refresh Rates");
        refreshBtn.getStyleClass().add("btn-refresh");
        refreshBtn.setOnAction(e -> loadRatesAsync(true));

        header.getChildren().addAll(logoBox, spacer, statusBox, refreshBtn);
        return header;
    }

    private HBox buildTickerBar() {
        HBox bar = new HBox(8);
        bar.getStyleClass().add("ticker-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(7, 24, 7, 24));

        Label icon = new Label("📊  LIVE RATES (USD Base):");
        icon.getStyleClass().add("ticker-icon");

        tickerLabel = new Label("Loading...");
        tickerLabel.getStyleClass().add("ticker-text");

        bar.getChildren().addAll(icon, tickerLabel);
        return bar;
    }

    private HBox buildMainContent() {
        HBox content = new HBox(20);
        content.setPadding(new Insets(20, 24, 12, 24));
        content.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);

        converterCard = buildConverterCard();
        VBox resultCard = buildResultCard();

        HBox.setHgrow(converterCard, Priority.ALWAYS);
        HBox.setHgrow(resultCard,    Priority.ALWAYS);

        content.getChildren().addAll(converterCard, resultCard);
        return content;
    }

    private VBox buildConverterCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPrefWidth(460);

        // ── Title ──
        Label title = new Label("💱  CURRENCY CONVERTER");
        title.getStyleClass().add("card-title");
        Label subtitle = new Label("Real-time rates · USD pivot routing · Strategy Pattern");
        subtitle.getStyleClass().add("card-subtitle");

        // ── Amount ──
        Label amtLabel = new Label("AMOUNT");
        amtLabel.getStyleClass().add("field-label");
        amountField = new TextField();
        amountField.setPromptText("Enter amount  (e.g.  100.00)");
        amountField.getStyleClass().add("amount-field");
        amountField.setOnAction(e -> onConvert());
        amountField.textProperty().addListener((obs, old, nv) -> {
            if (errorLabel.isVisible()) hideError();
        });

        // ── Error label (hidden initially) ──
        errorLabel = new Label();
        errorLabel.getStyleClass().addAll("error-label");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        // ── From currency ──
        Label fromLabel = new Label("FROM");
        fromLabel.getStyleClass().add("field-label");
        fromCombo = buildCurrencyCombo("USD");

        // ── Swap row ──
        HBox swapRow = new HBox(10);
        swapRow.setAlignment(Pos.CENTER);
        Label swapArrow = new Label("⇅");
        swapArrow.getStyleClass().add("swap-arrow");
        swapBtn = new Button("Swap Currencies");
        swapBtn.getStyleClass().add("btn-swap");
        swapBtn.setOnAction(e -> onSwap());
        swapRow.getChildren().addAll(swapArrow, swapBtn);

        // ── To currency ──
        Label toLabel = new Label("TO");
        toLabel.getStyleClass().add("field-label");
        toCombo = buildCurrencyCombo("INR");

        // ── Convert button ──
        convertBtn = new Button("Convert  →");
        convertBtn.getStyleClass().add("btn-convert");
        convertBtn.setMaxWidth(Double.MAX_VALUE);
        convertBtn.setPrefHeight(46);
        convertBtn.setOnAction(e -> onConvert());

        // ── Architecture note ──
        Label archNote = new Label(
            "✦  Strategy auto-selected: Direct if USD involved, " +
            "Cross-Rate (A→USD→B pivot) otherwise.");
        archNote.getStyleClass().add("arch-note");
        archNote.setWrapText(true);

        card.getChildren().addAll(
            title, subtitle,
            new Separator(),
            amtLabel, amountField, errorLabel,
            fromLabel, fromCombo,
            swapRow,
            toLabel, toCombo,
            convertBtn,
            new Separator(),
            archNote
        );
        return card;
    }

    private VBox buildResultCard() {
        VBox card = new VBox(0);
        card.getStyleClass().add("card");
        card.setPrefWidth(540);
        card.setAlignment(Pos.CENTER);

        // ── Placeholder (shown before first conversion) ──
        placeholderBox = new VBox(14);
        placeholderBox.setAlignment(Pos.CENTER);
        placeholderBox.setPadding(new Insets(30));
        VBox.setVgrow(placeholderBox, Priority.ALWAYS);

        Label placeholderIcon = new Label("💹");
        placeholderIcon.getStyleClass().add("placeholder-icon");
        Label placeholderMsg = new Label("Your conversion result\nwill appear here");
        placeholderMsg.getStyleClass().add("placeholder-text");
        placeholderMsg.setAlignment(Pos.CENTER);
        Label placeholderHint = new Label("Enter an amount and click Convert");
        placeholderHint.getStyleClass().add("placeholder-hint");
        placeholderBox.getChildren().addAll(placeholderIcon, placeholderMsg, placeholderHint);

        // ── Result display (hidden until first conversion) ──
        resultDisplayBox = new VBox(10);
        resultDisplayBox.setAlignment(Pos.CENTER);
        resultDisplayBox.setVisible(false);
        resultDisplayBox.setPadding(new Insets(20, 24, 20, 24));
        VBox.setVgrow(resultDisplayBox, Priority.ALWAYS);

        Label convertedFromLabel = new Label("Converted Amount");
        convertedFromLabel.getStyleClass().add("result-from-label");

        resultAmountLabel = new Label("—");
        resultAmountLabel.getStyleClass().add("result-amount");

        resultCurrencyLabel = new Label("—");
        resultCurrencyLabel.getStyleClass().add("result-currency");

        Separator sep = new Separator();
        sep.setMaxWidth(320);

        rateInfoLabel = new Label("—");
        rateInfoLabel.getStyleClass().add("rate-info");

        strategyLabel = new Label();
        strategyLabel.getStyleClass().add("strategy-label");
        strategyLabel.setWrapText(true);
        strategyLabel.setAlignment(Pos.CENTER);

        pivotStepsLabel = new Label();
        pivotStepsLabel.getStyleClass().add("pivot-steps");
        pivotStepsLabel.setWrapText(true);
        pivotStepsLabel.setAlignment(Pos.CENTER);

        timestampLabel = new Label();
        timestampLabel.getStyleClass().add("timestamp-label");

        resultDisplayBox.getChildren().addAll(
            convertedFromLabel,
            resultAmountLabel,
            resultCurrencyLabel,
            sep,
            rateInfoLabel,
            strategyLabel,
            pivotStepsLabel,
            timestampLabel
        );

        // Stack placeholder and result in same card space
        StackPane stack = new StackPane(placeholderBox, resultDisplayBox);
        VBox.setVgrow(stack, Priority.ALWAYS);
        card.getChildren().add(stack);

        return card;
    }

    private VBox buildHistoryPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("history-panel");
        panel.setPadding(new Insets(12, 24, 18, 24));
        panel.setPrefHeight(195);

        // ── Header ──
        HBox hdr = new HBox(12);
        hdr.setAlignment(Pos.CENTER_LEFT);
        Label histTitle = new Label("📋  CONVERSION HISTORY");
        histTitle.getStyleClass().add("history-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Button clearBtn = new Button("Clear All");
        clearBtn.getStyleClass().add("btn-clear");
        clearBtn.setOnAction(e -> historyService.clear());
        hdr.getChildren().addAll(histTitle, sp, clearBtn);

        // ── Table ──
        TableView<ConversionRecord> table = buildHistoryTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        panel.getChildren().addAll(hdr, table);
        return panel;
    }

    @SuppressWarnings("unchecked")
    private TableView<ConversionRecord> buildHistoryTable() {
        TableView<ConversionRecord> table = new TableView<>();
        table.getStyleClass().add("history-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label placeholder = new Label("No conversions yet — make your first conversion above!");
        placeholder.getStyleClass().add("table-placeholder");
        table.setPlaceholder(placeholder);

        TableColumn<ConversionRecord, String> fromCol = col("From",      cd -> cd.getValue().fromCurrencyProperty(), 75);
        TableColumn<ConversionRecord, String> toCol   = col("To",        cd -> cd.getValue().toCurrencyProperty(),   75);
        TableColumn<ConversionRecord, String> amtCol  = col("Amount",    cd -> cd.getValue().amountProperty(),       150);
        TableColumn<ConversionRecord, String> resCol  = col("Converted", cd -> cd.getValue().resultProperty(),       165);
        TableColumn<ConversionRecord, String> rateCol = col("Effective Rate", cd -> cd.getValue().rateProperty(),   200);
        TableColumn<ConversionRecord, String> strCol  = col("Strategy",  cd -> cd.getValue().strategyProperty(),    160);
        TableColumn<ConversionRecord, String> timeCol = col("Time",      cd -> cd.getValue().timeProperty(),        85);

        table.getColumns().addAll(fromCol, toCol, amtCol, resCol, rateCol, strCol, timeCol);
        table.setItems(historyService.getHistory());
        return table;
    }

    /** Helper to create a typed TableColumn with a lambda cell value factory. */
    private <T> TableColumn<ConversionRecord, String> col(
            String title,
            javafx.util.Callback<TableColumn.CellDataFeatures<ConversionRecord, String>,
                                  javafx.beans.value.ObservableValue<String>> factory,
            double prefWidth) {
        TableColumn<ConversionRecord, String> c = new TableColumn<>(title);
        c.setCellValueFactory(factory);
        c.setPrefWidth(prefWidth);
        c.setResizable(true);
        return c;
    }

    private ComboBox<String> buildCurrencyCombo(String defaultCurrency) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getStyleClass().add("currency-combo");
        combo.setMaxWidth(Double.MAX_VALUE);

        for (String code : CURRENCIES) {
            String flag = FLAGS.getOrDefault(code, "🏳");
            combo.getItems().add(flag + "  " + code);
        }

        // Set default value
        combo.getItems().stream()
             .filter(item -> item.endsWith(defaultCurrency))
             .findFirst()
             .ifPresent(combo::setValue);

        return combo;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  USER INTERACTION HANDLERS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Called when Convert button is clicked or Enter pressed.
     *
     * ── Requirement 3 Implementation ─────────────────────────────────────────
     * "Handle InputMismatchException: wrap user input logic in a try-catch block."
     * "Clear Scanner Buffer: call scanner.nextLine() to clear invalid token."
     *
     * GUI equivalent:
     *   try { amount = Double.parseDouble(text) }    ← "expects a number"
     *   catch (NumberFormatException e) {             ← "receives a letter/symbol"
     *       amountField.clear();                      ← "scanner.nextLine() to clear buffer"
     *       showError(...); shake(...);               ← "graceful recovery, back to menu"
     *   }
     */
    private void onConvert() {
        hideError();

        String amountText = amountField.getText().trim();

        // Guard: empty field
        if (amountText.isEmpty()) {
            showError("⚠  Please enter an amount to convert.",
                      ConversionErrorEvent.ErrorType.INVALID_FORMAT);
            AnimationHelper.shake(converterCard);
            return;
        }

        // ── REQUIREMENT 3: InputMismatchException equivalent ──────────────────
        // "The program must account for cases where Scanner.nextDouble() expects
        //  a number but receives a letter or symbol."
        double amount;
        try {
            amount = Double.parseDouble(amountText);

        } catch (NumberFormatException e) {
            // ── REQUIREMENT 3: Clear the Scanner Buffer ───────────────────────
            // "Inside the catch block, you must call scanner.nextLine() to clear
            //  the invalid token. If not done, the loop will continuously read
            //  the same stuck token and crash the app."
            //
            // GUI equivalent: clear the TextField so the invalid value is gone.
            // Without this, the same invalid text would be re-submitted on every
            // click — exactly the "infinite loop on stuck token" described.
            amountField.clear();
            amountField.requestFocus();  // route user back to input

            showError("⚠  Invalid input: '" + amountText + "' is not a number.\n" +
                      "Please enter digits only  (e.g. 100, 1500.50, 0.75)",
                      ConversionErrorEvent.ErrorType.INVALID_FORMAT);
            AnimationHelper.shake(converterCard);
            return;
        }

        // ── REQUIREMENT 2: Reject Negative Amounts ────────────────────────────
        // "The system must anticipate user error by immediately rejecting negative inputs."
        // "Instead of crashing or outputting negative/nonsensical money data..."
        if (amount < 0) {
            amountField.clear();
            amountField.requestFocus();
            showError("⚠  Negative amounts are not allowed.\n" +
                      "Currency values must be positive  (e.g. 100, 1500.50)",
                      ConversionErrorEvent.ErrorType.NEGATIVE_AMOUNT);
            AnimationHelper.shake(converterCard);
            return;
        }

        // Get selected currencies — strip flag emoji prefix ("🇺🇸  USD" → "USD")
        String fromCurrency = extractCode(fromCombo.getValue());
        String toCurrency   = extractCode(toCombo.getValue());

        if (fromCurrency == null || toCurrency == null) {
            showError("⚠  Please select both currencies.", ConversionErrorEvent.ErrorType.INVALID_FORMAT);
            return;
        }

        // Optimistic UI: disable button, show loading state
        convertBtn.setDisable(true);
        convertBtn.setText("Converting...");

        // ── Publish event — decoupled from processing ──────────────────────
        // ConversionHandler receives this and runs the strategy on a background thread.
        // This is the event-driven equivalent of: atm.handleWithdrawal(account, scanner)
        bus.publish(new ConversionRequestEvent(amount, fromCurrency, toCurrency));
    }

    /** Swaps the from/to currency selections with a rotate animation. */
    private void onSwap() {
        String fromVal = fromCombo.getValue();
        String toVal   = toCombo.getValue();
        fromCombo.setValue(toVal);
        toCombo.setValue(fromVal);
        AnimationHelper.rotate180(swapBtn);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EVENT BUS SUBSCRIBERS (called on JavaFX Application Thread)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Handles a successful ConversionResult published by ConversionHandler.
     *
     * ── Requirement 2: Graceful Recovery ────────────────────────────────────
     * After any previous error, the form is reset and the result is shown cleanly.
     * This is "route the user back to the start of the main menu" — in GUI form.
     */
    private void handleResult(ConversionResult result) {
        // Reset form state
        convertBtn.setDisable(false);
        convertBtn.setText("Convert  →");
        hideError();

        // Populate result fields
        resultCurrencyLabel.setText(result.getToCurrency());
        rateInfoLabel.setText(String.format("1 %s  =  %s %s",
                result.getFromCurrency(),
                AnimationHelper.formatResult(result.getExchangeRate()),
                result.getToCurrency()));

        if (result.isUsedPivot()) {
            strategyLabel.setText("⤷ Cross-Rate Strategy (USD Pivot)");
            pivotStepsLabel.setText(String.format(
                "%s  →  [USD]  →  %s",
                result.getFromCurrency(), result.getToCurrency()));
            pivotStepsLabel.setVisible(true);
        } else {
            strategyLabel.setText("⤷ Direct Conversion Strategy");
            pivotStepsLabel.setText(String.format(
                "%s  →  %s",
                result.getFromCurrency(), result.getToCurrency()));
            pivotStepsLabel.setVisible(true);
        }

        timestampLabel.setText("Updated " + result.getFormattedTimestamp()
                + "  •  " + result.getStrategyName());

        // Switch from placeholder to result display
        placeholderBox.setVisible(false);
        resultDisplayBox.setVisible(true);

        // Animate: count-up + pop effect
        AnimationHelper.countUp(resultAmountLabel, result.getConvertedAmount());
        AnimationHelper.fadeIn(resultDisplayBox);
        AnimationHelper.pop(resultAmountLabel);
    }

    /**
     * Handles an error event published by ConversionHandler or onConvert().
     *
     * ── Requirement 2 & 3 ────────────────────────────────────────────────────
     * "Display an error message and route the user back to the start."
     * "Prevent Infinite Loops" — after error, Convert button is re-enabled
     * so the user can try again without being stuck.
     */
    private void handleError(String message, ConversionErrorEvent.ErrorType type) {
        convertBtn.setDisable(false);
        convertBtn.setText("Convert  →");

        showError(message, type);
        AnimationHelper.shake(converterCard);

        // Route user back (clear invalid input for format errors)
        if (type == ConversionErrorEvent.ErrorType.INVALID_FORMAT ||
            type == ConversionErrorEvent.ErrorType.NEGATIVE_AMOUNT) {
            amountField.clear();
            amountField.requestFocus();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BACKGROUND TASKS
    // ══════════════════════════════════════════════════════════════════════════

    private void loadRatesAsync(boolean forceRefresh) {
        refreshBtn.setDisable(true);
        statusLabel.setText("⟳  Refreshing rates...");

        if (pulseTimeline != null) pulseTimeline.stop();
        pulseTimeline = AnimationHelper.pulse(refreshBtn);

        CompletableFuture.supplyAsync(() ->
            forceRefresh ? rateService.forceRefresh() : rateService.getRates()
        ).thenAccept(rates ->
            Platform.runLater(() -> {
                if (pulseTimeline != null) { pulseTimeline.stop(); pulseTimeline = null; }
                refreshBtn.setDisable(false);

                LocalDateTime fetchedAt = rateService.getLastFetchedTime();
                boolean live = rateService.isUsingLiveRates();

                statusLabel.setText(live
                    ? "✓  Rates updated " + fetchedAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    : "⚠  Offline mode — using representative rates");

                rateSourceBadge.setText(live
                    ? "LIVE  •  ECB via Frankfurter API  •  " + rateService.getCachedCurrencyCount() + " currencies"
                    : "OFFLINE  •  Fallback rates active");
                rateSourceBadge.getStyleClass().removeAll("badge-api", "badge-offline");
                rateSourceBadge.getStyleClass().add(live ? "badge-api" : "badge-offline");

                updateTickerDisplay(rates);
                bus.publish(new RateRefreshEvent(rates));
            })
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                if (pulseTimeline != null) { pulseTimeline.stop(); pulseTimeline = null; }
                refreshBtn.setDisable(false);
                statusLabel.setText("✗  Rate fetch failed");
            });
            return null;
        });
    }

    private void updateTickerDisplay(Map<String, Double> rates) {
        StringBuilder sb = new StringBuilder();
        for (String code : TICKER_CURRENCIES) {
            if (rates.containsKey(code)) {
                String flag = FLAGS.getOrDefault(code, "");
                sb.append(String.format("  %s %s: %.4f   │", flag, code, rates.get(code)));
            }
        }
        tickerLabel.setText(sb.toString());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UTILITY HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    /** Extracts "USD" from "🇺🇸  USD" combo item string. */
    private String extractCode(String comboValue) {
        if (comboValue == null || comboValue.isBlank()) return null;
        String[] parts = comboValue.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    private void showError(String message, ConversionErrorEvent.ErrorType type) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.getStyleClass().removeAll("error-negative", "error-format", "error-info");
        switch (type) {
            case NEGATIVE_AMOUNT -> errorLabel.getStyleClass().add("error-negative");
            case SAME_CURRENCY   -> errorLabel.getStyleClass().add("error-info");
            default              -> errorLabel.getStyleClass().add("error-format");
        }
        AnimationHelper.fadeIn(errorLabel);
    }

    private void hideError() {
        errorLabel.setVisible(false);
    }
}
