# ArtificialWarehouseSystem

microservice architecture research based on artificial warehouse system

Temat problemu:
System zarządzania sztucznym magazynem w architekturze mikroserwisowej
Problem:
Wiele firm boryka się z nieefektywnym zarządzaniem magazynem – ręczne operacje, brak aktualnych informacji o stanach
magazynowych, problemy z kompletacją zamówień i optymalizacją przestrzeni. Tradycyjne systemy często są monolityczne i
trudno je skalować.

Cel aplikacji:
Stworzenie rozproszonego systemu magazynowego opartego na mikroserwisach, który umożliwi automatyczne zarządzanie
stanami magazynowymi, rejestrowanie przepływu towarów, optymalizację rozmieszczenia produktów oraz analizę operacji
magazynowych.

Kluczowe mikroserwisy
1️⃣ Serwis zarządzania produktami: product-service

Przechowuje informacje o produktach, kategoriach, dostawcach.
Obsługuje CRUD dla produktów i ich dostępności.
Wystawia API do innych serwisów.
2️⃣ Serwis zamówień: order-service

Obsługuje przyjmowanie i realizację zamówień.
Rezerwuje produkty w magazynie i komunikuje się z innymi serwisami.
3️⃣ Serwis magazynowy: warehouse-service

Śledzi lokalizację produktów w magazynie.
Optymalizuje rozmieszczenie towarów.
4️⃣ Serwis wysyłek: shipment-service

Organizuje procesy logistyczne.
Współpracuje z firmami kurierskimi.
Generuje etykiety wysyłkowe.
5️⃣ Serwis analityki: analytics-service

Przetwarza dane o stanach magazynowych, rotacji produktów, wydajności pracowników.
Może wykorzystywać AI do optymalizacji rozmieszczenia towarów.
6️⃣ Serwis autoryzacji i użytkowników: auth-service

Zarządza rolami i dostępem (np. magazynier, administrator, klient).
Obsługuje logowanie i autoryzację JWT.
Kluczowe aspekty architektury mikroserwisowej
✅ Skalowalność – Możesz niezależnie skalować serwis zamówień, jeśli popyt rośnie.
✅ Komunikacja synchroniczna i asynchroniczna – REST API do CRUD, kolejki (np. RabbitMQ, Kafka) do aktualizacji stanów
magazynowych.
✅ Bezpieczeństwo – Uwierzytelnianie i autoryzacja użytkowników, zabezpieczenie komunikacji między mikroserwisami.
✅ Monitorowanie i logowanie – Centralizacja logów (np. ELK Stack) i metryki wydajności (np. Prometheus, Grafana).
✅ Konteneryzacja i orkiestracja – Docker + Kubernetes do zarządzania środowiskiem rozproszonym.
✅ Testowanie – Testy jednostkowe, integracyjne (API Gateway), obciążeniowe dla skalowania systemu.

Dlaczego to dobry temat?
🔹 Idealnie pokazuje zalety mikroserwisów: elastyczność, skalowalność, odporność na awarie.
🔹 Obejmuje zarówno backend (API, komunikacja między serwisami), jak i frontend (np. panel zarządzania magazynem).
🔹 W dalszym rozwoju pozwala na wykorzystanie nowoczesnych technologii: AI do optymalizacji, IoT (jeśli np. uwzględnisz
czujniki RFID do śledzenia towarów).

Oto propozycja typów komunikacji między serwisami w Twoim projekcie, uwzględniając różne podejścia (REST, gRPC, Kafka):

### 1. **auth-service** (serwis autoryzacji)

- **Typ komunikacji**: REST API
- **Dlaczego?**:
    - REST API jest standardem w przypadku autoryzacji i uwierzytelniania.
    - Łatwość integracji z klientami (np. frontend, inne serwisy).
    - Możliwość wykorzystania tokenów JWT w nagłówkach HTTP.

---

### 2. **analytics-service** (serwis analityczny)

