# Serverless Calculator with Audit Logging (AWS CDK + Go)

A full serverless microservice built with AWS CDK (TypeScript). A calculator API triggers an SQS queue, and a Go Lambda function logs every calculation to DynamoDB for a permanent audit trail.

## Architecture

```
API Gateway → SQS Queue → Lambda (Go) → DynamoDB (Audit Log)
```

## What It Does

- Calculator operations are sent via API
- Each calculation is queued in SQS
- Go Lambda processes the queue and writes audit entries to DynamoDB
- Full audit trail: rideId, fare, status

## AWS Services Used

- **AWS CDK** — Infrastructure as Code (TypeScript)
- **API Gateway** — HTTP entry point
- **SQS** — Message queue for decoupling
- **Lambda (Go)** — Serverless compute
- **DynamoDB** — NoSQL audit storage

## How to Deploy

### Prerequisites

```bash
npm install -g aws-cdk
aws configure   # Set up your credentials
go version      # Requires Go 1.18+
```

### Deploy

```bash
cd serverless-calculator
npm install
cdk bootstrap
cdk deploy
```

### Test

```bash
# Send a test calculation event
aws sqs send-message \
  --queue-url <YOUR_SQS_URL> \
  --message-body '{"rideId":"ride-001","totalFare":24.5,"status":"completed"}'
```

Check DynamoDB table for the audit log entry.

## Project Structure

```
Project-4/
├── main.go                        # Go Lambda handler
├── serverless-calculator-stack.ts # CDK stack definition
├── serverless-calculator/         # CDK project
│   ├── lib/                       # Stack constructs
│   ├── bin/                       # CDK app entry
│   └── cdk.json
├── go.mod / go.sum                # Go dependencies
└── README.md
```

## Environment

Set in CDK stack or Lambda console:
```
TABLE_NAME = your-dynamodb-table-name
```