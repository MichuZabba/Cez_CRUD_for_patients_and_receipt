# CEZ — CRUD dla Pacjentów i Recept

REST API do zarządzania pacjentami oraz receptami, inspirowane architekturą systemów e-zdrowia.
Aplikacja umożliwia tworzenie, pobieranie, wyszukiwanie i usuwanie pacjentów oraz recept z poziomu HTTP.

---

## Spis treści

- [Opis projektu](#opis-projektu)
- [Model danych](#model-danych)
- [Wzorce architektoniczne](#wzorce-architektoniczne)
- [Stack technologiczny](#stack-technologiczny)
- [Uruchomienie projektu](#uruchomienie-projektu)
- [Dokumentacja API (Swagger)](#dokumentacja-api-swagger)
- [Testowanie z Postmanem](#testowanie-z-postmanem)
- [Endpointy API](#endpointy-api)

---

## Opis projektu

Aplikacja stanowi backend systemu do rejestrowania pacjentów i wystawianych im recept.
Dane przechowywane są w pamięci operacyjnej (`ConcurrentHashMap`) — brak zewnętrznej bazy danych.

**Główne funkcjonalności:**
- Rejestracja i usuwanie pacjentów na podstawie numeru PESEL
- Wystawianie i usuwanie recept powiązanych z pacjentem
- Wyszukiwanie pacjentów i recept z filtrowaniem i stronicowaniem
- Walidacja danych wejściowych (PESEL, imię, nazwisko, dawka)
- Globalna obsługa błędów z czytelnymi komunikatami

---

## Model danych

### Pacjent (`Patient`)

| Pole       | Typ      | Opis                          | Walidacja                        |
|------------|----------|-------------------------------|----------------------------------|
| `pesel`    | `String` | Numer PESEL pacjenta          | Dokładnie 11 cyfr, niepuste      |
| `imie`     | `String` | Imię pacjenta                 | 2–50 znaków, niepuste            |
| `nazwisko` | `String` | Nazwisko pacjenta             | 2–50 znaków, niepuste            |

> PESEL jest kluczem identyfikującym pacjenta — musi być unikalny.

---

### Recepta (`Prescription`)

| Pole             | Typ      | Opis                                | Walidacja                   |
|------------------|----------|-------------------------------------|-----------------------------|
| `prescriptionId` | `UUID`   | Unikalny identyfikator recepty      | Generowany automatycznie    |
| `pesel`          | `String` | PESEL pacjenta, dla którego recepta | Dokładnie 11 cyfr, niepuste |
| `nazwaLeku`      | `String` | Nazwa przepisanego leku             | Niepusta                    |
| `dawka`          | `Double` | Dawka leku (mg, ml itp.)            | Wartość dodatnia (> 0)      |

---

## Wzorce architektoniczne

### CQRS (Command Query Responsibility Segregation)

Projekt stosuje wzorzec **CQRS**, który rozdziela operacje zapisu (komendy) od operacji odczytu (zapytania):

```
src/main/java/cez/
├── common/
│   └── cqrs/
│       ├── Command.java          ← marker interfejs dla komend
│       ├── CommandHandler.java   ← interfejs obsługi komend: void handle(C command)
│       ├── Query.java            ← marker interfejs dla zapytań
│       └── QueryHandler.java     ← interfejs obsługi zapytań: R handle(Q query)
│
├── patient/
│   ├── command/                  ← CreatePatientCommand, DeletePatientCommand + ich handlery
│   ├── query/                    ← GetPatientByPeselQuery, SearchPatientsQuery + ich handlery
│   ├── service/                  ← logika filtrowania i stronicowania
│   ├── repository/               ← przechowywanie danych w ConcurrentHashMap
│   ├── controller/               ← warstwa HTTP (REST)
│   ├── dto/                      ← obiekty żądania i odpowiedzi
│   └── model/                    ← rekord Patient
│
└── prescription/
    ├── command/                  ← CreatePrescriptionCommand, DeletePrescriptionCommand + handlery
    ├── query/                    ← GetAllPrescriptionByPeselQuery, SearchPrescriptionsQuery + handlery
    ├── service/
    ├── repository/
    ├── controller/
    ├── dto/
    └── model/                    ← rekord Prescription
```

**Komendy** (zapis, brak wartości zwrotnej):
- `CreatePatientCommand` — tworzy pacjenta
- `DeletePatientCommand` — usuwa pacjenta
- `CreatePrescriptionCommand` — tworzy receptę (generuje UUID automatycznie)
- `DeletePrescriptionCommand` — usuwa receptę po UUID i PESEL

**Zapytania** (odczyt, zwracają dane):
- `GetPatientByPeselQuery` — zwraca jednego pacjenta
- `SearchPatientsQuery` — zwraca stronicowaną listę pacjentów z filtrowaniem
- `GetAllPrescriptionByPeselQuery` — zwraca wszystkie recepty pacjenta
- `SearchPrescriptionsQuery` — zwraca stronicowaną listę recept z filtrowaniem

### Repository Pattern

Każda domena posiada własne repozytorium z interfejsem (`IPatientRepository`, `IPrescriptionRepository`) i implementacją (`PatientRepository`, `PrescriptionRepository`). Dane przechowywane są w `ConcurrentHashMap` (thread-safe).

### Global Exception Handler

`GlobalExceptionHandler` (`@RestControllerAdvice`) przechwytuje `IllegalArgumentException` i zwraca ujednoliconą odpowiedź `400 Bad Request` z polem `message`.

---

## Stack technologiczny

| Technologia                   | Wersja   | Rola                                            |
|-------------------------------|----------|-------------------------------------------------|
| **Java**                      | 17       | Język programowania                             |
| **Spring Boot**               | 4.1.0    | Framework aplikacyjny                           |
| **Spring Web MVC**            | —        | Warstwa REST, kontrolery HTTP                   |
| **Spring Validation**         | —        | Walidacja danych wejściowych (`@Valid`)          |
| **Spring Data Commons**       | —        | Obsługa stronicowania (`Pageable`, `Page`)       |
| **SpringDoc OpenAPI**         | 2.8.5    | Automatyczna dokumentacja Swagger UI            |
| **Maven**                     | 3.9.x    | Zarządzanie zależnościami i budowanie projektu  |
| **JUnit 5**                   | —        | Testy jednostkowe                               |


---

## Uruchomienie projektu

### Wymagania wstępne

- **Java 17** lub nowsza (zalecana: Java 17 LTS)
- **Git**
- Brak wymagań dotyczących bazy danych — dane przechowywane są w pamięci

### 1. Klonowanie repozytorium

```bash
git clone <adres-repozytorium>
cd Cez_CRUD_for_patients_and_receipt
```

### 2. Budowanie projektu

**Linux / macOS:**
```bash
./mvnw clean install
```

**Windows:**
```cmd
mvnw.cmd clean install
```

### 3. Uruchomienie aplikacji

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

Aplikacja startuje domyślnie na porcie **`8080`**.

```
http://localhost:8080
```

### 4. Uruchamianie testów

```bash
# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

---

## Dokumentacja API (Swagger)

Po uruchomieniu aplikacji interaktywna dokumentacja dostępna jest pod adresem:

```
http://localhost:8080/swagger-ui/index.html
```

Umożliwia przeglądanie wszystkich endpointów i testowanie ich bezpośrednio z poziomu przeglądarki.

---

## Testowanie z Postmanem

W repozytorium znajduje się gotowa kolekcja Postmana z wszystkimi endpointami:

```
Cez api.postman_collection.json
```

### Instrukcja importu kolekcji

1. Otwórz aplikację **Postman**
2. Kliknij przycisk **Import** (lewy górny róg)
3. Wybierz zakładkę **File**
4. Kliknij **Upload Files** i wskaż plik `Cez api.postman_collection.json` z głównego katalogu projektu
5. Kliknij **Import**

Kolekcja zostanie zaimportowana z podziałem na dwie grupy:
- **patients** — endpointy zarządzania pacjentami
- **prescriptions** — endpointy zarządzania receptami

### Konfiguracja zmiennej środowiskowej

Kolekcja używa zmiennej `{{baseUrl}}`. Aby ją ustawić:

1. Kliknij ikonę **oka** (Environment quick look) w prawym górnym rogu
2. Kliknij **Add** lub **Edit** przy aktywnym środowisku
3. Dodaj zmienną:
   - **Variable:** `baseUrl`
   - **Initial value:** `http://localhost:8080`
   - **Current value:** `http://localhost:8080`
4. Kliknij **Save**

> Alternatywnie możesz edytować kolekcję i w zakładce **Variables** zmienić wartość `baseUrl` bezpośrednio w kolekcji — domyślnie jest już ustawiona na `http://localhost:8080`.

---

## Endpointy API

Bazowy URL: `http://localhost:8080`

### Pacjenci

| Metoda   | Endpoint              | Opis                                      | Kody odpowiedzi     |
|----------|-----------------------|-------------------------------------------|---------------------|
| `GET`    | `/patients/{pesel}`   | Pobierz pacjenta po PESEL                 | `200 OK`            |
| `POST`   | `/patients/create`    | Utwórz nowego pacjenta                    | `201 Created`, `400`|
| `DELETE` | `/patients/{pesel}`   | Usuń pacjenta po PESEL                    | `204 No Content`    |
| `POST`   | `/patients/search`    | Wyszukaj pacjentów (stronicowanie)        | `200 OK`            |

#### POST /patients/create — ciało żądania
```json
{
  "pesel": "12345678901",
  "imie": "Jan",
  "nazwisko": "Kowalski"
}
```

#### POST /patients/search — ciało żądania
```json
{
  "page": 0,
  "size": 10,
  "nazwisko": "Kowalski",
  "pesel": ""
}
```

> Filtrowanie działa po częściowym dopasowaniu nazwiska (case-insensitive) **lub** dokładnym dopasowaniu PESEL.
> Wyniki posortowane alfabetycznie: najpierw po nazwisku, następnie po imieniu.

---

### Recepty

| Metoda   | Endpoint                   | Opis                                        | Kody odpowiedzi     |
|----------|----------------------------|---------------------------------------------|---------------------|
| `POST`   | `/prescriptions/create`    | Utwórz nową receptę                         | `201 Created`, `400`|
| `GET`    | `/prescriptions/{pesel}`   | Pobierz wszystkie recepty pacjenta po PESEL | `200 OK`            |
| `DELETE` | `/prescriptions/delete`    | Usuń receptę po UUID i PESEL                | `204 No Content`    |
| `POST`   | `/prescriptions/search`    | Wyszukaj recepty (stronicowanie)            | `200 OK`            |

#### POST /prescriptions/create — ciało żądania
```json
{
  "pesel": "12345678901",
  "nazwaLeku": "Aspirin",
  "dawka": 100.0
}
```

#### DELETE /prescriptions/delete — ciało żądania
```json
{
  "prescriptionId": "550e8400-e29b-41d4-a716-446655440000",
  "pesel": "12345678901"
}
```

#### POST /prescriptions/search — ciało żądania
```json
{
  "page": 0,
  "size": 10,
  "nazwaLeku": "Aspirin",
  "pesel": ""
}
```

> Filtrowanie działa po częściowym dopasowaniu nazwy leku (case-insensitive) **lub** dokładnym dopasowaniu PESEL.

---

### Odpowiedzi błędów

Przy błędnej walidacji lub zduplikowanym PESEL API zwraca `400 Bad Request`:

```json
{
  "timestamp": "2026-06-24T12:00:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "Patient with this PESEL already exists"
}
```
