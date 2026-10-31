# CampusSafe — Demo Flow for Evaluation

## Setup before the demo
1. Start MySQL Server.
2. Run `database/campussafe.sql` in MySQL Workbench.
3. Check the username/password in `Database.java`.
4. Run the app with `mvn javafx:run` from the folder containing `pom.xml`.

## Demonstration
1. Log in as Admin (`admin` / `Admin@123`, role `Admin`) on a fresh database.
2. Use **Create Student / Responder Account** to register a Responder account.
3. Register a Student account, then log in as Student and submit an emergency report.
4. Log in as Admin, select the report, choose the responder by name/username, and click **Assign Selected**.
5. Log in as that Responder. Only incidents assigned to that responder should appear.
6. Change the incident status to `Responding`, then `Resolved`.
7. Restart the app and verify the report, assignment and status remain saved in MySQL.

## How to explain authentication simply
- The app searches the `users` table for the entered username and selected role.
- It checks the entered password against a salted PBKDF2 hash stored in the database.
- Wrong username, password or role is rejected.
- Passwords are not stored as plain text.

## How to explain responder assignment simply
- Admin chooses a report and a registered responder.
- The responder's username is saved in `incidents.assigned_to` and status becomes `Assigned`.
- The Responder Portal filters incidents so a responder sees only incidents assigned to their username.

## Limitations to disclose honestly
- The first admin is created with a demo password (`Admin@123`); change it before any real use. A password-change screen is not included.
- Student and Responder self-registration is allowed in this academic prototype. Approval of responder accounts is not implemented.
- This is a prototype, not a replacement for real emergency services.
