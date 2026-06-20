# DecodeLabs Exchange — Project 4
### Event-Driven Currency Converter · JavaFX 21 · Strategy Pattern · Frankfurter API

---

## Overview

**Project 4** is an event-driven desktop currency exchange application built in Java 17 with a JavaFX 21 GUI. It elevates the architectural lessons of **Project 3** (ATM System — Strategy Pattern + Encapsulation) into a richer, decoupled system using the **Observer / Event Bus** pattern.

The application fetches **live exchange rates** from the free [Frankfurter API](https://www.frankfurter.app/) (powered by the ECB), routes conversions through a **USD pivot strategy**, and displays results with smooth animations in a dark premium UI.

---

## How Project 3 Maps to Project 4

| Project 3 Concept | Project 4 Equivalent |
|---|---|
| `WithdrawalStrategy` interface | `ConversionStrategy` interface (Direct / CrossRate) |
| `Account` (HAS-A Strategy) | `ConversionRequest` (HAS-A ConversionStrategy) |
| `ATM` routes to strategy | `ConversionEventBus` fires events → `ConversionHandler` processes |
| `Scanner` loop + `try-catch` | JavaFX `TextField` validators + `ConversionErrorEvent` |
| `scanner.nextLine()` in catch | `amountField.clear()` clears invalid GUI input |
| "Route back to main menu" | Re-enable Convert button + clear error after recovery |
| `TransactionHistory` log | `ConversionHistoryService` maintains live `ObservableList` |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        JavaFX UI Layer                          │
│  MainController.java (builds scene graph, handles events)       │
│  AnimationHelper.java (shake, countUp, fadeIn, rotate, pulse)   │
└────────────────────┬────────────────────────────────────────────┘
                     │ fires ConversionRequestEvent
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Event Bus Layer                               │
│  ConversionEventBus (publish/subscribe, thread-safe)            │
│  Events: ConversionRequestEvent, ConversionResultEvent,         │
│          ConversionErrorEvent, RateRefreshEvent                 │
└──────────┬──────────────────────┬──────────────────────────────┘
           │                      │
           ▼                      ▼
┌──────────────────┐   ┌──────────────────────────────────────────┐
│ RateService       │   │         ConversionHandler                │
│ (Frankfurter API) │   │  selects strategy via ConversionContext  │
│ HttpClient + JSON │   │  ┌──────────────────────────────────┐   │
│ Cache (1 hr TTL)  │   │  │ ConversionStrategy (interface)   │   │
└──────────────────┘   │  ├──────────────────────────────────┤   │
                        │  │ DirectConversionStrategy         │   │
                        │  │ CrossRateStrategy (USD pivot)    │   │
                        │  └──────────────────────────────────┘   │
                        └──────────────────────────────────────────┘
                                        │
                                        ▼
                        ┌──────────────────────────────────────────┐
                        │     ConversionHistoryService             │
                        │  ObservableList<ConversionRecord>        │
                        │  (bound live to TableView)               │
                        └──────────────────────────────────────────┘
```

---

## Project Structure

```
Project-4/
├── pom.xml                               ← Maven: JavaFX 21 + Jackson + javafx-maven-plugin
├── README.md
└── src/main/
    ├── java/com/decodelabs/exchange/
    │   ├── app/
    │   │   └── ExchangeApplication.java  ← JavaFX entry point (bootstrap + stage setup)
    │   ├── model/
    │   │   ├── ConversionRequest.java    ← HAS-A ConversionStrategy (parallel to Account)
    │   │   ├── ConversionResult.java     ← Immutable result record with metadata
    │   │   └── ConversionRecord.java     ← JavaFX Observable for TableView binding
    │   ├── strategy/
    │   │   ├── ConversionStrategy.java   ← Interface (parallel to WithdrawalStrategy)
    │   │   ├── DirectConversionStrategy.java  ← USD↔X one-hop conversion
    │   │   └── CrossRateStrategy.java    ← A→USD→B two-hop pivot logic
    │   ├── event/
    │   │   ├── ConversionEventBus.java   ← Thread-safe publish/subscribe hub
    │   │   ├── ConversionRequestEvent.java
    │   │   ├── ConversionResultEvent.java
    │   │   ├── ConversionErrorEvent.java
    │   │   └── RateRefreshEvent.java
    │   ├── service/
    │   │   ├── RateService.java          ← Frankfurter API + 1-hr cache
    │   │   ├── ConversionHandler.java    ← Bus subscriber → strategy executor
    │   │   └── ConversionHistoryService.java ← Singleton ObservableList
    │   └── ui/
    │       ├── MainController.java       ← Full JavaFX UI, programmatic scene graph
    │       └── AnimationHelper.java      ← Reusable animation utilities
    └── resources/com/decodelabs/exchange/
        └── styles.css                    ← Dark premium glassmorphism theme
```

---

## Requirements Implemented

### Requirement 1 — Dual Strategy Conversion (Direct + Cross-Rate)
- **Direct Strategy**: used when either currency is USD → `amount × rate`
- **Cross-Rate Strategy**: used for all other pairs → `A → USD → B` (pivot)
- Strategy is auto-selected at runtime in `ConversionHandler.selectStrategy()`
- The same `ConversionStrategy` interface from Project 3's `WithdrawalStrategy`

### Requirement 2 — Negative Amount Rejection + Graceful Recovery
- Both `MainController.onConvert()` and `ConversionHandler.handleRequest()` validate
- Negative amounts → `ConversionErrorEvent.NEGATIVE_AMOUNT` → red error label + shake
- After error: Convert button re-enabled, field cleared, user routed back to input
- GUI equivalent of: "display error, return to main menu" from Project 3

### Requirement 3 — InputMismatchException / Clear Scanner Buffer
- `Double.parseDouble()` throws `NumberFormatException` ≡ `Scanner.nextDouble()` `InputMismatchException`
- Caught in `onConvert()`: `amountField.clear()` + `requestFocus()` = `scanner.nextLine()`
- Also caught in `ConversionHandler`'s `CompletableFuture` → published as `ConversionErrorEvent`
- Visual shake animation reinforces "you must re-enter valid data"

---

## API: Frankfurter (Free, No Key Required)

| Endpoint | Purpose |
|---|---|
| `https://api.frankfurter.dev/v1/latest?base=USD` | All rates from USD base |
| `https://api.frankfurter.dev/v1/latest?base=EUR&symbols=INR` | Point pair rate |
| `https://api.frankfurter.dev/v1/currencies` | All available currencies |

- **Cache TTL**: 1 hour — avoids hammering the API on repeat conversions
- **Offline Fallback**: Representative static rates used if network unavailable
- **33 currencies** supported including USD, EUR, GBP, INR, JPY, AED, and more

---

## Build & Run

### Prerequisites
- Java 17+ (JDK)
- Maven 3.9+
- Internet connection (for live rates; fallback rates used offline)

### Commands
```powershell
# Navigate to project
cd "DECODE-LAB\JAVA\Project-4"

# Build (skip tests)
mvn clean package -q

# Run the application
mvn javafx:run
```

### IDE Setup (IntelliJ / Eclipse)
If running directly from IDE, add these JVM arguments:
```
--module-path <path-to-javafx-sdk>/lib
--add-modules javafx.controls,javafx.fxml
--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED
```

---

## UI Features

- **Dark glassmorphism theme** — deep navy/violet palette
- **Live ticker bar** — shows 12 major currencies in real time
- **33+ currencies** with emoji flag indicators in dropdowns
- **Swap button** — flips from/to currencies with a rotate animation
- **Animated result** — count-up number animation + green glow on success
- **Shake animation** — on validation error (visual = scanner buffer clear)
- **Error labels** — color-coded: red (format), orange (negative), blue (info)
- **Conversion history table** — live-bound `ObservableList`, timestamp per record
- **Offline mode badge** — shown when Frankfurter API is unreachable
- **Refresh button** — fetches fresh rates on demand with pulse animation

---

## Tech Stack

| Tool | Choice |
|---|---|
| Language | Java 17 |
| Build | Maven 3.9 |
| UI | JavaFX 21 (openjfx) |
| JSON | Jackson Databind 2.15 |
| HTTP | `java.net.http.HttpClient` (Java 11+ built-in) |
| API | [Frankfurter.dev](https://www.frankfurter.dev/) (free, ECB-sourced, migrated from frankfurter.app) |
| Architecture | Event-Driven + Strategy Pattern + Observer |

---

## Test Scenarios

| # | Scenario | Expected Result |
|---|---|---|
| 1 | USD → INR, amount = 100 | Direct Strategy, result ≈ 100 × USD/INR rate |
| 2 | EUR → INR, amount = 50 | Cross-Rate: EUR→USD→INR pivot shown |
| 3 | Enter `-100` | Shake + orange error label, field cleared |
| 4 | Enter `abc` | Shake + red error label, field cleared |
| 5 | Same currency | Blue info label, no conversion |
| 6 | Refresh button | Rates re-fetched from Frankfurter API |
| 7 | History table | All conversions logged with timestamp + strategy |
| 8 | Clear All | History table emptied |

---

*DecodeLabs Exchange — Project 4 · Java 17 · JavaFX 21 · Event-Driven Architecture*
