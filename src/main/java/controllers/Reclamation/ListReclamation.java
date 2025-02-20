package controllers.Reclamation;

import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ReclamationService;
import javafx.scene.input.KeyCode;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class ListReclamation {
    @FXML
    private ListView<Reclamation> reclamationListView;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button home_button;

    @FXML
    private Button btn_workbench11;

    private final ReclamationService reclamationService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ListReclamation() {
        reclamationService = new ReclamationService();
    }

    @FXML
    void initialize() {
        setupListView();
        loadReclamations();

        addButton.setOnAction(this::navigateToAddReclamation);
        deleteButton.setOnAction(e -> handleDelete());
        home_button.setOnAction(this::navigateToHome);
        btn_workbench11.setOnAction(this::navigateToRide);

        // Add double-click handler for update
        reclamationListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
                if (selectedReclamation != null) {
                    navigateToUpdate(event, selectedReclamation);
                }
            }
        });




        // Add delete key handler
        reclamationListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                handleDelete();
            }
        });
    }

    private void pageNavigation() {
        home_button.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
                Parent homeRoot = loader.load();
                Scene homeScene = new Scene(homeRoot);
                Stage stage = (Stage) home_button.getScene().getWindow();
                stage.setScene(homeScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupListView() {
        reclamationListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);

                if (empty || reclamation == null) {
                    setText(null);
                } else {
                    String status = reclamation.getStatus() == 0 ? "Pending" : "Resolved";
                    setText(String.format("ID: %d\nTitle: %s\nContent: %s\nDate: %s\nStatus: %s\nUser: %s\n",
                            reclamation.getIdReclamation(),
                            reclamation.getTitle(),
                            reclamation.getContent(),
                            dateFormat.format(reclamation.getDate()),
                            status,
                            reclamation.getUser().getName()));
                }
            }
        });
    }

    private void loadReclamations() {
        try {
            ObservableList<Reclamation> reclamations = FXCollections.observableArrayList(
                reclamationService.read()
            );
            reclamationListView.setItems(reclamations);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reclamations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToAddReclamation(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Reclamation/AddReclamation.fxml");
            System.out.println("Dashboard FXML Path: " + resource);

            if (resource == null) {
                throw new IOException("AddReclamation.fxml file not found!");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home - Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate to Home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToHome(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/dashboard/dashboard.fxml");
            System.out.println("Dashboard FXML Path: " + resource);

            if (resource == null) {
                throw new IOException("dashboard.fxml file not found!");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home - Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate to Home: " + e.getMessage());
            e.printStackTrace();
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
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
            e.printStackTrace();
        }
    }

    private void navigateToUpdate(MouseEvent event, Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/UpdateReclamation.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the reclamation data
            UpdateReclamation updateController = loader.getController();
            updateController.initData(reclamation);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
            e.printStackTrace();
        }
    }

    private void handleDelete() {
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Are you sure you want to delete this reclamation?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reclamationService.delete(selectedReclamation.getIdReclamation());
                    loadReclamations(); // Refresh the list
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation deleted successfully");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reclamation: " + e.getMessage());
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
