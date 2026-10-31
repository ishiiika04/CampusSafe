package com.campussafe;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

public class Main extends Application {

    static final DateTimeFormatter DB_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    Stage stage;

    ObservableList<Incident> incidents =
            FXCollections.observableArrayList();

    int incidentNumber = 1;
    boolean databaseReady = false;
    Database.User currentUser;

    String background = "#F4F7FB";
    String white = "#FFFFFF";
    String primary = "#6575B7";
    String primaryDark = "#4F5F9F";
    String lavender = "#E9E7F8";
    String blue = "#E4F0FA";
    String green = "#E4F3EC";
    String red = "#F8E2E5";
    String orange = "#FBEBD9";
    String yellow = "#FFF4D6";
    String text = "#28324A";
    String secondaryText = "#68738A";
    String border = "#DCE2EC";

    public void start(Stage s) {
        stage = s;
        stage.setTitle("CampusSafe");

        try {
            Database.createTables();
            Database.ensureDefaultAdmin();
            loadIncidentsFromDatabase();
            databaseReady = true;
            System.out.println("CampusSafe connected to MySQL.");
        } catch (SQLException ex) {
            databaseReady = false;
            System.out.println("MySQL is not connected: " + ex.getMessage());
            System.out.println("CampusSafe will open in temporary in-memory mode.");
        }

        login();
        stage.show();
    }

    void loadIncidentsFromDatabase() throws SQLException {
        List<Incident> savedIncidents = Database.getIncidents();
        incidents.setAll(savedIncidents);

        int largestNumber = 0;
        for (Incident incident : incidents) {
            try {
                String numberText = incident.id.get().replace("INC-", "");
                largestNumber = Math.max(largestNumber, Integer.parseInt(numberText));
            } catch (NumberFormatException ignored) {
            }
        }
        incidentNumber = largestNumber + 1;
    }

    void refreshIncidentsFromDatabase() {
        if (!databaseReady) {
            return;
        }

        try {
            loadIncidentsFromDatabase();
        } catch (SQLException ex) {
            databaseReady = false;
            showDatabaseWarning("Could not refresh records from MySQL. " + ex.getMessage());
        }
    }

    void showDatabaseWarning(String details) {
        new Alert(
                Alert.AlertType.WARNING,
                details + "\n\nPlease check MySQL and src/main/java/com/campussafe/Database.java settings."
        ).showAndWait();
    }

    Label title(String value) {
        Label label = new Label(value);
        label.setStyle(
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + text + ";"
        );
        return label;
    }

    Label subtitle(String value) {
        Label label = new Label(value);
        label.setStyle(
                "-fx-font-size:14px;" +
                "-fx-text-fill:" + secondaryText + ";"
        );
        return label;
    }

    Button primaryButton(String value) {
        Button button = new Button(value);
        button.setPrefHeight(42);
        button.setStyle(
                "-fx-background-color:" + primary + ";" +
                "-fx-text-fill:white;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:8;" +
                "-fx-cursor:hand;"
        );
        return button;
    }

    Button secondaryButton(String value) {
        Button button = new Button(value);
        button.setPrefHeight(40);
        button.setStyle(
                "-fx-background-color:" + white + ";" +
                "-fx-text-fill:" + primaryDark + ";" +
                "-fx-font-size:13px;" +
                "-fx-border-color:" + border + ";" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-cursor:hand;"
        );
        return button;
    }

    void styleField(Control control) {
        control.setStyle(
                "-fx-background-color:white;" +
                "-fx-border-color:" + border + ";" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;" +
                "-fx-padding:9;"
        );
    }

    HBox requiredFieldLabel(String value) {
        Label label = new Label(value);
        Label star = new Label("*");
        star.setStyle("-fx-text-fill:#D32F2F;-fx-font-weight:bold;");
        HBox row = new HBox(2, label, star);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    VBox card() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setStyle(
                "-fx-background-color:" + white + ";" +
                "-fx-background-radius:12;" +
                "-fx-border-color:" + border + ";" +
                "-fx-border-radius:12;"
        );
        return box;
    }

