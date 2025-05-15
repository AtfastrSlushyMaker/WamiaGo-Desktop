//UpdateResponse
package controllers.Response;

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
import services.ResponseService;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UpdateResponse {
    @FXML
    private Label reclamationTitleLabel;

    @FXML
    private Label reclamationContentLabel;

    @FXML
    private TextArea responseContentArea;

    @FXML
    private Button updateButton;

    @FXML
    private Button cancelButton;

    private final ResponseService responseService;
    private Response response;

    public UpdateResponse() {
        responseService = new ResponseService();
    }

    @FXML
    void initialize() {
        updateButton.setOnAction(this::handleUpdate);
        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
    }

    public void initData(Response response) {
        this.response = response;
        reclamationTitleLabel.setText(response.getReclamation().getTitle());
        reclamationContentLabel.setText(response.getReclamation().getContent());
        responseContentArea.setText(response.getContent());
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        String content = responseContentArea.getText().trim();

        if (content.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a response");
            return;
        }

        try {
            response.setContent(content);
            response.setDate(new Timestamp(System.currentTimeMillis()));

            responseService.update(response);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Response updated successfully");
            
            // Close the modal after successful update
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
