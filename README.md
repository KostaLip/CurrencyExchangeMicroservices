# API Gateway (port 8765)

Svim servisima se pristupa isključivo putem api-gateway (port 8765)

## 1. currency-exchange (port 8000):
- GET localhost:8765/currency-exchange?from=EUR&to=RSD

## 2. user-service (port 8770):
- GET localhost:8765/users
- GET localhost:8765/users/email?email=owner@gmail.com
- POST localhost:8765/users/newUser (Request Body)
- POST localhost:8765/users/newAdmin (Request Body)
- PUT localhost:8765/users (Request Body)
- DELETE localhost:8765/users?email=user@gmail.com

## 3. bank-account (port 8200):
- GET localhost:8765/bankAccounts
- GET localhost:8765/bankAccounts/email?email=user@gmail.com
- PUT localhost:8765/bankAccounts (Request Body)

## 4. currency-conversion (port 8100):
- GET localhost:8765/currency-conversion-feign?from=EUR&to=USD&quantity=100

## 5. crypto-exchange (port 8400):
- GET localhost:8765/crypto-exchange?from=SOL&to=ETH

## 6. crypto-wallet (port 8300):
- GET localhost:8765/crypto-wallet
- GET localhost:8765/crypto-wallet/email?email=user@gmail.com
- PUT localhost:8765/crypto-wallet (Request Body)

## 7. crypto-conversion (port 8500):
- GET localhost:8765/crypto-conversion-feign?from=SOL&to=BTC&quantity=10

## 8. trade-service (port 8600):
- GET localhost:8765/trade?from=SOL&to=RSD&quantity=2

## KORISNICI:

### 1. OWNER:
- email: owner@gmail.com
- password: password

### 2. ADMIN:
- email: admin@gmail.com
- password: password

### 3. USER:
- email: user@gmail.com
- password: password
