# Serverless Cost Calculator Engine

## Overview
This is a scalable, cost-effective serverless backend for calculating ride fares using AWS. It leverages a decoupled architecture ensuring high availability.

## Architecture
- **IaC:** AWS CDK (TypeScript)
- **Compute:** AWS Lambda (Go) - ARM64
- **Messaging:** AWS SQS
- **Database:** Amazon DynamoDB (On-Demand)
- **API Layer:** Amazon API Gateway (with Throttling)

## How to Run

1. **Prerequisites**
   - Node.js & npm
   - Go 1.20+
   - AWS CLI configured
   - AWS CDK installed (`npm install -g aws-cdk`)

2. **Setup**
   ```bash
   cd serverless-calculator
   npm install
   ```

3. **Deploy**
   ```bash
   cdk bootstrap
   cdk deploy
   ```

4. **Testing**
   Use the API endpoint outputted by CDK to test the calculation logic:
   `Total Fare = Base Fare + (Distance * 1.5) + (Time * 0.5)`
   
   *Example using cURL:*
   ```bash
   curl -X POST "https://<YOUR_API_ID>.execute-api.<REGION>.amazonaws.com/prod/calculate" \
     -H "Content-Type: application/json" \
     -d '{"rideId": "ride-123", "baseFare": 2.50, "distance": 10.0, "time": 15.0}'
   ```

## Privacy & Policies
This repository is sanitized and ready for public viewing. All sensitive data (e.g., API keys, gateways) has been omitted. Please refer to `SHARED_POLICIES.md` for guidelines on using and contributing to this project.