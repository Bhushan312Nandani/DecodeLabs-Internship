# =============================================================================
# config.py — System Constants & NIST 2024 Compliance Registry
# Project-3 | DecodeLabs | Cryptographic Password Generator
# =============================================================================
#
# ARCHITECTURAL ROLE: Static Configuration Registry
#   Every magic number lives HERE. No other file hard-codes a value.
#   This is the "single source of truth" principle of Enterprise Architecture.
#   Change MINIMUM_LENGTH once here — the entire system adapts.
#
# =============================================================================

# ---------------------------------------------------------------------------
# APPLICATION IDENTITY
# ---------------------------------------------------------------------------
APP_NAME    : str = "DecodeLabs :: Cryptographic Password Generator"
APP_VERSION : str = "1.0.0"
NIST_REF    : str = "NIST SP 800-63B (2024 Revision)"

# ---------------------------------------------------------------------------
# NIST SP 800-63B (2024) — Password Length Boundaries
#
# WHY LENGTH OVER COMPLEXITY?
#   Legacy rules (force 1 uppercase + 1 symbol + 1 digit) are well-intentioned
#   but backfire. Humans satisfy them with predictable patterns:
#       "Password1!"  →  technically compliant, trivially guessable.
#
#   NIST 2024 abolishes forced complexity. Length IS the primary defence
#   because entropy scales with EVERY added character exponentially.
# ---------------------------------------------------------------------------
MINIMUM_LENGTH : int = 15    # Below this, brute-force becomes feasible
MAXIMUM_LENGTH : int = 64    # Upper bound per NIST SP 800-63B guideline

# ---------------------------------------------------------------------------
# ENTROPY THRESHOLDS  —  H = L × log₂(N)
#   H = entropy in bits
#   L = password length
#   N = character pool size
#
# Every additional bit DOUBLES the brute-force search space.
# Modern GPU clusters test ~10¹² guesses/sec → 80 bits buys ~37 years.
# ---------------------------------------------------------------------------
ENTROPY_STRONG      : float = 60.0    # Acceptable minimum
ENTROPY_VERY_STRONG : float = 80.0    # Recommended for high-security
ENTROPY_ELITE       : float = 100.0   # Financial / government grade

# ---------------------------------------------------------------------------
# STRATEGY KEYS — used in main.py menu to select a CharsetStrategy
# ---------------------------------------------------------------------------
KEY_FULL_CHARSET  : str = "1"    # Letters + Digits + Symbols
KEY_ALPHANUMERIC  : str = "2"    # Letters + Digits only
KEY_PIN_ONLY      : str = "3"    # Digits only (numeric PIN)
