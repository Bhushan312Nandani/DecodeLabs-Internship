# 🏧 ATM Interface — Strategy Design Pattern

> **DecodeLabs | Java Project 3 | Batch 2026**

A professional Java-based ATM Banking System built using the **Strategy Design Pattern** — the same architectural pattern from *Head First Design Patterns* (Duck example), applied to a real-world banking scenario.

---

## 📐 Architecture — Strategy Pattern

This project implements the Strategy Pattern where **Account** is the abstract class (like `Duck`), each account type is a concrete subclass (like `MallardDuck`, `RubberDuck`), and behaviors are encapsulated in **interchangeable strategy interfaces**.

### The Duck → ATM Mapping

```
┌─────────────────────────┬──────────────────────────────────┐
│     Duck Pattern        │         ATM Pattern              │
├─────────────────────────┼──────────────────────────────────┤
│ Duck (abstract)         │ Account (abstract)               │
│ MallardDuck             │ SavingsAccount                   │
│ RedheadDuck             │ CurrentAccount                   │
│ RubberDuck              │ FixedDepositAccount              │
├─────────────────────────┼──────────────────────────────────┤
│ FlyBehavior (interface) │ WithdrawalStrategy (interface)   │
│ FlyWithWings            │ StandardWithdrawal               │
│ FlyNoFly                │ NoWithdrawal                     │
│ —                       │ LimitedWithdrawal                │
│ —                       │ PenaltyWithdrawal                │
├─────────────────────────┼──────────────────────────────────┤
│ QuackBehavior           │ InterestStrategy (interface)     │
│ Quack                   │ SavingsInterest (4% p.a.)        │
│ Squeak                  │ FixedDepositInterest (7% p.a.)   │
│ MuteQuack               │ NoInterest (0%)                  │
├─────────────────────────┼──────────────────────────────────┤
│ setFlyBehavior()        │ setWithdrawalStrategy()          │
│ setQuackBehavior()      │ setInterestStrategy()            │
└─────────────────────────┴──────────────────────────────────┘
```

### Class Diagram

```
                        ┌─────────────────────┐
                        │   <<abstract>>       │
                        │     Account          │
                        │─────────────────────│
                        │ - accountNumber      │
                        │ - holderName         │
                        │ - pin (private)      │
                        │ - balance (private)  │
                        │─────────────────────│
                        │ # withdrawalStrategy │──────► WithdrawalStrategy (interface)
                        │ # interestStrategy   │──────► InterestStrategy (interface)
                        │ # feeStrategy        │──────► TransactionFeeStrategy (interface)
                        │─────────────────────│
                        │ + deposit()          │
                        │ + performWithdrawal()│
                        │ + performInterest()  │
                        │ + setWithdrawalStrategy() │
                        │ + setInterestStrategy()   │
                        └───────┬───────┬───────┘
                   ┌────────────┼───────┼────────────┐
                   ▼            ▼       ▼            ▼
          ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐
          │SavingsAccount│ │CurrentAccount│ │FixedDepositAccount│
          │ (MallardDuck)│ │ (RedheadDuck)│ │   (RubberDuck)   │
          └──────────────┘ └──────────────┘ └──────────────────┘
```

---

## 🚀 How to Run

### Prerequisites
- **Java JDK 8+** installed
- PowerShell

### Steps

```powershell
# 1. Navigate to project directory
cd "JAVA\Project-3"

# 2. Compile all source files
javac -encoding UTF-8 -d out -sourcepath src src/com/decodelabs/atm/app/ATMApplication.java

# 3. Run the application
java -cp out com.decodelabs.atm.app.ATMApplication
```

---

## 🧪 Demo Accounts

The system comes pre-loaded with 6 demo accounts for testing:

| Account No | Holder Name       | Type          | PIN  | Balance      |
|------------|-------------------|---------------|------|--------------|
| SAV1001    | Aarav Sharma      | Savings       | 1234 | ₹50,000      |
| SAV1002    | Priya Patel       | Savings       | 5678 | ₹1,25,000    |
| CUR2001    | Vikram Industries | Current       | 4321 | ₹5,00,000    |
| CUR2002    | Neha Enterprises  | Current       | 8765 | ₹7,50,000    |
| FD3001     | Rohan Gupta       | Fixed Deposit | 1111 | ₹2,00,000    |
| FD3002     | Ananya Singh      | Fixed Deposit | 2222 | ₹5,00,000    |

### Try These Scenarios

