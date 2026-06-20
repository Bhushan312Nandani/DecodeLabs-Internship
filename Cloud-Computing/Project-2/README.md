# EC2 Machine Deployment Demo

An AWS EC2 instance setup and deployment demonstration.

## What It Covers

- Launching an EC2 instance on AWS
- Configuring Security Groups and key pairs
- SSH access to the instance
- Basic server setup and configuration

> 📹 Full demo recorded — video shows the complete EC2 setup process step by step.  
> *(Video file available locally — too large for direct GitHub upload)*

## Key Concepts

- **EC2**: Elastic Compute Cloud — virtual machine in AWS
- **Security Groups**: Firewall rules controlling inbound/outbound traffic
- **Key Pairs**: SSH authentication for secure access
- **AMI**: Amazon Machine Image — the OS template used for the instance

## AWS CLI Setup

```bash
aws configure
# Enter: Access Key, Secret Key, Region, Output format

aws ec2 describe-instances
```
