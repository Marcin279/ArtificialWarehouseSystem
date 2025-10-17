# JWT Authentication - Jak to działa w systemie mikroserwisów

## Architektura

### 1. **auth-service** (Port 8085)

- Odpowiada za rejestrację i logowanie użytkowników
- Generuje tokeny JWT podpisane **kluczem prywatnym RSA**
- Endpoint logowania: `POST http://localhost:8085/api/auth/login`
- Endpoint rejestracji: `POST http://localhost:8085/api/auth/register`

### 2. **products-service** (Port 8081)

- Waliduje tokeny JWT używając **klucza publicznego RSA**
- Wymaga tokenu JWT dla operacji POST/PUT/DELETE
- Publiczny dostęp do GET (odczyt produktów)

---

## Przepływ autentykacji

### Krok 1: Użytkownik loguje się do auth-service

**Request:**

```http
POST http://localhost:8085/api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNjk3NDU2...",
  "username": "john_doe",
  "role": "USER"
}
```

### Krok 2: Użytkownik używa tokenu do żądań w products-service

**Request (bez tokenu - FORBIDDEN):**

```http
POST http://localhost:8081/api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 4999.99,
  "stock": 10
}
```

**Response:** `401 Unauthorized - Missing or invalid Authorization header`

---

**Request (z tokenem - SUCCESS):**

```http
POST http://localhost:8081/api/products
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJqb2huX2RvZSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNjk3NDU2...

{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 4999.99,
  "stock": 10
}
```

**Response:** `201 Created` + dane produktu

---

## Jak działa walidacja JWT w products-service?

### 1. **JwtFilter** przechwytuje każde żądanie HTTP

- Sprawdza nagłówek `Authorization: Bearer <token>`
- Jeśli token brakuje → `401 Unauthorized`
- Jeśli token jest nieprawidłowy → `401 Unauthorized`
- Jeśli token jest prawidłowy → ustawia autentykację w Spring Security Context

### 2. **JwtUtil** waliduje token za pomocą klucza publicznego RSA

- Parsuje token używając klucza publicznego
- Sprawdza podpis (czy token został wygenerowany przez auth-service)
- Sprawdza datę wygaśnięcia
- Ekstraktuje username i role z tokenu

### 3. **SecurityConfig** decyduje kto ma dostęp

```java
.requestMatchers(HttpMethod.GET, "/api/products/**").

permitAll()        // Wszyscy
.

requestMatchers(HttpMethod.POST, "/api/products/**").

authenticated()   // Tylko zalogowani
.

requestMatchers(HttpMethod.PUT, "/api/products/**").

authenticated()    // Tylko zalogowani
.

requestMatchers(HttpMethod.DELETE, "/api/products/**").

authenticated() // Tylko zalogowani
```

---

## Kluczowe pliki

### auth-service

- `JwtUtil.java` - generuje tokeny JWT (klucz prywatny RSA)
- `AuthService.java` - logika logowania i rejestracji
- `application.yml` - zawiera klucz prywatny RSA

### products-service

- `JwtUtil.java` - waliduje tokeny JWT (klucz publiczny RSA)
- `JwtFilter.java` - przechwytuje requesty i waliduje tokeny
- `SecurityConfig.java` - konfiguruje politykę bezpieczeństwa
- `application.yml` - zawiera klucz publiczny RSA

---

## Zalety tego podejścia (RSA)

✅ **Bezpieczeństwo**: Klucz prywatny tylko w auth-service, inne serwisy mają tylko klucz publiczny  
✅ **Skalowalność**: Każdy mikroserwis może walidować tokeny bez wywoływania auth-service  
✅ **Brak sesji**: Stateless - token zawiera wszystkie informacje  
✅ **Wydajność**: Brak dodatkowych wywołań sieciowych do auth-service przy każdym requeście

---

## Testowanie

### 1. Uruchom serwisy

```bash
docker-compose up -d postgres-auth postgres-products
cd auth-service && mvn spring-boot:run
cd products-service && mvn spring-boot:run
```

### 2. Zarejestruj użytkownika

```bash
curl -X POST http://localhost:8085/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 3. Zaloguj się i otrzymaj token

```bash
curl -X POST http://localhost:8085/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123!"
  }'
```

Skopiuj wartość `token` z odpowiedzi.

### 4. Użyj tokenu do dodania produktu

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <WKLEJ_TOKEN_TUTAJ>" \
  -d '{
    "name": "Test Product",
    "description": "Test",
    "price": 99.99,
    "stock": 5
  }'
```

### 5. Sprawdź że bez tokenu nie działa

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Test",
    "price": 99.99,
    "stock": 5
  }'
```

Powinno zwrócić: `401 Unauthorized`

---

## Troubleshooting

### Problem: "Invalid token"

- Sprawdź czy klucze RSA są zgodne (publiczny w products-service musi odpowiadać prywatnemu w auth-service)
- Sprawdź czy token nie wygasł (domyślnie 24h)
- Sprawdź format nagłówka: `Authorization: Bearer <token>` (z spacją po "Bearer")

### Problem: "Missing or invalid Authorization header"

- Upewnij się że dodajesz nagłówek `Authorization`
- Format: `Bearer <token>` (nie zapomnij o "Bearer " na początku)

### Problem: Kompilacja failuje

```bash
mvn clean package -DskipTests -T 1C
```

---

## Następne kroki (opcjonalnie)

1. **Refresh tokens** - dodaj mechanizm odświeżania tokenów bez ponownego logowania
2. **Role-based access** - dodaj różne uprawnienia dla USER i ADMIN
3. **Token blacklist** - możliwość unieważnienia tokenów (logout)
4. **Gateway** - dodaj API Gateway, który będzie pośrednikiem między klientem a serwisami

