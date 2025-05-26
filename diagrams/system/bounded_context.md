# Bounded context diagram

```mermaid
graph TD
%% Style diagramu
classDef component fill:white,stroke:black,stroke-width:1px;
classDef database fill:white,stroke:black,stroke-width:1px;
classDef table fill:white,stroke:black,stroke-width:1px;

%% API Gateway
GW["API Gateway"]

%% Products Context
subgraph PC["Products Context"]
    PDB[(ProductsDB)]
    Products[Products]
    Categories[Categories]
    PDB --> Products
    PDB --> Categories
end

%% Orders Context
subgraph OC["Orders Context"]
    ODB[(OrdersDB)]
    Orders[Orders]
    OrderItems[OrderItems]
    ODB --> Orders
    ODB --> OrderItems
end

%% Warehouse Context
subgraph WC["Warehouse Context"]
    WDB[(WarehouseDB)]
    InventoryItems[InventoryItems]
    Locations[Locations]
    StockTransactions[StockTransactions]
    WDB --> InventoryItems
    WDB --> Locations
    WDB --> StockTransactions
end

%% Shipping Context
subgraph SC["Shipping Context"]
    SDB[(ShippingDB)]
    Shipments[Shipments]
    ShippingLabels[ShippingLabels]
    SDB --> Shipments
    SDB --> ShippingLabels
end

%% Relacje między kontekstami
OC -- "gRPC (getProductById)" --> PC
OC -- "Kafka (order-created-topic)" --> WC
OC -- "Kafka (order-created-topic)" --> SC
SC -- "REST (getOrderById)" --> OC
WC -- "gRPC (getProductById)" --> PC

%% Gateway API
GW -- "REST" --> PC
GW -- "REST" --> OC
GW -- "REST" --> WC
GW -- "REST" --> SC

%% Zastosowanie stylów
class GW,PC,OC,WC,SC component;
class PDB,ODB,WDB,SDB database;
class Products,Categories,Orders,OrderItems,InventoryItems,Locations,StockTransactions,Shipments,ShippingLabels table;


