# PayPal Payment Microservices System

A robust, enterprise-grade Spring Boot microservices-based payment processing gateway. This system coordinates secure online payments by integrating with official PayPal Sandbox REST APIs, utilizing Eureka Service Discovery, MySQL persistence, and Redis for access token caching.

---

## Architecture Diagram

The system employs a decentralized microservices architecture where services communicate over a virtualized container network and automatically register with Eureka.

```mermaid
flowchart TD
    subgraph Client Space
        Client[Client / REST Client]
    end

    subgraph Service Mesh (Docker Network)
        Gateway[Validation Service \n Port 8081]
        Discovery[Eureka Service Registry \n Port 8761]
        Processing[Processing Service \n Port 8082]
        Provider[Paypal Provider Service \n Port 8083]
        Database[(MySQL Database \n Port 3306/3308)]
        Cache[(Redis Token Cache \n Port 6379)]
    end

    subgraph External Provider
        PaypalAPI[PayPal Sandbox REST API]
    end

    Client -->|1. Create Payment| Gateway
    Gateway -->|2. Route & Validate| Processing
    Processing -->|3. Register Txn| Database
    Processing -->|4. Request PayPal Order| Provider
    Provider -->|5. Check Cache| Cache
    Provider -->|6. OAuth & Order Call| PaypalAPI
    Provider -->|7. Return Order Token & URL| Processing
    Processing -->|8. Update Status PENDING| Database
    Processing -->|9. Send Redirect URL| Gateway
    Gateway -->|10. Return Response| Client
```

---

## Microservices Core Details

| Service Name | Description | Port (Local) | Port (Docker Container) |
| :--- | :--- | :--- | :--- |
| **`Eureka Registry`** | Handles service registration, discovery, and dynamic routing. | `8761` | `8761` |
| **`Validation Service`** | Acts as the public gateway. Performs syntax and business rule validations. | `8081` | `8081` |
| **`Processing Service`** | Manages database lifecycle state transitions (Created $\rightarrow$ Pending $\rightarrow$ Approved). | `8082` | `8082` |
| **`PayPal Provider Service`** | Communicates with the external PayPal Sandbox endpoints. | `8083` | `8083` |

---

## Features & Implementation Details

*   **Lombok JDK 20/21 Support**: Configured with Lombok `1.18.36` to prevent compiler initialization crashes (`TypeTag :: UNKNOWN`) under modern Java runtimes.
*   **Redis-Backed Access Token Caching**: In `TokenService`, rather than making heavy roundtrips to PayPal OAuth for every request, OAuth tokens are stored in the `redis-cache` container. The token is cached dynamically using the PayPal response `expires_in` field minus 60 seconds (safe buffer TTL).
*   **Decoupled State Processors**: Employs the **Factory** and **State/Strategy** patterns to handle transaction status updates (`CreatedStatusProcessor`, `ApprovedStatusProcessor`, etc.) avoiding long conditional chains.
*   **Fault-Tolerant Containerization**: Completely configured with Docker Compose where databases and Redis spin up automatically alongside services inside a shared virtual bridge network.

---

## Project Directory Tree

The workspace is organized into separate Maven modules for clean boundaries:

```text
Payment-System-main/
│
├── Eureka-Service-Registry/             # Eureka Discovery Server
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── Payment Processing Service/          # State lifecycle controller
│   └── payments/
│       ├── src/                         # Includes schema.sql / data.sql
│       └── pom.xml
│
├── Payment Validation Service/          # Gateway entrypoint & validator
│   └── validation/
│       ├── src/
│       └── pom.xml
│
├── Paypal Provider Service/             # PayPal REST integration module
│   └── my paypal provider/
│       ├── src/
│       └── pom.xml
│
├── docker-compose.yml                   # Container orchestration spec
├── .gitignore                           # Git ignore rules for Maven/IDEs
└── README.md                            # Documentation
```

---

## 🐋 Beginner's Guide: Understanding Docker

If this is your first time using **Docker**, think of it as a **shipping container system** for your code. 

