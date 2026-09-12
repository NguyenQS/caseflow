# Learning Notes

## Arbeitsweise

Das Projekt ist als kompaktes Java-/Spring-Boot-Lernprojekt entstanden.

Beim Implementieren habe ich KI-Unterstützung unter anderem für Code-Vorschläge,
Fehlersuche und Erklärungen genutzt. Mein Schwerpunkt lag darauf, die verwendeten
Bausteine nachvollziehen zu können und nicht nur Code zu übernehmen.

Bei neuen Konzepten habe ich deshalb jeweils versucht zu verstehen:

- warum ein bestimmter Baustein benötigt wird
- wie er in die Gesamtarchitektur passt
- was beim Request konkret passiert
- welche Alternativen es gäbe
- wie sich Fehler erkennen und eingrenzen lassen

Neue Schritte habe ich lokal ausgeführt, getestet und bei Problemen die Ursache
nachvollzogen. Die Notizen halten vor allem diese Lernschritte fest.

## Projekt-Setup

Das Projekt verwendet Java 17, Spring Boot und Maven.

Maven übernimmt unter anderem:

- Dependency Management
- Kompilieren des Java-Codes
- Ausführen der Tests
- Packaging und Build-Schritte

Der Maven Wrapper ermöglicht es, die für das Projekt vorgesehene Maven-Version
zu verwenden, ohne Maven separat systemweit installieren zu müssen.

Spring Boot stellt die Grundlage der Anwendung bereit. Für die Web-Anwendung
wird ein eingebetteter Tomcat verwendet, sodass kein separater Webserver
installiert werden muss.

## Aufbau der Anwendung

Die Anwendung verwendet eine einfache Schichtenarchitektur:

HTTP Request  
→ Controller  
→ Service  
→ Repository  
→ PostgreSQL

### Controller

Der Controller ist für die HTTP-Schnittstelle zuständig.

Er nimmt Requests entgegen, liest Parameter oder Request Bodies und gibt
Responses zurück.

Beispiel:

`GET /api/cases/{id}`

Der `CaseController` nimmt die ID aus dem Pfad entgegen und delegiert die
eigentliche Verarbeitung an den `CaseService`.

### Service

Der Service enthält die Anwendungslogik.

Dadurch bleibt die HTTP-Verarbeitung im Controller von der eigentlichen
Logik getrennt.

Beispiele:

- Case abrufen
- Case anlegen
- Status eines Cases ändern
- Fehler auslösen, wenn eine ID nicht existiert

### Repository

Das Repository kapselt den Datenbankzugriff.

`CaseRepository` erweitert:

`JpaRepository<Case, Long>`

Dadurch stehen bereits Methoden wie diese zur Verfügung:

- `findAll()`
- `findById()`
- `save()`

ohne dass die Implementierung selbst geschrieben werden muss.

## Dependency Injection

Der `CaseController` erzeugt seinen `CaseService` nicht selbst.

Statt:

`new CaseService()`

wird der Service über den Konstruktor übergeben.

Spring erkennt die benötigten Komponenten und stellt die Abhängigkeiten beim
Start der Anwendung bereit.

Dadurch sind die Klassen weniger stark miteinander gekoppelt und lassen sich
leichter testen.

## DTOs

Für eingehende Requests werden eigene DTOs verwendet.

Beispiele:

- `CreateCaseRequest`
- `UpdateCaseStatusRequest`

Dadurch kann gezielt festgelegt werden, welche Felder ein Client überhaupt
setzen darf.

Beim Erstellen eines Cases werden beispielsweise nur Titel und Beschreibung
vom Client geliefert.

Werte wie:

- ID
- Status
- Erstellungszeitpunkt

werden von der Anwendung selbst gesetzt.

## Von In-Memory zu persistenter Speicherung

Zu Beginn wurden Cases in einer `ArrayList` gespeichert.

Das war ausreichend, um zunächst Controller, Service und REST-Endpunkte
aufzubauen.

Dabei wurde direkt sichtbar, dass diese Lösung nur temporär ist:

Nach einem Neustart der Anwendung waren alle Daten verloren.

Im nächsten Schritt wurde die `ArrayList` durch PostgreSQL und Spring Data JPA
ersetzt.

Dadurch konnten Cases dauerhaft gespeichert werden.

Ein interessanter Punkt war dabei, dass die Service-Struktur weitgehend
bestehen bleiben konnte. Geändert wurde hauptsächlich die Art, wie die Daten
gespeichert und geladen werden.

## JPA und Entities

`Case` ist als JPA-Entity definiert.

Wichtige Annotationen:

