package controllers.station.back;

import entities.Location;
import entities.Station;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import services.LocationService;
import services.StationService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StationsController {
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Station> stationTableView;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label statusLabel;

    private StationService stationService;
    private LocationService locationService;

    @FXML
    private Button exportToPdfButton;

    @FXML
    public void initialize() {
        // Initialize services
        stationService = new StationService();
        locationService = new LocationService();

        // Make sure table allows multiple selection for batch deletion
        stationTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Initialize TableView columns
        setupTableColumns();

        // Setup button actions
        addButton.setOnAction(event -> addStation());
        deleteButton.setOnAction(event -> deleteStations());
        refreshButton.setOnAction(event -> loadStations());

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterStations(newValue);
        });


        exportToPdfButton.setOnAction(event -> exportToPdf());

        // Double-click handler for editing
        stationTableView.setRowFactory(tv -> {
            TableRow<Station> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    updateStation(row.getItem());
                }
            });
            return row;
        });

        // Initial load - call this after the columns are set up
        loadStations();


    }

    private void setupTableColumns() {
        // Clear any existing columns first
        stationTableView.getColumns().clear();

        // Create columns
        TableColumn<Station, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Station, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getLocation() != null) {
                return new SimpleStringProperty(cellData.getValue().getLocation().getAddress());
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Station, Integer> totalDocksColumn = new TableColumn<>("Total Docks");
        totalDocksColumn.setCellValueFactory(new PropertyValueFactory<>("total_docks"));
        totalDocksColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Station, Integer> availableDocksColumn = new TableColumn<>("Available Docks");
        availableDocksColumn.setCellValueFactory(new PropertyValueFactory<>("available_docks"));
        availableDocksColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Station, Integer> availableBikesColumn = new TableColumn<>("Available Bikes");
        availableBikesColumn.setCellValueFactory(new PropertyValueFactory<>("available_bikes"));
        availableBikesColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Station, Integer> chargingBikesColumn = new TableColumn<>("Charging");
        chargingBikesColumn.setCellValueFactory(new PropertyValueFactory<>("charging_bikes"));
        chargingBikesColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<Station, Station.STATUS> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Station.STATUS status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString());

                    switch (status) {
                        case active:
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case inactive:
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        case maintenance:
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case disabled:
                            setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        // Action column with edit/delete buttons
        TableColumn<Station, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(createActionColumnCellFactory());

        // Add columns to table
        stationTableView.getColumns().addAll(
                nameColumn, locationColumn, totalDocksColumn,
                availableDocksColumn, availableBikesColumn, chargingBikesColumn,
                statusColumn, actionColumn
        );

        // Set column widths
        nameColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.15));
        locationColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.25));
        totalDocksColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.1));
        availableDocksColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.1));
        availableBikesColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.1));
        chargingBikesColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.1));
        statusColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.1));
        actionColumn.prefWidthProperty().bind(stationTableView.widthProperty().multiply(0.15));
    }

    private Callback<TableColumn<Station, Void>, TableCell<Station, Void>> createActionColumnCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Station, Void> call(TableColumn<Station, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox buttonContainer = new HBox(5);

                    {
                        // Configure edit button
                        editButton.getStyleClass().add("table-button");
                        editButton.getStyleClass().add("edit-button");
                        try {
                            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/station/icons/edit.png")));
                            editIcon.setFitHeight(16);
                            editIcon.setFitWidth(16);
                            editButton.setGraphic(editIcon);
                        } catch (Exception e) {
                            editButton.setText("Edit");
                        }
                        editButton.setTooltip(new Tooltip("Edit"));

                        // Configure delete button
                        deleteButton.getStyleClass().add("table-button");
                        deleteButton.getStyleClass().add("delete-button");
                        try {
                            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/station/icons/delete.png")));
                            deleteIcon.setFitHeight(16);
                            deleteIcon.setFitWidth(16);
                            deleteButton.setGraphic(deleteIcon);
                        } catch (Exception e) {
                            deleteButton.setText("Delete");
                        }
                        deleteButton.setTooltip(new Tooltip("Delete"));

                        // Configure button container
                        buttonContainer.setAlignment(Pos.CENTER);
                        buttonContainer.getChildren().addAll(editButton, deleteButton);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Set button actions only if the row has data
                            int index = getIndex();
                            if (index >= 0 && index < getTableView().getItems().size()) {
                                Station station = getTableView().getItems().get(index);
                                editButton.setOnAction(event -> updateStation(station));
                                deleteButton.setOnAction(event -> deleteSingleStation(station));
                                setGraphic(buttonContainer);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        };
    }

    private void filterStations(String searchText) {
        try {
            // If search text is empty, just load all stations
            if (searchText == null || searchText.isEmpty()) {
                loadStations();
                return;
            }

            // Determine which column to search based on the input
            String searchColumn = "name"; // Default to searching by name
            List<Station> searchResults = new ArrayList<>();

            try {
                // First try to search by name
                searchResults = stationService.search("name", searchText);

                // If status is mentioned, search by status instead
                if (searchText.equalsIgnoreCase("active") ||
                        searchText.equalsIgnoreCase("inactive") ||
                        searchText.equalsIgnoreCase("maintenance")) {
                    searchResults = stationService.search("status", searchText);
                }

                // Note: Location search is not supported by the search method
                // You might want to add "address" to your allowedColumns and handle it in the search method

                // Update table with search results
                stationTableView.setItems(FXCollections.observableArrayList(searchResults));

                // Update status label
                updateStatusLabel(searchResults.size() + " stations found");

            } catch (SQLException e) {
                showError("Search Error", "Database error while searching: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showError("Search Error", e.getMessage());
            }
        } catch (Exception e) {
            showError("Filter Error", "Error filtering stations: " + e.getMessage());
        }
    }

    public void loadStations() {
        // Show loading indicator
        loadingIndicator.setVisible(true);
        stationTableView.setDisable(true);
        updateStatusLabel("Loading stations...");

        // Load data asynchronously
        CompletableFuture.supplyAsync(() -> {
            try {
                return stationService.read();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(stations -> {
            Platform.runLater(() -> {
                try {
                    if (stations != null) {
                        // Create a new observable list to ensure the UI updates
                        stationTableView.setItems(FXCollections.observableArrayList(stations));
                        updateStatusLabel(stations.size() + " stations loaded");
                    } else {
                        stationTableView.setItems(FXCollections.observableArrayList());
                        updateStatusLabel("No stations available");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Load Error", "Error loading stations: " + e.getMessage());
                } finally {
                    loadingIndicator.setVisible(false);
                    stationTableView.setDisable(false);
                }
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                e.printStackTrace();
                showError("Load Error", "Error loading stations: " + e.getMessage());
                loadingIndicator.setVisible(false);
                stationTableView.setDisable(false);
            });
            return null;
        });
    }

    private void updateStatusLabel(String message) {
        statusLabel.setText(message);
    }

    public void deleteSingleStation(Station station) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Station");
        alert.setHeaderText("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete station: " + station.getName() + "?");

        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/modern-dialog.css").toExternalForm());
        } catch (Exception e) {
            // If stylesheet not found, continue without it
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                stationService.delete(station.getId());
                loadStations();
                showInfo("Success", "Station deleted successfully");
            } catch (Exception e) {
                showError("Delete Error", "Error deleting station: " + e.getMessage());
            }
        }
    }

    public void deleteStations() {
        List<Station> selectedStations = stationTableView.getSelectionModel().getSelectedItems();
        if (selectedStations.isEmpty()) {
            showWarning("No Selection", "Please select stations to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Stations");
        alert.setHeaderText("Are you sure you want to delete the selected stations?");
        alert.setContentText("This action cannot be undone.");

        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/modern-dialog.css").toExternalForm());
        } catch (Exception e) {
            // If stylesheet not found, continue without it
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int deletedCount = 0;
                for (Station station : selectedStations) {
                    stationService.delete(station.getId());
                    deletedCount++;
                }
                loadStations();
                showInfo("Success", deletedCount + " stations deleted successfully");
            } catch (Exception e) {
                showError("Delete Error", "Error deleting stations: " + e.getMessage());
            }
        }
    }

    public void addStation() {
        showStationDialog(null);
    }

    public void updateStation(Station station) {
        showStationDialog(station);
    }

    private void showStationDialog(Station station) {
        boolean isEdit = station != null;

        Dialog<Station> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Update Station" : "Add Station");
        dialog.setHeaderText(isEdit ? "Edit station details" : "Enter station details");

        // Apply CSS
        try {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/modern-dialog.css").toExternalForm());
        } catch (Exception e) {
            // If stylesheet not found, continue without it
        }

        // Set button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form layout
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(30, 30, 20, 30));

        // Create form fields
        TextField nameField = new TextField(isEdit ? station.getName() : "");
        nameField.setPromptText("Station name");

        ComboBox<Location> locationComboBox = new ComboBox<>();
        locationComboBox.setPromptText("Select location");
        locationComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Location location, boolean empty) {
                super.updateItem(location, empty);
                if (empty || location == null) {
                    setText(null);
                } else {
                    setText(location.getAddress());
                }
            }
        });
        locationComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Location location, boolean empty) {
                super.updateItem(location, empty);
                if (empty || location == null) {
                    setText(null);
                } else {
                    setText(location.getAddress());
                }
            }
        });

        Spinner<Integer> totalDocksSpinner = new Spinner<>(0, 100, isEdit ? station.getTotal_docks() : 10);
        totalDocksSpinner.setEditable(true);

        Spinner<Integer> availableDocksSpinner = new Spinner<>(0, 100, isEdit ? station.getAvailable_docks() : 5);
        availableDocksSpinner.setEditable(true);

        Spinner<Integer> availableBikesSpinner = new Spinner<>(0, 100, isEdit ? station.getAvailable_bikes() : 5);
        availableBikesSpinner.setEditable(true);

        Spinner<Integer> chargingBikesSpinner = new Spinner<>(0, 100, isEdit ? station.getCharging_bikes() : 0);
        chargingBikesSpinner.setEditable(true);

        ComboBox<Station.STATUS> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(Station.STATUS.values());
        statusComboBox.setValue(isEdit ? station.getStatus() : Station.STATUS.active);

        // Load locations
        try {
            List<Location> locations = locationService.read();
            locationComboBox.setItems(FXCollections.observableArrayList(locations));

            if (isEdit && station.getLocation() != null) {
                locationComboBox.getItems().stream()
                        .filter(l -> l.getId() == station.getLocation().getId())
                        .findFirst()
                        .ifPresent(locationComboBox::setValue);
            }
        } catch (Exception e) {
            showError("Load Error", "Error loading locations: " + e.getMessage());
        }

        // Layout the form
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        GridPane.setHgrow(nameField, Priority.ALWAYS);

        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationComboBox, 1, 1);
        GridPane.setHgrow(locationComboBox, Priority.ALWAYS);

        grid.add(new Label("Total Docks:"), 0, 2);
        grid.add(totalDocksSpinner, 1, 2);

        grid.add(new Label("Available Docks:"), 0, 3);
        grid.add(availableDocksSpinner, 1, 3);

        grid.add(new Label("Available Bikes:"), 0, 4);
        grid.add(availableBikesSpinner, 1, 4);

        grid.add(new Label("Charging Bikes:"), 0, 5);
        grid.add(chargingBikesSpinner, 1, 5);

        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusComboBox, 1, 6);

        // Add validation for numeric fields
        // Add validators to ensure docks and bikes counts make sense
        availableDocksSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            int total = totalDocksSpinner.getValue();
            int available = availableBikesSpinner.getValue();
            int charging = chargingBikesSpinner.getValue();

            if (newValue + available + charging > total) {
                availableDocksSpinner.getEditor().setStyle("-fx-background-color: #ffcccc;");
            } else {
                availableDocksSpinner.getEditor().setStyle("");
            }
        });

        availableBikesSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            int total = totalDocksSpinner.getValue();
            int docks = availableDocksSpinner.getValue();
            int charging = chargingBikesSpinner.getValue();

            if (newValue + docks + charging > total) {
                availableBikesSpinner.getEditor().setStyle("-fx-background-color: #ffcccc;");
            } else {
                availableBikesSpinner.getEditor().setStyle("");
            }
        });

        chargingBikesSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            int total = totalDocksSpinner.getValue();
            int docks = availableDocksSpinner.getValue();
            int available = availableBikesSpinner.getValue();

            if (newValue + docks + available > total) {
                chargingBikesSpinner.getEditor().setStyle("-fx-background-color: #ffcccc;");
            } else {
                chargingBikesSpinner.getEditor().setStyle("");
            }
        });

        totalDocksSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            int docks = availableDocksSpinner.getValue();
            int available = availableBikesSpinner.getValue();
            int charging = chargingBikesSpinner.getValue();

            if (docks + available + charging > newValue) {
                totalDocksSpinner.getEditor().setStyle("-fx-background-color: #ffcccc;");
            } else {
                totalDocksSpinner.getEditor().setStyle("");
            }
        });

        dialog.getDialogPane().setContent(grid);

        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!validateInput(
                        nameField.getText(),
                        locationComboBox.getValue(),
                        totalDocksSpinner.getValue(),
                        availableDocksSpinner.getValue(),
                        availableBikesSpinner.getValue(),
                        chargingBikesSpinner.getValue(),
                        statusComboBox.getValue()
                )) {
                    return null;
                }

                try {
                    if (isEdit) {
                        station.setName(nameField.getText());
                        station.setLocation(locationComboBox.getValue());
                        station.setTotal_docks(totalDocksSpinner.getValue());
                        station.setAvailable_docks(availableDocksSpinner.getValue());
                        station.setAvailable_bikes(availableBikesSpinner.getValue());
                        station.setCharging_bikes(chargingBikesSpinner.getValue());
                        station.setStatus(statusComboBox.getValue());
                        return station;
                    } else {
                        return new Station(
                                -1,
                                nameField.getText(),
                                locationComboBox.getValue(),
                                totalDocksSpinner.getValue(),
                                availableDocksSpinner.getValue(),
                                availableBikesSpinner.getValue(),
                                chargingBikesSpinner.getValue(),
                                statusComboBox.getValue()
                        );
                    }
                } catch (Exception e) {
                    showError("Input Error", "Error creating station: " + e.getMessage());
                }
            }
            return null;
        });

        // Show dialog and process result
        Optional<Station> result = dialog.showAndWait();
        result.ifPresent(resultStation -> {
            try {
                if (isEdit) {
                    stationService.update(resultStation);
                    showInfo("Success", "Station updated successfully");
                } else {
                    stationService.create(resultStation);
                    showInfo("Success", "Station added successfully");
                }
                loadStations();
            } catch (Exception e) {
                showError("Save Error", "Error saving station: " + e.getMessage());
            }
        });
    }

    private boolean validateInput(String name, Location location, Integer totalDocks, Integer availableDocks,
                                  Integer availableBikes, Integer chargingBikes, Station.STATUS status) {
        if (name == null || name.isEmpty()) {
            showWarning("Invalid Input", "Name field cannot be empty");
            return false;
        }
        if (location == null) {
            showWarning("Invalid Input", "Please select a location");
            return false;
        }
        if (status == null) {
            showWarning("Invalid Input", "Please select a status");
            return false;
        }
        if (totalDocks < 0 || availableDocks < 0 || availableBikes < 0 || chargingBikes < 0) {
            showWarning("Invalid Input", "Numeric values cannot be negative");
            return false;
        }
        if (availableDocks + availableBikes + chargingBikes > totalDocks) {
            showWarning("Invalid Input", "Sum of available docks, available bikes, and charging bikes cannot exceed total docks");
            return false;
        }
        return true;
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/modern-dialog.css").toExternalForm());
        } catch (Exception e) {
            // If stylesheet not found, continue without it
        }
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/modern-dialog.css").toExternalForm());
        } catch (Exception e) {
            // If stylesheet not found, continue without it
        }
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/modern-dialog.css").toExternalForm());
        } catch (Exception e) {
            // If stylesheet not found, continue without it
        }
        alert.showAndWait();
    }

    //PDF
    private void exportToPdf() {
        try {
            // Choose save location
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save PDF File");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("stations_report.pdf");
            java.io.File file = fileChooser.showSaveDialog(stationTableView.getScene().getWindow());

            if (file == null) {
                return; // User cancelled the operation
            }

            // Show loading indicator
            loadingIndicator.setVisible(true);
            updateStatusLabel("Generating PDF...");

            // Get current table data
            List<Station> stations = new StationService().read();

            // Generate PDF asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    // Create document
                    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                    com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
                    document.open();

                    // Add title
                    com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            18,
                            com.itextpdf.text.Font.BOLD);
                    com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Bicycle Stations Report", titleFont);
                    title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    title.setSpacingAfter(20);
                    document.add(title);

                    // Add timestamp
                    com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            12,
                            com.itextpdf.text.Font.NORMAL);
                    document.add(new com.itextpdf.text.Paragraph(
                            "Generated: " + java.time.LocalDateTime.now().format(
                                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            normalFont));
                    document.add(new com.itextpdf.text.Paragraph("\n"));

                    // Create table
                    com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(7);
                    table.setWidthPercentage(100);

                    // Set relative column widths
                    table.setWidths(new float[]{2.5f, 3.5f, 1.2f, 1.5f, 1.5f, 1.2f, 1.5f});

                    // Add table headers
                    com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            12,
                            com.itextpdf.text.Font.BOLD);
                    String[] headers = {"Name", "Location", "Total Docks", "Available Docks",
                            "Available Bikes", "Charging", "Status"};

                    for (String header : headers) {
                        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                                new com.itextpdf.text.Phrase(header, headerFont));
                        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        cell.setBackgroundColor(new com.itextpdf.text.BaseColor(240, 240, 240));
                        cell.setPadding(5);
                        table.addCell(cell);
                    }

                    // Add table rows
                    for (Station station : stations) {
                        // Name cell
                        table.addCell(new com.itextpdf.text.Phrase(station.getName(), normalFont));

                        // Location cell
                        String location = station.getLocation() != null ?
                                station.getLocation().getAddress() : "N/A";
                        table.addCell(new com.itextpdf.text.Phrase(location, normalFont));

                        // Numeric cells centered
                        com.itextpdf.text.pdf.PdfPCell totalDocksCell = new com.itextpdf.text.pdf.PdfPCell(
                                new com.itextpdf.text.Phrase(String.valueOf(station.getTotal_docks()), normalFont));
                        totalDocksCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        table.addCell(totalDocksCell);

                        com.itextpdf.text.pdf.PdfPCell availableDocksCell = new com.itextpdf.text.pdf.PdfPCell(
                                new com.itextpdf.text.Phrase(String.valueOf(station.getAvailable_docks()), normalFont));
                        availableDocksCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        table.addCell(availableDocksCell);

                        com.itextpdf.text.pdf.PdfPCell availableBikesCell = new com.itextpdf.text.pdf.PdfPCell(
                                new com.itextpdf.text.Phrase(String.valueOf(station.getAvailable_bikes()), normalFont));
                        availableBikesCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        table.addCell(availableBikesCell);

                        com.itextpdf.text.pdf.PdfPCell chargingBikesCell = new com.itextpdf.text.pdf.PdfPCell(
                                new com.itextpdf.text.Phrase(String.valueOf(station.getCharging_bikes()), normalFont));
                        chargingBikesCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        table.addCell(chargingBikesCell);

                        // Status cell with colors
                        com.itextpdf.text.pdf.PdfPCell statusCell = new com.itextpdf.text.pdf.PdfPCell();
                        com.itextpdf.text.Font statusFont = new com.itextpdf.text.Font(
                                com.itextpdf.text.Font.FontFamily.HELVETICA,
                                12,
                                com.itextpdf.text.Font.BOLD);

                        switch (station.getStatus()) {
                            case active:
                                statusFont.setColor(new com.itextpdf.text.BaseColor(0, 128, 0)); // Green
                                break;
                            case inactive:
                                statusFont.setColor(new com.itextpdf.text.BaseColor(220, 0, 0)); // Red
                                break;
                            case maintenance:
                                statusFont.setColor(new com.itextpdf.text.BaseColor(255, 165, 0)); // Orange
                                break;
                            case disabled:
                                statusFont.setColor(new com.itextpdf.text.BaseColor(128, 128, 128)); // Gray
                                break;
                        }

                        statusCell.setPhrase(new com.itextpdf.text.Phrase(
                                station.getStatus().toString(), statusFont));
                        statusCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        table.addCell(statusCell);
                    }

                    document.add(table);

                    // Add summary section
                    document.add(new com.itextpdf.text.Paragraph("\n"));
                    com.itextpdf.text.Font summaryFont = new com.itextpdf.text.Font(
                            com.itextpdf.text.Font.FontFamily.HELVETICA,
                            14,
                            com.itextpdf.text.Font.BOLD);
                    document.add(new com.itextpdf.text.Paragraph("Summary", summaryFont));
                    document.add(new com.itextpdf.text.Paragraph("\n"));

                    // Calculate summary data
                    int totalStations = stations.size();
                    long activeStations = stations.stream()
                            .filter(s -> s.getStatus() == Station.STATUS.active)
                            .count();
                    int totalBikes = stations.stream()
                            .mapToInt(s -> s.getAvailable_bikes() + s.getCharging_bikes())
                            .sum();
                    int totalDocks = stations.stream()
                            .mapToInt(Station::getTotal_docks)
                            .sum();

                    // Add summary data
                    document.add(new com.itextpdf.text.Paragraph(
                            "Total Stations: " + totalStations, normalFont));
                    document.add(new com.itextpdf.text.Paragraph(
                            "Active Stations: " + activeStations + " (" +
                                    String.format("%.1f%%", (double)activeStations/totalStations*100) + ")",
                            normalFont));
                    document.add(new com.itextpdf.text.Paragraph(
                            "Total Bikes Available: " + totalBikes, normalFont));
                    document.add(new com.itextpdf.text.Paragraph(
                            "Total Docking Capacity: " + totalDocks, normalFont));

                    document.close();

                    // Update UI on JavaFX thread
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        showInfo("Export Successful", "Stations data exported to PDF successfully.");
                        updateStatusLabel(stations.size() + " stations exported to PDF");
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        showError("Export Failed", "Failed to export PDF: " + e.getMessage());
                        updateStatusLabel("PDF export failed");
                    });
                }
            });

        } catch (Exception e) {
            loadingIndicator.setVisible(false);
            showError("Export Error", "Error exporting to PDF: " + e.getMessage());
        }
    }

}