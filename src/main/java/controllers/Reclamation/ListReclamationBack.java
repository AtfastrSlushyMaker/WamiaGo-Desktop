package controllers.Reclamation;

import controllers.Response.AddResponse;
import controllers.Response.UpdateResponse;
import entities.Reclamation;
import entities.Response;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ReclamationService;
import services.ResponseService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListReclamationBack {
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
    private MultipleSelectionModel<Reclamation> selectionModel;

    public ListReclamationBack() {
        reclamationService = new ReclamationService();
    }

    @FXML
    void initialize() {
        // Setup date display
        if (date != null) {
            date.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        }

        // Setup buttons with hover effects
        setupButtonStyles(faqButton, "#17a2b8");
        setupButtonStyles(statsButton, "#28a745");
        setupButtonStyles(responseButton, "#28a745");
        setupButtonStyles(refreshButton, "#6c757d");

        // Add button actions
        if (faqButton != null) {
            faqButton.setOnAction(event -> showFAQDialog());
        }
        if (statsButton != null) {
            statsButton.setOnAction(event -> showReclamationStatsChart());
        }
        responseButton.setOnAction(this::handleBulkResponse);
        refreshButton.setOnAction(e -> loadReclamations());

        // Setup status filter with modern styling
        statusFilter.setItems(FXCollections.observableArrayList("All", "Pending", "Resolved"));
        statusFilter.getSelectionModel().selectFirst();
        statusFilter.setStyle("-fx-background-color: white; -fx-border-color: #ced4da; -fx-border-radius: 5; -fx-padding: 5; -fx-min-width: 120;");

        // Setup search field with modern styling
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #ced4da; -fx-border-radius: 5; -fx-padding: 8; -fx-min-width: 200;");

        // Setup list view with better width and selection mode
        reclamationListView.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        reclamationListView.setPrefWidth(800);
        reclamationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectionModel = reclamationListView.getSelectionModel();

        setupListView();
        loadReclamations();
        setupSearch();
        updateStatistics();
    }

    @FXML
    private void openChatbot() {
        chatbotPane.setVisible(!chatbotPane.isVisible());
        if (chatbotPane.isVisible()) {
            userInput.requestFocus(); // Donner le focus au champ de saisie
        }
    }

    private void showFAQDialog() {
        Stage faqStage = new Stage();
        faqStage.setTitle("Frequently Asked Questions");
        faqStage.initModality(Modality.APPLICATION_MODAL);

        VBox faqContainer = new VBox(10);
        faqContainer.setStyle("-fx-padding: 20px; -fx-background-color: white;");
        faqContainer.setPrefWidth(600);

        Label titleLabel = new Label("Frequently Asked Questions");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        faqContainer.getChildren().add(titleLabel);

        Accordion faqAccordion = new Accordion();
        faqAccordion.setStyle("-fx-background-color: transparent;");

        List<FAQ> faqs = createFAQs();
        for (FAQ faq : faqs) {
            TitledPane pane = new TitledPane();
            pane.setText(faq.getQuestion());
            pane.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label answerLabel = new Label(faq.getAnswer());
            answerLabel.setWrapText(true);
            answerLabel.setStyle("-fx-padding: 10px; -fx-text-fill: #495057;");
            pane.setContent(answerLabel);

            faqAccordion.getPanes().add(pane);
        }

        faqContainer.getChildren().add(faqAccordion);

        Scene scene = new Scene(faqContainer);
        faqStage.setScene(scene);
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
        reclamationListView.setCellFactory(lv -> new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation reclamation, boolean empty) {
                super.updateItem(reclamation, empty);
                if (empty || reclamation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox mainContainer = new VBox(10);
                    mainContainer.setPadding(new Insets(15));
                    mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
                    mainContainer.setPrefWidth(750);

                    // Header with title and status
                    HBox headerBox = new HBox(10);
                    headerBox.setAlignment(Pos.CENTER_LEFT);
                    
                    Label titleLabel = new Label(reclamation.getTitle());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
                    titleLabel.setWrapText(true);
                    HBox.setHgrow(titleLabel, Priority.ALWAYS);
                    
                    Label statusLabel = new Label(reclamation.getStatus() == 0 ? "Pending" : "Resolved");
                    statusLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 15; " +
                            (reclamation.getStatus() == 0 ? 
                            "-fx-background-color: #fff3cd; -fx-text-fill: #856404;" : 
                            "-fx-background-color: #d4edda; -fx-text-fill: #155724;"));
                    
                    headerBox.getChildren().addAll(titleLabel, statusLabel);

                    // Content section
                    VBox contentBox = new VBox(5);
                    contentBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
                    
                    Label contentLabel = new Label(reclamation.getContent());
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
                    
                    Label dateLabel = new Label("Submitted on: " + dateFormat.format(reclamation.getDate()));
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
                    
                    contentBox.getChildren().addAll(contentLabel, dateLabel);

                    // Response section (if resolved)
                    if (reclamation.getStatus() == 1) {
                        try {
                            List<Response> responses = new ResponseService().getResponsesByReclamationId(reclamation.getIdReclamation());
                            if (!responses.isEmpty()) {
                                VBox responsesBox = new VBox(10);
                                responsesBox.setStyle("-fx-background-color: #e9ecef; -fx-padding: 15; -fx-background-radius: 5;");
                                
                                Label responsesHeader = new Label("Responses");
                                responsesHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                                responsesBox.getChildren().add(responsesHeader);

                                responses.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));

                                for (Response response : responses) {
                                    VBox responseBox = new VBox(5);
                                    responseBox.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 0);");
                                    
                                    Label responseContent = new Label(response.getContent());
                                    responseContent.setWrapText(true);
                                    responseContent.setStyle("-fx-font-size: 13px; -fx-text-fill: #495057;");
                                    
                                    Label responseDate = new Label("Responded on: " + dateFormat.format(response.getDate()));
                                    responseDate.setStyle("-fx-font-size: 11px; -fx-text-fill: #6c757d;");
                                    
                                    Button editButton = new Button("Edit");
                                    editButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-cursor: hand;");
                                    editButton.setOnAction(e -> handleEditResponse(response));
                                    
                                    HBox responseActions = new HBox(10);
                                    responseActions.setAlignment(Pos.CENTER_RIGHT);
                                    responseActions.getChildren().add(editButton);
                                    
                                    responseBox.getChildren().addAll(responseContent, responseDate, responseActions);
                                    responsesBox.getChildren().add(responseBox);
                                }
                                
                                mainContainer.getChildren().add(responsesBox);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Label errorLabel = new Label("Error loading responses");
                            errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-style: italic;");
                            mainContainer.getChildren().add(errorLabel);
                        }
                    }

                    // Action buttons
                    HBox actionBox = new HBox(10);
                    actionBox.setAlignment(Pos.CENTER_RIGHT);

                    if (reclamation.getStatus() == 0) {
                        Button respondButton = new Button("Respond");
                        respondButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
                        respondButton.setOnAction(e -> {
                            Platform.runLater(() -> openResponseDialog(reclamation));
                        });
                        actionBox.getChildren().add(respondButton);
                    }

                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
                    deleteButton.setOnAction(e -> {
                        try {
                            reclamationService.delete(reclamation.getIdReclamation());
                            loadReclamations();
                        } catch (SQLException ex) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete reclamation: " + ex.getMessage());
                        }
                    });

                    actionBox.getChildren().add(deleteButton);

                    mainContainer.getChildren().addAll(headerBox, contentBox, actionBox);
                    setGraphic(mainContainer);
                }
            }
        });

        // Add hover effect to list items
        reclamationListView.setOnMouseEntered(e -> {
            reclamationListView.setStyle("-fx-background-color: transparent;");
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

    private void navigateToHome(ActionEvent event) {
        try {
            URL resource = getClass().getResource("/dashboard/dashboardTemplate.fxml");
            System.out.println("dashboardTemplate FXML Path: " + resource);

            if (resource == null) {
                throw new IOException("dashboardTemplate.fxml file not found!");
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

    private void handleBulkResponse(ActionEvent event) {
        // Get the selected item directly from the ListView
        Reclamation selectedReclamation = reclamationListView.getSelectionModel().getSelectedItem();
        
        if (selectedReclamation == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reclamation to respond to.");
            return;
        }

        // Check if the selected reclamation is pending
        if (selectedReclamation.getStatus() != 0) {
            showAlert(Alert.AlertType.WARNING, "Invalid Selection", "You can only respond to pending reclamations.");
            return;
        }

        // Open the response dialog for the selected reclamation
        openResponseDialog(selectedReclamation);
    }

    private void openResponseDialog(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Response/AddResponse.fxml"));
            Parent root = loader.load();

            AddResponse controller = loader.getController();
            controller.initData(reclamation);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Response - " + reclamation.getTitle());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            // Center the dialog on the screen
            dialogStage.centerOnScreen();
            
            // Set minimum size for better usability
            dialogStage.setMinWidth(500);
            dialogStage.setMinHeight(400);
            
            dialogStage.initOwner(reclamationListView.getScene().getWindow());
            
            // Show dialog and wait for it to close
            dialogStage.showAndWait();
            
            // Refresh the list after response
            Platform.runLater(this::loadReclamations);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open response dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditResponse(Response response) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Response/UpdateResponse.fxml"));
            Parent root = loader.load();

            UpdateResponse controller = loader.getController();
            controller.initData(response);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Response");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            
            // Set the owner window to center the dialog
            dialogStage.initOwner(reclamationListView.getScene().getWindow());
            
            // Show the dialog and wait for it to be closed
            dialogStage.showAndWait();

            // Close the dialog and return to the reclamation list
            dialogStage.close();
            
            // Refresh the reclamation list after response is updated
            loadReclamations();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open edit dialog: " + e.getMessage());
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

    private void setupButtonStyles(Button button, String color) {
        if (button != null) {
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
            button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", -10%); -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
        }
    }
}
