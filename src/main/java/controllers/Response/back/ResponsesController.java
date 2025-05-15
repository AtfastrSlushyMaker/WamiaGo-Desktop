//Back
//ResponsesController
package controllers.Response.back;



import controllers.Response.UpdateResponse;
import entities.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ResponseService;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class ResponsesController {
    @FXML
    private ListView<Response> responseListView;

    @FXML
    private Button home_button;

    @FXML
    private Button deleteButton;

    private final ResponseService responseService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ResponsesController() {
        responseService = new ResponseService();
    }

    @FXML
    void initialize() {
        setupListView();
        loadResponses();
        home_button.setOnAction(this::navigateToHome);
        deleteButton.setOnAction(e -> handleDelete());
        responseListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Response selectedResponse = responseListView.getSelectionModel().getSelectedItem();
                if (selectedResponse != null) {
                    navigateToUpdate(event, selectedResponse);
                }
            }
        });
        responseListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                handleDelete();
            }
        });
    }

    private void setupListView() {
        responseListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Response response, boolean empty) {
                super.updateItem(response, empty);
                if (empty || response == null) {
                    setText(null);
                } else {
                    setText(String.format("Reclamation: %s\nResponse: %s\nDate: %s",
                            response.getReclamation().getTitle(),
                            response.getContent(),
                            dateFormat.format(response.getDate())));
                }
            }
        });
    }

    private void loadResponses() {
        try {
            ObservableList<Response> responses = FXCollections.observableArrayList(responseService.read());
            responseListView.setItems(responses);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load responses: " + e.getMessage());
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

    private void navigateToUpdate(javafx.scene.input.MouseEvent event, Response response) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Response/UpdateResponse.fxml"));
            Parent root = loader.load();

            UpdateResponse updateController = loader.getController();
            updateController.initData(response);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete() {
        Response selectedResponse = responseListView.getSelectionModel().getSelectedItem();
        if (selectedResponse == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a response to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Are you sure you want to delete this response?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    responseService.delete(selectedResponse.getId_response());
                    loadResponses();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Response deleted successfully");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete response: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

