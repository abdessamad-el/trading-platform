# Trading Platform

An **event-driven trading backend** built with **Spring Boot**, **Apache Kafka**, and **PostgreSQL**, integrating with **Alpaca** for market data and execution.

---

## Project Goals

- Ingest **live market data** from a broker
- Execute **trades** via broker APIs
- Own **orders, fills, positions, and PnL**.
- Be extensible for:
    - algorithmic trading
    - live trading
    - additional providers

---

##  Tech Stack

- Java 17
- Spring Boot 3.5.9
- Apache Kafka
- PostgreSQL
- Flyway
- Alpaca Java SDK.
---


##  High-Level Architecture

```mermaid
flowchart LR
  C[Client<br/>Swagger/Postman] -->|REST| API[Spring Boot API]

  subgraph APP[Spring Boot App]
    API --> ORD[Order Controller<br/>POST /orders]
    API --> QRY[Query Controllers<br/>GET /orders /positions /pnl /account]
    ORD --> EXE[ExecutionProvider<br/>]
    QRY --> DB[(Postgres)]
  end

  EXE -->|submit order| ALP_REST[Alpaca Trading REST<br/>]

  subgraph STREAMS[Alpaca Streams]
    ALP_MKT[Market Data WS<br/>]
    ALP_TRADE[Trade Updates WS<br/>]
  end

  ALP_MKT --> MKT_NORM[Market Normalizer]
  ALP_TRADE --> TRD_NORM[Trade Update Normalizer]

  MKT_NORM -->|QuoteUpdated| KQ[(Kafka<br/>market.quotes.v1)]
  TRD_NORM -->|OrderUpdate| KU[(Kafka<br/>trading.order_updates.v1)]
  TRD_NORM -->|OrderFilled| KF[(Kafka<br/>trading.fills.v1)]

  subgraph CONSUMERS[Kafka Consumers / Projectors]
    CO[Order Projector]
    CP[Portfolio Projector]
  end

  KU --> CO
  KF --> CP

  CO --> DB
  CP --> DB
```

---
##  Event Lifecycle
```mermaid
sequenceDiagram
participant AlpM as Alpaca Market WS
participant AlpT as Alpaca Trade WS
participant N as Normalizers
participant K as Kafka
participant API as REST API
participant EP as ExecutionProvider
participant OP as Order Projector
participant PP as Portfolio Projector
participant DB as Postgres

AlpM->>N: market quotes/bars (raw)
N->>K: QuoteUpdated -> market.quotes.v1

API->>EP: OrderRequest
EP->>API: OrderSubmitted (alpacaOrderId)

AlpT->>N: trade_updates (raw)
N->>K: OrderUpdate -> trading.order_updates.v1
N->>K: OrderFilled -> trading.fills.v1

K->>OP: consume OrderUpdate
OP->>DB: update orders.status + append order_events

K->>PP: consume OrderFilled
PP->>DB: update positions + pnl/account snapshots
```


## Data Model

```mermaid
erDiagram
    ACCOUNTS ||--o{ ORDERS : places
    ACCOUNTS ||--o{ FILLS : results_in
    ORDERS ||--o{ FILLS : produces
    ACCOUNTS ||--o{ POSITIONS : has
    ACCOUNTS ||--o{ ACCOUNT_SNAPSHOTS : reports
    POSITIONS }o--|| INSTRUMENTS : for
    ORDERS }o--|| INSTRUMENTS : for
    FILLS }o--|| INSTRUMENTS : for

    ACCOUNTS {
      uuid account_id PK
      string name
      string provider "ALPACA"
      string environment "PAPER|LIVE"
      string provider_account_id
      string base_ccy
      datetime created_at
    }

    INSTRUMENTS {
      string symbol PK
      string asset_class "EQUITY|CRYPTO|OPTION"
      string exchange
      bool active
    }

    ORDERS {
      uuid order_id PK
      uuid account_id FK
      string provider_order_id
      string symbol FK
      string side "BUY|SELL"
      string type "MARKET|LIMIT"
      decimal qty
      decimal limit_price
      string time_in_force
      string status "NEW|SUBMITTED|ACCEPTED|PARTIALLY_FILLED|FILLED|CANCELED|REJECTED"
      datetime submitted_at
      datetime updated_at
    }

    FILLS {
      uuid fill_id PK
      uuid account_id FK
      uuid order_id FK
      string provider_trade_id
      string symbol FK
      string side "BUY|SELL"
      decimal qty
      decimal price
      decimal fee
      string fee_ccy
      datetime filled_at
    }

    POSITIONS {
      uuid position_id PK
      uuid account_id FK
      string symbol FK
      decimal qty
      decimal avg_price
      decimal realized_pnl
      decimal unrealized_pnl
      datetime updated_at
    }

    ACCOUNT_SNAPSHOTS {
      uuid snapshot_id PK
      uuid account_id FK
      decimal cash
      decimal equity
      decimal realized_pnl
      decimal unrealized_pnl
      datetime as_of
      string source "INTERNAL|ALPACA|COMPARE"
    }


```


---
