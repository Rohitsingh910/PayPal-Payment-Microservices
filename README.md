# PayPal Payment Microservices System

A Spring Boot microservices-based payment processing system that integrates with PayPal Sandbox APIs for secure online payments.

## Overview

This project demonstrates a payment workflow where a merchant application submits a payment request, validates the request, processes the payment, communicates with PayPal, and returns the result to the merchant.

### Features

✔ PayPal Order Creation and Capture API Integration
✔ OAuth 2.0 Secure Authentication
✔ Payment Status Lifecycle Management
✔ Redis-Based Access Token Caching
✔ Circuit Breaker Fault Tolerance
✔ Centralized Exception Handling
✔ Distributed Logging with Micrometer
✔ Spring JDBC + MySQL Persistence
✔ Factory & Builder Design Patterns
✔ AWS EC2, RDS, and Secrets Manager Deployment
✔ Unit Testing with JUnit & Mockito
✔ RESTful Microservices Architecture

---

# Architecture

<img width="1109" height="565" alt="WhatsApp Image 2026-06-14 at 21 52 39" src="https://github.com/user-attachments/assets/e08e5987-4b25-4514-a605-c2fb21b5397f" />

```text
Response Flow:
PayPal -> Provider -> Processing -> Validation -> Merchant
```

---

# Microservices

| Service                 | Responsibility                          |
| ----------------------- | --------------------------------------- |
| Eureka Server           | Load Balancing                          |
| Validation Service      | Validates incoming requests             |
| Processing Service      | Coordinates payment processing          |
| PayPal Provider Service | Communicates with PayPal APIs           |

---

# Microservices Communication

paypal project.drawio

# Service Port Mapping

| Service                 | Port |
| ----------------------- | ---- |
| Eureka Server           | 8761 |
| Validation Service      | 8081 |
| Processing Service      | 8082 |
| PayPal Provider Service | 8083 |

## Application Flow:

<img width="933" height="574" alt="paypal project drawio" src="https://github.com/user-attachments/assets/6cd9d3eb-f549-4dce-932d-5effb259241c" />




---

# Technology Stack

## Backend

* Java 17
* Spring Boot
* Spring JDBC
* Mysql
* PayPal Sandbox
* Lombok
* Maven
* Redis Cache
* Docker
* Docker Compose
  
---

# Project Structure

```text
paypal-payment-system/
│
├── eureka-service/
│
├── validation-service/
│
├── processing-service/
│
├── paypal-provider-service/
│
├── docker-compose.yml
│
├── README.md
│
└── .env.example
```

---

# Prerequisites

* Java 17+
* Maven 3.8+
* Docker
* Docker Compose
* Git

---

# Environment Configuration
## Steps to get clientId & clientScrete
* go to https://developer.paypal.com 
* Sign up
* go to dashboard then Apps & Credential
* you can seen clentId and secret

Create a file named:

```text
.env
```

Example:

```env

PAYPAL_CLIENT_ID=YOUR_CLIENT_ID
PAYPAL_CLIENT_SECRET=YOUR_SECRET
```

---

# Running the Project

## Build
### For each Microservice :
```bash
mvn clean package
```

---

## Run Using Docker

Build and start:

```bash
docker-compose up --build
```

Detached mode:

```bash
docker-compose up -d
```

Stop services:

```bash
docker-compose down
```

---

# API Endpoints

## Create Payment

### Request

```http
[POST /payments/create](http://localhost:8081/validation/createPayment)
```

### Sample Request

```json
{
  "userId":302,
  "paymentMethodId": 1,
  "providerId": 1,
  "paymentTypeId": 1,
  "amount": 100.5,
  "currency": "USD",
  "merchantTransactionReference": "TXN-101"
}
```

### Success Response

```json
{
    "txnReference": "bd127164-7fd4-4722-9c35-9ab2042b027b",
    "txnStatusId": 3,
    "redirectUrl": "https://www.sandbox.paypal.com/checkoutnow?token=2W34390202916212B",
    "providerReference": "2W34390202916212B"
}
```

---

## Complete Payment

### Request

```http
[GET /payments/{transactionId}](http://localhost:8081/validation/{txnReference}/completePayment)
```
put txnReference received from create payment call.

### Sample Response

```json
{
  "transactionId": "PAYPAL-12345",
  "status": "COMPLETED"
}
```

---

# Validation Rules

| Field                        | Validation     |
| ---------------------------- | -------------- |
| userId                       | Required       |
| paymentMethodId              | Must be 1      |
| providerId                   | Must be 1      |
| paymentTypeId                | Must be 1      |
| amount                       | Greater than 0 |
| currency                     | Required       |
| merchantTransactionReference | Required       |

---

# Error Handling

Example Error Response:

```json
{
    "errorCode": "30009",
    "errorMessage": "Invalid URL. Please check and try again."
}
```

---


# Testing

Run unit tests:

```bash
mvn test
```

Run integration tests:

```bash
mvn verify
```

---

# Future Enhancements
* Frontend Implemtation
* Stripe Integration
* Kafka Event Processing
* JWT Authentication
* Kubernetes Deployment
* AWS Deployment

---


## Docker Logs

```bash
docker-compose logs -f
```

---

## Rebuild Containers

```bash
docker-compose down
docker-compose up --build
```

---

# Author

Rohit Singh

Java Backend Developer | Spring Boot | Microservices | Docker

