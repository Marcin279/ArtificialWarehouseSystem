# System flowchart diagram

```mermaid
flowchart LR
    %% Definicje stylów
    classDef service fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef database fill:#fff8e1,stroke:#ffa000,stroke-width:2px
    classDef message fill:#f1f8e9,stroke:#558b2f,stroke-width:2px
    classDef client fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px
    classDef monitoring fill:#e1bee7,stroke:#6a1b9a,stroke-width:2px

    %% Klient
    CLIENT[Klient]:::client

    %% Gateway
    API[API Gateway]

    %% Główne serwisy
    subgraph Serwisy
        direction TB
        OS[OrdersService]:::service
        PS[ProductsService]:::service
        WS[WarehouseService]:::service
        SS[ShippingService]:::service
    end

    %% Bazy danych
    subgraph Bazy
        direction TB
        ODB[(OrdersDB)]:::database
        PDB[(ProductsDB)]:::database
        WDB[(WarehouseDB)]:::database
        SDB[(ShippingDB)]:::database
    end

    %% Kafka
    subgraph Komunikacja asynchroniczna
        direction TB
        KAFKA[Kafka Message Broker]:::message
    end

    %% Monitoring - umieszczony pod sekcją serwisów
    subgraph Monitoring
        direction LR
        PROM[Prometheus]:::monitoring
        GRAF[Grafana]:::monitoring
    end

    %% Połączenie klienta z API Gateway
    CLIENT --> API

    %% Połączenia API Gateway
    API --> OS
    API --> PS
    API --> WS
    API --> SS

    %% Połączenia z bazami danych
    OS <--> ODB
    PS <--> PDB
    WS <--> WDB
    SS <--> SDB

    %% gRPC połączenia - linie ciągłe z opisem
    OS -- gRPC --> PS
    WS -- gRPC --> PS
    OS -- gRPC --> WS

    %% Kafka połączenia - linie przerywane
    OS -. order-created-topic .-> KAFKA
    KAFKA -. order-created-topic .-> WS
    KAFKA -. order-created-topic .-> SS

    %% REST API połączenia - punktowane linie
    SS -. REST .-> OS

    %% Inne zdarzenia Kafka
    WS -. product-reserved .-> KAFKA
    WS -. product-released .-> KAFKA
    WS -. stock-updated .-> KAFKA