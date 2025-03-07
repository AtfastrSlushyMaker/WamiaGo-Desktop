package controllers.Reclamation;

import controllers.Response.AddResponse;
import controllers.Response.ListResponse;
import entities.Reclamation;
import entities.Response;
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
import services.ResponseService;
import utils.SessionManager;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.AnchorPane;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

public class ListReclamation {
    @FXML
    private Button details;
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
    private Button exportButton;

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

    @FXML
    private Button faqButton;

    @FXML
    private Button statsButton;

    @FXML
    private ListView<String> chatHistory;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    @FXML
    private Button chatbotButton;

    @FXML
    private AnchorPane chatbotPane;

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
        if (date != null) {
            date.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        } else {
            System.err.println("Warning: date Label is null in initialize method");
        }

        //setup faqButton
        if (faqButton != null) {
            faqButton.setOnAction(event -> showFAQDialog());
        }

        //setup statsButton
        if (statsButton != null) {
            statsButton.setOnAction(event -> showReclamationStatsChart());
        }

        if (details != null) {
            details.setOnAction(event -> showResponsesDialog());
        }

        // Setup status filter
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList("All", "Pending", "Resolved"));
            statusFilter.getSelectionModel().selectFirst();
        }

        setupListView();
        loadReclamations();
        setupSearch();
        updateStatistics();

        // Initialize export button and context menu
        initializeExportButton();

        if (addButton != null) {
            addButton.setOnAction(this::navigateToAddReclamation);
        }

        if (deleteButton != null) {
            deleteButton.setOnAction(e -> handleDelete());
        }

        if (home_button != null) {
            home_button.setOnAction(this::navigateToHome);
        }

        if (btn_workbench11 != null) {
            btn_workbench11.setOnAction(this::navigateToRide);
        }

        if (refreshButton != null) {
            refreshButton.setOnAction(e -> loadReclamations());
        }

        if (chatbotButton != null) {
            chatbotButton.setOnAction(e -> openChatbot());
        }

        // Add double-click handler for update
        if (reclamationListView != null) {
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
    }

    private void initializeExportButton() {
        if (exportButton != null) {
            exportButton.setOnAction(event -> exportSelectedReclamation());
        }

        // Setup context menu for right-click actions
        setupContextMenu();
    }

    private void setupContextMenu() {
        if (reclamationListView == null) return;

        ContextMenu contextMenu = new ContextMenu();

        MenuItem viewDetailsItem = new MenuItem("View Details");
        viewDetailsItem.setOnAction(event -> showResponsesDialog());

        MenuItem exportToPdfItem = new MenuItem("Export to PDF");
        exportToPdfItem.setOnAction(event -> exportSelectedReclamation());

        MenuItem markAsResolvedItem = new MenuItem("Mark as Resolved");
        markAsResolvedItem.setOnAction(event -> markAsResolved());

        contextMenu.getItems().addAll(viewDetailsItem, exportToPdfItem, markAsResolvedItem);

        reclamationListView.setContextMenu(contextMenu);
    }

    @FXML
    private void exportSelectedReclamation() {
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation to export.");
            return;
        }

        boolean success = ReclamationPdfExporter.exportReclamationToPdf(selectedReclamation, this);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation exported to PDF successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to export reclamation to PDF.");
        }
    }

    @FXML
    private void markAsResolved() {
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation to mark as resolved.");
            return;
        }

        if (selectedReclamation.getStatus() == 1) {
            showAlert(Alert.AlertType.INFORMATION, "Information", "This reclamation is already resolved.");
            return;
        }

        // Ask for confirmation
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Action");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Are you sure you want to mark this reclamation as resolved?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Update status to resolved (1)
                    selectedReclamation.setStatus(1);
                    reclamationService.update(selectedReclamation);

                    // Refresh the list
                    loadReclamations();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation marked as resolved.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update reclamation status: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void openChatbot() {
        if (chatbotPane != null) {
            chatbotPane.setVisible(!chatbotPane.isVisible());
            if (chatbotPane.isVisible() && userInput != null) {
                userInput.requestFocus(); // Donner le focus au champ de saisie
            }
        }
    }

    private void showFAQDialog() {
        // Create a new Stage (dialog window)
        Stage faqStage = new Stage();
        faqStage.setTitle("Frequently Asked Questions");

        // Create the main container
        VBox faqContainer = new VBox(10);
        faqContainer.setStyle("-fx-padding: 20px;");

        // Create an Accordion for expandable FAQ sections
        Accordion faqAccordion = new Accordion();

        // Predefined FAQs
        List<FAQ> faqs = createFAQs();

        // Create TitledPanes for each FAQ
        for (FAQ faq : faqs) {
            TitledPane pane = new TitledPane();
            pane.setText(faq.getQuestion());

            // Create a label for the answer
            Label answerLabel = new Label(faq.getAnswer());
            answerLabel.setWrapText(true);
            answerLabel.setStyle("-fx-padding: 10px;");

            // Set the content of the TitledPane
            pane.setContent(answerLabel);

            // Add to the accordion
            faqAccordion.getPanes().add(pane);
        }

        // Add accordion to container
        faqContainer.getChildren().add(faqAccordion);

        // Create scene and set it to the stage
        Scene scene = new Scene(faqContainer, 500, 600);
        faqStage.setScene(scene);

        // Show the dialog
        faqStage.show();
    }

    // Inner class to represent FAQ
    private static class FAQ {
        private String question;
        private String answer;

        public FAQ(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
    }

    // Method to create predefined FAQs
    private List<FAQ> createFAQs() {
        List<FAQ> faqs = new ArrayList<>();

        // Add some sample FAQs related to reclamations/complaints
        faqs.add(new FAQ(
                "How do I submit a reclamation?",
                "To submit a reclamation, click on 'Add New Reclamation' button, fill out the form with details of your complaint, and submit."
        ));

        faqs.add(new FAQ(
                "What is the status of my reclamation?",
                "You can check the status of your reclamation in the list view. Reclamations are marked as 'Pending' or 'Resolved'."
        ));

        faqs.add(new FAQ(
                "How long does it take to resolve a reclamation?",
                "Our team aims to respond to and resolve reclamations within 3-5 business days. The exact time may vary depending on the complexity of the issue."
        ));

        faqs.add(new FAQ(
                "Can I update my reclamation after submission?",
                "Yes, you can update your reclamation by double-clicking on it in the list view and modifying the details."
        ));

        faqs.add(new FAQ(
                "What information should I include in my reclamation?",
                "Please provide a clear and concise title, detailed description of the issue, date of occurrence, and any supporting information or documents if possible."
        ));

        return faqs;
    }

    private void setupSearch() {
        if (allReclamations == null || searchField == null || statusFilter == null) return;

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
        if (searchField == null || statusFilter == null) return;

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
        if (allReclamations == null || totalReclamationsLabel == null ||
                pendingReclamationsLabel == null || resolvedReclamationsLabel == null) return;

        int total = allReclamations.size();
        int pending = (int) allReclamations.stream().filter(r -> r.getStatus() == 0).count();
        int resolved = total - pending;

        totalReclamationsLabel.setText("Total: " + total);
        pendingReclamationsLabel.setText("Pending: " + pending);
        resolvedReclamationsLabel.setText("Resolved: " + resolved);
    }

    private void setupListView() {
        if (reclamationListView == null) return;

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

            if (reclamationListView != null) {
                reclamationListView.setItems(filteredReclamations);
            }
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
        if (reclamationListView == null) return;

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
        if (reclamationListView == null) return;

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

    private void showReclamationStatsChart() {
        if (allReclamations == null) return;

        // Create a new stage for the chart
        Stage chartStage = new Stage();
        chartStage.setTitle("Reclamation Statistics");
        chartStage.initModality(Modality.APPLICATION_MODAL);

        // Create the chart axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, calculateMaxValue(), 1);
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        // Disable animations to create our custom animation
        barChart.setAnimated(false);

        // Configure chart appearance
        barChart.setTitle("Reclamation Status Distribution");
        xAxis.setLabel("Status");
        yAxis.setLabel("Number of Reclamations");

        // Create series for the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reclamations");

        // Calculate statistics
        int totalReclamations = allReclamations.size();
        int pendingReclamations = (int) allReclamations.stream()
                .filter(r -> r.getStatus() == 0)
                .count();
        int resolvedReclamations = totalReclamations - pendingReclamations;

        // Create layout
        VBox layout = new VBox(10);
        layout.getChildren().add(barChart);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Create and show the scene
        Scene scene = new Scene(layout, 600, 400);
        chartStage.setScene(scene);

        // Animate the chart
        Platform.runLater(() -> {
            // Add data with animation
            animateBarChart(barChart, series, pendingReclamations, resolvedReclamations);
            chartStage.show();
        });
    }

    private void animateBarChart(BarChart<String, Number> barChart,
                                 XYChart.Series<String, Number> series,
                                 int pendingReclamations,
                                 int resolvedReclamations) {
        // Clear any existing data
        barChart.getData().clear();

        // Create a timeline for animation
        Timeline timeline = new Timeline();

        // Animate Pending Reclamations
        XYChart.Data<String, Number> pendingData = new XYChart.Data<>("Pending", 0);
        series.getData().add(pendingData);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                new KeyValue(pendingData.YValueProperty(), pendingReclamations)
        ));

        // Animate Resolved Reclamations
        XYChart.Data<String, Number> resolvedData = new XYChart.Data<>("Resolved", 0);
        series.getData().add(resolvedData);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000),
                new KeyValue(resolvedData.YValueProperty(), resolvedReclamations)
        ));

        // Add series to chart after creating data points
        barChart.getData().add(series);

        // Style the chart nodes after they are created
        series.getData().forEach(data -> {
            Node node = data.getNode();
            if (node != null) {
                if (data.getXValue().equals("Pending")) {
                    node.setStyle("-fx-bar-fill: #FFC107;"); // Yellow for Pending
                } else if (data.getXValue().equals("Resolved")) {
                    node.setStyle("-fx-bar-fill: #4CAF50;"); // Green for Resolved
                }
            }
        });

        // Play the animation
        timeline.play();
    }

    // Helper method to calculate max value for Y-axis
    private double calculateMaxValue() {
        int totalReclamations = allReclamations.size();
        int pendingReclamations = (int) allReclamations.stream()
                .filter(r -> r.getStatus() == 0)
                .count();
        int resolvedReclamations = totalReclamations - pendingReclamations;

        // Add some padding to the max value
        return Math.max(pendingReclamations, resolvedReclamations) * 1.2;
    }

    @FXML
    private void showResponses() {
        if (reclamationListView == null) return;

        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation to view responses.");
            return;
        }

        try {
            // Load the responses interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Response/ListResponse.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the selected reclamation
            ListResponse controller = loader.getController();
            controller.initData(selectedReclamation);

            // Show the new scene
            Stage stage = (Stage) reclamationListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load responses: " + e.getMessage());
        }
    }

    private void showResponsesDialog() {
        if (reclamationListView == null) return;

        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a reclamation to view responses.");
            return;
        }

        try {
            // Create a dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Responses for Reclamation");
            dialog.setHeaderText("Responses for: " + selectedReclamation.getTitle());

            // Add buttons
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

            // Create a ListView for responses
            ListView<Response> responsesListView = new ListView<>();

            // Get responses for the selected reclamation from your service
            List<Response> responses = new ResponseService().getResponsesByReclamationId(selectedReclamation.getIdReclamation());

            if (responses.isEmpty()) {
                // Show a message if there are no responses
                Label noResponsesLabel = new Label("No responses found for this reclamation.");
                dialog.getDialogPane().setContent(noResponsesLabel);
            } else {
                // Set up the ListView with responses
                responsesListView.setItems(FXCollections.observableArrayList(responses));

                // Set up cell factory to customize how responses are displayed
                responsesListView.setCellFactory(param -> new ListCell<Response>() {
                    @Override
                    protected void updateItem(Response response, boolean empty) {
                        super.updateItem(response, empty);

                        if (empty || response == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Create a formatted display for each response
                            VBox content = new VBox(5);

                            Label contentLabel = new Label(response.getContent());
                            contentLabel.setWrapText(true);

                            Label dateLabel = new Label("Date: " + dateFormat.format(response.getDate()));
                            Label userLabel = null;
                            try {
                                userLabel = new Label("Responded by: " + new ResponseService().getUserFromResponse(response).getName());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            content.getChildren().addAll(contentLabel, dateLabel, userLabel);
                            setGraphic(content);
                            setText(null);
                        }
                    }
                });

                // Make the ListView scrollable and set a reasonable height
                ScrollPane scrollPane = new ScrollPane(responsesListView);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(400);

                dialog.getDialogPane().setContent(scrollPane);
            }

            // Adjust dialog size
            dialog.getDialogPane().setPrefWidth(500);

            // Show the dialog
            dialog.showAndWait();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load responses: " + e.getMessage());
        }
    }
}