package controllers.station.back;

import entities.Location;
import entities.Station;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import services.LocationService;
import services.StationService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private ScrollPane stationsScrollPane;
    @FXML
    private FlowPane stationsFlowPane;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label statusLabel;
    @FXML
    private Button exportToPdfButton;
    @FXML
    private WebView mapWebView;
    @FXML
    private SplitPane contentSplitPane;
    @FXML
    private Button toggleMapButton;
    @FXML
    private Label mapCoordinatesLabel;

    private StationService stationService;
    private LocationService locationService;
    private Set<Station> selectedStations = new HashSet<>();
    private WebEngine webEngine;
    private boolean isMapVisible = true;
    private double lastClickedLat;
    private double lastClickedLng;
    private final String MAP_HTML_RESOURCE = "/map.html";
    private File tempMapHtmlFile;

    @FXML
    public void initialize() {
        // Initialize services
        stationService = new StationService();
        locationService = new LocationService();

        // Setup button actions
        addButton.setOnAction(event -> addStation());
        deleteButton.setOnAction(event -> deleteStations());
        refreshButton.setOnAction(event -> loadStations());
        exportToPdfButton.setOnAction(event -> exportToPdf());

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterStations(newValue);
        });

        // Configure FlowPane
        stationsFlowPane.prefWidthProperty().bind(stationsScrollPane.widthProperty().subtract(20));
        initializeWebView();
        // Initial load
        loadStations();
    }
    private void initializeWebView() {
        webEngine = mapWebView.getEngine();

        try {
            createTempMapHtmlFile();
            webEngine.load(tempMapHtmlFile.toURI().toString());

            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    // Expose this Java controller to JavaScript
                    window.setMember("stationApp", this);
                    loadStationsOnMap();
                }
            });
        } catch (IOException e) {
            showError("Map Error", "Could not initialize map: " + e.getMessage());
        }
    }

    public void handleMapClick(double lat, double lng) {
        lastClickedLat = lat;
        lastClickedLng = lng;

        Platform.runLater(() -> {
            showCreateStationDialog(lat, lng);
        });
    }
    private void showCreateStationDialog(double lat, double lng) {
        // Create a dialog
        Dialog<Station> dialog = new Dialog<>();
        dialog.setTitle("Create New Station");
        dialog.setHeaderText("Create a new station at location: " +
                String.format("%.6f, %.6f", lat, lng));
        dialog.initStyle(StageStyle.UTILITY);

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField stationNameField = new TextField();
        stationNameField.setPromptText("Station Name");

        ComboBox<String> statusComboBox = new ComboBox<>(
                FXCollections.observableArrayList("Active", "Inactive", "Maintenance", "Disabled"));
        statusComboBox.setValue("Active");

        Spinner<Integer> capacitySpinner = new Spinner<>(1, 100, 10);
        capacitySpinner.setEditable(true);

        Spinner<Integer> availableBikesSpinner = new Spinner<>(0, 100, 5);
        availableBikesSpinner.setEditable(true);

        Spinner<Integer> chargingBikesSpinner = new Spinner<>(0, 100, 0);
        chargingBikesSpinner.setEditable(true);

        // Add fields to the grid
        grid.add(new Label("Station Name:"), 0, 0);
        grid.add(stationNameField, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusComboBox, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacitySpinner, 1, 2);
        grid.add(new Label("Available Bikes:"), 0, 3);
        grid.add(availableBikesSpinner, 1, 3);
        grid.add(new Label("Charging Bikes:"), 0, 4);
        grid.add(chargingBikesSpinner, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        Platform.runLater(stationNameField::requestFocus);

        // Validate inputs and convert to a station when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    // Validate input
                    if (stationNameField.getText().trim().isEmpty()) {
                        throw new IllegalArgumentException("Station name cannot be empty");
                    }

                    int capacity = capacitySpinner.getValue();
                    int availableBikes = availableBikesSpinner.getValue();
                    int chargingBikes = chargingBikesSpinner.getValue();

                    if (availableBikes + chargingBikes > capacity) {
                        throw new IllegalArgumentException("Total bikes cannot exceed capacity");
                    }

                    // Create location
                    Location location = new Location();
                    location.setAddress(stationNameField.getText());
                    location.setLatitude(lat);
                    location.setLongitude(lng);

                    // Create station
                    Station station = new Station();
                    station.setName("Station "+stationNameField.getText().trim());
                    station.setStatus(Station.STATUS.valueOf(statusComboBox.getValue().toLowerCase()));
                    station.setAvailable_bikes(availableBikes);
                    station.setCharging_bikes(chargingBikes);
                    station.setAvailable_docks(capacity - availableBikes - chargingBikes);
                    station.setLocation(location);

                    return station;
                } catch (IllegalArgumentException e) {
                    showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Station> result = dialog.showAndWait();
        result.ifPresent(station -> {
            try {
                // First save the location
                station.setLocation(locationService.createLocation(station.getLocation()));

                // Then save the station
                stationService.create(station);

                // Show temporary marker for the new station
                addTemporaryMarkerToMap(lat, lng);

                // Refresh the stations list
                loadStations();

                showInfo("Success", "Station created successfully!");
            } catch (SQLException e) {
                showError("Database Error", "Could not create station: " + e.getMessage());
            }
        });
    }

    private void addTemporaryMarkerToMap(double lat, double lng) {
        if (webEngine != null) {
            Platform.runLater(() -> {
                webEngine.executeScript("window.mapFunctions.addTemporaryMarker(" + lat + ", " + lng + ")");
                webEngine.executeScript("window.mapFunctions.centerMap(" + lat + ", " + lng + ", 15)");
            });
        }
    }
    private void loadStationsOnMap() {
        try {
            List<Station> stations = stationService.read();

            // Convert stations to JSON
            JSONArray stationsArray = new JSONArray();
            for (Station station : stations) {
                JSONObject stationObj = new JSONObject();
                stationObj.put("name", station.getName());
                stationObj.put("status", station.getStatus());
                stationObj.put("available_bikes", station.getAvailable_bikes());
                stationObj.put("available_docks", station.getAvailable_docks());
                stationObj.put("charging_bikes", station.getCharging_bikes());

                if (station.getLocation() != null) {
                    JSONObject locationObj = new JSONObject();
                    locationObj.put("latitude", station.getLocation().getLatitude());
                    locationObj.put("longitude", station.getLocation().getLongitude());
                    stationObj.put("location", locationObj);
                }

                stationsArray.put(stationObj);
            }

            // Add stations to map
            Platform.runLater(() -> {
                webEngine.executeScript("window.mapFunctions.addStations('" + stationsArray.toString().replace("'", "\\'") + "')");
            });
        } catch (SQLException e) {
            showError("Database Error", "Could not load stations on map: " + e.getMessage());
        }
    }
    private void createTempMapHtmlFile() throws IOException {
        // Read the map.html resource
        URL mapHtmlUrl = getClass().getResource(MAP_HTML_RESOURCE);
        if (mapHtmlUrl == null) {
            // Create the map.html file if it doesn't exist in resources
            tempMapHtmlFile = File.createTempFile("map", ".html");
            tempMapHtmlFile.deleteOnExit();

            // Write the map HTML content to the temp file
            try (FileWriter writer = new FileWriter(tempMapHtmlFile)) {
                writer.write(generateMapHtml());
            }
        } else {
            // If it's in resources, load directly
            webEngine.load(mapHtmlUrl.toString());
        }
    }
    private String generateMapHtml() {
        // This method generates the HTML content for the map if the resource isn't available
        // You can paste the entire HTML content from the map.html file we created earlier
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\" />\n" +
                "    <title>Bike Stations Map</title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    \n" +
                "    <!-- Leaflet CSS -->\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" \n" +
                "          integrity=\"sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=\" \n" +
                "          crossorigin=\"\" />\n" +
                "    \n" +
                "    <!-- Leaflet JavaScript -->\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\" \n" +
                "            integrity=\"sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=\" \n" +
                "            crossorigin=\"\"></script>\n" +
                "    \n" +
                "    <style>\n" +
                "        html, body {\n" +
                "            height: 100%;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        #map {\n" +
                "            width: 100%;\n" +
                "            height: 100vh;\n" +
                "        }\n" +
                "        .station-icon {\n" +
                "            background-color: #3388ff;\n" +
                "            border-radius: 50%;\n" +
                "            border: 2px solid white;\n" +
                "            text-align: center;\n" +
                "            color: white;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .active-station {\n" +
                "            background-color: #00b300;\n" +
                "        }\n" +
                "        .inactive-station {\n" +
                "            background-color: #ff3333;\n" +
                "        }\n" +
                "        .maintenance-station {\n" +
                "            background-color: #ff9900;\n" +
                "        }\n" +
                "        .disabled-station {\n" +
                "            background-color: #808080;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    \n" +
                "    <script>\n" +
                "        // Initialize the map\n" +
                "        const map = L.map('map').setView([36.8065, 10.1815], 13); // Default to Tunisia's coordinates\n" +
                "        \n" +
                "        // Add the OpenStreetMap tiles\n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            maxZoom: 19,\n" +
                "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                "        }).addTo(map);\n" +
                "        \n" +
                "        // Create a markers layer group\n" +
                "        const markersLayer = L.layerGroup().addTo(map);\n" +
                "        \n" +
                "        // Handle map click event\n" +
                "        map.on('click', function(e) {\n" +
                "            // Send the coordinates to the JavaFX application\n" +
                "            if (window.stationApp) {\n" +
                "                window.stationApp.handleMapClick(e.latlng.lat, e.latlng.lng);\n" +
                "            }\n" +
                "            \n" +
                "            // Update coordinates display\n" +
                "            if (window.stationApp) {\n" +
                "                window.stationApp.updateCoordinates(e.latlng.lat, e.latlng.lng);\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        // Mouse move to update coordinates\n" +
                "        map.on('mousemove', function(e) {\n" +
                "            if (window.stationApp) {\n" +
                "                window.stationApp.updateCoordinates(e.latlng.lat, e.latlng.lng);\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        // Function to add station markers to the map\n" +
                "        function addStations(stationsJson) {\n" +
                "            // Clear existing markers\n" +
                "            markersLayer.clearLayers();\n" +
                "            \n" +
                "            try {\n" +
                "                const stations = JSON.parse(stationsJson);\n" +
                "                \n" +
                "                stations.forEach(station => {\n" +
                "                    if (station.location && station.location.latitude && station.location.longitude) {\n" +
                "                        // Create custom icon based on station status\n" +
                "                        const iconClass = getStationStatusClass(station.status);\n" +
                "                        const icon = L.divIcon({\n" +
                "                            className: `station-icon ${iconClass}`,\n" +
                "                            html: '<div style=\"width: 100%; height: 100%;\">' + \n" +
                "                                  (station.available_bikes + station.charging_bikes) + \n" +
                "                                  '</div>',\n" +
                "                            iconSize: [30, 30]\n" +
                "                        });\n" +
                "                        \n" +
                "                        // Create marker\n" +
                "                        const marker = L.marker([station.location.latitude, station.location.longitude], {\n" +
                "                            icon: icon,\n" +
                "                            title: station.name\n" +
                "                        }).addTo(markersLayer);\n" +
                "                        \n" +
                "                        // Add popup\n" +
                "                        let popupContent = `\n" +
                "                            <div class=\"station-info\">\n" +
                "                                <h3>${station.name}</h3>\n" +
                "                                <p><strong>Status:</strong> ${station.status}</p>\n" +
                "                                <p><strong>Available Bikes:</strong> ${station.available_bikes}</p>\n" +
                "                                <p><strong>Available Docks:</strong> ${station.available_docks}</p>\n" +
                "                                <p><strong>Charging Bikes:</strong> ${station.charging_bikes}</p>\n" +
                "                            </div>\n" +
                "                        `;\n" +
                "                        \n" +
                "                        marker.bindPopup(popupContent);\n" +
                "                    }\n" +
                "                });\n" +
                "            } catch (error) {\n" +
                "                console.error(\"Error parsing stations JSON:\", error);\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // Helper function to get station status class\n" +
                "        function getStationStatusClass(status) {\n" +
                "            switch(status.toLowerCase()) {\n" +
                "                case 'active': return 'active-station';\n" +
                "                case 'inactive': return 'inactive-station';\n" +
                "                case 'maintenance': return 'maintenance-station';\n" +
                "                case 'disabled': return 'disabled-station';\n" +
                "                default: return '';\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        // Function to add a temporary marker at a specific location\n" +
                "        function addTemporaryMarker(lat, lng) {\n" +
                "            // Clear any existing temporary markers\n" +
                "            markersLayer.clearLayers();\n" +
                "            \n" +
                "            // Create a marker at the clicked location\n" +
                "            const marker = L.marker([lat, lng], {\n" +
                "                draggable: true,\n" +
                "                title: \"New Station Location\"\n" +
                "            }).addTo(markersLayer);\n" +
                "            \n" +
                "            // Add popup to the marker\n" +
                "            marker.bindPopup(\"<b>New Station Location</b><br>Drag to adjust.\").openPopup();\n" +
                "            \n" +
                "            // Handle drag end to update coordinates\n" +
                "            marker.on('dragend', function(e) {\n" +
                "                if (window.stationApp) {\n" +
                "                    window.stationApp.updateCoordinates(marker.getLatLng().lat, marker.getLatLng().lng);\n" +
                "                }\n" +
                "            });\n" +
                "            \n" +
                "            return marker;\n" +
                "        }\n" +
                "        \n" +
                "        // Function to center the map on specific coordinates\n" +
                "        function centerMap(lat, lng, zoom) {\n" +
                "            map.setView([lat, lng], zoom || 15);\n" +
                "        }\n" +
                "        \n" +
                "        // Expose functions for JavaFX to call\n" +
                "        window.mapFunctions = {\n" +
                "            addStations: addStations,\n" +
                "            addTemporaryMarker: addTemporaryMarker,\n" +
                "            centerMap: centerMap\n" +
                "        };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
    private void toggleMapVisibility() {
        isMapVisible = !isMapVisible;

        if (isMapVisible) {
            // Show map
            if (contentSplitPane.getItems().size() == 1) {
                contentSplitPane.getItems().add(mapWebView);
                contentSplitPane.setDividerPositions(0.6);
            }
            toggleMapButton.setText("Hide Map");
        } else {
            // Hide map
            if (contentSplitPane.getItems().size() > 1) {
                contentSplitPane.getItems().remove(mapWebView);
            }
            toggleMapButton.setText("Show Map");
        }
    }
    private VBox createStationCard(Station station) {
        // Main card container
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPrefHeight(300);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("station-card");

        // Card selection state
        BooleanProperty isSelected = new SimpleBooleanProperty(false);

        // Update background based on selection state
        isSelected.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                card.getStyleClass().add("station-card-selected");
                selectedStations.add(station);
            } else {
                card.getStyleClass().remove("station-card-selected");
                selectedStations.remove(station);
            }
        });

        // Handle card selection
        card.setOnMouseClicked(event -> {
            if (event.isControlDown()) {
                // Toggle selection with Ctrl key
                isSelected.set(!isSelected.get());
            } else if (event.getClickCount() == 2) {
                // Double-click to edit
                updateStation(station);
            } else {
                // Single click selects only this card
                clearAllSelections();
                isSelected.set(true);
            }
        });

        // Station header with name
        Label nameLabel = new Label(station.getName());
        nameLabel.getStyleClass().add("station-card-title");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        nameLabel.setAlignment(Pos.CENTER);

        // Location
        Label locationLabel = new Label(station.getLocation() != null ? station.getLocation().getAddress() : "No location");
        locationLabel.getStyleClass().add("station-card-location");
        locationLabel.setWrapText(true);

        // Status indicator
        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER);

        Region statusIndicator = new Region();
        statusIndicator.setPrefSize(12, 12);
        statusIndicator.setMinSize(12, 12);
        statusIndicator.setMaxSize(12, 12);

        Label statusLabel = new Label(station.getStatus().toString());
        statusLabel.getStyleClass().add("station-card-status");

        // Set status color
        switch (station.getStatus()) {
            case active:
                statusIndicator.setStyle("-fx-background-color: green; -fx-background-radius: 6;");
                statusLabel.setTextFill(Color.GREEN);
                break;
            case inactive:
                statusIndicator.setStyle("-fx-background-color: red; -fx-background-radius: 6;");
                statusLabel.setTextFill(Color.RED);
                break;
            case maintenance:
                statusIndicator.setStyle("-fx-background-color: orange; -fx-background-radius: 6;");
                statusLabel.setTextFill(Color.ORANGE);
                break;
            case disabled:
                statusIndicator.setStyle("-fx-background-color: gray; -fx-background-radius: 6;");
                statusLabel.setTextFill(Color.GRAY);
                break;
        }

        statusBox.getChildren().addAll(statusIndicator, statusLabel);

        // Station metrics
        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(10);
        metricsGrid.setVgap(8);
        metricsGrid.setPadding(new Insets(15, 0, 15, 0));

        // Add metrics labels
        addMetric(metricsGrid, 0, "Total Docks:", String.valueOf(station.getTotal_docks()));
        addMetric(metricsGrid, 1, "Available Docks:", String.valueOf(station.getAvailable_docks()));
        addMetric(metricsGrid, 2, "Available Bikes:", String.valueOf(station.getAvailable_bikes()));
        addMetric(metricsGrid, 3, "Charging Bikes:", String.valueOf(station.getCharging_bikes()));

        // Visual indicator of dock/bike capacity
        ProgressBar capacityBar = new ProgressBar();
        capacityBar.setPrefWidth(Double.MAX_VALUE);
        double availableRatio = (double) (station.getAvailable_bikes() + station.getCharging_bikes()) / station.getTotal_docks();
        capacityBar.setProgress(availableRatio);

        // Style the capacity bar based on availability
        if (availableRatio > 0.7) {
            capacityBar.getStyleClass().add("capacity-high");
        } else if (availableRatio > 0.3) {
            capacityBar.getStyleClass().add("capacity-medium");
        } else {
            capacityBar.getStyleClass().add("capacity-low");
        }

        Label capacityLabel = new Label("Bike Availability");
        capacityLabel.getStyleClass().add("capacity-label");

        // Action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);

        Button editButton = new Button();
        editButton.getStyleClass().add("card-edit-button");
        try {
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/station/icons/edit.png")));
            editIcon.setFitHeight(16);
            editIcon.setFitWidth(16);
            editButton.setGraphic(editIcon);
        } catch (Exception e) {
            editButton.setText("Edit");
        }
        editButton.setTooltip(new Tooltip("Edit"));
        editButton.setOnAction(event -> {
            event.consume(); // Prevent event bubbling to the card
            updateStation(station);
        });

        Button deleteButton = new Button();
        deleteButton.getStyleClass().add("card-delete-button");
        try {
            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/station/icons/delete.png")));
            deleteIcon.setFitHeight(16);
            deleteIcon.setFitWidth(16);
            deleteButton.setGraphic(deleteIcon);
        } catch (Exception e) {
            deleteButton.setText("Delete");
        }
        deleteButton.setTooltip(new Tooltip("Delete"));
        deleteButton.setOnAction(event -> {
            event.consume(); // Prevent event bubbling to the card
            deleteSingleStation(station);
        });

        actionButtons.getChildren().addAll(editButton, deleteButton);

        // Add all components to card
        card.getChildren().addAll(
                nameLabel,
                statusBox,
                new Separator(),
                locationLabel,
                metricsGrid,
                capacityLabel,
                capacityBar,
                new Separator(),
                actionButtons
        );

        return card;
    } private void addMetric(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("metric-label");

        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("metric-value");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
    private void clearAllSelections() {
        selectedStations.clear();

        for (javafx.scene.Node node : stationsFlowPane.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                card.getStyleClass().remove("station-card-selected");
            }
        }
    }

    private void filterStations(String searchText) {
        try {
            // If search text is empty, just load all stations
            if (searchText == null || searchText.isEmpty()) {
                loadStations();
                return;
            }

            // Show loading indicator
            loadingIndicator.setVisible(true);
            updateStatusLabel("Searching stations...");

            CompletableFuture.supplyAsync(() -> {
                try {
                    List<Station> searchResults = new ArrayList<>();

                    // First try to search by name
                    searchResults = stationService.search("name", searchText);

                    // If status is mentioned, search by status instead
                    if (searchText.equalsIgnoreCase("active") ||
                            searchText.equalsIgnoreCase("inactive") ||
                            searchText.equalsIgnoreCase("maintenance") ||
                            searchText.equalsIgnoreCase("disabled")) {
                        searchResults = stationService.search("status", searchText);
                    }

                    return searchResults;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).thenAccept(stations -> {
                Platform.runLater(() -> {
                    try {
                        // Clear existing content
                        stationsFlowPane.getChildren().clear();
                        selectedStations.clear();

                        if (stations != null && !stations.isEmpty()) {
                            // Create station cards
                            for (Station station : stations) {
                                VBox stationCard = createStationCard(station);
                                stationsFlowPane.getChildren().add(stationCard);
                            }
                            updateStatusLabel(stations.size() + " stations found");
                        } else {
                            updateStatusLabel("No stations found");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Search Error", "Error searching stations: " + e.getMessage());
                    } finally {
                        loadingIndicator.setVisible(false);
                    }
                });
            }).exceptionally(e -> {
                Platform.runLater(() -> {
                    e.printStackTrace();
                    showError("Search Error", "Error searching stations: " + e.getMessage());
                    loadingIndicator.setVisible(false);
                });
                return null;
            });

        } catch (Exception e) {
            showError("Filter Error", "Error filtering stations: " + e.getMessage());
        }
    }
    private void updateStatusLabel(String message) {
        statusLabel.setText(message);
    }

    private void loadStations() {
        loadingIndicator.setVisible(true);
        statusLabel.setText("Loading stations...");

        CompletableFuture.runAsync(() -> {
            try {
                List<Station> stations = stationService.read();

                Platform.runLater(() -> {
                    // Clear existing stations
                    stationsFlowPane.getChildren().clear();
                    selectedStations.clear();

                    // Add stations to the UI
                    for (Station station : stations) {
                        stationsFlowPane.getChildren().add(createStationCard(station));
                    }

                    // Load stations on map
                    loadStationsOnMap();

                    // Update UI
                    loadingIndicator.setVisible(false);
                    statusLabel.setText(stations.size() + " stations loaded");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    statusLabel.setText("Error loading stations");
                    showError("Database Error", "Could not load stations: " + e.getMessage());
                });
            }
        });
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
        statusComboBox.setValue(isEdit ? Station.STATUS.valueOf(station.getStatus().name()) : Station.STATUS.active);

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
            java.io.File file = fileChooser.showSaveDialog(stationsFlowPane.getScene().getWindow());

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