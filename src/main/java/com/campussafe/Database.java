package com.campussafe;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Database {

    private static final String URL =
            "jdbc:mysql://localhost:3306/campussafe?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "CampusSafe@123";
    private static final DateTimeFormatter DB_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTables() throws SQLException {
        String usersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "full_name VARCHAR(100) NOT NULL, " +
                "username VARCHAR(60) NOT NULL UNIQUE, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String incidentsTable =
                "CREATE TABLE IF NOT EXISTS incidents (" +
                "id VARCHAR(20) PRIMARY KEY, " +
                "emergency_type VARCHAR(100) NOT NULL, " +
                "location VARCHAR(200) NOT NULL, " +
                "description TEXT NOT NULL, " +
                "priority VARCHAR(20) NOT NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'Reported', " +
                "assigned_to VARCHAR(60), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(usersTable);
            statement.executeUpdate(incidentsTable);
            try {
                statement.executeUpdate("ALTER TABLE incidents ADD COLUMN assigned_to VARCHAR(60)");
            } catch (SQLException ignored) {
                // Column already exists.
            }
        }
    }

    public static void ensureDefaultAdmin() throws SQLException {
        String check = "SELECT COUNT(*) FROM users WHERE role = 'Admin'";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(check)) {
            result.next();
            if (result.getInt(1) > 0) return;
        }
        registerUser("Campus Admin", "admin", "Admin@123", "Admin");
    }

    public static void registerUser(String fullName, String username,
                                   String password, String role) throws SQLException {
        String sql = "INSERT INTO users (full_name, username, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName.trim());
            statement.setString(2, username.trim().toLowerCase());
            statement.setString(3, hashPassword(password));
            statement.setString(4, role);
            statement.executeUpdate();
        }
    }

    public static User authenticate(String username, String password, String role) throws SQLException {
        String sql = "SELECT id, full_name, username, password_hash, role FROM users WHERE username = ? AND role = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim().toLowerCase());
            statement.setString(2, role);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next() && checkPassword(password, result.getString("password_hash"))) {
                    return new User(result.getInt("id"), result.getString("full_name"),
                            result.getString("username"), result.getString("role"));
                }
            }
        }
        return null;
    }

    public static List<User> getResponders() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, username, role FROM users WHERE role = 'Responder' ORDER BY full_name";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                users.add(new User(result.getInt("id"), result.getString("full_name"),
                        result.getString("username"), result.getString("role")));
            }
        }
        return users;
    }

    private static String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not secure the password.", ex);
        }
    }

    private static boolean checkPassword(String password, String savedValue) {
        try {
            String[] parts = savedValue.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] savedHash = Base64.getDecoder().decode(parts[1]);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, savedHash.length * 8);
            byte[] enteredHash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
            if (savedHash.length != enteredHash.length) return false;
            int difference = 0;
            for (int i = 0; i < savedHash.length; i++) difference |= savedHash[i] ^ enteredHash[i];
            return difference == 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void saveIncident(Main.Incident incident) throws SQLException {
        String sql = "INSERT INTO incidents (id, emergency_type, location, description, priority, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, incident.id.get());
            statement.setString(2, incident.type.get());
            statement.setString(3, incident.location.get());
            statement.setString(4, incident.description.get());
            statement.setString(5, incident.priority.get());
            statement.setString(6, incident.status.get());
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.parse(incident.reportedAt.get(), DB_DATE_TIME)));
            statement.executeUpdate();
        }
    }

    public static List<Main.Incident> getIncidents() throws SQLException {
        List<Main.Incident> incidents = new ArrayList<>();
        String sql = "SELECT id, emergency_type, location, description, priority, status, assigned_to, created_at FROM incidents ORDER BY created_at DESC";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                Timestamp timestamp = result.getTimestamp("created_at");
                String reportedAt = timestamp == null
                        ? LocalDateTime.now().format(DB_DATE_TIME)
                        : timestamp.toLocalDateTime().format(DB_DATE_TIME);
                Main.Incident incident = new Main.Incident(result.getString("id"),
                        result.getString("emergency_type"), result.getString("location"),
                        result.getString("description"), result.getString("priority"),
                        result.getString("status"), reportedAt);
                incident.assignedTo.set(result.getString("assigned_to") == null ? "Unassigned" : result.getString("assigned_to"));
                incidents.add(incident);
            }
        }
        return incidents;
    }

    public static void assignIncident(String incidentId, String username) throws SQLException {
        String sql = "UPDATE incidents SET status = 'Assigned', assigned_to = ? WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, incidentId);
            statement.executeUpdate();
        }
    }

    public static void updateStatus(String incidentId, String newStatus) throws SQLException {
        String sql = "UPDATE incidents SET status = ? WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setString(2, incidentId);
            statement.executeUpdate();
        }
    }

    public static class User {
        public final int id;
        public final String fullName;
        public final String username;
        public final String role;

        public User(int id, String fullName, String username, String role) {
            this.id = id;
            this.fullName = fullName;
            this.username = username;
            this.role = role;
        }

        @Override
        public String toString() {
            return fullName + " (@" + username + ")";
        }
    }
}
