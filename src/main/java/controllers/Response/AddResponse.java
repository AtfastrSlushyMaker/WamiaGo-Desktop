//AddResponse
package controllers.Response;

import entities.Reclamation;
import entities.Response;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.ReclamationService;
import services.ResponseService;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class AddResponse {
    @FXML
    private Label reclamationTitleLabel;

    @FXML
    private Label reclamationContentLabel;

    @FXML
    private TextArea responseContentArea;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private final ResponseService responseService;
    private Reclamation reclamation;

    public AddResponse() {
        responseService = new ResponseService();
    }

    @FXML
    void initialize() {
        // Set up text area with better styling
        responseContentArea.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: white; -fx-border-color: #ced4da; -fx-border-radius: 5;");
        responseContentArea.setWrapText(true);
        responseContentArea.setPromptText("Enter your response here...");

        // Set up labels with better styling
        reclamationTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        reclamationContentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057; -fx-wrap-text: true;");

        // Set up buttons with better styling
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
        cancelButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");

        // Add hover effects to buttons
        submitButton.setOnMouseEntered(e -> submitButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
        submitButton.setOnMouseExited(e -> submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
        
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #5a6268; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));

        // Set up button actions
        submitButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });

        // Add keyboard shortcuts
        responseContentArea.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.ENTER) {
                handleSubmit(new ActionEvent());
            } else if (e.getCode() == KeyCode.ESCAPE) {
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
            }
        });
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String content = responseContentArea.getText().trim();

        if (content.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a response");
            responseContentArea.requestFocus();
            return;
        }

        try {
            Response response = new Response(
                    reclamation,
                    content,
                    new Timestamp(System.currentTimeMillis())
            );

            if (responseService.create(response)) {
                ReclamationService reclamationService = new ReclamationService();
                reclamation.setStatus(1);
                reclamationService.update(reclamation);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Response added successfully");
                
                // Close the dialog after successful submission
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to add response");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initData(Reclamation reclamation) {
        this.reclamation = reclamation;
        reclamationTitleLabel.setText(reclamation.getTitle());
        reclamationContentLabel.setText(reclamation.getContent());
        responseContentArea.requestFocus(); // Set focus to the response area
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}