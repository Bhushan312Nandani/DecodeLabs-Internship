import * as cdk from 'aws-cdk-lib';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as dynamodb from 'aws-cdk-lib/aws-dynamodb';
import * as sqs from 'aws-cdk-lib/aws-sqs';
import { SqsEventSource } from 'aws-cdk-lib/aws-lambda-event-sources';
import { Construct } from 'constructs';

export class ServerlessCalculatorStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // 1. Storage Layer (DynamoDB NoSQL Table)
    const auditTable = new dynamodb.Table(this, 'AuditRecordTable', {
      partitionKey: { name: 'rideId', type: dynamodb.AttributeType.STRING },
      billingMode: dynamodb.BillingMode.PAY_PER_REQUEST, // True Serverless variable model
      removalPolicy: cdk.RemovalPolicy.DESTROY, // Deletes table when stack is torn down
    });

    // 2. Messaging Layer (SQS Queue)
    const auditQueue = new sqs.Queue(this, 'AuditQueue', {
      visibilityTimeout: cdk.Duration.seconds(30),
    });

    // 3. High-Performance Compute Tier (Go Lambdas)
    const calculatorLambda = new lambda.Function(this, 'CalculatorFunction', {
      runtime: lambda.Runtime.PROVIDED_AL2023, // OS runtime for natively compiled binaries
      handler: 'bootstrap',
      code: lambda.Code.fromAsset('backend/calculator/dist'),
      architecture: lambda.Architecture.ARM_64, // Cost-efficient ARM architecture
      environment: {
        QUEUE_URL: auditQueue.queueUrl,
      },
    });

    const auditorLambda = new lambda.Function(this, 'AuditorFunction', {
      runtime: lambda.Runtime.PROVIDED_AL2023,
      handler: 'bootstrap',
      code: lambda.Code.fromAsset('backend/auditor/dist'),
      architecture: lambda.Architecture.ARM_64,
      environment: {
        TABLE_NAME: auditTable.tableName,
      },
    });

    // 4. Granular IAM Policy Mappings (Least Privilege Boundary Access)
    auditQueue.grantSendMessages(calculatorLambda);
    auditTable.grantWriteData(auditorLambda);
    auditorLambda.addEventSource(new SqsEventSource(auditQueue));

    // 5. API Gateway Layer with Throttling and Rate Limiting Configuration
    const api = new apigateway.RestApi(this, 'CalculatorRestApi', {
      restApiName: 'Serverless Cost Calculator Engine',
      deployOptions: {
        stageName: 'prod',
        // Throttling configuration applies directly at the stage layer here:
        throttlingBurstLimit: 10, // Absolute maximum concurrent token burst capacity
        throttlingRateLimit: 5,   // Steady state limit (max 5 requests per second)
      },
    });

    const calculateIntegration = new apigateway.LambdaIntegration(calculatorLambda);
    const calculateResource = api.root.addResource('calculate');
    calculateResource.addMethod('POST', calculateIntegration);
  }
}