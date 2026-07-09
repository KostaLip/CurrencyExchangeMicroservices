# Currency Exchange Microservices

A Spring Boot / Spring Cloud microservices platform for currency and cryptocurrency exchange, account management, and trading. The system is built as a set of independently deployable Java services, discoverable through a naming/registry server and exposed to clients through a single API Gateway.

## Architecture Overview

All client traffic goes through a central **API Gateway**, which routes requests to the appropriate backend microservice. Services register themselves with a **Naming Server** (service discovery / registry, in the style of Spring Cloud Netflix Eureka), which allows the gateway and other services to locate them dynamically instead of relying on hardcoded hosts/ports.

Some services communicate with each other directly using declarative REST clients (Feign), as indicated by the `-feign` endpoints exposed by the conversion services (e.g. `currency-conversion` calling `currency-exchange`, and `crypto-conversion` calling `crypto-exchange`).

```
Client
  │
  ▼
API Gateway (8765)
  │
  ├── user-service (8770)
  ├── bank-account (8200)
  ├── currency-exchange (8000)
  ├── currency-conversion (8100)
  ├── crypto-exchange (8400)
  ├── crypto-wallet (8300)
  ├── crypto-conversion (8500)
  └── trade-service (8600)
  
Naming Server ── service registry/discovery for all of the above
ServiceLibrary ── shared models/DTOs used across services
Util ── shared utility code
```

## Modules

| Module | Description |
|---|---|
| `APIGateway` | Single entry point (port **8765**) that routes all external requests to the internal microservices. |
| `NamingServer` | Service registry used for service discovery so microservices can find and call one another by name. |
| `UserService` | Manages users (owners, admins, regular users) — CRUD operations and lookups by email. |
| `BankAccount` | Manages users' fiat bank accounts (balances tied to a user's email). |
| `CurrencyExchange` | Provides exchange rate lookups between fiat currencies (e.g. EUR → RSD). |
| `CurrencyConversion` | Converts a given quantity of one fiat currency into another, using `CurrencyExchange` (via Feign) for the rate. |
| `CryptoExchange` | Provides exchange rate lookups between cryptocurrencies (e.g. SOL → ETH). |
| `CryptoWallet` | Manages users' crypto wallets/holdings (tied to a user's email). |
| `CryptoConversion` | Converts a given quantity of one cryptocurrency into another, using `CryptoExchange` (via Feign) for the rate. |
| `TradeService` | Executes trades between currencies/cryptocurrencies (e.g. SOL → RSD) by combining the exchange/conversion services. |
| `ServiceLibrary` | Shared library (common models/DTOs) reused across the microservices. |
| `Util` | Shared utility/helper code used by multiple services. |

**Tech stack:** Java (Spring Boot / Spring Cloud), Docker (each service ships with a Dockerfile and the whole system can be started via `docker-compose.yaml`).

## Getting Started

### Prerequisites
- Java (JDK) and Maven/Gradle as required by the individual services
- Docker & Docker Compose

### Run with Docker Compose
```bash
git clone https://github.com/KostaLip/CurrencyExchangeMicroservices.git
cd CurrencyExchangeMicroservices
docker-compose up --build
```

This brings up the Naming Server, API Gateway, and all backend microservices. Once everything is registered and healthy, all requests should be made through the **API Gateway on port 8765** — the individual services should not be called directly.

## API Reference

All endpoints below are called through the API Gateway: `http://localhost:8765/...`

### 1. Currency Exchange (internally port 8000)
- `GET /currency-exchange?from=EUR&to=RSD` — get the exchange rate between two fiat currencies

### 2. User Service (internally port 8770)
- `GET /users` — list all users
- `GET /users/email?email=owner@gmail.com` — get a user by email
- `POST /users/newUser` (request body) — register a new standard user
- `POST /users/newAdmin` (request body) — register a new admin user
- `PUT /users` (request body) — update a user
- `DELETE /users?email=user@gmail.com` — delete a user

### 3. Bank Account (internally port 8200)
- `GET /bankAccounts` — list all bank accounts
- `GET /bankAccounts/email?email=user@gmail.com` — get a bank account by owner's email
- `PUT /bankAccounts` (request body) — update a bank account

### 4. Currency Conversion (internally port 8100)
- `GET /currency-conversion-feign?from=EUR&to=USD&quantity=100` — convert an amount from one fiat currency to another

### 5. Crypto Exchange (internally port 8400)
- `GET /crypto-exchange?from=SOL&to=ETH` — get the exchange rate between two cryptocurrencies

### 6. Crypto Wallet (internally port 8300)
- `GET /crypto-wallet` — list all crypto wallets
- `GET /crypto-wallet/email?email=user@gmail.com` — get a wallet by owner's email
- `PUT /crypto-wallet` (request body) — update a wallet

### 7. Crypto Conversion (internally port 8500)
- `GET /crypto-conversion-feign?from=SOL&to=BTC&quantity=10` — convert an amount from one cryptocurrency to another

### 8. Trade Service (internally port 8600)
- `GET /trade?from=SOL&to=RSD&quantity=2` — execute a trade converting a given quantity from one asset to another (fiat or crypto)

## Default / Seed Users

| Role | Email | Password |
|---|---|---|
| Owner | owner@gmail.com | password |
| Admin | admin@gmail.com | password |
| User | user@gmail.com | password |
