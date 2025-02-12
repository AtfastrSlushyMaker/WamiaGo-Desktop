package org.wamiago.wamiago.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.wamiago.wamiago.entities.Request;
import javafx.beans.property.SimpleStringProperty;

public class MainViewController {
    @FXML
    private Label pageTitle;

    @FXML
    private Label requestsCount;

    @FXML
    private Label ridesCount;

    @FXML
    private Label driversCount;

    @FXML
    private TableView<Request> recentActivitiesTable;

    @FXML
    private TableColumn<Request, String> timeColumn;

    @FXML
    private TableColumn<Request, String> activityColumn;

    @FXML
    private TableColumn<Request, String> statusColumn;

    private ObservableList<Request> requestData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTable();
        loadData();
    }

    private void setupTable() {
        // Setup table columns using proper property mapping
        timeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRequestDate().toString()));

        activityColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty("Request #" + cellData.getValue().getIdRequest()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty());

        // Bind the table to the data
        recentActivitiesTable.setItems(requestData);
    }

    private void loadData() {
        try {
            // Here you would typically load your requests from your database
            // For example:
            // requestData.addAll(requestService.getAllRequests());

            // Update counters
            updateCounters();
        } catch (Exception e) {
            // Handle any errors that occur during data loading
            e.printStackTrace();
            showError("Error loading data");
        }
    }

    private void updateCounters() {
        try {
            // Here you would typically get these counts from your service layer
            // For example:
            // requestsCount.setText(String.valueOf(requestService.getActiveRequestsCount()));
            // ridesCount.setText(String.valueOf(rideService.getActiveRidesCount()));
            // driversCount.setText(String.valueOf(driverService.getAvailableDriversCount()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error updating counters");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add methods for handling button clicks if needed
    @FXML
    private void handleDashboardAction() {
        pageTitle.setText("Dashboard");
        loadData();
    }

    @FXML
    private void handleRequestsAction() {
        pageTitle.setText("Requests");
        loadData();
    }
}