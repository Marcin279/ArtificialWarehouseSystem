# Diagram wzorca Database per Service

```mermaid
%%{init: {'theme': 'default', 'themeVariables': { 'fontSize': '16px', 'fontFamily': 'Arial' }}}%%
graph TD
    subgraph "Architektura mikrousług z Database per Service"
        subgraph "products-service"
            ProductAPI[REST API]
            ProductService[Serwis Produktów]
            ProductGRPC[gRPC Server]
        end

        subgraph "orders-service"
            OrderAPI[REST API]
            OrderService[Serwis Zamówień]
            OrderGRPC[gRPC Client]
        end

        ProductDB[(PostgreSQL\nProducts DB)]
        OrdersDB[(PostgreSQL\nOrders DB)]

        Kafka[Apache Kafka]

        %% Połączenia wewnętrzne w usługach
        ProductAPI --> ProductService
        ProductService --> ProductDB
        ProductService --> ProductGRPC

        OrderAPI --> OrderService
        OrderService --> OrdersDB
        OrderService --> OrderGRPC

        %% Komunikacja synchroniczna
        OrderGRPC -.->|gRPC| ProductGRPC

        %% Komunikacja asynchroniczna
        ProductService -->|Publikuj zdarzenia| Kafka
        Kafka -->|Subskrypcja zdarzeń| OrderService
    end