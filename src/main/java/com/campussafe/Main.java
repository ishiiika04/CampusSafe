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

public class Main extends Application {

    Stage stage;

    ObservableList<Incident> incidents =
            FXCollections.observableArrayList();

    int incidentNumber = 1;

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

    @Override
    public void start(Stage s) {
        stage = s;
        stage.setTitle("CampusSafe");
        login();
        stage.show();
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
        logo.setStyle(
                "-fx-font-size:38px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + primaryDark + ";"
        );

        Label sub = new Label(
                "Smart Campus Emergency & Response Management System"
        );
        sub.setStyle(
                "-fx-font-size:15px;" +
                "-fx-text-fill:" + secondaryText + ";"
        );

        VBox loginCard = card();
        loginCard.setMaxWidth(440);
        loginCard.setPadding(new Insets(30));

        Label loginTitle = new Label("Welcome Back");
        loginTitle.setStyle(
                "-fx-font-size:22px;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:" + text + ";"
        );

        Label loginSub = subtitle(
                "Sign in to access the CampusSafe system"
        );

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setPrefHeight(42);
        styleField(username);

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setPrefHeight(42);
        styleField(password);

        ComboBox<String> role = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Student",
                        "Responder",
                        "Admin"
                )
        );

        role.setPromptText("Select Role");
        role.setPrefHeight(42);
        role.setMaxWidth(Double.MAX_VALUE);
        styleField(role);

        Label message = new Label();
        message.setStyle(
                "-fx-text-fill:#C85A65;" +
                "-fx-font-size:13px;"
        );

        Button loginButton = primaryButton("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(e -> {

            if (username.getText().isBlank()
                    || password.getText().isBlank()
                    || role.getValue() == null) {

                message.setText(
                        "Please enter username, password and role."
                );
                return;
            }

            switch (role.getValue()) {

                case "Student" -> student();

                case "Responder" -> responder();

                case "Admin" -> admin();
            }
        });

        loginCard.getChildren().addAll(
                loginTitle,
                loginSub,
                new Label("Username"),
                username,
                new Label("Password"),
                password,
                new Label("Role"),
                role,
                loginButton,
                message
        );

        center.getChildren().addAll(
                logo,
                sub,
                loginCard
        );

        root.setCenter(center);

        Scene scene = new Scene(root, 1100, 700);

        stage.setScene(scene);
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

        TextField location = new TextField();
        location.setPromptText("Enter campus location");
        location.setPrefHeight(42);
        styleField(location);

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

            if (type.getValue() == null
                    || location.getText().isBlank()
                    || description.getText().isBlank()) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please fill all emergency details."
                ).showAndWait();

                return;
            }

            String selectedType = type.getValue();
            String selectedLocation = location.getText();
            String selectedDescription = description.getText();
            String selectedPriority = priority(selectedType);

            String id = String.format(
                    "INC-%03d",
                    incidentNumber++
            );

            Incident incident = new Incident(
                    id,
                    selectedType,
                    selectedLocation,
                    selectedPriority,
                    "Reported"
            );

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
                    "Priority: " + selectedPriority + "\n" +
                    "Status: Reported"
            );

            alert.showAndWait();

            type.setValue(null);
            location.clear();
            description.clear();
            priorityLabel.setText(
                    "Priority: Not selected"
            );
        });

        Button back = secondaryButton("Logout");

        back.setOnAction(e -> login());

        form.getChildren().addAll(
                formTitle,
                new Label("Emergency Type"),
                type,
                new Label("Location"),
                location,
                new Label("Description"),
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
        refresh.setOnAction(e -> admin());

        Button logout = secondaryButton("Logout");
        logout.setOnAction(e -> login());

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

        Button assign = primaryButton(
                "Assign Selected"
        );

        assign.setPrefWidth(180);

        assign.setOnAction(e -> {

            Incident selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select an emergency first."
                ).showAndWait();

                return;
            }

            selected.status.set("Assigned");

            table.refresh();

            new Alert(
                    Alert.AlertType.INFORMATION,
                    selected.id.get()
                    + " has been assigned to a responder."
            ).showAndWait();
        });

        VBox tableCard = card();

        tableCard.getChildren().addAll(
                reports,
                table,
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
                e -> login()
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

        id.setPrefWidth(100);
        type.setPrefWidth(220);
        location.setPrefWidth(180);
        priority.setPrefWidth(120);
        status.setPrefWidth(150);

        table.getColumns().addAll(
                id,
                type,
                location,
                priority,
                status
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
        SimpleStringProperty priority;
        SimpleStringProperty status;

        Incident(
                String id,
                String type,
                String location,
                String priority,
                String status
        ) {

            this.id =
                    new SimpleStringProperty(id);

            this.type =
                    new SimpleStringProperty(type);

            this.location =
                    new SimpleStringProperty(location);

            this.priority =
                    new SimpleStringProperty(priority);

            this.status =
                    new SimpleStringProperty(status);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}