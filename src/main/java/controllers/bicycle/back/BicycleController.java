package controllers.bicycle.back;

import entities.Bicycle;
import entities.Station;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import services.BicycleService;
import services.StationService;
import utils.CsvExporter.CsvExporter;
import utils.PdfReportGenerator.PdfReportGenerator;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BicycleController {
    @FXML private Button batchUpdateButton, addButton, deleteButton, exportCsvButton, exportPdfButton;
    @FXML private Label totalBicyclesLabel, availableBicyclesLabel, lowBatteryLabel, maintenanceLabel;
    @FXML private ComboBox<Bicycle.STATUS> statusFilter;
    @FXML private ComboBox<Station> stationFilter;
    @FXML private TextField searchField;
    @FXML private FlowPane bicycleGridView;

    private final BicycleService bicycleService = new BicycleService();
    private final StationService stationService = new StationService();

    private ObservableList<Bicycle> masterBicycleList = FXCollections.observableArrayList();
    private FilteredList<Bicycle> filteredBicycleList;

    @FXML
    public void initialize() {
        setupGridView();
        setupFilters();
        setupButtons();
        setupContextMenu();
        setupTooltips();
        loadBicycles();
    }

    private void setupGridView() {
        bicycleGridView.getChildren().clear();
        bicycleGridView.getStyleClass().add("bicycle-grid");
    }

    private Node createBicycleCard(Bicycle bicycle) {
        HBox card = new HBox(15); // Add 15 pixels of spacing between sidebar and content
        card.setPadding(new Insets(15)); // Add 15 pixels of padding on all sides

        card.getStyleClass().addAll("bicycle-card", "card-with-sidebar",
                "border-" + bicycle.getStatus().toString().toLowerCase().replace("_", "-"));
        card.setStyle(
                "-fx-border-color: " + getStatusBorderColor(bicycle.getStatus()) + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-background-color: white;" // Optional: add a white background
        );

        // Colored sidebar based on status
        Region sidebar = new Region();
        sidebar.getStyleClass().addAll("card-sidebar",
                "sidebar-" + bicycle.getStatus().toString().toLowerCase().replace("_", "-"));

        VBox cardContent = new VBox(10);
        cardContent.getStyleClass().add("card-content");

        // Top section with header, status, and checkbox
        HBox topSection = new HBox(10);
        topSection.setAlignment(Pos.CENTER_LEFT);

        CheckBox selectCheckbox = new CheckBox();
        selectCheckbox.setUserData(bicycle);
        selectCheckbox.getStyleClass().add("selection-checkbox");

        Label idLabel = new Label("BICYCLE #" + bicycle.getId());
        idLabel.getStyleClass().add("card-title");

        Label statusBadge = new Label(bicycle.getStatus().toString().replace("_", " "));
        statusBadge.getStyleClass().addAll("status-chip",
                "status-" + bicycle.getStatus().toString().toLowerCase().replace("_", "-"));

        topSection.getChildren().addAll(selectCheckbox, idLabel, new Region(), statusBadge);
        HBox.setHgrow(topSection.getChildren().get(3), Priority.ALWAYS);

        // Battery and Station Section
        HBox infoSection = new HBox(20);
        infoSection.setAlignment(Pos.CENTER_LEFT);

        // Battery Graphic
        VBox batteryContainer = new VBox(5);
        batteryContainer.setAlignment(Pos.CENTER);
        batteryContainer.setMinWidth(80);
        Node batteryGraphic = createBatteryGraphic(bicycle.getBattery_level());
        Label batteryLabel = new Label(bicycle.getBattery_level() + "%");
        batteryLabel.getStyleClass().add("metric-value");
        batteryLabel.setStyle("-fx-text-fill: " + getBatteryColor(bicycle.getBattery_level()) + ";");
        batteryContainer.getChildren().addAll(batteryGraphic, batteryLabel);

        VBox stationContainer = new VBox(5);
        Label stationTitleLabel = new Label("Station");
        stationTitleLabel.getStyleClass().add("section-label");
        Label stationNameLabel = new Label(bicycle.getStation() != null ?
                bicycle.getStation().getName() : "Unassigned");
        stationNameLabel.getStyleClass().add("station-name");
        stationNameLabel.setWrapText(true);

// Add these lines to constrain the width
        stationContainer.setMaxWidth(Region.USE_PREF_SIZE);
        stationNameLabel.setMaxWidth(Double.MAX_VALUE);  // Allow label to expand
        stationContainer.getChildren().addAll(stationTitleLabel, stationNameLabel);
        HBox.setHgrow(stationContainer, Priority.ALWAYS);

// In the infoSection HBox:
        infoSection.setMaxWidth(Double.MAX_VALUE);  // Allow HBox to expand
        // Metrics Section
        GridPane metricsGrid = new GridPane();
        metricsGrid.getStyleClass().add("metrics-grid");
        metricsGrid.setHgap(20);
        metricsGrid.setVgap(10);

        metricsGrid.add(createMetricItem("Range",
                String.format("%.1f km", bicycle.getRange_km()),
                "route-icon.png"), 0, 0);

        // Action Buttons
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_RIGHT);
        actionBar.getStyleClass().add("action-bar");

        Button btnEdit = createImageButton("edit.png", "#4dabf7");
        btnEdit.setOnAction(e -> updateBicycle(bicycle));

        Button btnDelete = createImageButton("delete.png", "#ff6b6b");
        btnDelete.setOnAction(e -> deleteSingleBicycle(bicycle));

        actionBar.getChildren().addAll(btnEdit, btnDelete);

        // Clear any previous children to prevent duplication
        cardContent.getChildren().clear();
        cardContent.getChildren().addAll(topSection, new Separator(), infoSection, metricsGrid, actionBar);

        card.getChildren().addAll(sidebar, cardContent);
        card.setAlignment(Pos.CENTER_LEFT);


        setupCardInteractions(card, bicycle);

        return card;
    }

    private Button createImageButton(String iconName, String color) {
        Button btn = new Button();
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/station/icons/" + iconName)));
        icon.setFitHeight(18);
        icon.setFitWidth(18);
        btn.setGraphic(icon);
        btn.getStyleClass().add("icon-button");
        return btn;
    }

    private Node createMetricItem(String title, String value, String iconName) {
        VBox container = new VBox(6);
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("metric-item");

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/station/icons/" + iconName)));
        icon.setFitHeight(24);
        icon.setFitWidth(24);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-label");

        container.getChildren().addAll(icon, valueLabel, titleLabel);
        return container;
    }

    private void deleteSingleBicycle(Bicycle bicycle) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Bicycle");
        confirmation.setHeaderText("Delete Bicycle #" + bicycle.getId() + "?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bicycleService.delete(bicycle.getId());
                loadBicycles();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete bicycle: " + e.getMessage());
            }
        }
    }

    private String getStatusColor(Bicycle.STATUS status) {
        return switch (status) {
            case available -> "#38d9a9";
            case in_use -> "#ffd43b";
            case maintenance -> "#ff6b6b";
            case charging -> "#adb5bd";
            case reserved -> "#74c0fc";
        };
    }

    private String getBatteryColor(double level) {
        if (level < 15) return "#ff6b6b";  // Red
        if (level < 40) return "#ffd43b";  // Yellow
        return "#38d9a9";  // Green
    }

    private Node createBatteryGraphic(double level, String color) {
        StackPane fill = new StackPane();
        fill.setStyle("-fx-background-color: " + color + "; " +
                "-fx-background-insets: 2; " +
                "-fx-pref-height: " + level + "%;");
        return fill;
    }

    private void setupCardInteractions(HBox card, Bicycle bicycle) {
        // Find the checkbox in the card
        CheckBox checkbox = (CheckBox) ((HBox) ((VBox) card.getChildren().get(1)).getChildren().get(0)).getChildren().get(0);

        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                updateBicycle(bicycle);
            }
        });

        card.setOnMousePressed(e -> {
            if (e.getClickCount() == 1 && e.getButton() == MouseButton.PRIMARY) {
                checkbox.setSelected(!checkbox.isSelected());
            }
        });
    }

    private Node createStationInfo(Station station) {
        VBox stationInfo = new VBox(5);
        stationInfo.getStyleClass().add("station-info");

        Label stationLabel = new Label("Station");
        stationLabel.getStyleClass().add("section-label");

        Label stationName = new Label(station != null ? station.getName() : "No Station");
        stationName.getStyleClass().add("station-name");

        stationInfo.getChildren().addAll(stationLabel, stationName);
        return stationInfo;
    }

    private HBox createHeaderRow(Bicycle bicycle, Label statusBadge, Node batteryGraphic) {
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // ID and Status
        VBox idSection = new VBox(4);
        Label idLabel = new Label("BICYCLE #" + bicycle.getId());
        idLabel.getStyleClass().add("card-title");
        idSection.getChildren().addAll(idLabel, statusBadge);

        // Battery
        VBox batterySection = new VBox(5);
        Label batteryLabel = new Label(bicycle.getBattery_level() + "%");
        batteryLabel.setStyle("-fx-font-weight: 700; -fx-font-size: 14px;");
        batterySection.getChildren().addAll(batteryGraphic, batteryLabel);

        headerRow.getChildren().addAll(idSection, new Region(), batterySection);
        HBox.setHgrow(headerRow.getChildren().get(1), Priority.ALWAYS);
        return headerRow;
    }


    private Node createBatteryGraphic(double level) {
        VBox battery = new VBox();
        battery.getStyleClass().add("battery-graphic");

        StackPane fill = new StackPane();
        fill.getStyleClass().add("battery-fill");

        // Determine battery fill percentage classes
        if (level >= 80) fill.getStyleClass().add("battery-80");
        else if (level >= 60) fill.getStyleClass().add("battery-60");
        else if (level >= 40) fill.getStyleClass().add("battery-40");
        else if (level >= 20) fill.getStyleClass().add("battery-20");
        else fill.getStyleClass().add("battery-critical");

        fill.setStyle(String.format("-fx-background-insets: 2; -fx-pref-height: %.1f%%;", level));

        battery.getChildren().add(fill);
        return battery;
    }


    private Button createIconButton(String svgPath, String color) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web(color));

        Button btn = new Button();
        btn.setGraphic(icon);
        btn.getStyleClass().add("icon-button");
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 6;");
        btn.setCursor(Cursor.HAND);
        return btn;
    }

    private void setupButtons() {
        addButton.setOnAction(e -> showBicycleDialog("Add Bicycle", null));
        deleteButton.setOnAction(e -> deleteSelectedBicycles());
        batchUpdateButton.setOnAction(e -> batchUpdateBicycles());
        exportCsvButton.setOnAction(e -> exportToCsv());
        exportPdfButton.setOnAction(e -> exportToPdf());
    }

    private void setupContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit Bicycle");
        editItem.setOnAction(e -> {
            Bicycle selected = getSelectedBicycle();
            if (selected != null) updateBicycle(selected);
        });

        MenuItem deleteItem = new MenuItem("Delete Bicycle");
        deleteItem.setOnAction(e -> deleteSelectedBicycles());

        menu.getItems().addAll(editItem, deleteItem);

        // Add context menu to each bicycle card
        bicycleGridView.getChildren().forEach(node -> {
            node.setOnContextMenuRequested(event -> {
                VBox card = (VBox) node;
                HBox topContainer = (HBox) ((VBox) card.getChildren().get(0)).getChildren().get(0);
                CheckBox checkbox = (CheckBox) topContainer.getChildren().get(0);
                checkbox.setSelected(true);
                menu.show(node, event.getScreenX(), event.getScreenY());
            });
        });
    }

    private Bicycle getSelectedBicycle() {
        Bicycle selected = null;
        for (Node node : bicycleGridView.getChildren()) {
            HBox card = (HBox) node;
            VBox cardContent = (VBox) card.getChildren().get(1);
            HBox topSection = (HBox) cardContent.getChildren().get(0);
            CheckBox checkbox = (CheckBox) topSection.getChildren().get(0);
            if (checkbox.isSelected()) {
                selected = (Bicycle) checkbox.getUserData();
                break;
            }
        }
        return selected;

    }


    private void setupTooltips() {
        Tooltip.install(lowBatteryLabel, new Tooltip("Batteries below 30%"));
        Tooltip.install(maintenanceLabel, new Tooltip("Bicycles in maintenance mode"));
    }

    private void loadBicycles() {
        try {
            bicycleGridView.getChildren().clear();
            masterBicycleList.setAll(bicycleService.read());

            masterBicycleList.forEach(bicycle ->
                    bicycleGridView.getChildren().add(createBicycleCard(bicycle))
            );

            setupContextMenu(); // Reapply context menu to new cards
            applyFilters();
        } catch (Exception e) {
            showAlert("Error", "Failed to load bicycles: " + e.getMessage());
        }
    }

    private void setupFilters() {
        statusFilter.getItems().addAll(Bicycle.STATUS.values());
        statusFilter.getItems().add(0, null);

        try {
            List<Station> stations = stationService.read();
            stationFilter.getItems().add(null);
            stationFilter.getItems().addAll(stations);
            stationFilter.setConverter(createStationConverter());
        } catch (Exception e) {
            showAlert("Error", "Failed to load stations: " + e.getMessage());
        }

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        stationFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private StringConverter<Station> createStationConverter() {
        return new StringConverter<Station>() {
            @Override
            public String toString(Station station) {
                return station != null ? station.getName() : "All Stations";
            }

            @Override
            public Station fromString(String name) {
                return stationFilter.getItems().stream()
                        .filter(s -> s != null && s.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
            }
        };
    }

    private void applyFilters() {
        bicycleGridView.getChildren().clear(); // Clear first to avoid duplicates
        bicycleGridView.getChildren().addAll(masterBicycleList
                .stream()
                .filter(bicycle ->
                        matchesSearch(bicycle) &&
                                matchesStatus(bicycle) &&
                                matchesStation(bicycle)
                )
                .map(this::createBicycleCard)
                .collect(Collectors.toList())
        );

        updateStatistics(); // Update stats based on filtered list
    }

    private boolean matchesSearch(Bicycle bicycle) {
        String searchText = searchField.getText().trim().toLowerCase();
        return searchText.isEmpty() ||
                String.valueOf(bicycle.getId()).contains(searchText) ||
                (bicycle.getStation() != null &&
                        bicycle.getStation().getName().toLowerCase().contains(searchText)) ||
                bicycle.getStatus().toString().toLowerCase().contains(searchText);
    }

    private boolean matchesStatus(Bicycle bicycle) {
        Bicycle.STATUS selectedStatus = statusFilter.getValue();
        return selectedStatus == null || bicycle.getStatus() == selectedStatus;
    }

    private boolean matchesStation(Bicycle bicycle) {
        Station selectedStation = stationFilter.getValue();
        return selectedStation == null ||
                (bicycle.getStation() != null &&
                        bicycle.getStation().getId() == selectedStation.getId());
    }

    private void updateStatistics() {
        long total = bicycleGridView.getChildren().size();
        long available = masterBicycleList.stream()
                .filter(b -> b.getStatus() == Bicycle.STATUS.available)
                .count();
        long lowBattery = masterBicycleList.stream()
                .filter(b -> b.getBattery_level() < 30)
                .count();
        long maintenance = masterBicycleList.stream()
                .filter(b -> b.getStatus() == Bicycle.STATUS.maintenance)
                .count();

        totalBicyclesLabel.setText("Total: " + total);
        availableBicyclesLabel.setText("Available: " + available);
        lowBatteryLabel.setText("Low Battery: " + lowBattery);
        maintenanceLabel.setText("Maintenance: " + maintenance);
    }

    private long countFilteredBicycles(ObservableList<Node> filteredCards,
                                       Predicate<Bicycle> condition) {
        return filteredCards.stream()
                .map(node -> {
                    HBox card = (HBox) node; // Updated to HBox
                    CheckBox checkbox = (CheckBox) card.getChildren().get(0);
                    return (Bicycle) checkbox.getUserData();
                })
                .filter(condition)
                .count();
    }

    private List<Bicycle> getSelectedBicycles() {
        return bicycleGridView.getChildren().stream()
                .filter(node -> {
                    if (node instanceof HBox card) {
                        VBox cardContent = (VBox) card.getChildren().get(1);
                        HBox topSection = (HBox) cardContent.getChildren().get(0);
                        CheckBox checkbox = (CheckBox) topSection.getChildren().get(0);
                        return checkbox.isSelected();
                    }
                    return false;
                })
                .map(node -> {
                    HBox card = (HBox) node;
                    VBox cardContent = (VBox) card.getChildren().get(1);
                    HBox topSection = (HBox) cardContent.getChildren().get(0);
                    CheckBox checkbox = (CheckBox) topSection.getChildren().get(0);
                    return (Bicycle) checkbox.getUserData();
                })
                .collect(Collectors.toList());
    }
    





    private void deleteSelectedBicycles() {
        List<Bicycle> selectedBicycles = getSelectedBicycles();

        if (selectedBicycles.isEmpty()) {
            showAlert("No Selection", "Please select bicycles to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Bicycles");
        confirmation.setHeaderText("Delete " + selectedBicycles.size() + " bicycles?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bicycleService.batchDelete(
                        selectedBicycles.stream()
                                .map(Bicycle::getId)
                                .collect(Collectors.toList())
                );
                loadBicycles();
            } catch (Exception e) {
                showAlert("Error", "Failed to delete: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateBicycle(Bicycle bicycle) {
        showBicycleDialog("Update Bicycle", bicycle);
    }

    private void batchUpdateBicycles() {
        List<Bicycle> selectedBicycles = getSelectedBicycles();

        if (selectedBicycles.isEmpty()) {
            showAlert("No Selection", "Please select bicycles to update.");
            return;
        }

        Dialog<BatchUpdateData> dialog = new Dialog<>();
        dialog.setTitle("Batch Update Bicycles");
        dialog.setHeaderText("Update " + selectedBicycles.size() + " Bicycles");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        CheckBox updateStatusCheck = new CheckBox("Update Status");
        ComboBox<Bicycle.STATUS> newStatusCombo = new ComboBox<>();
        newStatusCombo.getItems().addAll(Bicycle.STATUS.values());
        newStatusCombo.setDisable(true);

        CheckBox updateStationCheck = new CheckBox("Update Station");
        ComboBox<Station> newStationCombo = new ComboBox<>();
        try {
            newStationCombo.getItems().addAll(stationService.read());
            newStationCombo.setConverter(createStationConverter());
            newStationCombo.setDisable(true);
        } catch (Exception e) {
            showAlert("Error", "Failed to load stations: " + e.getMessage());
        }

        updateStatusCheck.selectedProperty().addListener((obs, oldVal, newVal) ->
                newStatusCombo.setDisable(!newVal));
        updateStationCheck.selectedProperty().addListener((obs, oldVal, newVal) ->
                newStationCombo.setDisable(!newVal));

        grid.add(updateStatusCheck, 0, 0);
        grid.add(newStatusCombo, 1, 0);
        grid.add(updateStationCheck, 0, 1);
        grid.add(newStationCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new BatchUpdateData(
                        updateStatusCheck.isSelected() ? newStatusCombo.getValue() : null,
                        updateStationCheck.isSelected() ? newStationCombo.getValue() : null
                );
            }
            return null;
        });

        Optional<BatchUpdateData> result = dialog.showAndWait();
        result.ifPresent(updateData -> {
            try {
                List<Bicycle> updatedBicycles = selectedBicycles.stream()
                        .map(bicycle -> {
                            if (updateData.newStatus != null) {
                                bicycle.setStatus(updateData.newStatus);
                            }
                            if (updateData.newStation != null) {
                                bicycle.setStation(updateData.newStation);
                            }
                            bicycle.setLast_updated(new Timestamp(System.currentTimeMillis()));
                            return bicycle;
                        })
                        .collect(Collectors.toList());

                bicycleService.batchUpdate(updatedBicycles);
                loadBicycles(); // Refresh the grid
            } catch (Exception e) {
                showAlert("Error", "Failed to update bicycles: " + e.getMessage());
            }
        });
    }

    private void showBicycleDialog(String title, Bicycle existingBicycle) {
        Dialog<Bicycle> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title + " Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<Station> stationComboBox = new ComboBox<>();
        ComboBox<Bicycle.STATUS> statusComboBox = new ComboBox<>();
        TextField batteryField = new TextField();
        TextField rangeField = new TextField();

        try {
            List<Station> stations = stationService.read();
            stationComboBox.getItems().addAll(stations);
            stationComboBox.setConverter(new StringConverter<Station>() {
                @Override
                public String toString(Station station) {
                    return station != null ? station.getName() : "";
                }

                @Override
                public Station fromString(String string) {
                    return stations.stream()
                            .filter(s -> s.getName().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
        } catch (Exception e) {
            showAlert("Error", "Failed to load stations: " + e.getMessage());
        }

        statusComboBox.getItems().addAll(Bicycle.STATUS.values());

        if (existingBicycle != null) {
            stationComboBox.setValue(existingBicycle.getStation());
            statusComboBox.setValue(existingBicycle.getStatus());
            batteryField.setText(String.valueOf(existingBicycle.getBattery_level()));
            rangeField.setText(String.valueOf(existingBicycle.getRange_km()));
        }

        grid.add(new Label("Station:"), 0, 0);
        grid.add(stationComboBox, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusComboBox, 1, 1);
        grid.add(new Label("Battery Level (%):"), 0, 2);
        grid.add(batteryField, 1, 2);
        grid.add(new Label("Range (km):"), 0, 3);
        grid.add(rangeField, 1, 3);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable validateInputs = () -> {
            boolean isValid = true;

            if (stationComboBox.getValue() == null || statusComboBox.getValue() == null) {
                isValid = false;
            }

            try {
                float battery = Float.parseFloat(batteryField.getText());
                if (battery < 0 || battery > 100) isValid = false;
            } catch (NumberFormatException e) {
                isValid = false;
            }

            try {
                float range = Float.parseFloat(rangeField.getText());
                if (range < 0) isValid = false;
            } catch (NumberFormatException e) {
                isValid = false;
            }

            saveButton.setDisable(!isValid);
        };

        stationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs.run());
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateInputs.run());
        batteryField.textProperty().addListener((obs, oldVal, newVal) -> validateInputs.run());
        rangeField.textProperty().addListener((obs, oldVal, newVal) -> validateInputs.run());

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Bicycle bicycle = (existingBicycle != null) ? existingBicycle : new Bicycle();
                bicycle.setStation(stationComboBox.getValue());
                bicycle.setStatus(statusComboBox.getValue());
                bicycle.setBattery_level(Float.parseFloat(batteryField.getText()));
                bicycle.setRange_km(Float.parseFloat(rangeField.getText()));
                bicycle.setLast_updated(new Timestamp(System.currentTimeMillis()));
                return bicycle;
            }
            return null;
        });

        Optional<Bicycle> result = dialog.showAndWait();
        result.ifPresent(bicycle -> {
            try {
                if (existingBicycle == null) {
                    bicycleService.create(bicycle);
                } else {
                    bicycleService.update(bicycle);
                }
                loadBicycles();
            } catch (Exception e) {
                showAlert("Error", "Failed to save bicycle: " + e.getMessage());
            }
        });
    }

    private void exportToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Bicycles to CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                List<Bicycle> bicyclesToExport = bicycleGridView.getChildren().stream()
                        .map(node -> {
                            HBox card = (HBox) node; // Updated to HBox
                            CheckBox checkbox = (CheckBox) card.getChildren().get(0);
                            return (Bicycle) checkbox.getUserData();
                        })
                        .collect(Collectors.toList());
                CsvExporter.exportBicyclesToCsv(bicyclesToExport, file);
                showAlert("Export Successful",
                        "Bicycles exported successfully to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Export Failed",
                        "Failed to export CSV: " + e.getMessage());
            }
        }
    }

    private void exportToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Bicycles Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                List<Bicycle> bicyclesToExport = bicycleGridView.getChildren().stream()
                        .map(node -> {
                            HBox card = (HBox) node; // Updated to HBox
                            CheckBox checkbox = (CheckBox) card.getChildren().get(0);
                            return (Bicycle) checkbox.getUserData();
                        })
                        .collect(Collectors.toList());

                PdfReportGenerator.generateBicycleReport(bicyclesToExport, file);
                showAlert("Export Successful",
                        "PDF report generated successfully to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Export Failed",
                        "Failed to generate PDF: " + e.getMessage());
            }
        }
    }

    private static class BatchUpdateData {
        Bicycle.STATUS newStatus;
        Station newStation;

        BatchUpdateData(Bicycle.STATUS status, Station station) {
            this.newStatus = status;
            this.newStation = station;
        }
    }

    // Helper method to get border color based on bicycle status
    private String getStatusBorderColor(Bicycle.STATUS status) {
        return switch (status) {
            case available -> "#38d9a9";     // Green for available
            case in_use -> "#ffd43b";         // Yellow for in-use
            case maintenance -> "#ff6b6b";    // Red for maintenance
            case charging -> "#adb5bd";       // Gray for charging
            case reserved -> "#74c0fc";       // Blue for reserved
        };
    }
}