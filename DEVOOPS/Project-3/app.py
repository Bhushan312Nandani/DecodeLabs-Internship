"""
=============================================================
  PROJECT-3: Automated Software Factory — Flask Application
  CI/CD Pipeline Demonstration
=============================================================
  This application represents the "product" that moves through
  our automated pipeline:
    Source Code → Build → Test → Package → Deploy
=============================================================
"""

from flask import Flask, jsonify
import os

app = Flask(__name__)

# ------------------------------------------------------------------
# Application Version — bumping this and pushing triggers the
# entire automated pipeline end-to-end (the whole point of CI/CD).
# ------------------------------------------------------------------
APP_VERSION = "3.0.0"
APP_NAME = "DevOps Automated Factory"


@app.route("/")
def home():
    """Root endpoint — health-check for the deployed service."""
    return jsonify({
        "status": "running",
        "app": APP_NAME,
        "version": APP_VERSION,
        "message": "🏭 The Automated Software Factory is LIVE!",
        "pipeline": {
            "ci": "Continuous Integration  — code merged & tested automatically",
            "cd": "Continuous Deployment   — verified build shipped automatically"
        }
    })


@app.route("/health")
def health():
    """
    Health-check endpoint.
    Load-balancers and orchestration tools ping this to decide
    whether to route traffic here.  Returning HTTP 200 = healthy.
    """
    return jsonify({"status": "healthy", "version": APP_VERSION}), 200


@app.route("/pipeline-info")
def pipeline_info():
    """Describes every stage of the CI/CD pipeline for learning purposes."""
    stages = [
        {
            "stage": 1,
            "name": "Source Code Ingest",
            "what": "Developer pushes code → GitHub receives it → workflow triggered",
            "why": "Every change starts the factory — nothing ships without going through the line"
        },
        {
            "stage": 2,
            "name": "Sterile Build Environment",
            "what": "Fresh Ubuntu runner clones the repo and installs dependencies",
            "why": "Eliminates 'works on my machine' — every build is identical"
        },
        {
            "stage": 3,
            "name": "Automated Testing — The Gatekeeper",
            "what": "pytest runs every unit test; pipeline halts on any failure",
            "why": "Broken code can never reach production — the robot enforces quality"
        },
        {
            "stage": 4,
            "name": "Build & Package Artifact",
            "what": "Docker image built and tagged with the commit SHA",
            "why": "Immutable, versioned artifact — you can roll back to any commit"
        },
        {
            "stage": 5,
            "name": "Push to Registry",
            "what": "Image pushed to Docker Hub using secrets stored in GitHub Vault",
            "why": "Central registry — no passwords in code, credentials injected at runtime"
        },
        {
            "stage": 6,
            "name": "Secure SSH Deployment",
            "what": "Pipeline SSHes into production server and pulls the new image",
            "why": "Zero manual steps — the pipeline deploys itself automatically"
        }
    ]
    return jsonify({"pipeline_stages": stages, "total_stages": len(stages)})


# ------------------------------------------------------------------
# Simple calculator — gives the test suite something real to assert.
# ------------------------------------------------------------------
def add(a, b):
    return a + b


def subtract(a, b):
    return a - b


def multiply(a, b):
    return a * b


def divide(a, b):
    if b == 0:
        raise ValueError("Cannot divide by zero")
    return a / b


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    debug = os.environ.get("FLASK_DEBUG", "false").lower() == "true"
    app.run(host="0.0.0.0", port=port, debug=debug)