- **Typ komunikacji**: Apache Kafka
- **Dlaczego?**:
    - Serwis analityczny może subskrybować zdarzenia z innych serwisów (np. `OrderCreated`, `ShipmentCreated`).
    - Kafka umożliwia asynchroniczne przetwarzanie dużej ilości danych w czasie rzeczywistym.
    - Możliwość ponownego przetwarzania zdarzeń w przypadku awarii.

---

### 3. **order-service** (serwis zamówień)

- **Typ komunikacji**: REST API / Apache Kafka
- **Dlaczego?**:
    - REST API: Do synchronizacji z serwisami, które potrzebują szczegółowych danych zamówienia (np.
      `shipment-service`).
    - Kafka: Do publikowania zdarzeń związanych z zamówieniami (np. `OrderCreated`, `OrderCancelled`), które mogą być
      subskrybowane przez inne serwisy.

---

### 4. **product-service** (serwis produktów)

- **Typ komunikacji**: gRPC
- **Dlaczego?**:
    - gRPC zapewnia szybką i wydajną komunikację, co jest istotne przy częstym pobieraniu danych o produktach (np. ceny,
      dostępność).
    - Binarny format Protobuf zmniejsza rozmiar przesyłanych danych.

---

### 5. **shipment-service** (serwis przesyłek)

- **Typ komunikacji**: REST API / Apache Kafka
- **Dlaczego?**:
    - REST API: Do pobierania szczegółów zamówienia z `order-service` w sposób synchroniczny.
    - Kafka: Do subskrybowania zdarzeń, takich jak `OrderCreated`, w celu automatycznego tworzenia przesyłek.

---

### 6. **warehouse-service** (serwis magazynowy)

- **Typ komunikacji**: gRPC / Apache Kafka
- **Dlaczego?**:
    - gRPC: Do synchronizacji stanów magazynowych w czasie rzeczywistym (np. rezerwacja produktów).
    - Kafka: Do obsługi zdarzeń, takich jak `ProductReserved` lub `StockUpdated`, które mogą być publikowane przez inne
      serwisy.

---

### Podsumowanie:

- **REST API**: auth-service, order-service (do synchronizacji z shipment-service).
- **gRPC**: product-service, warehouse-service (do szybkiej komunikacji w czasie rzeczywistym).
- **Apache Kafka**: analytics-service, order-service (do publikowania zdarzeń), shipment-service, warehouse-service.

Dzięki takiemu podziałowi możesz porównać różne podejścia w praktyce i opisać ich zalety oraz wady w swojej pracy
magisterskiej.

Najczęściej używane serwisy w Twoim projekcie prawdopodobnie będą to:

auth-service - Serwis autoryzacji będzie wykorzystywany przy każdym żądaniu, aby uwierzytelnić użytkownika i sprawdzić
jego uprawnienia.

order-service - Serwis zamówień będzie często używany do tworzenia, aktualizowania i pobierania informacji o
zamówieniach.

product-service - Serwis produktów będzie często wykorzystywany do pobierania informacji o produktach, takich jak ceny,
dostępność czy szczegóły.

Pozostałe serwisy, takie jak shipment-service, warehouse-service czy analytics-service, mogą być używane rzadziej, w
zależności od specyfiki Twojego projektu i wymagań biznesowych

# Przepływ pracy w systemie mikrousług

## Główne komponenty systemu

- Product-service - zarządzanie katalogiem produktów
- Order-service - obsługa zamówień
- Warehouse-service - zarządzanie magazynem i stanem produktów
- Shipping-service - obsługa wysyłek i dostarczeń

## Przepływ pracy od początku do końca

1. Przeglądanie produktów
    - Klient przegląda dostępne produkty w Product-service
    - Product-service udostępnia katalog produktów wraz z cenami i opisami