- `@Entity` markiert die Klasse als persistierbare Entity.
- `@Id` kennzeichnet den Primärschlüssel.
- `@GeneratedValue` überlässt die ID-Erzeugung der Datenbank.
- `@Enumerated(EnumType.STRING)` speichert den Status als lesbaren String.

Die Verbindung sieht grob so aus:

Java Entity  
→ JPA / Hibernate  
→ SQL  
→ PostgreSQL

JPA ist dabei die Schnittstelle bzw. Abstraktion. Hibernate übernimmt in diesem
Projekt die konkrete Umsetzung.

## PostgreSQL und Docker Compose

PostgreSQL läuft lokal in einem Docker-Container.

Docker Compose stellt die Datenbank mit einer definierten Konfiguration bereit.

Dadurch lässt sich die benötigte Datenbankumgebung reproduzierbar starten.

Die Anwendung verbindet sich über:

`localhost:5432`

mit PostgreSQL.

Ein Docker Volume sorgt dafür, dass die Daten auch nach einem Neustart des
Containers erhalten bleiben.

## Request-Validierung

Eingehende Requests werden mit Bean Validation geprüft.

Im `CreateCaseRequest` werden beispielsweise verwendet:

- `@NotBlank`
- `@Size`

Im Controller aktiviert `@Valid` die Prüfung des DTOs.

Dadurch werden ungültige Daten abgefangen, bevor sie den Service erreichen.

Beispiel:

Ein leerer Titel führt zu:

`HTTP 400 Bad Request`

## Exception Handling

Beim ersten Versuch führte eine Anfrage nach einer unbekannten Case-ID zu einem
HTTP-500-Fehler.

Das war fachlich nicht passend.

Ein nicht vorhandener Case ist kein unerwarteter interner Serverfehler, sondern
ein normaler Fehlerfall der API.

Deshalb wurde eine eigene:

`CaseNotFoundException`

eingeführt.

Ein `GlobalExceptionHandler` übersetzt diese Exception in:

`HTTP 404 Not Found`

Ablauf:

GET /api/cases/999  
→ CaseService findet keinen Case  
→ CaseNotFoundException  
→ GlobalExceptionHandler  
→ HTTP 404

Auch Validierungsfehler werden dort in strukturierte Fehlerantworten
umgewandelt.

Beispiel:

```json
{
  "error": "Validation failed",
  "errors": {
    "title": "Title must not be blank"
  }
}
```

## Statusänderung eines Cases

Über:

`PATCH /api/cases/{id}/status`

kann der Status eines vorhandenen Cases geändert werden.

Dafür wird ein eigenes `UpdateCaseStatusRequest`-DTO verwendet.

So kann der Client nur den Status ändern und nicht gleichzeitig andere Felder
wie ID, Titel oder Erstellungszeitpunkt überschreiben.

Der Service:

1. lädt den Case über das Repository
2. prüft, ob er existiert
3. ändert den Status
4. speichert den Case erneut

Für die teilweise Änderung einer Ressource wird `PATCH` verwendet.

## Unit Tests mit JUnit und Mockito

Der `CaseService` wird isoliert mit Unit Tests getestet.

Dabei wird das echte `CaseRepository` durch ein Mockito-Mock ersetzt.

Dadurch hängt der Test nicht von PostgreSQL oder JPA ab.

Getestet werden unter anderem:

- Abrufen aller Cases
- Abrufen eines vorhandenen Cases
- Fehlerfall bei unbekannter ID
- Erstellen und Speichern eines Cases
- Ändern und Speichern des Status

Dabei werden unter anderem verwendet:

- `@Test`
- `@BeforeEach`
- `mock()`
- `when(...).thenReturn(...)`
- `assertEquals()`
- `assertThrows()`
- `verify()`

Besonders hilfreich war für mich der Unterschied zwischen:

- Ergebnis prüfen
- Interaktion prüfen

Mit `assertEquals()` wird beispielsweise geprüft, ob der Status wirklich
geändert wurde.

Mit `verify()` wird zusätzlich geprüft, ob das Repository tatsächlich zum
Speichern aufgerufen wurde.

## Controller-Tests mit MockMvc

Zusätzlich zu den Service-Tests wird auch die Web-Schicht getestet.

Mit `MockMvc` können HTTP-Requests innerhalb des Spring-Testkontexts simuliert
werden, ohne einen echten Server auf Port 8080 zu starten.

Dabei wird der echte `CaseController` verwendet.

Der `CaseService` wird dagegen durch ein Mockito-Mock ersetzt.

Getestet werden aktuell unter anderem:

- `GET /api/cases` liefert HTTP 200
- die Response enthält die erwartete JSON-Struktur
- ein ungültiger POST-Request liefert HTTP 400

