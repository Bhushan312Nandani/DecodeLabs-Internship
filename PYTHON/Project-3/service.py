# =============================================================================
# service.py — PasswordService (Facade Pattern)
# Project-3 | DecodeLabs | Cryptographic Password Generator
# =============================================================================
#
# PATTERN: Facade (Gang of Four — Structural)
#
# PROBLEM IT SOLVES:
#   The system has multiple sub-systems:
#       - Input validation (NIST boundary checks)
#       - Strategy registry lookup
#       - PasswordGenerator (engine)
#       - Entropy calculation (inside engine)
#   Without a Facade, main.py would need to import and coordinate all of them.
#   Any change to one sub-system would cascade into main.py.
#
# SOLUTION (Facade):
#   PasswordService is a single, simple API surface. main.py only ever calls:
#       service.validate_length(raw)   →  validated integer or raises ValueError
#       service.generate(length, key)  →  full result dict
#   The Facade hides ALL internal complexity. main.py stays clean.
#
# HOW FACADE SUPPORTS STRATEGY:
#   The Facade is NOT a strategy itself. It is the "shop front" that:
#     1. Receives the user's strategy key ("1", "2", or "3")
#     2. Looks it up in STRATEGY_REGISTRY
#     3. Passes the correct strategy to PasswordGenerator (the Context)
#     4. Returns the finished result
#
# RELATIONSHIP DIAGRAM:
#
#   main.py
#     │
#     │  calls ──►  PasswordService  (Facade — this file)
#                        │
#                        ├── validates via config.py constants
#                        ├── looks up   STRATEGY_REGISTRY (strategies.py)
#                        └── delegates  PasswordGenerator.generate() (engine.py)
#
# =============================================================================

from config     import MINIMUM_LENGTH, MAXIMUM_LENGTH, NIST_REF
from strategies import STRATEGY_REGISTRY
from engine     import PasswordGenerator


class PasswordService:
    """
    Facade over the entire password generation sub-system.
    One class. Two public methods. Zero complexity exposed to main.py.
    """

    # ------------------------------------------------------------------
    # PHASE: INPUT VALIDATION
    # ------------------------------------------------------------------

    def validate_length(self, raw_input: str) -> int:
        """
        Environmental Validation Gate.

        Transforms raw string input into a safe, NIST-compliant integer.
        Raises ValueError with an informative message on any failure.
        This is the "Digital Poka-Yoke" — mistake-proofing at the boundary.

        Validation chain:
            1. Is it an integer at all?         → catches "hello", "12.5"
            2. Is it within NIST bounds?        → catches 5, 200
        """
        # GUARD 1: Type safety — can this string become an integer?
        try:
            length = int(raw_input)
        except ValueError:
            raise ValueError(
                f"[INVALID INPUT] '{raw_input}' is not an integer. "
                f"Please enter a whole number."
            )

        # GUARD 2: NIST 2024 lower boundary
        if length < MINIMUM_LENGTH:
            raise ValueError(
                f"[NIST VIOLATION] Length {length} is below the minimum of "
                f"{MINIMUM_LENGTH} characters required by {NIST_REF}."
            )

        # GUARD 3: NIST 2024 upper boundary
        if length > MAXIMUM_LENGTH:
            raise ValueError(
                f"[NIST VIOLATION] Length {length} exceeds the maximum of "
                f"{MAXIMUM_LENGTH} characters defined by {NIST_REF}."
            )

        return length   # Safe, validated integer

    # ------------------------------------------------------------------
    # PHASE: PROCESS (orchestration)
    # ------------------------------------------------------------------

    def generate(self, length: int, strategy_key: str) -> dict:
        """
        Full generation pipeline orchestrated by the Facade.

        Steps:
            1. Resolve strategy key → concrete strategy object
            2. Inject strategy into PasswordGenerator (Context)
            3. Execute generation
            4. Return the complete result payload

        Returns the dict produced by PasswordGenerator.generate():
            {password, length, pool_size, entropy_bits,
             strength_label, strategy_name}
        """
        # STEP 1: Resolve strategy — no if/elif chain; registry does the work
        strategy = STRATEGY_REGISTRY.get(strategy_key)
        if strategy is None:
            raise ValueError(
                f"[INVALID STRATEGY] Key '{strategy_key}' not found in registry."
            )

        # STEP 2: Construct Context and inject the selected strategy
        generator = PasswordGenerator(strategy)

        # STEP 3: Delegate generation — Facade stays thin, engine does the work
        result = generator.generate(length)

        return result
