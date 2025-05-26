# Event Driven Architecture with Kafka

```mermaid
graph TD
subgraph "Orders Service"
OrderService[OrderService]
OrderGrpcService[OrderGrpcService]
ProductGrpcClientService[ProductGrpcClientService]
end

    subgraph "Products Service"
        ProductService[ProductService]
        ProductGrpcService[ProductGrpcService]
    end

    subgraph "Warehouse Service"
        InventoryService[InventoryService]
        WarehouseEventConsumer[WarehouseEventConsumer]
        WarehouseEventProducer[WarehouseEventProducer]
    end

    subgraph "Shipping Service"
        ShipmentService[ShipmentService]
        OrderEventListener[OrderEventListener]
    end

    %% Kafka Topics
    OrderCreatedTopic[/order-created-topic\]
    StockUpdatedTopic[/stock-updated\]
    ProductReservedTopic[/product-reserved\]

    %% Przepływy komunikacji
    OrderGrpcService -->|publikuje|OrderCreatedTopic
    OrderGrpcService -->|używa gRPC|ProductGrpcClientService
    ProductGrpcClientService -->|pobiera informacje|ProductGrpcService
    
    WarehouseEventConsumer -->|słucha|OrderCreatedTopic
    WarehouseEventConsumer -->|rezerwuje produkty|InventoryService
    InventoryService -->|publikuje zdarzenia|WarehouseEventProducer
    WarehouseEventProducer -->|publikuje|StockUpdatedTopic
    WarehouseEventProducer -->|publikuje|ProductReservedTopic
    
    OrderEventListener -->|słucha|OrderCreatedTopic
    OrderEventListener -->|tworzy przesyłkę|ShipmentService
    
    classDef service fill:#f96,stroke:#333,stroke-width:1px;
    classDef kafka fill:#c9e,stroke:#333,stroke-width:1px;
    
    class OrderService,OrderGrpcService,ProductService,ProductGrpcService,InventoryService,ShipmentService,WarehouseEventConsumer,WarehouseEventProducer,OrderEventListener service;
    class OrderCreatedTopic,StockUpdatedTopic,ProductReservedTopic kafka;