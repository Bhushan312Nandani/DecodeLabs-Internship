"""
=============================================================
  PROJECT-3: TEST SUITE — The Ultimate Gatekeeper
=============================================================
  These tests are the HEART of CI.  The pipeline runs them
  automatically on every push.  If ANY test fails, the build
  stops — broken code is NEVER shipped to production.

  Run locally:   pytest tests/ -v
  Run in CI:     pytest tests/ -v --tb=short (see ci-cd.yml)
=============================================================
"""

import pytest
import json
import sys
import os

# Make sure the app module is importable from the project root
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app import app, add, subtract, multiply, divide


# ──────────────────────────────────────────────────────────────────
#  FIXTURE — provides a test client that mimics a browser/curl call
# ──────────────────────────────────────────────────────────────────
@pytest.fixture
def client():
    """
    Flask test client.
    TESTING=True disables error catching so exceptions surface
    directly into the test output — easier to debug in CI logs.
    """
    app.config["TESTING"] = True
    with app.test_client() as client:
        yield client


# ──────────────────────────────────────────────────────────────────
#  GROUP 1 — API / HTTP Endpoint Tests
# ──────────────────────────────────────────────────────────────────

class TestAPIEndpoints:
    """Validates every HTTP route the application exposes."""

    def test_home_returns_200(self, client):
        """The root endpoint must return HTTP 200 — pipeline health check."""
        response = client.get("/")
        assert response.status_code == 200, (
            f"Expected 200 but got {response.status_code}. "
            "Root endpoint is down — deployment would fail health check."
        )

    def test_home_json_structure(self, client):
        """Home endpoint must return valid JSON with required fields."""
        response = client.get("/")
        data = json.loads(response.data)

        required_fields = ["status", "app", "version", "message", "pipeline"]
        for field in required_fields:
            assert field in data, (
                f"Missing field '{field}' in API response. "
                "Contract violation — consumers of this API will break."
            )

    def test_home_status_is_running(self, client):
        """Status field must be 'running' — signals the factory is active."""
        response = client.get("/")
        data = json.loads(response.data)
        assert data["status"] == "running"

    def test_home_has_pipeline_keys(self, client):
        """Pipeline object must contain both 'ci' and 'cd' keys."""
        response = client.get("/")
        data = json.loads(response.data)
        assert "ci" in data["pipeline"]
        assert "cd" in data["pipeline"]

    def test_health_endpoint_returns_200(self, client):
        """
        /health must return 200.
        This is what Kubernetes / load balancers ping — if it fails
        the container is taken offline automatically.
        """
        response = client.get("/health")
        assert response.status_code == 200

    def test_health_returns_healthy(self, client):
        """/health body must report {'status': 'healthy'}."""
        response = client.get("/health")
        data = json.loads(response.data)
        assert data["status"] == "healthy"

    def test_pipeline_info_endpoint(self, client):
        """/pipeline-info must return all 6 pipeline stages."""
        response = client.get("/pipeline-info")
        assert response.status_code == 200
        data = json.loads(response.data)
        assert data["total_stages"] == 6
        assert len(data["pipeline_stages"]) == 6

    def test_pipeline_stage_has_required_keys(self, client):
        """Each pipeline stage must have: stage, name, what, why."""
        response = client.get("/pipeline-info")
        data = json.loads(response.data)
        for stage in data["pipeline_stages"]:
            for key in ["stage", "name", "what", "why"]:
                assert key in stage, (
                    f"Stage missing key '{key}': {stage}"
                )

    def test_unknown_route_returns_404(self, client):
        """Flask must return 404 for unknown routes — no silent failures."""
        response = client.get("/this-does-not-exist")
        assert response.status_code == 404


# ──────────────────────────────────────────────────────────────────
#  GROUP 2 — Business Logic / Unit Tests
# ──────────────────────────────────────────────────────────────────

class TestCalculatorFunctions:
    """
    Unit tests for the calculator module.
    These test pure logic — no HTTP, no Flask, just functions.
    Unit tests run in milliseconds; they're the fastest gatekeeper.
    """

    # --- add ---
    def test_add_positive_numbers(self):
        assert add(3, 5) == 8

    def test_add_negative_numbers(self):
        assert add(-3, -5) == -8

    def test_add_zero(self):
        assert add(10, 0) == 10

    def test_add_floats(self):
        assert add(1.5, 2.5) == pytest.approx(4.0)

    # --- subtract ---
    def test_subtract_basic(self):
        assert subtract(10, 4) == 6

    def test_subtract_to_negative(self):
        assert subtract(3, 10) == -7

    def test_subtract_same_number(self):
        assert subtract(7, 7) == 0

    # --- multiply ---
    def test_multiply_basic(self):
        assert multiply(4, 5) == 20

    def test_multiply_by_zero(self):
        assert multiply(999, 0) == 0

    def test_multiply_negatives(self):
        assert multiply(-3, -4) == 12

    def test_multiply_mixed_signs(self):
        assert multiply(-3, 4) == -12

    # --- divide ---
    def test_divide_basic(self):
        assert divide(10, 2) == 5.0

    def test_divide_float_result(self):
        assert divide(7, 2) == pytest.approx(3.5)

    def test_divide_by_zero_raises(self):
        """
        The Gatekeeper catches this:
        If divide() silently returned 0 or None instead of raising,
        callers would silently produce wrong results — a classic bug
        that automated tests catch before users ever see it.
        """
        with pytest.raises(ValueError, match="Cannot divide by zero"):
            divide(10, 0)

    def test_divide_negative(self):
        assert divide(-10, 2) == -5.0


# ──────────────────────────────────────────────────────────────────
#  GROUP 3 — Integration / Content Tests
# ──────────────────────────────────────────────────────────────────

class TestIntegration:
    """
    Integration tests check that different parts of the system
    work together correctly — the app + its config + its routes.
    """

    def test_app_version_is_set(self, client):
        """Version must be present so we can track what's deployed."""
        response = client.get("/")
        data = json.loads(response.data)
        assert data["version"] != ""
        assert data["version"] is not None

    def test_app_version_format(self, client):
        """Version must follow semantic versioning: MAJOR.MINOR.PATCH."""
        response = client.get("/")
        data = json.loads(response.data)
        parts = data["version"].split(".")
        assert len(parts) == 3, (
            f"Version '{data['version']}' is not semver — expected X.Y.Z format"
        )
        for part in parts:
            assert part.isdigit(), f"Non-numeric version segment: '{part}'"

    def test_content_type_is_json(self, client):
        """All API responses must declare application/json content-type."""
        for route in ["/", "/health", "/pipeline-info"]:
            response = client.get(route)
            assert "application/json" in response.content_type, (
                f"Route '{route}' returned content-type: {response.content_type}"
            )

    def test_pipeline_stages_are_ordered(self, client):
        """Stages must be in order 1..N — ordering matters for the conveyor belt."""
        response = client.get("/pipeline-info")
        data = json.loads(response.data)
        stage_numbers = [s["stage"] for s in data["pipeline_stages"]]
        assert stage_numbers == sorted(stage_numbers), (
            "Pipeline stages are out of order!"
        )
