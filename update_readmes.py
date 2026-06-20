import os

files = {
    "readme.md": '''# ?? DecodeLabs Engineering Internship Portfolio

Welcome to my software engineering portfolio! This repository showcases the real-world projects I built during my internship at DecodeLabs. 

My work spans across multiple technology domains, demonstrating my ability to build full-stack web applications, deploy secure cloud infrastructure, train artificial intelligence models, and automate complex deployment pipelines.

## ?? Portfolio Domains

### ?? [Artificial Intelligence](./AI/)
* **For Non-Technical Readers:** Smart systems that can chat with users and automatically categorize data.
* **For Technical Readers:** Implementations of K-Nearest Neighbors (KNN) ML classifiers and fuzzy-string matching algorithms using Python, scikit-learn, and Gradio.

### ?? [Cloud Computing](./Cloud-Computing/)
* **For Non-Technical Readers:** Highly scalable applications hosted on Amazon's internet servers.
* **For Technical Readers:** Serverless event-driven architectures utilizing AWS Lambda, S3, API Gateway, SQS, and DynamoDB (built with Python and Go/AWS CDK).

### ?? [DevOps & CI/CD](./DEVOOPS/)
* **For Non-Technical Readers:** "Software Factories" that automatically test and deliver code to production servers without human intervention.
* **For Technical Readers:** Fully automated CI/CD pipelines using GitHub Actions, Docker containerization, and secure SSH deployments to EC2 instances.

### ??? [Cyber Security](./Cyber-Security/)
* **For Non-Technical Readers:** Tools to scan networks for vulnerabilities and encrypt private messages.
* **For Technical Readers:** Python-based network scanners, end-to-end RSA encryption engines, and Man-in-the-Middle (MITM) attack simulations.

### ?? [Blockchain](./Block-Chain/)
* **For Non-Technical Readers:** Decentralized digital ledgers and unhackable voting systems.
* **For Technical Readers:** Custom Python Proof-of-Work blockchain simulations (demonstrating 51% attacks) and Solidity-based smart contracts for decentralized voting.

### ?? [Backend Development](./Backend%20Development/)
* **For Non-Technical Readers:** The "brains" behind websites that handle user logins, security, and live data (like weather forecasts).
* **For Technical Readers:** Node.js and Express REST APIs featuring role-based JWT authentication middleware and external API integrations.

### ?? [Frontend & Full-Stack](./Frontend-Development/)
* **For Non-Technical Readers:** Beautiful, responsive websites and end-to-end applications you can interact with.
* **For Technical Readers:** Responsive HTML/CSS/JS interfaces integrated with backend APIs.

### ? [Java Engineering](./JAVA/)
* **For Non-Technical Readers:** Fast, enterprise-grade software calculators and business logic.
* **For Technical Readers:** Object-Oriented Java applications and Spring Boot microservices with integrated Maven CI/CD pipelines.

### ?? [Python Engineering](./PYTHON/)
* **For Non-Technical Readers:** Automated trading bots and financial expense trackers.
* **For Technical Readers:** Flask web apps, Algorithmic Trading Engines, and PostgreSQL database integrations using the Prisma ORM.

### ?? [Robotics & Automation](./Robotics-Automation/)
* **For Non-Technical Readers:** Visual factory inspectors and simulated robot arms.
* **For Technical Readers:** Computer Vision (OpenCV) automated defect inspection systems and ROS2 Gazebo robotic arm physics simulations.

---

## ?? Note on Video Demonstrations
Many of these projects contain full video demonstrations (.mp4). Because these files are large, they are managed via **Git Large File Storage (LFS)**. 
*If you see a message saying "we can’t show files that are this big right now" on GitHub, simply click the **"Download raw file"** button to watch the video locally!*
''',

    "AI/README.md": '''# ?? Artificial Intelligence

## Overview
**The Goal:** To build intelligent systems capable of processing human input and making data-driven decisions.

* **For Non-Technical Readers:** This folder contains a smart chatbot that understands what you mean even if you make typos, and an AI that can identify different species of flowers based on their measurements.
* **For Technical Readers:** Implementations of rule-based natural language processing using fuzzy matching, and supervised machine learning (K-Nearest Neighbors) using Python and scikit-learn.

## Projects Included

1. **[Project-1: Rule-Based Chatbot](./Project-1/)**
   * A Gradio web app that uses the difflib library to perform fuzzy-string matching, allowing it to understand misspelled user intents.
2. **[Project-2: Iris Classifier](./Project-2/)**
   * An ML pipeline that scales data and trains a KNN classifier on the UCI Iris dataset, outputting precision metrics (Accuracy, F1-Score, Confusion Matrix).
''',

    "Cloud-Computing/README.md": '''# ?? Cloud Computing (AWS)

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
''',

    "DEVOOPS/README.md": '''# ?? DevOps & CI/CD

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
   * A production-ready CI/CD pipeline. On every code push, it spins up an Ubuntu runner, lints the code (lake8), runs unit tests (pytest), builds a Docker artifact, pushes it to Docker Hub, and uses SSH keys to securely deploy the container to an AWS EC2 instance.
''',

    "Cyber-Security/README.md": '''# ??? Cyber Security

## Overview
**The Goal:** To understand how to protect networks, encrypt sensitive data, and simulate how hackers exploit vulnerabilities.

* **For Non-Technical Readers:** This folder contains tools that can scan a Wi-Fi network to see what devices are connected, and encryption programs that scramble messages so only the intended recipient can read them.
* **For Technical Readers:** Python-based network socket scanners, cryptographic algorithm implementations from scratch, and simulation of network interception techniques.

## Projects Included

1. **[Project-1: Network Security Scanner](./Project-1/)**
   * A Python/JS web application that scans network IP addresses and ports to monitor active services and identify potential vulnerabilities.
2. **[Project-2: Cryptography & Attack Simulations](./Project-2/)**
   * Implements end-to-end RSA encryption (public/private key generation), a Caesar Cipher (with a brute-force decryption tool), and a simulated Man-in-the-Middle (MITM) attack intercepting communications.
''',

    "Block-Chain/README.md": '''# ?? Blockchain Engineering

## Overview
**The Goal:** To build decentralized, tamper-proof systems using cryptography and distributed ledgers.

* **For Non-Technical Readers:** This contains a miniature version of the technology behind Bitcoin. It demonstrates how "miners" secure the network, and proves how a hacker would need to control 51% of the network to successfully fake a transaction.
* **For Technical Readers:** A ground-up Python Proof-of-Work blockchain, and Ethereum smart contract development using Solidity.

## Projects Included

1. **[Project-1: Python Blockchain & 51% Attack](./Project-1/)**
   * A peer-to-peer blockchain simulation featuring block hashing, mining difficulty, and a simulated "Smart Hacker" executing a successful 51% attack by out-mining the honest network.
2. **[Project-2: Voting Smart Contract](./Project-2/)**
   * A Solidity (.sol) decentralized application (dApp) that allows registered wallet addresses to securely cast unchangeable votes for specific proposals.
''',

    "Backend Development/README.md": '''# ?? Backend Development

## Overview
**The Goal:** To build secure, fast, and reliable server-side logic that powers web applications.

* **For Non-Technical Readers:** If the frontend is the "paint and steering wheel" of a car, the backend is the engine. These projects act as the engine—handling user logins securely, talking to databases, and fetching live weather data from the internet.
* **For Technical Readers:** RESTful APIs built with Node.js and Express, emphasizing middleware architecture, JWT token security, and asynchronous external API requests.

## Projects Included

1. **[Project-3: Secure JWT Authentication API](./Project-3/)**
   * A Node.js user authentication system. Uses JSON Web Tokens (JWT) and custom middleware to restrict access so that regular users and dmins only see the data they are authorized to see.
2. **[Project-4: Weather API Backend](./Project-4/)**
   * An Express.js server that securely manages a private API key to fetch real-time global weather data from OpenWeatherMap, serving it back to a frontend interface.
''',

    "Robotics-Automation/README.md": '''# ?? Robotics & Automation

## Overview
**The Goal:** To write code that controls physical robots and automates visual quality-control inspections on factory assembly lines.

* **For Non-Technical Readers:** This contains a program that acts as a "Virtual Factory Inspector" (it looks at photos of products and automatically flags broken ones), as well as a 3D simulation of a robot arm picking up a coffee cup.
* **For Technical Readers:** Industrial Computer Vision using OpenCV, and kinematics physics simulations using the Robot Operating System (ROS2) inside Gazebo.

## Projects Included

1. **[Project-1: ROS2 Pick-and-Place Simulation](./Project-1/)**
   * A complete ROS2 Gazebo workspace simulating a robotic arm. It uses Python controllers to execute inverse kinematics to grab and move objects in a 3D environment. *(See the README inside for the video demo link!)*
2. **[Project-2: Automated Defect Inspection System](./Project-2/)**
   * A Computer Vision script (cv2) that binarizes images from a conveyor belt, traces the product's contours, and mathematically isolates "Convexity Defects" to trigger a Pass/Fail PLC signal based on structural anomalies.
'''
}

for filepath, content in files.items():
    if os.path.exists(filepath):
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Updated {filepath}")
