package com.decodelabs.exchange.app;

import com.decodelabs.exchange.service.ConversionHandler;
import com.decodelabs.exchange.service.RateService;
import com.decodelabs.exchange.ui.MainController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX Application entry point for the DecodeLabs Currency Exchange System.
 *
 * ── Architecture Role ─────────────────────────────────────────────────────
 * ExchangeApplication is the "main method" equivalent of the traditional
 * Project-3 console runner. It:
 *   1. Bootstraps all services (RateService, ConversionHandler)
 *   2. Creates the MainController (pure view layer)
 *   3. Wires the scene and shows the stage
 *
 * ── Project-3 Parallel ────────────────────────────────────────────────────
 *   Project-3 ATMApplication.main()     →  ExchangeApplication.start()
 *   new Scanner(System.in)              →  JavaFX event loop
 *   atm.start()                         →  mainController.initialize()
 *   System.exit(0)                      →  stage.setOnCloseRequest(...)
 *
 * ── Threading ─────────────────────────────────────────────────────────────
 * All services are instantiated on the JavaFX Application Thread here.
 * ConversionHandler internally uses CompletableFuture for non-blocking API calls.
 * Platform.runLater() in MainController ensures UI updates happen on FX thread.
 */
public class ExchangeApplication extends Application {

    /** JavaFX Application window dimensions */
    private static final double MIN_WIDTH  = 980;
    private static final double MIN_HEIGHT = 700;
    private static final double PREF_WIDTH  = 1180;
    private static final double PREF_HEIGHT = 780;

    @Override
    public void start(Stage primaryStage) {
        // ── 1. Bootstrap Services ────────────────────────────────────────────
        // RateService: fetches & caches exchange rates from Frankfurter API
        RateService rateService = new RateService();

        // ConversionHandler: subscribes to bus, selects strategy, processes conversions
        // Constructed here so it registers its bus subscription before any events arrive.
        new ConversionHandler(rateService);

        // ── 2. Build UI ──────────────────────────────────────────────────────
        // MainController constructs the entire scene graph programmatically.
        // No FXML needed — all layout and styling done via Java + CSS style classes.
        MainController controller = new MainController(rateService);
        Parent root = controller.buildUI();

        // ── 3. Apply Stylesheet ──────────────────────────────────────────────
        // styles.css provides the dark premium theme with glassmorphism cards.
        String css = getClass().getResource("/com/decodelabs/exchange/styles.css").toExternalForm();
        root.getStylesheets().add(css);

        // ── 4. Configure Scene & Stage ───────────────────────────────────────
        Scene scene = new Scene(root, PREF_WIDTH, PREF_HEIGHT);
        primaryStage.setTitle("DecodeLabs Exchange  ·  Event-Driven Currency Converter  ·  Java Project 4");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        // Graceful shutdown — mirrors Project-3's: systemRunning = false; break;
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("[ExchangeApplication] Window closed — shutting down.");
            javafx.application.Platform.exit();
        });

        primaryStage.show();

        // ── 5. Post-show initialization ──────────────────────────────────────
        // Wire event subscriptions and trigger the initial rate fetch AFTER the
        // stage is visible (guarantees FX thread is fully running).
        controller.initialize();

        System.out.println("[ExchangeApplication] DecodeLabs Exchange started successfully.");
    }

    /**
     * Application entry point.
     *
     * On Java 17+ with JavaFX as a module, launching via the Maven JavaFX plugin
     * (mvn javafx:run) automatically handles the module path. If running from an
     * IDE, ensure JavaFX JVM args are configured (see README.md).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
