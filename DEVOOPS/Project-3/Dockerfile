# =============================================================
#  PROJECT-3: Dockerfile
#  Creates an immutable, versioned artifact — the deployable
#  package produced at Stage 4 of the pipeline.
#
#  Multi-stage build: keeps the final image small & secure
#    Stage 1 (builder) — installs deps, runs nothing sensitive
#    Stage 2 (runtime) — copies only what's needed to run
# =============================================================

# ── Stage 1: Builder ─────────────────────────────────────────
FROM python:3.11-slim AS builder

# Prevent Python from writing .pyc files to disk
ENV PYTHONDONTWRITEBYTECODE=1
# Prevent Python from buffering stdout/stderr (essential for CI logs)
ENV PYTHONUNBUFFERED=1

WORKDIR /build

# Copy ONLY requirements first — Docker layer-caches this.
# If requirements.txt hasn't changed, pip install is skipped → fast builds.
COPY requirements.txt .
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM python:3.11-slim AS runtime

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Security: don't run as root inside the container
RUN useradd --create-home --shell /bin/bash appuser
USER appuser

WORKDIR /app

# Copy installed packages from builder
COPY --from=builder /usr/local/lib/python3.11/site-packages /usr/local/lib/python3.11/site-packages
COPY --from=builder /usr/local/bin /usr/local/bin

# Copy application source
COPY --chown=appuser:appuser app.py .

# Expose the port the app listens on
EXPOSE 5000

# Health-check — Docker itself monitors the container's health
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:5000/health')" || exit 1

# The command that starts the factory
CMD ["python", "app.py"]
