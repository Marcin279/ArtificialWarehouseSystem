# ArtificialWarehouseSystem
microservice architecture research based on artificial warehouse system

Temat problemu:
System zarządzania sztucznym magazynem w architekturze mikroserwisowej
Problem:
Wiele firm boryka się z nieefektywnym zarządzaniem magazynem – ręczne operacje, brak aktualnych informacji o stanach magazynowych, problemy z kompletacją zamówień i optymalizacją przestrzeni. Tradycyjne systemy często są monolityczne i trudno je skalować.

Cel aplikacji:
Stworzenie rozproszonego systemu magazynowego opartego na mikroserwisach, który umożliwi automatyczne zarządzanie stanami magazynowymi, rejestrowanie przepływu towarów, optymalizację rozmieszczenia produktów oraz analizę operacji magazynowych.

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
✅ Komunikacja synchroniczna i asynchroniczna – REST API do CRUD, kolejki (np. RabbitMQ, Kafka) do aktualizacji stanów magazynowych.
✅ Bezpieczeństwo – Uwierzytelnianie i autoryzacja użytkowników, zabezpieczenie komunikacji między mikroserwisami.
✅ Monitorowanie i logowanie – Centralizacja logów (np. ELK Stack) i metryki wydajności (np. Prometheus, Grafana).
✅ Konteneryzacja i orkiestracja – Docker + Kubernetes do zarządzania środowiskiem rozproszonym.
✅ Testowanie – Testy jednostkowe, integracyjne (API Gateway), obciążeniowe dla skalowania systemu.

Dlaczego to dobry temat?
🔹 Idealnie pokazuje zalety mikroserwisów: elastyczność, skalowalność, odporność na awarie.
🔹 Obejmuje zarówno backend (API, komunikacja między serwisami), jak i frontend (np. panel zarządzania magazynem).
🔹 W dalszym rozwoju pozwala na wykorzystanie nowoczesnych technologii: AI do optymalizacji, IoT (jeśli np. uwzględnisz czujniki RFID do śledzenia towarów).


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
    - Docker Compose will pull required images, build the services, and start containers as defined in the `docker-compose.yml` file.
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