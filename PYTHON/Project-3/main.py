# =============================================================================
# main.py — Entry Point & IPO Orchestrator
# Project-3 | DecodeLabs | Cryptographic Password Generator
# =============================================================================
#
# ARCHITECTURAL ROLE: Input-Process-Output (IPO) Controller
#
# This file is the ONLY file a user ever runs. It knows nothing about secrets,
# string modules, entropy math, or strategies. It only knows the Facade (service).
# This is Enterprise Architecture: the entry point stays thin and clean.
#
# IPO STRUCTURE:
#   ┌─────────────┐    ┌──────────────────────┐    ┌──────────────────┐
#   │   INPUT     │ →  │      PROCESS         │ →  │     OUTPUT       │
#   │             │    │                      │    │                  │
#   │  Get length │    │  service.generate()  │    │  Print results   │
#   │  Get mode   │    │  (Facade delegates   │    │  Save to file    │
#   │  Validate   │    │   to Strategy +      │    │                  │
#   │             │    │   Engine)            │    │                  │
#   └─────────────┘    └──────────────────────┘    └──────────────────┘
#
# PATTERN ROLES IN THIS SYSTEM:
#   Strategy   → strategies.py  (CharsetStrategy duck-typed variants)
#   Context    → engine.py      (PasswordGenerator — uses the strategy)
#   Facade     → service.py     (PasswordService — orchestrates everything)
#   Config     → config.py      (NIST constants — single source of truth)
#   IPO Entry  → main.py        (this file — user-facing orchestrator)
#
# =============================================================================

from service import PasswordService
from config  import (
    APP_NAME, APP_VERSION, NIST_REF,
    MINIMUM_LENGTH, MAXIMUM_LENGTH,
    KEY_FULL_CHARSET, KEY_ALPHANUMERIC, KEY_PIN_ONLY
)
from strategies import STRATEGY_REGISTRY


# ---------------------------------------------------------------------------
# OUTPUT HELPERS  (pure display functions — no logic)
# ---------------------------------------------------------------------------

def print_banner() -> None:
    """Render the application header."""
    print("=" * 65)
    print(f"  {APP_NAME}")
    print(f"  Version {APP_VERSION}  |  Compliant with {NIST_REF}")
    print("=" * 65)


def print_strategy_menu() -> None:
    """Render the strategy selection menu."""
    print("\n  CHARACTER SET (Select Your Strategy)")
    print("  " + "-" * 42)
    for key, strategy in STRATEGY_REGISTRY.items():
        print(f"  [{key}]  {strategy.name:<16}  —  {strategy.description}")
    print()


def print_result(result: dict) -> None:
    """
    OUTPUT PHASE: Render the complete result payload.
    Reads from the result dict returned by the Facade — no direct engine access.
    """
    print("\n" + "=" * 65)
    print("  GENERATION COMPLETE")
    print("=" * 65)
    print(f"  Strategy      : {result['strategy_name']}")
    print(f"  Length        : {result['length']} characters")
    print(f"  Pool Size     : {result['pool_size']} unique characters")
    print(f"  Entropy       : {result['entropy_bits']:.2f} bits  "
          f"(H = L × log₂(N))")
    print(f"  Strength      : {result['strength_label']}")
    print("-" * 65)
    print(f"  PASSWORD: {result['password']}")
    print("=" * 65)


def save_to_file(result: dict) -> None:
    """
    Persist the result to output.txt for external review.
    Uses a context manager ('with') — guaranteed file closure even on crash.
    """
    output_path = "output.txt"
    with open(output_path, "w") as f:
        f.write(f"DecodeLabs | Cryptographic Password Generator\n")
        f.write(f"Compliance: {NIST_REF}\n")
        f.write("=" * 60 + "\n")
        f.write(f"Strategy     : {result['strategy_name']}\n")
        f.write(f"Length       : {result['length']} characters\n")
        f.write(f"Pool Size    : {result['pool_size']} unique characters\n")
        f.write(f"Entropy      : {result['entropy_bits']:.2f} bits\n")
        f.write(f"Strength     : {result['strength_label']}\n")
        f.write("-" * 60 + "\n")
        f.write(f"PASSWORD: {result['password']}\n")
    print(f"\n  [SAVED] Result written to {output_path}")


# ---------------------------------------------------------------------------
# MAIN — IPO Entry Point
# ---------------------------------------------------------------------------

def main() -> None:
    """
    The entry point. Three clean phases — nothing more.
    All complexity is delegated to PasswordService (Facade).
    """
    service = PasswordService()
    print_banner()

    while True:
        # ==================================================================
        # PHASE 1 — INPUT
        # Capture raw user input. No processing here — only collection.
        # ==================================================================

        # --- Input: Strategy Selection ---
        print_strategy_menu()
        strategy_key = input("  Select strategy [1/2/3]: ").strip()
        if strategy_key not in STRATEGY_REGISTRY:
            print(f"\n  [ERROR] Invalid choice. Please enter 1, 2, or 3.\n")
            continue

        # --- Input: Password Length ---
        print(f"\n  LENGTH  (NIST 2024: {MINIMUM_LENGTH}–{MAXIMUM_LENGTH} characters)")
        raw_length = input("  Enter desired password length: ").strip()

        # ==================================================================
        # PHASE 2 — PROCESS
        # Delegate ALL processing to the Facade. main.py stays ignorant
        # of secrets, string modules, entropy math, and strategy internals.
        # ==================================================================
        try:
            # Validation Gate — raises ValueError on bad input
            validated_length = service.validate_length(raw_length)

            # Generation — Facade coordinates Strategy + Engine
            result = service.generate(validated_length, strategy_key)

        except ValueError as error:
            # STABILITY: Catch the error, inform the user, keep running.
            # The system does NOT crash — it self-recovers.
            print(f"\n  [VALIDATION ERROR] {error}\n")
            continue

        # ==================================================================
        # PHASE 3 — OUTPUT
        # Present results. No computation here — only display and persistence.
        # ==================================================================
        print_result(result)
        save_to_file(result)

        # --- Sentinel: Continue or Exit ---
        print()
        again = input("  Generate another password? [y/n]: ").strip().lower()
        if again != 'y':
            print("\n  [SYSTEM] Session closed. Passwords are ephemeral — store securely.\n")
            break


# ---------------------------------------------------------------------------
# GUARD: Prevent execution when imported as a module
# ---------------------------------------------------------------------------
if __name__ == "__main__":
    main()
