# ToDo-App

Eine einfache ToDo-App, die es Benutzern ermöglicht, Aufgaben zu erstellen, zu bearbeiten, zu löschen und nach Priorität oder Enddatum zu sortieren.

## Installationsanleitung

1. **Clonen des Repositories:**
   ```bash
   git clone https://github.com/dein-benutzername/todo-app.git
   cd todo-app
   
2. **Öffnen des Projekts in Android Studio**

Starte Android Studio.
Wähle "Open an existing Android Studio project".
Navigiere zum Verzeichnis des geklonten Repositories und öffne es.
Abhängigkeiten synchronisieren:

Stelle sicher, dass alle Abhängigkeiten in den build.gradle-Dateien synchronisiert werden.
App auf einem Gerät oder Emulator ausführen:

Schließe ein Android-Gerät an oder starte einen Emulator.
Klicke auf den "Run"-Button in Android Studio.

## Funktionsbeschreibung
**Die ToDo-App bietet verschiedene Funktionen**

- Aufgaben erstellen: Benutzer können neue Aufgaben mit Name, Priorität, Enddatum und Beschreibung hinzufügen.
- Aufgaben anzeigen: Zeigt eine Liste aller offenen und abgeschlossenen Aufgaben an.
- Aufgaben bearbeiten: Benutzer können vorhandene Aufgaben bearbeiten, um deren Details zu aktualisieren.
- Aufgaben löschen: Aufgaben können entfernt werden.
- Aufgaben als abgeschlossen/unerledigt markieren: Aufgaben können als abgeschlossen oder unerledigt markiert werden.
- Sortierfunktion: Aufgaben können nach Priorität oder Enddatum sortiert werden.
- Filterfunktion: Aufgaben können nach ihrem Status (alle, offen, abgeschlossen) gefiltert werden.

## Verwendete Technologien
Programmiersprache: Kotlin

Framework: Jetpack Compose für die Benutzeroberfläche

Datenbank: SQLite

Navigation: Jetpack Navigation Compose

## Bekannte Probleme
**Eingabedatenvalidierung:** Die Validierung der Benutzereingaben könnte in bestimmten Szenarien verbessert werden

**Fehlende Tests:** Es gibt derzeit keine automatisierten Tests 