2. Składanie zamówienia
    - Klient składa zamówienie przez Order-service
    - Order-service pobiera przez gRPC informacje o produktach z Product-service (ceny, dostępność)
    - Order-service wywołuje przez gRPC metodę checkInventory z Warehouse-service, aby potwierdzić dostępność
    - Jeśli produkty są dostępne, Order-service wywołuje reserveItems w Warehouse-service
    - Warehouse-service rezerwuje produkty (zmniejsza dostępną ilość, zwiększa zarezerwowaną)
    - Warehouse-service tworzy wpis w stock_transactions z typem RESERVATION
    - Warehouse-service odsyła potwierdzenie rezerwacji do Order-service
    - Order-service zapisuje zamówienie i publikuje zdarzenie order-created do Kafki
    - Warehouse-service publikuje zdarzenie product-reserved do Kafki
3. Przetwarzanie wysyłki
    - Shipping-service odbiera zdarzenie order-created z Kafki
    - Shipping-service tworzy nową przesyłkę dla zamówienia
    - Shipping-service może pobrać dodatkowe informacje o zamówieniu z Order-service przez HTTP (widoczne w
      konfiguracji)
    - Gdy przesyłka jest gotowa, Shipping-service publikuje zdarzenie order-shipped
4. Finalizacja zamówienia
    - Po wysłaniu, Warehouse-service zmienia stan zarezerwowanych produktów na wydane
    - Warehouse-service tworzy wpis w stock_transactions z typem STOCK_REMOVAL
    - Warehouse-service publikuje zdarzenie stock-updated do Kafki
    - Product-service może odbierać informacje o zmianach stanu magazynowego
5. Obsługa anulowania (przypadek alternatywny)
    - W przypadku anulowania, Order-service publikuje zdarzenie order-cancelled do Kafki
    - Warehouse-service odbiera zdarzenie i zwalnia rezerwację
    - Warehouse-service tworzy wpis w stock_transactions z typem RESERVATION_RELEASE
    - Warehouse-service publikuje zdarzenie product-released do Kafki
    - Shipping-service odbiera zdarzenie anulowania i odpowiednio aktualizuje status

## Prerequisites

Before proceeding, ensure the following tools are installed and properly configured:

- **Java Development Kit (JDK)**
- **Apache Maven**
- **Docker** (including Docker Compose)
- A valid `docker-compose.yml` file in the project directory

---

## Step 1: Build the Project Using Maven

1. Open a terminal or command prompt.
2. Navigate to the root directory of your project (where the `pom.xml` file is located).
3. Run the following command to clean and build the project:

   ```bash
   mvn clean package -DskipTests
   ```

   ### Explanation:
    - **`clean`**: Removes previously compiled files to ensure a fresh build.
    - **`package`**: Compiles, tests, and packages the application into executable JAR files.
    - **`-DskipTests`**: Skips running tests during the build process for faster execution.

4. Verify that the JAR files for each microservice are generated in the respective `target/` directories.

---

## Step 2: Deploy the Application Using Docker Compose

1. Ensure the `docker-compose.yml` file defines all necessary services and their configurations.
2. Run the following command to start the services:

   ```bash
   docker compose up
   ```

   ### Notes:
    - Docker Compose will pull required images, build the services, and start containers as defined in the
      `docker-compose.yml` file.
    - Logs for all services will appear in the terminal.

3. Optionally, run in detached mode (background):

   ```bash
   docker compose up -d
   ```

---

## Step 3: Verify the Deployment

1. Confirm that the containers are running:

   ```bash
   docker ps
   ```

2. Check the logs for any specific service:

   ```bash
   docker logs <container_name>
   ```

3. Access the services using the URLs or ports defined in `docker-compose.yml`.

---

## Step 4: Shut Down the Application

To stop the application and remove containers, networks, and volumes created by Docker Compose, run:

```bash
docker compose down
```

---

## Troubleshooting

- **Build issues with Maven**:
    - Check for errors in the `pom.xml` file.
    - Ensure all dependencies are correctly defined and available.

- **Docker Compose errors**:
    - Verify that Docker is running.
    - Validate the syntax of the `docker-compose.yml` file.
    - Ensure required images are available or accessible.

---