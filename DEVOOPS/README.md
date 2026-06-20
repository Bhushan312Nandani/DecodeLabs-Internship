# ⚙️ DevOps & CI/CD

## Overview
**The Goal:** To eliminate manual software deployment by building "Software Factories" that automatically test, package, and release code.

* **For Non-Technical Readers:** Normally, releasing software is a slow, manual process. These projects build automated pipelines. The moment a developer finishes coding, a robot tests the code, wraps it in a protective container, and installs it on the live server automatically.
* **For Technical Readers:** Comprehensive Continuous Integration / Continuous Deployment (CI/CD) pipelines utilizing GitHub Actions, Docker, and secure SSH execution.

## Projects Included

1. **[Project-1: DevOps Basics](./Project-1/)**
   * Introduction to Linux bash scripting and server management.
2. **[Project-2: Docker Containerization](./Project-2/)**
   * Packaging a Python Flask application into an immutable Docker image for consistent deployment across any environment.
3. **[Project-3: Automated GitHub Actions Pipeline](./Project-3/)**
   * A production-ready CI/CD pipeline. On every code push, it spins up an Ubuntu runner, lints the code (`flake8`), runs unit tests (`pytest`), builds a Docker artifact, pushes it to Docker Hub, and uses SSH keys to securely deploy the container to an AWS EC2 instance.
