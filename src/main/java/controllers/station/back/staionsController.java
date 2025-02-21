package controllers.station.back;

import entities.Location;
import entities.Station;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import services.LocationService;
import services.StationService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class staionsController {
    @FXML
    private Button add_button;

    @FXML
    private Button delete_button;

    @FXML
    private ListView<Station> station_listView;

    @FXML
    public void initialize() {
        loadStations();
        add_button.setOnAction(event -> addStation());

    }
public void loadStations() {
        try {
            station_listView.getItems().clear();
            station_listView.getItems().addAll(new StationService().read());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addStation() {
        // Create a custom dialog
        Dialog<Station> dialog = new Dialog<>();
        dialog.setTitle("Add Station");
        dialog.setHeaderText("Enter station details");

        // Set dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Station Name");

        ComboBox<String> locationComboBox = new ComboBox<>();
        locationComboBox.setPromptText("Location");
        List<Location> locations = null;
        try {
            locations = new LocationService().read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Location> finalLocations = locations;
        for (Location location : finalLocations) {
            locationComboBox.getItems().add(location.getAddress());
        }

        TextField totalDocksField = new TextField();
        totalDocksField.setPromptText("Total Docks");

        TextField availableDocksField = new TextField();
        availableDocksField.setPromptText("Available Docks");

        TextField availableBikesField = new TextField();
        availableBikesField.setPromptText("Available Bikes");

        TextField chargingBikesField = new TextField();
        chargingBikesField.setPromptText("Charging Bikes");

        ComboBox<Station.STATUS> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(Station.STATUS.values());
        statusComboBox.setPromptText("Status");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Location:"), 0, 1);
        grid.add(locationComboBox, 1, 1);
        grid.add(new Label("Total Docks:"), 0, 2);
        grid.add(totalDocksField, 1, 2);
        grid.add(new Label("Available Docks:"), 0, 3);
        grid.add(availableDocksField, 1, 3);
        grid.add(new Label("Available Bikes:"), 0, 4);
        grid.add(availableBikesField, 1, 4);
        grid.add(new Label("Charging Bikes:"), 0, 5);
        grid.add(chargingBikesField, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusComboBox, 1, 6);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Station object when Save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText();
                String selectedLocationName = locationComboBox.getValue();
                String totalDocks = totalDocksField.getText();
                String availableDocks = availableDocksField.getText();
                String availableBikes = availableBikesField.getText();
                String chargingBikes = chargingBikesField.getText();
                Station.STATUS status = statusComboBox.getValue();

                if (name.isEmpty() || selectedLocationName == null || totalDocks.isEmpty() || availableDocks.isEmpty() ||
                        availableBikes.isEmpty() || chargingBikes.isEmpty() || status == null) {
                    showAlert("Invalid Input", "Please fill in all fields.");
                    return null;
                }

                Location selectedLocation = finalLocations.stream()
                        .filter(location -> location.getAddress().equals(selectedLocationName))
                        .findFirst()
                        .orElse(null);

                if (selectedLocation == null) {
                    showAlert("Invalid Input", "Selected location is not valid.");
                    return null;
                }

                try {
                    int totalDocksInt = Integer.parseInt(totalDocks);
                    int availableDocksInt = Integer.parseInt(availableDocks);
                    int availableBikesInt = Integer.parseInt(availableBikes);
                    int chargingBikesInt = Integer.parseInt(chargingBikes);

                    Station newStation = new Station();
                    newStation.setName(name);
                    newStation.setLocation(selectedLocation);
                    newStation.setTotal_docks(totalDocksInt);
                    newStation.setAvailable_docks(availableDocksInt);
                    newStation.setAvailable_bikes(availableBikesInt);
                    newStation.setCharging_bikes(chargingBikesInt);
                    newStation.setStatus(status);

                    return newStation;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for docks and bikes.");
                    return null;
                }
            }
            return null;
        });

        // Show dialog and process result
        Optional<Station> result = dialog.showAndWait();
        result.ifPresent(station -> {
            station_listView.getItems().add(station);
            try {
                new StationService().create(station);
                loadStations();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
    }


}
