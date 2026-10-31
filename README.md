# CampusSafe — Step 3 + Login Authentication + Responder Assignment

Beginner-friendly JavaFX + MySQL/JDBC academic prototype.

## Included
- Real login checked against the MySQL `users` table.
- Passwords are stored as salted PBKDF2 hashes, not plain text.
- Student and Responder accounts can be created from the app.
- A starter Admin account is created automatically if the database has no Admin.
- Admin can select a registered Responder and assign a specific incident to them.
- Responders see only incidents assigned to their username.
- Incident status updates and assignments are saved to MySQL.
- Incident descriptions and assigned responder appear in the table.

## Before running
You need JDK 21 or newer, Maven, MySQL Server, and MySQL Workbench (or another way to run the SQL file).

### 1. Create the database
Open `database/campussafe.sql` in MySQL Workbench and execute the whole script. It creates `campussafe`, `users`, and `incidents` tables.

### 2. Set the MySQL password
Open `src/main/java/com/campussafe/Database.java` and update these lines to match your local MySQL login:

```java
private static final String USER = "root";
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
```

Do not upload your real database password to GitHub.

### 3. Run in VS Code
Open the folder that contains `pom.xml`, open Terminal → New Terminal, and run:

```bash
mvn javafx:run
```

The first run may take a few minutes while Maven downloads dependencies.

## First login and setup
1. Start MySQL Server and run the SQL script.
2. Start CampusSafe.
3. Sign in using the initial administrator account:
   - Username: `admin`
   - Password: `Admin@123`
   - Role: `Admin`
4. Create a Responder account from **Create Student / Responder Account** (use a different username, and choose role `Responder`).
5. Log out and sign in as Admin again. Open the dashboard, select an incident, choose the responder from **Assign to responder**, then click **Assign Selected**.
6. Log out and sign in with that responder's account. The responder should see only incidents assigned to that username and can update their status.
7. Create Student accounts the same way to submit reports.

**Important:** Change the starter Admin password before using this outside a classroom demo. A password-change screen is not included yet. Account registration is a classroom prototype; for real deployment, responder accounts should be approved by an administrator.

## Quick database check
In MySQL Workbench, run:

```sql
USE campussafe;
SELECT full_name, username, role FROM users;
SELECT id, emergency_type, status, assigned_to FROM incidents;
```

## Current scope / limitations
- Password authentication and role matching are implemented against MySQL.
- Passwords are hashed with Java's PBKDF2WithHmacSHA256 before saving.
- Student/Responder self-registration is available; admin approval for responder accounts is not implemented.
- Admin can assign to a specific registered responder; responders see only their assigned incidents.
- There is no password reset/change-password page yet.
- The project is an academic prototype and does not contact real emergency services.
- MySQL must be connected for login, account creation, and permanent data storage. If MySQL is unavailable, login is blocked rather than allowing a fake login.

## Folder map
- `src/main/java/com/campussafe/Main.java` — JavaFX screens and app flow.
- `src/main/java/com/campussafe/Database.java` — JDBC, password hashing, login, registration and assignments.
- `database/campussafe.sql` — database/table setup.
- `docs/TEACHER_DEMO.md` — demo flow and explanation.
- `pom.xml` — JavaFX and MySQL dependencies.
