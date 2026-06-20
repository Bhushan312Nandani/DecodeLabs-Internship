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

## GitHub Actions & Required Secrets

The pipeline is defined in `.github/workflows/ci-cd.yml`. It automatically triggers when code is pushed to the repository.

To make the deployment stage of the pipeline work, you must configure the following **Secrets** in your GitHub repository (go to **Settings** → **Secrets and variables** → **Actions**):

| Secret Name | Description |
|---|---|
| `DOCKERHUB_USERNAME` | Your Docker Hub username. |
| `DOCKERHUB_TOKEN` | A personal access token from Docker Hub. |
| `PROD_SERVER_IP` | The IP address of your production server (e.g., AWS EC2). |
| `PROD_SERVER_USER` | The SSH username for the server (e.g., `ubuntu` or `ec2-user`). |
| `PROD_SSH_PRIVATE_KEY` | The raw text contents of your `.pem` SSH private key. |

*Note: Without these secrets, the linting and testing stages will run successfully, but the Docker build and SSH deployment stages will fail.*

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
