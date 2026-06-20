#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { ServerlessCalculatorStack } from '../lib/serverless-calculator-stack';

const app = new cdk.App();
new ServerlessCalculatorStack(app, 'ServerlessCalculatorStack', {
  env: { account: process.env.CDK_DEFAULT_ACCOUNT, region: process.env.CDK_DEFAULT_REGION },
});