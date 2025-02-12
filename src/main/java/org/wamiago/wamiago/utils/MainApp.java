package org.wamiago.wamiago.utils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;



public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F4F7FA;");

        // Sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Main Content
        VBox mainContent = createMainContent();
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("Taxi Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2C3E50; -fx-pref-width: 250px;");

        Label title = new Label("Taxi Management");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button[] buttons = {
                createMenuButton("Dashboard"),
                createMenuButton("Requests"),
                createMenuButton("Rides"),
                createMenuButton("Drivers"),
                createMenuButton("Reports")
        };

        sidebar.getChildren().add(title);
        sidebar.getChildren().addAll(buttons);
        return sidebar;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; " +
                        "-fx-padding: 10px; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT;"
        );
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #34495E; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;"));
        return button;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label header = new Label("Dashboard");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatsCard("Active Requests", "25", "#3498DB"),
                createStatsCard("Ongoing Rides", "12", "#2ECC71"),
                createStatsCard("Available Drivers", "18", "#E74C3C")
        );

        TableView<String> recentActivities = new TableView<>();
        recentActivities.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String, String> timeCol = new TableColumn<>("Time");
        TableColumn<String, String> activityCol = new TableColumn<>("Activity");
        TableColumn<String, String> statusCol = new TableColumn<>("Status");

        recentActivities.getColumns().addAll(timeCol, activityCol, statusCol);

        content.getChildren().addAll(header, statsBox, new Label("Recent Activities"), recentActivities);
        return content;
    }

    private VBox createStatsCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #7F8C8D; -fx-font-size: 14px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
