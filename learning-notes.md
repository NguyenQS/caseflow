# Learning Notes

## Projekt-Setup

Das Projekt verwendet Java 17, Spring Boot und Maven.

Maven verwaltet den Build-Prozess und die Abhängigkeiten des Projekts.
Der Maven Wrapper ermöglicht es, Maven projektbezogen zu verwenden,
ohne Maven separat systemweit installieren zu müssen.

## REST-Controller und Service-Schicht

Der Controller kümmert sich um HTTP-Anfragen und HTTP-Antworten.
Die Anwendungslogik liegt dagegen im Service.

Aktueller Ablauf:

GET /api/cases
→ CaseController
→ CaseService
→ List<Case>
→ JSON-Response

Spring stellt dem `CaseController` den benötigten `CaseService`
über Constructor Injection zur Verfügung.

Der Controller kennt dadurch seine Abhängigkeit zum Service,
muss den Service aber nicht selbst mit `new CaseService()` erzeugen.

## Request DTO

Für eingehende POST-Anfragen wird `CreateCaseRequest` verwendet,
anstatt direkt ein vollständiges `Case` entgegenzunehmen.

Dadurch kann gezielt festgelegt werden, welche Werte von außen
übergeben werden dürfen.

Zum Beispiel werden folgende Werte durch die Anwendung selbst gesetzt:

- ID
- Status
- Erstellungszeitpunkt

Der Client übergibt aktuell nur Titel und Beschreibung.

## In-Memory-Speicherung

Cases werden momentan in einer `ArrayList` gespeichert.

Diese Speicherung ist nur temporär:
Nach einem Neustart der Anwendung sind die Daten wieder verloren.

Als nächster Schritt soll diese In-Memory-Lösung durch eine
persistente Speicherung mit PostgreSQL ersetzt werden.

## Fehlerbehandlung

Beim ersten Versuch führte die Anfrage nach einem nicht vorhandenen
Case zu einem HTTP-500-Fehler.

Das war fachlich nicht passend, da ein nicht vorhandener Case
kein unerwarteter interner Serverfehler ist.

Deshalb wurde eine eigene `CaseNotFoundException` eingeführt.
Ein globaler Exception Handler übersetzt diese Exception in
einen HTTP-404-Status.

Ablauf:

GET /api/cases/999
→ CaseService findet keinen passenden Case
→ CaseNotFoundException
→ GlobalExceptionHandler
→ HTTP 404

## Persistenz mit PostgreSQL und JPA

Die bisherige In-Memory-Speicherung wurde durch PostgreSQL ersetzt.

Der Zugriff auf die Datenbank erfolgt über Spring Data JPA.
`Case` ist jetzt eine JPA-Entity und wird einer Datenbanktabelle zugeordnet.

Wichtige Bestandteile:

- `@Entity` markiert die Klasse als persistierbare JPA-Entity.
- `@Id` kennzeichnet den Primärschlüssel.
- `@GeneratedValue` überlässt die ID-Erzeugung der Datenbank.
- `@Enumerated(EnumType.STRING)` speichert den Status als lesbaren String.
- `CaseRepository` erweitert `JpaRepository<Case, Long>` und stellt dadurch
  Methoden wie `findAll()`, `findById()` und `save()` bereit.

Der Ablauf beim Speichern ist nun:

POST /api/cases
→ CaseController
→ CaseService
→ CaseRepository
→ Hibernate/JPA
→ PostgreSQL

Nach einem Neustart der Spring-Boot-Anwendung bleiben die Daten erhalten.
Damit ist die Speicherung jetzt persistent.