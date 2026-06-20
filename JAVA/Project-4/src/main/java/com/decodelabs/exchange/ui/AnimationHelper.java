package com.decodelabs.exchange.ui;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Reusable JavaFX animation helpers for the currency exchange UI.
 *
 * ── Requirement Coverage ─────────────────────────────────────────
 * Req 2 — Graceful Recovery:
 *   shake() gives tactile feedback when validation fails.
 *   fadeIn() smoothly shows the error label (not jarring).
 *
 * Req 3 — Prevent Infinite Loops / Clear Buffer:
 *   After an error, shake() + clearing the TextField is the GUI
 *   equivalent of scanner.nextLine() clearing the buffer in Project-3.
 *   The user visually understands they must re-enter valid data.
 */
public class AnimationHelper {

    /**
     * Shakes a node left-right — the GUI equivalent of "clearing the scanner buffer".
     * Visual signal: "your input was invalid, please re-enter."
     *
     * Project-3 equivalent:
     *   catch (InputMismatchException e) {
     *       scanner.nextLine(); // ← clear buffer
     *   }
     * Here: the shake IS the "clear buffer" — user sees the rejection.
     *
     * @param node the JavaFX node to shake (typically the converter card)
     */
    public static void shake(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(70), node);
        shake.setFromX(0);
        shake.setByX(12);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> node.setTranslateX(0));
        shake.play();
    }

    /**
     * Fades a node in from transparent to fully opaque.
     * Used for: error labels appearing, result card appearing.
     *
     * @param node     the node to fade in
     */
    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Fades a node out then makes it invisible.
     *
     * @param node the node to fade out
     */
    public static void fadeOut(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(250), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> node.setVisible(false));
        fade.play();
    }

    /**
     * Animates a label's text counting up from 0 to targetValue.
     * Uses ease-out curve for a satisfying "slot machine" effect on the result.
     *
     * @param label       the label to animate
     * @param targetValue the final numeric value to count to
     */
    public static void countUp(Label label, double targetValue) {
        final long   startTime = System.currentTimeMillis();
        final long   duration  = 900L; // milliseconds

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - startTime;

                if (elapsed >= duration) {
                    // Snap to exact final value
                    label.setText(formatResult(targetValue));
                    stop();
                    return;
                }

                // Ease-out cubic: progress slows as it approaches target
                double t      = (double) elapsed / duration;
                double eased  = 1.0 - Math.pow(1.0 - t, 3);
                double current = targetValue * eased;
                label.setText(formatResult(current));
            }
        };
        timer.start();
    }

    /**
     * Formats a result number intelligently:
     * - Large numbers (>1000): 2 decimal places  → "83,450.00"
     * - Small numbers (<0.01): 6 decimal places  → "0.000012"
     * - Otherwise:             4 decimal places  → "83.4567"
     */
    public static String formatResult(double value) {
        if (value >= 1000) {
            return String.format("%,.2f", value);
        } else if (value < 0.01 && value > 0) {
            return String.format("%.6f", value);
        } else {
            return String.format("%,.4f", value);
        }
    }

    /**
     * Brief scale pop — used when result appears (success micro-animation).
     *
     * @param node the node to pop
     */
    public static void pop(Node node) {
        ScaleTransition expand = new ScaleTransition(Duration.millis(180), node);
        expand.setFromX(0.92);
        expand.setFromY(0.92);
        expand.setToX(1.06);
        expand.setToY(1.06);

        ScaleTransition contract = new ScaleTransition(Duration.millis(140), node);
        contract.setFromX(1.06);
        contract.setFromY(1.06);
        contract.setToX(1.0);
        contract.setToY(1.0);

        SequentialTransition seq = new SequentialTransition(node, expand, contract);
        seq.play();
    }

    /**
     * Rotates a node 180° — used for the swap currencies button.
     *
     * @param node the node to rotate
     */
    public static void rotate180(Node node) {
        RotateTransition rotate = new RotateTransition(Duration.millis(280), node);
        rotate.setByAngle(180);
        rotate.setInterpolator(Interpolator.EASE_BOTH);
        rotate.play();
    }

    /**
     * Pulses a node's opacity — used for the loading indicator.
     *
     * @param node the node to pulse
     * @return the running Timeline (call stop() to cancel)
     */
    public static Timeline pulse(Node node) {
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(node.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(700),
                new KeyValue(node.opacityProperty(), 0.3, Interpolator.EASE_BOTH)),
            new KeyFrame(Duration.millis(1400),
                new KeyValue(node.opacityProperty(), 1.0, Interpolator.EASE_BOTH))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();
        return pulse;
    }
}