    void login() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + background + ";");
        VBox center = new VBox(20);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(40));

        Label logo = new Label("CampusSafe");
        logo.setStyle("-fx-font-size:38px;-fx-font-weight:bold;-fx-text-fill:" + primaryDark + ";");
        Label sub = new Label("Smart Campus Emergency & Response Management System");
        sub.setStyle("-fx-font-size:15px;-fx-text-fill:" + secondaryText + ";");

        VBox loginCard = card();
        loginCard.setMaxWidth(440);
        loginCard.setPadding(new Insets(30));
        Label loginTitle = new Label("Welcome Back");
        loginTitle.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + text + ";");
        Label loginSub = subtitle("Sign in with your registered account");

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setPrefHeight(42);
        styleField(username);
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setPrefHeight(42);
        styleField(password);
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Student", "Responder", "Admin"));
        role.setPromptText("Select Role");
        role.setPrefHeight(42);
        role.setMaxWidth(Double.MAX_VALUE);
        styleField(role);
        Label message = new Label();
        message.setWrapText(true);
        message.setStyle("-fx-text-fill:#C85A65;-fx-font-size:13px;");

        Button loginButton = primaryButton("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> {
            if (username.getText().isBlank() || password.getText().isBlank() || role.getValue() == null) {
                message.setText("Please enter username, password and role.");
                return;
            }
            if (!databaseReady) {
                message.setText("MySQL is not connected. Start MySQL and check Database.java password.");
                return;
            }
            try {
                Database.User user = Database.authenticate(username.getText(), password.getText(), role.getValue());
                if (user == null) {
                    message.setText("Incorrect username, password or role.");
                    return;
                }
                currentUser = user;
                refreshIncidentsFromDatabase();
                switch (user.role) {
                    case "Student" -> student();
                    case "Responder" -> responder();
                    case "Admin" -> admin();
                    default -> message.setText("This account role is not supported.");
                }
            } catch (SQLException ex) {
                message.setText("Login failed: " + ex.getMessage());
            }
        });

        Button createAccount = secondaryButton("Create Student / Responder Account");
        createAccount.setMaxWidth(Double.MAX_VALUE);
        createAccount.setOnAction(e -> registerScreen());

        Label starterAdmin = new Label("First admin login: admin  |  Admin@123");
        starterAdmin.setWrapText(true);
        starterAdmin.setStyle("-fx-text-fill:" + secondaryText + ";-fx-font-size:12px;");

        loginCard.getChildren().addAll(loginTitle, loginSub, new Label("Username"), username,
                new Label("Password"), password, new Label("Role"), role, loginButton,
                createAccount, message, starterAdmin);
        center.getChildren().addAll(logo, sub, loginCard);
        root.setCenter(center);
        stage.setScene(new Scene(root, 1100, 760));
    }

    void registerScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + background + ";");
        VBox form = card();
        form.setMaxWidth(470);
        form.setPadding(new Insets(30));
        Label heading = title("Create Account");
        Label note = subtitle("Register a Student or Responder account. Admin accounts are created separately.");
        note.setWrapText(true);
        TextField fullName = new TextField();
        fullName.setPromptText("Full name");
        styleField(fullName);
        TextField username = new TextField();
        username.setPromptText("Choose username");
        styleField(username);
        PasswordField password = new PasswordField();
        password.setPromptText("Create password (minimum 6 characters)");
        styleField(password);
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Student", "Responder"));
        role.setPromptText("Account type");
        role.setMaxWidth(Double.MAX_VALUE);
        styleField(role);
        Label message = new Label();
        message.setWrapText(true);
        Button create = primaryButton("Create Account");
        create.setMaxWidth(Double.MAX_VALUE);
        create.setOnAction(e -> {
            if (fullName.getText().isBlank() || username.getText().isBlank() || password.getText().length() < 6 || role.getValue() == null) {
                message.setText("Fill every field. Password must have at least 6 characters.");
                return;
            }
            if (!databaseReady) {
                message.setText("MySQL is not connected. Check the connection first.");
                return;
            }
            try {
                Database.registerUser(fullName.getText(), username.getText(), password.getText(), role.getValue());
                new Alert(Alert.AlertType.INFORMATION, "Account created! Return to Login and sign in.").showAndWait();
                login();
            } catch (SQLException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("Duplicate")) {
                    message.setText("That username is already taken. Choose another one.");
                } else {
                    message.setText("Could not create account: " + ex.getMessage());
                }
            }
        });
        Button back = secondaryButton("Back to Login");
        back.setMaxWidth(Double.MAX_VALUE);
        back.setOnAction(e -> { currentUser = null; login(); });
        form.getChildren().addAll(heading, note, new Label("Full Name"), fullName,
                new Label("Username"), username, new Label("Password"), password,
                new Label("Role"), role, create, back, message);
        VBox wrapper = new VBox(form);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(30));
        root.setCenter(wrapper);
        stage.setScene(new Scene(root, 1000, 720));
    }

    String priority(String type) {

        if (type == null) {
            return "—";
        }

        return switch (type) {

            case "Fire Emergency",
                 "Serious Medical Emergency",
                 "Security Threat" -> "CRITICAL";

            case "Accident" -> "HIGH";

            case "Infrastructure Hazard" -> "MEDIUM";

            default -> "LOW";
        };
    }

    boolean isDuplicateIncident(String type, String location, String description) {
        LocalDateTime now = LocalDateTime.now();
        String normalizedType = normalizeText(type);
        String normalizedLocation = normalizeText(location);
        String normalizedDescription = normalizeText(description);

        for (Incident existing : incidents) {
            if (!normalizeText(existing.type.get()).equals(normalizedType)
                    || !normalizeText(existing.location.get()).equals(normalizedLocation)
                    || !normalizeText(existing.description.get()).equals(normalizedDescription)) {
                continue;
            }
            try {
                LocalDateTime reportedAt = LocalDateTime.parse(existing.reportedAt.get(), DB_DATE_TIME);
                long minutes = Math.abs(Duration.between(reportedAt, now).toMinutes());
                if (minutes <= 10) {
                    return true;
                }
            } catch (Exception ignored) {
                // If a legacy timestamp cannot be parsed, do not block the report.
            }
        }
        return false;
    }

    String normalizeText(String value) {
        if (value == null) return "";
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    void student() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + background + ";");

        VBox header = new VBox(5);
        header.setPadding(new Insets(25));

        header.getChildren().addAll(
                title("Student Portal"),
                subtitle(
                        "Report a campus emergency and provide its location and details."
                )
        );

        root.setTop(header);

        VBox form = card();
        form.setMaxWidth(650);
        form.setPadding(new Insets(30));

        Label formTitle = new Label("Report Emergency");
        formTitle.setStyle(
                "-fx-font-size:21px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + text + ";"
        );

        ComboBox<String> type = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Fire Emergency",
                        "Serious Medical Emergency",
                        "Security Threat",
                        "Accident",
                        "Infrastructure Hazard",
                        "Minor / General Issue"
                )
        );

        type.setPromptText("Select emergency type");
        type.setPrefHeight(42);
        type.setMaxWidth(Double.MAX_VALUE);
        styleField(type);

        // Campus location dropdown. Replace/add options to match your actual campus map.
        ComboBox<String> location = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Main Gate",
                        "Reception / Security Office",
                        "Administrative Block",
                        "Academic Block A",
                        "Academic Block B",
                        "Computer Lab",
                        "Science Lab",
                        "Library",
                        "Auditorium",
                        "Seminar Hall",
                        "Canteen / Cafeteria",
                        "Boys Hostel",
                        "Girls Hostel",
                        "Hostel Mess",
                        "Medical Room",
                        "Faculty Rooms",
                        "Parking Area",
                        "Sports Ground",
                        "Indoor Sports Complex",
                        "Garden / Open Area",
                        "Staircase / Corridor",
                        "Lift Area",
                        "Washroom",
                        "Electrical / Maintenance Room",
                        "Other Campus Location"
                )
        );
        location.setPromptText("Select campus location");
        location.setPrefHeight(42);
        location.setMaxWidth(Double.MAX_VALUE);
        styleField(location);

        TextField otherLocation = new TextField();
        otherLocation.setPromptText("Enter exact campus location");
        otherLocation.setPrefHeight(42);
        styleField(otherLocation);
        otherLocation.setVisible(false);
        otherLocation.setManaged(false);

        location.setOnAction(e -> {
            boolean isOther = "Other Campus Location".equals(location.getValue());
            otherLocation.setVisible(isOther);
            otherLocation.setManaged(isOther);
            if (!isOther) {
                otherLocation.clear();
            }
        });

        TextArea description = new TextArea();
        description.setPromptText(
                "Describe what happened..."
        );
        description.setPrefRowCount(5);
        description.setWrapText(true);
        styleField(description);

        Label priorityLabel = new Label(
                "Priority: Not selected"
        );

        priorityLabel.setPadding(
                new Insets(10)
        );

        priorityLabel.setStyle(
                "-fx-background-color:" + lavender + ";" +
                "-fx-background-radius:8;" +
                "-fx-text-fill:" + primaryDark + ";" +
                "-fx-font-weight:bold;"
        );

        type.setOnAction(e -> {

            String p = priority(type.getValue());

            priorityLabel.setText(
                    "Priority: " + p
            );

            if (p.equals("CRITICAL")) {
                priorityLabel.setStyle(
                        "-fx-background-color:" + red + ";" +
                        "-fx-background-radius:8;" +
                        "-fx-text-fill:#A74351;" +
                        "-fx-font-weight:bold;"
                );
            } else if (p.equals("HIGH")) {
                priorityLabel.setStyle(
                        "-fx-background-color:" + orange + ";" +
                        "-fx-background-radius:8;" +
                        "-fx-text-fill:#A76532;" +
                        "-fx-font-weight:bold;"
                );
            } else if (p.equals("MEDIUM")) {
                priorityLabel.setStyle(
                        "-fx-background-color:" + yellow + ";" +
                        "-fx-background-radius:8;" +
                        "-fx-text-fill:#92711F;" +
                        "-fx-font-weight:bold;"
                );
            } else {
                priorityLabel.setStyle(
                        "-fx-background-color:" + green + ";" +
                        "-fx-background-radius:8;" +
                        "-fx-text-fill:#39785A;" +
                        "-fx-font-weight:bold;"
                );
            }
        });

        Button submit = primaryButton(
                "Submit Emergency"
        );
        submit.setMaxWidth(Double.MAX_VALUE);

        submit.setOnAction(e -> {

            String selectedLocation = location.getValue();
            if (selectedLocation != null && selectedLocation.equals("Other Campus Location")) {
                selectedLocation = otherLocation.getText().trim();
            }

            if (type.getValue() == null
                    || selectedLocation == null
                    || selectedLocation.isBlank()
                    || description.getText().isBlank()) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please fill all emergency details."
                ).showAndWait();

                return;
            }

            String selectedType = type.getValue();
            String selectedDescription = description.getText().trim();
            String selectedPriority = priority(selectedType);

            if (isDuplicateIncident(selectedType, selectedLocation, selectedDescription)) {
                new Alert(
                        Alert.AlertType.WARNING,
                        "Possible duplicate detected! An identical incident with the same type, location and description was reported within the last 10 minutes. Please check the existing incident before submitting again."
                ).showAndWait();
                return;
            }

            String id = String.format(
                    "INC-%03d",
                    incidentNumber++
            );

            Incident incident = new Incident(
                    id,
                    selectedType,
                    selectedLocation,
                    selectedDescription,
                    selectedPriority,
                    "Reported",
                    LocalDateTime.now().format(DB_DATE_TIME)
            );

            if (databaseReady) {
                try {
                    Database.saveIncident(incident);
                } catch (SQLException ex) {
                    databaseReady = false;
                    showDatabaseWarning("The report could not be saved to MySQL. It will remain only until the app closes. " + ex.getMessage());
                }
            } else {
                System.out.println("MySQL offline: this report is stored only while the app is open.");
            }

            incidents.add(incident);

            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION
            );

            alert.setTitle("Emergency Submitted");
            alert.setHeaderText(
                    "Emergency reported successfully"
            );

            alert.setContentText(
                    "Incident ID: " + id + "\n\n" +
                    "Type: " + selectedType + "\n" +
                    "Location: " + selectedLocation + "\n" +
                    "Description: " + selectedDescription + "\n" +
                    "Priority: " + selectedPriority + "\n" +
                    "Status: Reported"
            );

            alert.showAndWait();

            type.setValue(null);
            location.setValue(null);
            otherLocation.clear();
            otherLocation.setVisible(false);
            otherLocation.setManaged(false);
            description.clear();
            priorityLabel.setText(
                    "Priority: Not selected"
            );
        });

        Button back = secondaryButton("Logout");

        back.setOnAction(e -> { currentUser = null; login(); });

        form.getChildren().addAll(
                formTitle,
                requiredFieldLabel("Emergency Type"),
                type,
                requiredFieldLabel("Location"),
                location,
                otherLocation,
                requiredFieldLabel("Description"),
                description,
                priorityLabel,
                submit,
                back
        );

        HBox center = new HBox(form);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(10, 30, 30, 30));

        root.setCenter(center);

        stage.setScene(
                new Scene(root, 1100, 700)
        );
    }

    void admin() {

        BorderPane root = new BorderPane();
        root.setStyle(
                "-fx-background-color:" + background + ";"
        );

        VBox header = new VBox(5);
        header.setPadding(
                new Insets(25, 30, 15, 30)
        );

        HBox top = new HBox();

        VBox heading = new VBox(
                4,
                title("Admin Dashboard"),
                subtitle(
                        "Campus emergency monitoring and management"
                )
        );

        Region spacer = new Region();
        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button refresh = secondaryButton("Refresh");
        refresh.setOnAction(e -> {
            refreshIncidentsFromDatabase();
            admin();
        });

        Button logout = secondaryButton("Logout");
        logout.setOnAction(e -> { currentUser = null; login(); });

        HBox buttons = new HBox(
                10,
                refresh,
                logout
        );

        top.setAlignment(Pos.CENTER_LEFT);
        top.getChildren().addAll(
                heading,
                spacer,
                buttons
        );

        header.getChildren().add(top);

        root.setTop(header);

        VBox content = new VBox(20);
        content.setPadding(
                new Insets(10, 30, 30, 30)
        );

        HBox stats = new HBox(15);

        stats.getChildren().addAll(
                statCard(
                        "Total Emergencies",
                        String.valueOf(incidents.size()),
                        blue
                ),
                statCard(
                        "Critical",
                        String.valueOf(
                                countPriority("CRITICAL")
                        ),
                        red
                ),
                statCard(
                        "Active",
                        String.valueOf(
                                countActive()
                        ),
                        lavender
                ),
                statCard(
                        "Resolved",
                        String.valueOf(
                                countStatus("Resolved")
                        ),
                        green
                )
        );

        HBox charts = new HBox(
                20,
                priorityChart(),
                typeChart()
        );

        Label reports = new Label(
                "Recent Emergency Reports"
        );

        reports.setStyle(
                "-fx-font-size:21px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + text + ";"
        );

        TableView<Incident> table = table();

        ComboBox<Database.User> responderChoice = new ComboBox<>();
        responderChoice.setPromptText("Choose a registered responder");
        responderChoice.setPrefWidth(300);
        styleField(responderChoice);
        try {
            responderChoice.setItems(FXCollections.observableArrayList(Database.getResponders()));
        } catch (SQLException ex) {
            showDatabaseWarning("Could not load responders: " + ex.getMessage());
        }
        Button assign = primaryButton("Assign Selected");
        assign.setPrefWidth(180);
        assign.setOnAction(e -> {
            Incident selected = table.getSelectionModel().getSelectedItem();
            Database.User responderUser = responderChoice.getValue();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select an emergency first.").showAndWait();
                return;
            }
            if (responderUser == null) {
                new Alert(Alert.AlertType.WARNING, "Please choose a registered responder. Create a Responder account first if the list is empty.").showAndWait();
                return;
            }
            if (!databaseReady) {
                showDatabaseWarning("A database connection is required to assign an incident to a specific responder.");
                return;
            }
            try {
                Database.assignIncident(selected.id.get(), responderUser.username);
                selected.status.set("Assigned");
                selected.assignedTo.set(responderUser.username);
                table.refresh();
                new Alert(Alert.AlertType.INFORMATION, selected.id.get() + " assigned to " + responderUser.fullName + " (@" + responderUser.username + ").").showAndWait();
            } catch (SQLException ex) {
                showDatabaseWarning("Could not save responder assignment: " + ex.getMessage());
            }
        });

        VBox tableCard = card();

        tableCard.getChildren().addAll(
                reports,
                table,
                new Label("Assign to responder"),
                responderChoice,
                assign
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        content.getChildren().addAll(
                stats,
                charts,
                tableCard
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background:" + background + ";"
        );

        root.setCenter(scroll);

        stage.setScene(
                new Scene(root, 1250, 850)
        );
    }

    VBox statCard(
            String heading,
            String value,
            String color
    ) {

        Label h = new Label(heading);

        h.setStyle(
                "-fx-font-size:14px;" +
                "-fx-text-fill:" + secondaryText + ";"
        );

        Label v = new Label(value);

        v.setStyle(
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + text + ";"
        );

        VBox box = new VBox(
                8,
                h,
                v
        );

        box.setPadding(
                new Insets(20)
        );

        box.setPrefWidth(250);
        box.setPrefHeight(105);

        box.setStyle(
                "-fx-background-color:" + color + ";" +
                "-fx-background-radius:12;"
        );

        return box;
    }

    BarChart<String, Number> priorityChart() {

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();

        xAxis.setLabel("Priority");
        yAxis.setLabel("Number");

        BarChart<String, Number> chart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        chart.setTitle(
                "Priority Overview"
        );

        chart.setLegendVisible(false);
        chart.setPrefWidth(540);
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> data =
                new XYChart.Series<>();

        data.getData().add(
                new XYChart.Data<>(
                        "Critical",
                        countPriority("CRITICAL")
                )
        );

        data.getData().add(
                new XYChart.Data<>(
                        "High",
                        countPriority("HIGH")
                )
        );

        data.getData().add(
                new XYChart.Data<>(
                        "Medium",
                        countPriority("MEDIUM")
                )
        );

        data.getData().add(
                new XYChart.Data<>(
                        "Low",
                        countPriority("LOW")
                )
        );

        chart.getData().add(data);

        return chart;
    }

    BarChart<String, Number> typeChart() {

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();

        xAxis.setLabel("Emergency Type");
        yAxis.setLabel("Number");

        BarChart<String, Number> chart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        chart.setTitle(
                "Emergency Type Overview"
        );

        chart.setLegendVisible(false);
        chart.setPrefWidth(620);
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> data =
                new XYChart.Series<>();

        data.getData().add(
                new XYChart.Data<>(
                        "Fire",
                        countType("Fire Emergency")
                )
        );

        data.getData().add(
                new XYChart.Data<>(
                        "Medical",
                        countType(
                                "Serious Medical Emergency"
                        )
                )
        );

        data.getData().add(
                new XYChart.Data<>(
                        "Security",
                        countType("Security Threat")
                )
        );

        data.getData().add(
                new XYChart.Data<>(
                        "Accident",
                        countType("Accident")
                )
        );

        data.getData().add(
                new XYChart.Data<>(
                        "Infrastructure",
                        countType(
                                "Infrastructure Hazard"
                        )
                )
        );

        chart.getData().add(data);

        return chart;
    }

    void responder() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color:" + background + ";"
        );

        VBox header = new VBox(5);

        header.setPadding(
                new Insets(25, 30, 15, 30)
        );

        HBox top = new HBox();

        VBox heading = new VBox(
                4,
                title("Responder Portal"),
                subtitle(
                        "View assigned emergencies and update their status."
                )
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button logout =
                secondaryButton("Logout");

        logout.setOnAction(
                e -> { currentUser = null; login(); }
        );

        top.getChildren().addAll(
                heading,
                spacer,
                logout
        );

        header.getChildren().add(top);

        root.setTop(header);

        VBox content = card();

        content.setPadding(
                new Insets(25)
        );

        Label section = new Label(
                "Emergency Reports"
        );

        section.setStyle(
                "-fx-font-size:21px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + text + ";"
        );

        TableView<Incident> table =
                table();
        if (currentUser != null && currentUser.role.equals("Responder")) {
            ObservableList<Incident> assignedIncidents = FXCollections.observableArrayList();
            for (Incident incident : incidents) {
                if (currentUser.username.equalsIgnoreCase(incident.assignedTo.get())) {
                    assignedIncidents.add(incident);
                }
            }
            table.setItems(assignedIncidents);
        }

        ComboBox<String> status =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                "Assigned",
                                "Responding",
                                "Resolved"
                        )
                );

        status.setPromptText(
                "Select new status"
        );

        status.setPrefHeight(42);

        styleField(status);

        Button update =
                primaryButton(
                        "Update Status"
                );

        update.setPrefWidth(200);

        update.setOnAction(e -> {

            Incident selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select an emergency."
                ).showAndWait();

                return;
            }

            if (status.getValue() == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a status."
                ).showAndWait();

                return;
            }

            selected.status.set(
                    status.getValue()
            );

            if (databaseReady) {
                try {
                    Database.updateStatus(selected.id.get(), status.getValue());
                } catch (SQLException ex) {
                    databaseReady = false;
                    showDatabaseWarning("Status changed on screen, but could not be saved to MySQL. " + ex.getMessage());
                }
            }

            table.refresh();

            new Alert(
                    Alert.AlertType.INFORMATION,
                    "Status updated successfully."
            ).showAndWait();

            status.setValue(null);
        });

        content.getChildren().addAll(
                section,
                table,
                status,
                update
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        BorderPane.setMargin(
                content,
                new Insets(10, 30, 30, 30)
        );

        root.setCenter(content);

        stage.setScene(
                new Scene(root, 1150, 750)
        );
    }

    String formatReportedAt(String value) {
        if (value == null || value.isBlank()) {
            return "N/A";
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(value, DB_DATE_TIME);
            return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        } catch (Exception ex) {
            // Keep the original value visible if an older record uses another format.
            return value;
        }
    }

    TableView<Incident> table() {

        TableView<Incident> table =
                new TableView<>();

        TableColumn<Incident, String> id =
                column(
                        "ID",
                        x -> x.id
                );

        TableColumn<Incident, String> type =
                column(
                        "Emergency",
                        x -> x.type
                );

        TableColumn<Incident, String> location =
                column(
                        "Location",
                        x -> x.location
                );

        TableColumn<Incident, String> description =
                column(
                        "Description",
                        x -> x.description
                );

        TableColumn<Incident, String> priority =
                column(
                        "Priority",
                        x -> x.priority
                );

        TableColumn<Incident, String> status =
                column(
                        "Status",
                        x -> x.status
                );
        TableColumn<Incident, String> assigned =
                column(
                        "Assigned To",
                        x -> x.assignedTo
                );
        TableColumn<Incident, String> reportedAt =
                column(
                        "Reported Date & Time",
                        x -> new SimpleStringProperty(
                                formatReportedAt(x.reportedAt.get())
                        )
                );

        id.setPrefWidth(90);
        type.setPrefWidth(220);
        location.setPrefWidth(180);
        description.setPrefWidth(250);
        priority.setPrefWidth(120);
        status.setPrefWidth(120);
        assigned.setPrefWidth(130);
        reportedAt.setPrefWidth(175);

        table.getColumns().addAll(
                id,
                type,
                location,
                description,
                priority,
                status,
                assigned,
                reportedAt
        );

        table.setItems(incidents);

        table.setPrefHeight(280);

        return table;
    }

    TableColumn<Incident, String> column(
            String heading,
            java.util.function.Function<
                    Incident,
                    SimpleStringProperty
            > function
    ) {

        TableColumn<Incident, String> column =
                new TableColumn<>(heading);

        column.setCellValueFactory(
                data ->
                        function.apply(
                                data.getValue()
                        )
        );

        return column;
    }

    int countPriority(String value) {

        int count = 0;

        for (Incident incident : incidents) {

            if (incident.priority.get()
                    .equals(value)) {

                count++;
            }
        }

        return count;
    }

    int countStatus(String value) {

        int count = 0;

        for (Incident incident : incidents) {

            if (incident.status.get()
                    .equals(value)) {

                count++;
            }
        }

        return count;
    }

    int countActive() {

        int count = 0;

        for (Incident incident : incidents) {

            if (!incident.status.get()
                    .equals("Resolved")) {

                count++;
            }
        }

        return count;
    }

    int countType(String value) {

        int count = 0;

        for (Incident incident : incidents) {

            if (incident.type.get()
                    .equals(value)) {

                count++;
            }
        }

        return count;
    }

    static class Incident {

        SimpleStringProperty id;
        SimpleStringProperty type;
        SimpleStringProperty location;
        SimpleStringProperty description;
        SimpleStringProperty priority;
        SimpleStringProperty status;
        SimpleStringProperty assignedTo;
        SimpleStringProperty reportedAt;

        Incident(
                String id,
                String type,
                String location,
                String description,
                String priority,
                String status
        ) {
            this(id, type, location, description, priority, status,
                    LocalDateTime.now().format(DB_DATE_TIME));
        }

        Incident(
                String id,
                String type,
                String location,
                String description,
                String priority,
                String status,
                String reportedAt
        ) {
            this.id = new SimpleStringProperty(id);
            this.type = new SimpleStringProperty(type);
            this.location = new SimpleStringProperty(location);
            this.description = new SimpleStringProperty(description);
            this.priority = new SimpleStringProperty(priority);
            this.status = new SimpleStringProperty(status);
            this.assignedTo = new SimpleStringProperty("Unassigned");
            this.reportedAt = new SimpleStringProperty(
                    reportedAt == null || reportedAt.isBlank()
                            ? LocalDateTime.now().format(DB_DATE_TIME)
                            : reportedAt
            );
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}