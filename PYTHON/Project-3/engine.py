# =============================================================================
# engine.py — PasswordGenerator (Strategy Context)
# Project-3 | DecodeLabs | Cryptographic Password Generator
# =============================================================================
#
# ARCHITECTURAL ROLE: The Core Processing Engine (P in IPO)
#
# This class IS the Strategy Pattern's "Context":
#   - It holds a reference to whichever strategy is currently active
#   - It delegates pool-building to that strategy
#   - It owns ALL fixed logic: secrets generation, list-join memory model,
#     and mathematical entropy calculation
#
# KEY DECISIONS MADE HERE:
#
#   1. CRYPTOGRAPHIC SECURITY — secrets vs random
#      The standard `random` module is DETERMINISTIC. Given the same seed,
#      it produces the same sequence. An attacker who knows the seed can
#      reproduce every "random" password ever generated.
#      `secrets` uses the OS cryptographic random source (e.g., /dev/urandom
#      on Linux, CryptGenRandom on Windows) — unpredictable by design.
#
#   2. LINEAR TIME COMPLEXITY — list append + join vs string concatenation
#
#      JUNIOR APPROACH (Quadratic overhead):
#          password = ""
#          for _ in range(length):
#              password += char      ← Creates a NEW string object each time!
#                                       Old string is destroyed. Memory thrashes.
#                                       O(n²) total bytes copied.
#
#      ENTERPRISE APPROACH (Linear — O(n)):
#          chars = []
#          for _ in range(length):
#              chars.append(char)    ← O(1) amortised — list grows in-place
#          password = ''.join(chars) ← ONE allocation, ONE pass. Done.
#
#   3. ENTROPY MATHEMATICS
#      H = L × log₂(N)
#      H = entropy in bits
#      L = password length (characters)
#      N = size of the character pool
#      Each additional bit doubles the brute-force search space.
#
# =============================================================================

import secrets  # Cryptographically secure — MANDATORY for authentication
import math     # For log₂ entropy calculation


class PasswordGenerator:
    """
    Strategy Context.
    Knows HOW to generate (secrets, list.join, entropy math).
    Does NOT know WHAT characters to use — it asks the strategy.
    """

    def __init__(self, strategy) -> None:
        """
        Inject a strategy at construction time.
        Any object with a build_pool() method is a valid strategy (duck typing).
        """
        self._strategy = strategy

    # ------------------------------------------------------------------
    # PUBLIC INTERFACE
    # ------------------------------------------------------------------

    def set_strategy(self, strategy) -> None:
        """
        Hot-swap the strategy at runtime without touching any other code.
        This is the Open/Closed Principle in action.
        """
        self._strategy = strategy

    def generate(self, length: int) -> dict:
        """
        Execute the full generation pipeline for the given length.

        Returns a dict (result payload) containing:
            password        : str   — the generated password
            length          : int   — confirmed length
            pool_size       : int   — number of unique characters available
            entropy_bits    : float — H = L × log₂(N)
            strength_label  : str   — human-readable strength tier
            strategy_name   : str   — which strategy was active
        """
        # STEP 1: Ask the strategy for its character pool
        pool      : str = self._strategy.build_pool()
        pool_size : int = len(pool)

        # STEP 2: ENTERPRISE APPROACH — build password using list + join
        #   append() is O(1) amortised (list over-allocates capacity)
        #   join()   is O(n) — single allocation, single pass
        #   Total: O(n) time, O(n) space. Optimal.
        char_accumulator: list = []
        for _ in range(length):
            # secrets.choice() samples from OS-level entropy source
            secure_char = secrets.choice(pool)
            char_accumulator.append(secure_char)

        # ONE join at the very end — not inside the loop
        password: str = ''.join(char_accumulator)

        # STEP 3: Calculate information entropy
        entropy_bits: float = self._calculate_entropy(length, pool_size)

        # STEP 4: Classify strength based on entropy
        strength_label: str = self._classify_strength(entropy_bits)

        # STEP 5: Return a structured result payload (not a raw string)
        return {
            "password"      : password,
            "length"        : length,
            "pool_size"     : pool_size,
            "entropy_bits"  : entropy_bits,
            "strength_label": strength_label,
            "strategy_name" : self._strategy.name,
        }

    # ------------------------------------------------------------------
    # PRIVATE HELPERS
    # ------------------------------------------------------------------

    def _calculate_entropy(self, length: int, pool_size: int) -> float:
        """
        Mathematical provision of security.

        Formula:  H = L × log₂(N)

        Example (Full Charset, length=20):
            N = 94 characters
            H = 20 × log₂(94) = 20 × 6.555 ≈ 131.1 bits
            → An attacker must try 2¹³¹ combinations on average.
        """
        if pool_size <= 1:
            return 0.0
        return length * math.log2(pool_size)

    def _classify_strength(self, entropy_bits: float) -> str:
        """
        Map entropy bits to a human-readable strength tier.
        Thresholds derived from config.py — no magic numbers here.
        """
        from config import ENTROPY_STRONG, ENTROPY_VERY_STRONG, ENTROPY_ELITE

        if entropy_bits >= ENTROPY_ELITE:
            return "[ELITE]       Financial / Government Grade"
        elif entropy_bits >= ENTROPY_VERY_STRONG:
            return "[VERY STRONG] Recommended for High-Security"
        elif entropy_bits >= ENTROPY_STRONG:
            return "[STRONG]      Acceptable Minimum"
        else:
            return "[WEAK]        Increase length or use Full Charset"
