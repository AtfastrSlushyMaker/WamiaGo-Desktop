package controllers.station.back;

import entities.Location;
import entities.Station;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import services.LocationService;
import services.StationService;

import java.util.List;
import java.util.Optional;

public class StationsController {
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
        delete_button.setOnAction(event -> deleteStations());

        // Double-click handler for editing
        station_listView.setCellFactory(lv -> new ListCell<>() {
            private final ImageView icon = new ImageView(
                    new Image(getClass().getResourceAsStream("/images/station/icons/bike_station.png"))
            );

            {
                icon.setFitHeight(20);
                icon.setFitWidth(20);
            }

            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                if (empty || station == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    Label text = new Label(station.getName() + " - " + station.getLocation().getAddress());
                    container.getChildren().addAll(icon, text);
                    setGraphic(container);
                }
            }
        });

        station_listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !station_listView.getSelectionModel().isEmpty()) {
                Station selectedStation = station_listView.getSelectionModel().getSelectedItem();
                updateStation(selectedStation);
            }
        });
    }



    public void deleteStations() {
        List<Station> selectedStations = station_listView.getSelectionModel().getSelectedItems();
        if (selectedStations.isEmpty()) {
            showAlert("No Selection", "Please select stations to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Stations");
        alert.setHeaderText("Are you sure you want to delete the selected stations?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                for (Station station : selectedStations) {
                    new StationService().delete(station.getId());
                }
                loadStations();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        Dialog<Station> dialog = new Dialog<>();
        dialog.setTitle("Add Station");
        dialog.setHeaderText("Enter station details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        ComboBox<String> locationComboBox = new ComboBox<>();
        TextField totalDocksField = new TextField();
        TextField availableDocksField = new TextField();
        TextField availableBikesField = new TextField();
        TextField chargingBikesField = new TextField();
        ComboBox<Station.STATUS> statusComboBox = new ComboBox<>();

        try {
            List<Location> locations = new LocationService().read();
            locations.forEach(location -> locationComboBox.getItems().add(location.getAddress()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        statusComboBox.getItems().addAll(Station.STATUS.values());

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!validateInput(
                        nameField.getText(),
                        locationComboBox.getValue(),
                        totalDocksField.getText(),
                        availableDocksField.getText(),
                        availableBikesField.getText(),
                        chargingBikesField.getText(),
                        statusComboBox.getValue()
                )) {
                    return null;
                }

                try {
                    Location location = new LocationService().read().stream()
                            .filter(l -> l.getAddress().equals(locationComboBox.getValue()))
                            .findFirst()
                            .orElse(null);

                    return new Station(
                            -1
                            ,
                            nameField.getText(),
                            location,
                            Integer.parseInt(totalDocksField.getText()),
                            Integer.parseInt(availableDocksField.getText()),
                            Integer.parseInt(availableBikesField.getText()),
                            Integer.parseInt(chargingBikesField.getText()),
                            statusComboBox.getValue()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        Optional<Station> result = dialog.showAndWait();
        result.ifPresent(station -> {
            try {
                new StationService().create(station);
                loadStations();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean validateInput(String name, String location, String totalDocks, String availableDocks,
                                  String availableBikes, String chargingBikes, Station.STATUS status) {
        if (name == null || name.isEmpty()) {
            showAlert("Invalid Input", "Name field cannot be empty");
            return false;
        }
        if (location == null) {
            showAlert("Invalid Input", "Please select a location");
            return false;
        }
        try {
            Integer.parseInt(totalDocks);
            Integer.parseInt(availableDocks);
            Integer.parseInt(availableBikes);
            Integer.parseInt(chargingBikes);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Numeric fields must contain valid numbers");
            return false;
        }
        if (status == null) {
            showAlert("Invalid Input", "Please select a status");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();  // Fixed: Added showAndWait()
    }

    public void updateStation(Station station) {
        Dialog<Station> dialog = new Dialog<>();
        dialog.setTitle("Update Station");
        dialog.setHeaderText("Edit station details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(station.getName());
        ComboBox<String> locationComboBox = new ComboBox<>();
        TextField totalDocksField = new TextField(String.valueOf(station.getTotal_docks()));
        TextField availableDocksField = new TextField(String.valueOf(station.getAvailable_docks()));
        TextField availableBikesField = new TextField(String.valueOf(station.getAvailable_bikes()));
        TextField chargingBikesField = new TextField(String.valueOf(station.getCharging_bikes()));
        ComboBox<Station.STATUS> statusComboBox = new ComboBox<>();

        try {
            List<Location> locations = new LocationService().read();
            locations.forEach(l -> locationComboBox.getItems().add(l.getAddress()));
            locationComboBox.setValue(station.getLocation().getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }

        statusComboBox.getItems().addAll(Station.STATUS.values());
        statusComboBox.setValue(station.getStatus());

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!validateInput(
                        nameField.getText(),
                        locationComboBox.getValue(),
                        totalDocksField.getText(),
                        availableDocksField.getText(),
                        availableBikesField.getText(),
                        chargingBikesField.getText(),
                        statusComboBox.getValue()
                )) {
                    return null;
                }

                try {
                    Location location = new LocationService().read().stream()
                            .filter(l -> l.getAddress().equals(locationComboBox.getValue()))
                            .findFirst()
                            .orElse(null);

                    station.setName(nameField.getText());
                    station.setLocation(location);
                    station.setTotal_docks(Integer.parseInt(totalDocksField.getText()));
                    station.setAvailable_docks(Integer.parseInt(availableDocksField.getText()));
                    station.setAvailable_bikes(Integer.parseInt(availableBikesField.getText()));
                    station.setCharging_bikes(Integer.parseInt(chargingBikesField.getText()));
                    station.setStatus(statusComboBox.getValue());

                    return station;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        Optional<Station> result = dialog.showAndWait();
        result.ifPresent(updatedStation -> {
            try {
                new StationService().update(updatedStation);
                loadStations();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}