Dadurch werden Service-Logik und Web-Schicht getrennt getestet.

Aktueller Stand:

8 Tests, 0 Fehler.

## Tomcat und DispatcherServlet

Spring Boot verwendet in diesem Projekt einen eingebetteten Tomcat.

Tomcat nimmt die HTTP-Verbindungen technisch entgegen.

Innerhalb von Spring MVC läuft der Request anschließend über den
`DispatcherServlet`.

Vereinfacht:

HTTP Request  
→ Tomcat  
→ DispatcherServlet  
→ passender Controller

Der `DispatcherServlet` übernimmt dabei das Routing innerhalb von Spring und
entscheidet, welche Controller-Methode für einen Request zuständig ist.

## Continuous Integration mit GitHub Actions

Zum Abschluss wurde ein GitHub-Actions-Workflow eingerichtet.

Bei Pushes auf `main` und bei Pull Requests werden die Tests automatisch auf
einem frischen Ubuntu-Runner ausgeführt.

Der erste CI-Lauf schlug fehl, weil der Maven Wrapper unter Linux nicht
ausführbar war.

Nach dieser Korrektur lief Maven, die Tests scheiterten aber weiterhin.

Die Ursache war der Spring-Kontexttest:

Die Anwendung erwartete PostgreSQL auf Port 5432, aber auf dem GitHub-Runner
lief noch keine Datenbank.

Deshalb startet der Workflow nun zusätzlich einen PostgreSQL-Service-Container.

Der Ablauf ist jetzt:

Push / Pull Request  
→ GitHub Actions  
→ Java 17 einrichten  
→ PostgreSQL starten  
→ `./mvnw test`  
→ Build erfolgreich oder fehlgeschlagen

Seit dieser Anpassung läuft die CI erfolgreich durch.

Dabei wurde für mich konkret nachvollziehbar, dass CI nicht einfach nur
"Tests auf einem anderen Rechner" bedeutet.

Auch externe Abhängigkeiten wie eine Datenbank müssen dort reproduzierbar
bereitgestellt werden.

## Was ich aus dem Projekt mitnehme

Besonders wichtig waren für mich folgende Punkte:

- Trennung von Controller, Service und Repository
- Dependency Injection
- DTOs und Request-Validierung
- JPA-Entities und persistente Speicherung
- Fehlerbehandlung über HTTP-Statuscodes
- Unterschied zwischen Unit Tests und Web-/Controller-Tests
- Mocking mit Mockito
- automatisierte Tests in GitHub Actions
- reproduzierbare Datenbankumgebung mit Docker

Das Projekt ist bewusst kompakt gehalten.

Als nächste sinnvolle Erweiterungen sehe ich unter anderem:

- Integrationstests mit Testcontainers
- Datenbankmigrationen mit Flyway
- Logging
- Filter- und Suchfunktionen
- zusätzliche fachliche Regeln für Statusübergänge

## Derived Queries mit Spring Data JPA

Die Statusfilterung wurde zunächst in Java mit `stream().filter(...)`
umgesetzt.

Dabei wurden alle Cases aus PostgreSQL geladen und erst anschließend in Java
gefiltert.

Anschließend wurde die Filterung in das Repository verlagert:

```java
List<Case> findByStatus(CaseStatus status);
```

Spring Data JPA kann aus diesem Methodennamen automatisch eine Query ableiten.

Das Muster ist dabei vereinfacht:

`findBy` + Property

Da `Case` ein Feld `status` besitzt, wird aus `findByStatus(...)` sinngemäß:

```sql
SELECT *
FROM cases
WHERE status = ?
```

Im Service wird die Methode über eine Method Reference verwendet:

```java
status
    .map(caseRepository::findByStatus)
    .orElseGet(caseRepository::findAll);
```

Wenn ein Status vorhanden ist, führt das Repository die gefilterte
Datenbankabfrage aus. Ohne Status werden alle Cases geladen.

Dadurch bleibt das API-Verhalten gleich, aber die Filterung findet nicht mehr
erst nach dem Laden aller Datensätze in Java statt.

## Refactoring und Tests

Beim Umbau von `getAllCases()` auf eine allgemeinere `getCases(...)`-Methode
blieb das Verhalten der REST-API nach außen unverändert.

Zwei Tests verwendeten intern aber noch die alte Service-Methode und schlugen
dadurch fehl.

Dadurch wurde für mich konkret sichtbar, dass Refactoring nicht nur
Produktionscode betrifft: Wenn sich interne Schnittstellen ändern, müssen auch
abhängige Tests angepasst werden.

Nach der Anpassung liefen wieder alle 8 Tests erfolgreich durch.