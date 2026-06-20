# ☁️ Cloud Computing (AWS)

## Overview
**The Goal:** To design scalable, serverless applications using Amazon Web Services (AWS) that run entirely in the cloud without managing traditional physical servers.

* **For Non-Technical Readers:** These projects are web applications (like a resume uploader and a calculator) that run on Amazon's global servers, meaning they can handle thousands of users at once without crashing.
* **For Technical Readers:** Event-driven microservices utilizing AWS Lambda, S3, API Gateway, SQS, DynamoDB, and RDS. Infrastructure is deployed via AWS CDK (Infrastructure as Code).

## Projects Included

1. **[Project-1: Serverless Resume Uploader](./Project-1/)**
   * Uses Lambda to generate secure pre-signed URLs, allowing a web browser to upload files directly to an S3 bucket.
2. **[Project-2: EC2 Deployment Demo](./Project-2/)**
   * A video demonstration of launching and configuring a virtual machine (EC2) with SSH security groups.
3. **[Project-3: Serverless Database Proxy](./Project-3%20Resources/)**
   * An advanced security layer that intercepts MySQL database write queries via API Gateway/Lambda, holding them in a "Pending" state until an Admin approves them via a web dashboard.
4. **[Project-4: Serverless Calculator Audit Logger](./Project-4/)**
   * An AWS CDK (Go/TypeScript) app where a calculator API sends events to an SQS queue, triggering a Lambda function to permanently record the transaction in a DynamoDB NoSQL table.
