//AddReclamation
package controllers.Reclamation;

import entities.Reclamation;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import services.ReclamationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AddReclamation {
    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private Button submitButton;

    @FXML
    private Button btn_workbench1; // Home button

    @FXML
    private Button btn_workbench11;

    @FXML private Button CANCEL_BUTTON;


    private final ReclamationService reclamationService;

    // TODO: This should be set from your authentication system
    private User currentUser;

    public AddReclamation() {
        reclamationService = new ReclamationService();
        // Temporary user for testing - replace with actual logged-in user
        currentUser = SessionManager.getInstance().getUser(); // Set to an existing user ID in your database
    }

    @FXML
    void initialize() {
        submitButton.setOnAction(event -> handleSubmit(event));

        // Add navigation handlers

        btn_workbench11.setOnAction(event ->navigateToRide(event));
        btn_workbench1.setOnAction(event -> navigateToHome(event));

        CANCEL_BUTTON.setOnAction(event -> navigateToList(event));
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields");
            return;
        }

        try {
            Reclamation reclamation = new Reclamation(
                    currentUser,
                    title,
                    content,
                    new Timestamp(System.currentTimeMillis()),
                    0 // Initial status
            );

            if (reclamationService.create(reclamation)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation added successfully");
                // Navigate to ListReclamation after successful creation
                navigateToList(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reclamation");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reclamation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard/dashboard.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
        }
    }

    private void navigateToRide(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/rides/rides.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
        }
    }

    private void navigateToList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Reclamation/ListReclamation.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la navigation : " + e.getMessage());
        }
    }

    private void clearForm() {
        titleField.clear();
        contentArea.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
