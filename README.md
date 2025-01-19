# ToDo-App

## Installationsanweisung

1. Klone das Repository: [https://github.com/luisgrohmannhhn2024/ToDoAppA9](https://github.com/luisgrohmannhhn2024/ToDoAppA9)

2. Öffne **Android Studio**.

3. Wähle **Project from Version Control** aus.

4. Gib bei **Repository URL** folgende Adresse ein:
https://github.com/luisgrohmannhhn2024/ToDoAppA9.git

5. Öffne den **Device Manager** und erstelle ein virtuelles Gerät:
- **Gerätemodell:** Pixel 8
- **API-Version:** API 35

6. Starte die App über den **grünen Run-Button**.

7. Verwende und teste die App.

## Funktionsbeschreibung

Die ToDo-App ist eine vollständig funktionsfähige Anwendung, die es Nutzern ermöglicht, ihre Aufgaben einfach zu erstellen, zu verwalten und zu organisieren. Die erstellen ToDos werden in einer Datenbank gespeichert und verwaltet. 

### 1. Erstellung eines neuen ToDos
- Über den **`+`-Button** kann der Nutzer ein neues ToDo erstellen.
- Der Nutzer gibt zunächst einen **Namen** für das ToDo ein und wählt eine **Priorität**:
  - **Prioritätsstufen:** Niedrig (Standard), Mittel oder Hoch.
- Anschließend wird ein **Fälligkeitsdatum** festgelegt. Dieses kann:
  - **Manuell** eingegeben oder
  - Über einen **Kalender-Button** ausgewählt werden.
- Zusätzlich kann der Nutzer eine **Beschreibung** hinzufügen, um das ToDo detaillierter zu beschreiben oder wichtige Notizen zu ergänzen.
- **Pflichtfelder:** "Name" und "Fälligkeitsdatum" müssen ausgefüllt werden.
- Mit einem **"Speichern"-Button** wird das ToDo gespeichert.

---

### 2. Verwaltung der offenen ToDos
- Nach der Erstellung eines ToDos kann der Nutzer über den roten **`Aktive ToDos`-Button** auf dem Dashboard zu den offenen Aufgaben wechseln.
- In der Liste der ToDos kann der Nutzer:
  - ToDos **aufklappen**, um Details wie Notizen oder das Fälligkeitsdatum einzusehen.
  - ToDos werden je nach Priorität **farblich unterschiedlich** dargestellt (z. B. Rot für hoch, Gelb für mittel, Grün für niedrig).
  - Ein ToDo durch **längeres Tippen** bearbeiten oder löschen.

---

### 3. Filter- und Sortieroptionen
- Die App bietet eine **Filterfunktion**, um ToDos nach Priorität oder Fälligkeit zu sortieren.
- Filter können bei Bedarf wieder entfernt werden.

---

### 4. Erinnerung an überfällige ToDos
- Wenn ein ToDo das Fälligkeitsdatum überschreitet, wird es mit einem **Ausrufezeichen** markiert.
- Überfällige ToDos können mithilfe eines speziellen Buttons hervorgehoben werden.

---

### 5. Erledigte ToDos
- Sobald ein ToDo abgeschlossen ist, kann es direkt über einen Button am ToDo als **"erledigt"** markiert werden.
- Erledigte ToDos werden automatisch in die Kategorie **`Erledigte ToDos`** verschoben, die über das Dashboard erreichbar ist.
- In der Kategorie "Erledigte ToDos" kann der Nutzer:
  - Erledigte ToDos **löschen**, wenn sie nicht mehr benötigt werden.
  - Einen Filter anwenden, um bestimmte abgeschlossene ToDos einfacher zu finden.
  - Neue ToDos erstellen.

---

### Zusammenfassung
Die App bietet eine intuitive und übersichtliche Benutzeroberfläche, die es ermöglicht, Aufgaben effektiv zu verwalten und stets den Überblick über offene, überfällige und abgeschlossene ToDos zu behalten.
