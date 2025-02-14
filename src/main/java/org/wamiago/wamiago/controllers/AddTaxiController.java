package org.wamiago.wamiago.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddTaxiController {

    @FXML
    private TextField locationAField;

    @FXML
    private TextField locationDField;

    @FXML
    private TextField driverIdField;

    @FXML
    private TextField clientIdField;

    @FXML
    private Button addTaxiButton;

    @FXML
    void initialize() {
        // Initialization if needed
    }

    @FXML
    private void onAddTaxiButtonClick() {
        // Retrieve the values from the fields
        String locationA = locationAField.getText();
        String locationD = locationDField.getText();
        String driverId = driverIdField.getText();
        String clientId = clientIdField.getText();

        // Validate inputs
        if (locationA.isEmpty() || locationD.isEmpty() || driverId.isEmpty() || clientId.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
        } else {
            // Here, you can add logic to save the data (e.g., to a database)
            showAlert("Success", "Taxi added successfully!");
            // Optionally clear the fields after successful addition
            locationAField.clear();
            locationDField.clear();
            driverIdField.clear();
            clientIdField.clear();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
