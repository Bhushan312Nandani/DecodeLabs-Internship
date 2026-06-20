# Complete CI/CD Pipeline

A fully automated CI/CD pipeline built using GitHub Actions. It tests, lints, and builds a Dockerized Python application, and prepares it for deployment.

## What It Does

- **Continuous Integration**: Automatically runs `pytest` and `flake8` on every push to the repository.
- **Continuous Deployment / Delivery**: Builds a Docker image upon successful tests.
- **Docker Compose**: Easily orchestrates the application and any needed services locally.

## How to Run Locally

```bash
docker-compose up --build
```

## GitHub Actions

The pipeline is defined in `.github/workflows/`. It will automatically trigger when code is pushed to the repository.

## Files

| File | Description |
|---|---|
| `app.py` | Python application |
| `Dockerfile` | Docker image definition |
| `docker-compose.yml` | Multi-container orchestration |
| `pytest.ini` | Test configuration |
| `requirements.txt` | Dependencies |
| `tests/` | Unit test suite |
| `.github/` | CI/CD pipeline workflows |
