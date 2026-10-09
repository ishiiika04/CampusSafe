# CampusSafe - Working Prototype
This is the early working prototype for the CampusSafe PBL project.

Includes:
- Login screen with Student / Responder / Admin roles
- Student emergency reporting form
- Rule-based priority engine
- Admin dashboard with sample incident records
- Responder portal and status updates
- Basic navigation

Demo flow:
Login -> Student -> Report Emergency -> Priority
Login -> Admin -> View/assign incident
Login -> Responder -> Update status

This version uses sample in-memory data. MySQL/JDBC is intentionally the next phase.

Run:
mvn validate
mvn javafx:run

Setup: Java 26.0.2, Maven 3.9.16, JavaFX 21.0.6
