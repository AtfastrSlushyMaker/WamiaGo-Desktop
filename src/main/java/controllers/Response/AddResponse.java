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
import services.ResponseService;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

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

    @FXML
    private Button home_button;

    private final ResponseService responseService;
    private Reclamation reclamation;

    public AddResponse() {
        responseService = new ResponseService();
    }

    @FXML
    void initialize() {
        submitButton.setOnAction(this::handleSubmit);
        cancelButton.setOnAction(this::navigateToList);
        home_button.setOnAction(this::navigateToHome);
    }

    public void initData(Reclamation reclamation) {
        this.reclamation = reclamation;
        // Display reclamation details
        reclamationTitleLabel.setText(reclamation.getTitle());
        reclamationContentLabel.setText(reclamation.getContent());
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String content = responseContentArea.getText().trim();

        if (content.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a response");
            return;
        }

        try {
            Response response = new Response(
                    reclamation,
                    content,
                    new Timestamp(System.currentTimeMillis())
            );

            if (responseService.create(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Response added successfully");
                navigateToList(event);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Response added successfully");
                navigateToList(event);            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add response: " + e.getMessage());
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