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
    
    @FXML
    private Button home_button;

    private final ResponseService responseService;
    private Response response;

    public UpdateResponse() {
        responseService = new ResponseService();
    }

    @FXML
    void initialize() {
        updateButton.setOnAction(this::handleUpdate);
        cancelButton.setOnAction(this::navigateToList);
        home_button.setOnAction(this::navigateToHome);
    }

    public void initData(Response response) {
        this.response = response;
        // Display reclamation and response details
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
            // Update the response content and timestamp
            response.setContent(content);
            response.setDate(new Timestamp(System.currentTimeMillis()));

            // Update in database
            responseService.update(response);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Response updated successfully");
            navigateToList(event);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Response/ListResponse.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
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
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
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
