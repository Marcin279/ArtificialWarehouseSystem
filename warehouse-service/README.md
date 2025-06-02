# Logika serwisu warehouse-service

Serwis magazynowy jest kluczowym elementem systemu, odpowiedzialnym za zarządzanie stanem magazynowym i lokalizacją
produktów. Oto jak powinna wyglądać jego logika:

## Główne funkcjonalności

1. Zarządzanie stanem magazynowym
    - Śledzenie dostępnej ilości produktów
    - Rozróżnianie między dostępną a zarezerwowaną ilością
    - Aktualizowanie stanu przy dodawaniu/usuwaniu produktów

2. Zarządzanie lokalizacjami
    - Organizacja magazynu: sekcje, półki, pojemniki
    - Przydzielanie produktów do lokalizacji
    - Optymalizacja wykorzystania przestrzeni

3. Obsługa rezerwacji produktów
    - Rezerwowanie produktów dla zamówień
    - Zwalnianie rezerwacji (np. przy anulowaniu zamówienia)
    - Śledzenie rezerwacji według zamówień
    - Rejestrowanie historii transakcji

Zapisywanie wszystkich operacji magazynowych
Audyt zmian stanu magazynowego

# Komunikacja z innymi serwisami

## gRPC (komunikacja synchroniczna)

- Metody udostępniane przez serwis:
- checkInventory(productId) - sprawdzenie dostępności produktu
- reserveItems(orderId, items[]) - rezerwacja produktów dla zamówienia
- releaseItems(reservationId) - zwolnienie rezerwacji

## Kafka (komunikacja asynchroniczna)

### Publikowane zdarzenia:

- product-reserved - gdy produkt został zarezerwowany
- product-released - gdy rezerwacja została zwolniona
- stock-updated - gdy zmienił się stan magazynowy

### Nasłuchiwane zdarzenia:

- order-created - aby zarezerwować produkty
- order-cancelled - aby zwolnić rezerwacje

## Struktura danych

Bazując na utworzonych tabelach, serwis powinien zarządzać:

- inventory_items - stan magazynowy produktów z podziałem na ilości (total, available, reserved)
- locations - informacje o fizycznych lokalizacjach w magazynie
- stock_transactions - historia wszystkich operacji na stanie magazynowym

## Przepływ pracy

- Zamówienie zostaje utworzone (order-service)
- Order-service wywołuje warehouse-service przez gRPC, aby zarezerwować produkty
- Warehouse-service rezerwuje produkty i aktualizuje stan magazynowy
- Warehouse-service publikuje zdarzenie product-reserved do Kafki
- Gdy zamówienie jest gotowe do wysyłki, shipping-service konsumuje te informacje
- Jeśli zamówienie jest anulowane, warehouse-service zwalnia rezerwację i publikuje product-released

Ta architektura zapewnia zarówno szybką synchroniczną komunikację przez gRPC dla krytycznych operacji, jak i
asynchroniczne przetwarzanie zdarzeń przez Kafkę dla informowania innych serwisów o zmianach stanu magazynowego.