1.  **What is a Container?**
    Normally, to run this system, you need to install Java 17, MySQL, and Redis on your computer. If someone else runs your code, they must install the same things. A **Docker Container** packages your code and *exactly* the environment it needs (Java, libraries, OS settings) into a single box. It runs the same on any computer.
2.  **Image vs. Container**:
    *   **Image**: A read-only blueprint or template (like a CD-ROM or installer file).
    *   **Container**: The actual running instance of that Image (like a game running after installation).
3.  **What is Docker Compose?**
    Since our project has 4 Java microservices, 1 MySQL database, and 1 Redis cache (6 containers total), starting them one-by-one is tedious. **Docker Compose** reads the `docker-compose.yml` file and starts, configures, and links all 6 containers with a single command!

---

## 🚀 Running the Project

### Option 1: Running with Docker Compose (Recommended)

Make sure you have **Docker Desktop** installed and running on your system.

#### 1. Build the JAR packages locally:
Before running docker containers, Maven must build the JAR binaries:
```cmd
# Run this command in each of the 4 microservice directories containing a pom.xml
.\mvnw.cmd clean package -DskipTests
```

#### 2. Start the Container Stack:
Open your terminal in the project root folder (where `docker-compose.yml` is located) and run:
```cmd
docker compose up -d
```
> [!NOTE]
> The `-d` flag runs the containers in "detached" (background) mode, freeing up your terminal.

#### 3. View Running Status:
To see which containers are active:
```cmd
docker compose ps
```

#### 4. Check Container Logs:
To see logs of a specific service (for example, the PayPal provider):
```cmd
docker compose logs -f paypal-provider-service
```
> [!NOTE]
> Press `Ctrl + C` to stop viewing live logs.

#### 5. Stop the Containers:
To stop all services and free up system memory:
```cmd
docker compose down
```

---

### Option 2: Native Local Execution (Without Docker)

If you wish to run the microservices directly on your machine:
1.  Ensure you have a local **MySQL Server** running on port `3306` (or `3307`) with database `payments` initialized.
2.  Ensure you have a local **Redis Server** running on port `6379`.
3.  Run the **Eureka Server** first:
    ```cmd
    cd "Eureka-Service-Registry"
    .\mvnw.cmd spring-boot:run
    ```
4.  Run the other three services using the `local` profile:
    ```cmd
    .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
    ```

---

## 🎯 Verification: Testing the API

You can test the end-to-end payment flow using **Postman** or **cURL** in Command Prompt (CMD):

### Step 1: Create & Initiate a Payment
```cmd
curl -X POST http://localhost:8081/validation/createPayment -H "Content-Type: application/json" -d "{\"userId\":302,\"paymentMethodId\":1,\"providerId\":1,\"paymentTypeId\":1,\"amount\":100.5,\"currency\":\"USD\",\"merchantTransactionReference\":\"TXN-101\"}"
```

**Response Output (PENDING state)**:
```json
{
  "txnReference": "f4309887-ce34-4c35-a94b-b417ef98736c",
  "txnStatusId": 3,
  "redirectUrl": "https://www.sandbox.paypal.com/checkoutnow?token=63V23857FS7491321",
  "providerReference": "63V23857FS7491321"
}
```

### Step 2: Complete / Capture the Payment
```cmd
curl -X POST http://localhost:8081/validation/f4309887-ce34-4c35-a94b-b417ef98736c/completePayment
```

---

## 💡 Future Enhancements

Here are some excellent ways to continue expanding this repository:
1.  **Automatic Reconciliation Job**: Implement a Spring Scheduler task to query the PayPal status of stuck/unapproved payments and auto-update the database records.
2.  **API Gateway**: Introduce a Spring Cloud API Gateway on port `8080` to act as a centralized entrypoint, handling CORS, JWT auth, and rate-limiting.
3.  **RabbitMQ Notifications**: Send asynchronous `PaymentCompletedEvent` messages to a queue for audit logging.

---

### Author
**Rohit Singh**
*Java Backend Developer | Spring Boot | Microservices | Docker*
