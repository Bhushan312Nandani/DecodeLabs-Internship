# =============================================================================
# strategies.py — Strategy Pattern (GoF Behavioral)
# Project-3 | DecodeLabs | Cryptographic Password Generator
# =============================================================================
#
# PATTERN: Strategy (Gang of Four — Behavioral)
#
# PROBLEM IT SOLVES:
#   The password generator has ONE axis of variability:
#       "What characters should be in the pool?"
#   Without Strategy, we'd write a bloated if/elif chain inside the generator
#   every time a new mode is needed. Adding "PIN mode" means editing the core.
#   That violates the Open/Closed Principle (open for extension, closed for
#   modification).
#
# SOLUTION:
#   Encapsulate each character-pool algorithm in its own class (a Strategy).
#   The generator (Context) calls strategy.build_pool() — it doesn't know or
#   care which concrete strategy is active. Swap strategies freely.
#
# DUCK TYPING (Python's version of the Strategy interface):
#   Python doesn't need a formal interface. If an object has build_pool(),
#   name, and description — it IS a valid strategy. This is duck typing:
#       "If it walks like a duck and quacks like a duck, it's a duck."
#   No inheritance required. Pure structural compatibility.
#
# CLASS DIAGRAM:
#
#   PasswordGenerator (Context in engine.py)
#          │
#          │  uses ──►  <duck-typed strategy>
#          │                      │
#          │         ┌────────────┼─────────────┐
#          │         ▼            ▼              ▼
#          │  FullCharset    Alphanumeric      PinOnly
#          │  Strategy       Strategy          Strategy
#          │
#          └── calls strategy.build_pool() → gets character pool string
#
# =============================================================================

import string  # Standard library — do NOT manually type character sets


# ---------------------------------------------------------------------------
# STRATEGY 1: Full Charset
# Pool: lowercase + uppercase + digits + punctuation symbols
# Best for: high-security authentication tokens, account passwords
# ---------------------------------------------------------------------------
class FullCharsetStrategy:
    """
    Strategy: Maximum entropy pool.
    Uses the string module to standardise all four character classes.
    Manual typing (e.g. 'abcdefg...') is an anti-pattern — error-prone
    and unmaintainable. string.ascii_letters is the enterprise standard.
    """

    name        : str = "Full Charset"
    description : str = "Lowercase + Uppercase + Digits + Symbols (Max Entropy)"

    def build_pool(self) -> str:
        """
        Construct and return the character pool as a single string.
        string module guarantees correctness — no missed or duplicated chars.
        """
        # ENTERPRISE APPROACH: Use string module constants, not manual typing
        letters = string.ascii_letters      # 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
        digits  = string.digits             # '0123456789'
        symbols = string.punctuation        # '!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~'

        # Concatenation here is VALID — it happens ONCE at pool-build time,
        # not inside the generation loop. No memory overhead concern.
        return letters + digits + symbols


# ---------------------------------------------------------------------------
# STRATEGY 2: Alphanumeric
# Pool: lowercase + uppercase + digits (no symbols)
# Best for: APIs where symbols break URL encoding, usernames, tokens
# ---------------------------------------------------------------------------
class AlphanumericStrategy:
    """
    Strategy: Symbol-free pool.
    Balances strong entropy with broad compatibility (URLs, forms, databases).
    """

    name        : str = "Alphanumeric"
    description : str = "Lowercase + Uppercase + Digits (No Symbols)"

    def build_pool(self) -> str:
        letters = string.ascii_letters   # 52 characters
        digits  = string.digits          # 10 characters
        return letters + digits          # Pool of 62 characters


# ---------------------------------------------------------------------------
# STRATEGY 3: PIN Only
# Pool: digits only
# Best for: numeric PINs, OTPs, short verification codes
# Note: Very low entropy — ONLY viable with extremely short use-cases
# ---------------------------------------------------------------------------
class PinOnlyStrategy:
    """
    Strategy: Digits-only pool.
    Entropy is low (log₂(10) ≈ 3.32 bits/char) — the system will warn the
    user about this. Included to demonstrate Strategy's open/closed principle:
    adding a new mode required ZERO changes to the generator or service.
    """

    name        : str = "PIN / Numeric"
    description : str = "Digits Only (0-9) — Low Entropy, Use with Caution"

    def build_pool(self) -> str:
        return string.digits    # '0123456789' — 10 characters


# ---------------------------------------------------------------------------
# STRATEGY REGISTRY
# Maps the user's menu key → the corresponding strategy instance.
# The Facade (service.py) imports this dict — no if/elif chains anywhere.
# ---------------------------------------------------------------------------
from config import KEY_FULL_CHARSET, KEY_ALPHANUMERIC, KEY_PIN_ONLY

STRATEGY_REGISTRY: dict = {
    KEY_FULL_CHARSET : FullCharsetStrategy(),
    KEY_ALPHANUMERIC : AlphanumericStrategy(),
    KEY_PIN_ONLY     : PinOnlyStrategy(),
}
