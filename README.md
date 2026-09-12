# CaseFlow

Kleine Java-/Spring-Boot-Anwendung zur Verwaltung von Vorgängen.

Das Projekt dient dazu, meine Java-Kenntnisse praktisch aufzufrischen und
moderne Backend-Entwicklung mit Spring Boot, JPA, PostgreSQL und Tests
anzuwenden.

## Tech Stack

- Java 17
- Spring Boot
- Maven
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Docker Compose
- JUnit
- Mockito
- MockMvc
- Git
- GitHub Actions (CI)

## Architektur

Die Anwendung folgt einer einfachen Schichtenarchitektur:

HTTP Request  
→ Controller  
→ Service  
→ Repository  
→ PostgreSQL

### Controller

Verarbeitet HTTP-Requests und HTTP-Responses.

### Service

Enthält die Anwendungslogik.

### Repository

Kapselt den Datenbankzugriff über Spring Data JPA.

## Features

Aktuell unterstützt die API:

- Cases anlegen
- alle Cases abrufen
- einzelnen Case per ID abrufen
- Status eines Cases ändern
- Request-Validierung
- strukturierte Fehlerantworten
- persistente Speicherung in PostgreSQL

## API

### Alle Cases abrufen

`GET /api/cases`

### Einzelnen Case abrufen

`GET /api/cases/{id}`

### Case anlegen

`POST /api/cases`

Beispiel:

```json
{
  "title": "Address change",
  "description": "Customer reported a new address"
}
```

### Status ändern

`PATCH /api/cases/{id}/status`

Beispiel:

```json
{
  "status": "IN_PROGRESS"
}
```

Mögliche Statuswerte:

- `OPEN`
- `IN_PROGRESS`
- `COMPLETED`

## Lokales Setup

Voraussetzungen:

- Java 17
- Docker
- Docker Compose

PostgreSQL starten:

```powershell
docker compose up -d
```

Spring Boot starten:

```powershell
.\mvnw.cmd spring-boot:run
```

Die Anwendung läuft anschließend unter:

`http://localhost:8080`

## Tests

Tests ausführen:

```powershell
.\mvnw.cmd test
```

Aktuell vorhanden:

- Unit Tests für die Service-Schicht
- Controller-/Web-Tests mit MockMvc
- Validierungs- und Fehlerfälle

## Learning Notes

Zusätzliche Lernnotizen zu Architektur, JPA, Validierung, Exception Handling
und Tests befinden sich in:

`learning-notes.md`

## Mögliche Erweiterungen

- Integrationstests mit Testcontainers
- Datenbankmigrationen mit Flyway
- Logging
- zusätzliche Filter- und Suchfunktionen