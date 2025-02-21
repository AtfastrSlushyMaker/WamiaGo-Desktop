package controllers.bicycle.back;

import entities.Bicycle;
import entities.Station;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import services.BicycleService;
import services.StationService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class bicycleController {
    @FXML
    private Button add_button;
    @FXML
    private Button delete_button;
    @FXML
    private ListView<Bicycle> bicycle_listView;

    private final BicycleService bicycleService = new BicycleService();
    private final StationService stationService = new StationService();

    @FXML
    public void initialize() {

        setupListView();
        loadBicycles();
        add_button.setOnAction(event -> addBicycle());
        delete_button.setOnAction(event -> deleteBicycles());
    }

    private void setupListView() {
        bicycle_listView.setCellFactory(lv -> {
            ListCell<Bicycle> cell = new ListCell<>() {
                private final ImageView icon = new ImageView(
                        new Image(getClass().getResourceAsStream("/images/station/icons/Ebike-side.png"))
                );
                {
                    icon.setFitHeight(20);
                    icon.setFitWidth(20);
                }

                @Override
                protected void updateItem(Bicycle bicycle, boolean empty) {
                    super.updateItem(bicycle, empty);
                    if (empty || bicycle == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox container = new HBox(10);
                        Label text = new Label(String.format("Bike #%d - %s (%.0f%%)",
                                bicycle.getId(),
                                bicycle.getStatus().toString(),
                                bicycle.getBattery_level()));
                        container.getChildren().addAll(icon, text);
                        setGraphic(container);
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    Bicycle selectedBicycle = cell.getItem();
                    updateBicycle(selectedBicycle);
                }
            });

            return cell;
        });
    }

    private void loadBicycles() {
        try {
            bicycle_listView.getItems().clear();
            bicycle_listView.getItems().addAll(bicycleService.read());
        } catch (Exception e) {
            showAlert("Error", "Failed to load bicycles: " + e.getMessage());
        }
    }

    private void addBicycle() {
        Dialog<Bicycle> dialog = new Dialog<>();
        dialog.setTitle("Add Bicycle");
        dialog.setHeaderText("Enter bicycle details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Station> stationCombo = new ComboBox<>();
        ComboBox<Bicycle.STATUS> statusCombo = new ComboBox<>();
        TextField batteryField = new TextField();
        TextField rangeField = new TextField();

        try {
            stationCombo.getItems().addAll(stationService.read());
            statusCombo.getItems().addAll(Bicycle.STATUS.values());
        } catch (Exception e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }

        grid.add(new Label("Station:"), 0, 0);
        grid.add(stationCombo, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusCombo, 1, 1);
        grid.add(new Label("Battery Level (%):"), 0, 2);
        grid.add(batteryField, 1, 2);
        grid.add(new Label("Range (km):"), 0, 3);
        grid.add(rangeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                if (validateInput(
                        stationCombo.getValue(),
                        statusCombo.getValue(),
                        batteryField.getText(),
                        rangeField.getText()
                )) {
                    Bicycle bicycle = new Bicycle();
                    bicycle.setStation(stationCombo.getValue());
                    bicycle.setStatus(statusCombo.getValue());
                    bicycle.setBattery_level(Float.parseFloat(batteryField.getText()));
                    bicycle.setRange_km(Float.parseFloat(rangeField.getText()));
                    bicycle.setLast_updated(new Timestamp(System.currentTimeMillis()));
                    return bicycle;
                }
            }
            return null;
        });

        Optional<Bicycle> result = dialog.showAndWait();
        result.ifPresent(bicycle -> {
            try {
                bicycleService.create(bicycle);
                loadBicycles();
            } catch (Exception e) {
                showAlert("Error", "Failed to create bicycle: " + e.getMessage());
            }
        });
    }

    private void updateBicycle(Bicycle bicycle) {
        Dialog<Bicycle> dialog = new Dialog<>();
        dialog.setTitle("Update Bicycle");
        dialog.setHeaderText("Edit bicycle details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Station> stationCombo = new ComboBox<>();
        ComboBox<Bicycle.STATUS> statusCombo = new ComboBox<>();
        TextField batteryField = new TextField();
        TextField rangeField = new TextField();

        try {
            stationCombo.getItems().addAll(stationService.read());
            statusCombo.getItems().addAll(Bicycle.STATUS.values());

            stationCombo.setValue(bicycle.getStation());
            statusCombo.setValue(bicycle.getStatus());
            batteryField.setText(String.valueOf(bicycle.getBattery_level()));
            rangeField.setText(String.valueOf(bicycle.getRange_km()));
        } catch (Exception e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }

        grid.add(new Label("Station:"), 0, 0);
        grid.add(stationCombo, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusCombo, 1, 1);
        grid.add(new Label("Battery Level (%):"), 0, 2);
        grid.add(batteryField, 1, 2);
        grid.add(new Label("Range (km):"), 0, 3);
        grid.add(rangeField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                if (validateInput(
                        stationCombo.getValue(),
                        statusCombo.getValue(),
                        batteryField.getText(),
                        rangeField.getText()
                )) {
                    bicycle.setStation(stationCombo.getValue());
                    bicycle.setStatus(statusCombo.getValue());
                    bicycle.setBattery_level(Float.parseFloat(batteryField.getText()));
                    bicycle.setRange_km(Float.parseFloat(rangeField.getText()));
                    bicycle.setLast_updated(new Timestamp(System.currentTimeMillis()));
                    return bicycle;
                }
            }
            return null;
        });

        Optional<Bicycle> result = dialog.showAndWait();
        result.ifPresent(updatedBicycle -> {
            try {
                bicycleService.update(updatedBicycle);
                loadBicycles();
            } catch (Exception e) {
                showAlert("Error", "Failed to update bicycle: " + e.getMessage());
            }
        });
    }

    private void deleteBicycles() {
        List<Bicycle> selected = bicycle_listView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showAlert("No Selection", "Please select bicycles to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Bicycles");
        alert.setHeaderText("Are you sure you want to delete " + selected.size() + " bicycles?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selected.forEach(bicycle -> {
                try {
                    bicycleService.delete(bicycle.getId());
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete bicycle #" + bicycle.getId() + ": " + e.getMessage());
                }
            });
            loadBicycles();
        }
    }

    private boolean validateInput(Station station, Bicycle.STATUS status, String battery, String range) {
        if (station == null) {
            showAlert("Invalid Input", "Please select a station");
            return false;
        }
        if (status == null) {
            showAlert("Invalid Input", "Please select a status");
            return false;
        }
        try {
            float batteryLevel = Float.parseFloat(battery);
            if (batteryLevel < 0 || batteryLevel > 100) {
                showAlert("Invalid Input", "Battery level must be between 0 and 100");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Invalid battery level format");
            return false;
        }
        try {
            float rangeKm = Float.parseFloat(range);
            if (rangeKm < 0) {
                showAlert("Invalid Input", "Range cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Invalid range format");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}