1. **Savings Account (SAV1001)** → Withdraw ₹30,000 → See daily limit rejection (₹25,000 cap)
2. **Current Account (CUR2001)** → Withdraw any amount → See ₹5 flat fee applied
3. **Fixed Deposit (FD3001)** → Try to withdraw → See "Withdrawal Not Permitted" (like `FlyNoFly`)
4. **Wrong PIN × 3** → Account gets frozen via runtime strategy swap to `NoWithdrawal`

---

## 📁 Project Structure

```
Project-3/
├── src/
│   └── com/decodelabs/atm/
│       ├── model/                              # Core Models
│       │   ├── Account.java                    # Abstract class (THE DUCK)
│       │   ├── SavingsAccount.java             # Like MallardDuck
│       │   ├── CurrentAccount.java             # Like RedheadDuck
│       │   ├── FixedDepositAccount.java        # Like RubberDuck
│       │   ├── Transaction.java                # Transaction record
│       │   └── TransactionType.java            # Enum
│       │
│       ├── strategy/                           # Strategy Interfaces & Implementations
│       │   ├── withdrawal/                     # Like FlyBehavior
│       │   │   ├── WithdrawalStrategy.java     # Interface
│       │   │   ├── StandardWithdrawal.java     # Like FlyWithWings
│       │   │   ├── LimitedWithdrawal.java      # Daily limit (₹25,000)
│       │   │   ├── PenaltyWithdrawal.java      # FD early break (1% penalty)
│       │   │   └── NoWithdrawal.java           # Like FlyNoFly
│       │   ├── interest/                       # Like QuackBehavior
│       │   │   ├── InterestStrategy.java       # Interface
│       │   │   ├── SavingsInterest.java        # Like Quack (4% p.a.)
│       │   │   ├── FixedDepositInterest.java   # Like Squeak (7% p.a.)
│       │   │   └── NoInterest.java             # Like MuteQuack (0%)
│       │   └── fee/                            # 3rd Behavior Family
│       │       ├── TransactionFeeStrategy.java # Interface
│       │       ├── NoFee.java                  # Free transactions
│       │       ├── FlatFee.java                # ₹5 per transaction
│       │       └── PercentageFee.java          # 0.1% of amount
│       │
│       ├── service/                            # Business Logic
│       │   ├── ATM.java                        # ATM machine (Client)
│       │   ├── Bank.java                       # Account management
│       │   └── TransactionHistory.java         # Transaction log
│       │
│       └── app/
│           └── ATMApplication.java             # Main entry point
│
├── .gitignore
└── README.md
```

---

## 🔑 Key OOP Concepts Demonstrated

### 1. Strategy Pattern
- **"Program to an interface, not an implementation"**
- Account HAS-A `WithdrawalStrategy`, `InterestStrategy`, `TransactionFeeStrategy`
- Behaviors are encapsulated and interchangeable at runtime

### 2. Abstraction
- `Account` is abstract — cannot be instantiated directly
- Forces subclasses to implement `getAccountType()` and `displayAccountInfo()`

### 3. Encapsulation
- `balance` and `pin` are private — never directly accessible
- PIN is only validated through `validatePin()`, never exposed
- Balance changes only through `credit()` and `debit()` methods

### 4. Inheritance
- `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` extend `Account`
- Inherit shared methods (`deposit()`, `validatePin()`) and override abstract ones

### 5. Polymorphism
- ATM calls `account.performWithdrawal()` without knowing the account type
- The correct strategy executes based on the runtime type

### 6. Runtime Behavior Change
```java
// Freeze an account (runtime strategy swap)
account.setWithdrawalStrategy(new NoWithdrawal());

// Break a Fixed Deposit early
fdAccount.setWithdrawalStrategy(new PenaltyWithdrawal(1.0));
```

---

## ✨ Features

- ✅ Multiple account types with different behaviors
- ✅ PIN-based authentication with 3-attempt lockout
- ✅ Deposit, Withdrawal, Balance Inquiry, Fund Transfer
- ✅ Daily withdrawal limits for Savings accounts
- ✅ Transaction fee system (No Fee / Flat Fee / Percentage Fee)
- ✅ Interest rate information per account type
- ✅ Transaction history with mini-statements
- ✅ Account freezing via runtime strategy swap
- ✅ Professional console UI with formatted output

---

## 🛠 Technologies

- **Language**: Java (JDK 8+)
- **Pattern**: Strategy Design Pattern
- **Paradigm**: Object-Oriented Programming
- **Build**: Manual compilation with `javac`

---

## 👨‍💻 Author

**DecodeLabs** — Industrial Training Kit, Batch 2026

---

## 📜 License

This project is part of the DecodeLabs Java Training Program.
