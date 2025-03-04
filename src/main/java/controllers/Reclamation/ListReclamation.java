package controllers.Reclamation;

import controllers.Response.AddResponse;
import entities.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ReclamationService;
import javafx.scene.input.KeyCode;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

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

    @FXML
    private Button responseButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private Button refreshButton;

    @FXML
    private Label totalReclamationsLabel;

    @FXML
    private Label pendingReclamationsLabel;

    @FXML
    private Label resolvedReclamationsLabel;

    @FXML
    private Label date;

    private final ReclamationService reclamationService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private ObservableList<Reclamation> allReclamations;
    private FilteredList<Reclamation> filteredReclamations;

    public ListReclamation() {
        reclamationService = new ReclamationService();
    }

    @FXML
    void initialize() {
        // Setup date display
        // Setup date display
        if (date != null) {
            date.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        } else {
            System.err.println("Warning: date Label is null in initialize method");
        }

        // Setup status filter
        statusFilter.setItems(FXCollections.observableArrayList("All", "Pending", "Resolved"));
        statusFilter.getSelectionModel().selectFirst();

        setupListView();
        loadReclamations();
        setupSearch();
        updateStatistics();

        addButton.setOnAction(this::navigateToAddReclamation);
        deleteButton.setOnAction(e -> handleDelete());
        home_button.setOnAction(this::navigateToHome);
        btn_workbench11.setOnAction(this::navigateToRide);
        responseButton.setOnAction(this::handleResponse);
        refreshButton.setOnAction(e -> loadReclamations());

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

    private void setupSearch() {
        // Setup initial filtered list
        filteredReclamations = new FilteredList<>(allReclamations);
        reclamationListView.setItems(filteredReclamations);

        // Add listeners for search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Add listeners for status filter
        statusFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String statusValue = statusFilter.getValue();

        Predicate<Reclamation> searchPredicate = reclamation ->
                searchText.isEmpty() ||
                        reclamation.getTitle().toLowerCase().contains(searchText) ||
                        reclamation.getContent().toLowerCase().contains(searchText) ||
                        reclamation.getUser().getName().toLowerCase().contains(searchText);

        Predicate<Reclamation> statusPredicate = reclamation -> {
            if ("All".equals(statusValue)) {
                return true;
            } else if ("Pending".equals(statusValue)) {
                return reclamation.getStatus() == 0;
            } else if ("Resolved".equals(statusValue)) {
                return reclamation.getStatus() == 1;
            }
            return true;
        };

        filteredReclamations.setPredicate(searchPredicate.and(statusPredicate));
        updateStatistics();
    }

    private void updateStatistics() {
        int total = allReclamations.size();
        int pending = (int) allReclamations.stream().filter(r -> r.getStatus() == 0).count();
        int resolved = total - pending;

        totalReclamationsLabel.setText("Total: " + total);
        pendingReclamationsLabel.setText("Pending: " + pending);
        resolvedReclamationsLabel.setText("Resolved: " + resolved);
    }

    private void setupListView() {
        reclamationListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);

                if (empty || reclamation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String status = reclamation.getStatus() == 0 ? "Pending" : "Resolved";

                    // Create a better formatted cell
                    VBox content = new VBox(5);

                    Label titleLabel = new Label(reclamation.getTitle());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    Label contentLabel = new Label(reclamation.getContent());
                    contentLabel.setWrapText(true);

                    HBox metaData = new HBox(10);
                    Label dateLabel = new Label("Date: " + dateFormat.format(reclamation.getDate()));
                    Label statusLabel = new Label("Status: " + status);
                    statusLabel.setStyle(reclamation.getStatus() == 0 ?
                            "-fx-text-fill: #D32F2F; -fx-font-weight: bold;" :
                            "-fx-text-fill: #388E3C; -fx-font-weight: bold;");
                    Label userLabel = new Label("User: " + reclamation.getUser().getName());

                    metaData.getChildren().addAll(dateLabel, statusLabel, userLabel);
                    content.getChildren().addAll(titleLabel, contentLabel, metaData);

                    setGraphic(content);
                    setText(null);
                }
            }
        });
    }

    private void loadReclamations() {
        try {
            allReclamations = FXCollections.observableArrayList(
                    reclamationService.read()
            );

            if (filteredReclamations == null) {
                filteredReclamations = new FilteredList<>(allReclamations);
            } else {
                filteredReclamations = new FilteredList<>(allReclamations, filteredReclamations.getPredicate());
            }

            reclamationListView.setItems(filteredReclamations);
            updateStatistics();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reclamations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Navigation methods remain the same...
    private void navigateToAddReclamation(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/Reclamation/AddReclamation.fxml");
            System.out.println("AddReclamation FXML Path: " + resource);

            if (resource == null) {
                throw new IOException("AddReclamation.fxml file not found!");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Reclamation");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate to Add Reclamation: " + e.getMessage());
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

    @FXML
    private void handleResponse(ActionEvent event) {
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation to respond to");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Response/AddResponse.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the reclamation data
            controllers.Response.AddResponse responseController = loader.getController();
            responseController.initData(selectedReclamation);

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