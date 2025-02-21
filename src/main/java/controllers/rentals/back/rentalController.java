package controllers.rentals.back;

import entities.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import services.BicycleRentalService;
import services.BicycleService;
import services.StationService;
import services.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class rentalController {
    @FXML
    private ListView<BicycleRental> rental_listView;
    @FXML
    private Button add_button;
    @FXML
    private Button delete_button;

    private final BicycleRentalService rentalService = new BicycleRentalService();
    private final UserService userService = new UserService();
    private final BicycleService bicycleService = new BicycleService();
    private final StationService stationService = new StationService();

    @FXML
    public void initialize() {
        loadRentals();
        setupListView();
        add_button.setOnAction(event -> showRentalDialog(null));
        delete_button.setOnAction(event -> deleteSelectedRentals());
    }

    private void setupListView() {
        rental_listView.setCellFactory(lv -> new ListCell<BicycleRental>() {
            private final ImageView icon = new ImageView(
                    new Image(getClass().getResourceAsStream("/images/station/icons/bike_rental.png"))
            );
            {
                icon.setFitHeight(20);
                icon.setFitWidth(20);
            }

            @Override
            protected void updateItem(BicycleRental rental, boolean empty) {
                super.updateItem(rental, empty);
                if (empty || rental == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    String status = rental.getEnd_time() == null ? "Active" : "Completed";
                    String text = String.format("Rental #%d - %s (%s) | Duration: %d min",
                            rental.getId(),
                            rental.getUser().getName(),
                            status,
                            rental.getDuration_minutes());
                    container.getChildren().addAll(icon, new Label(text));
                    setGraphic(container);
                }
            }
        });

        rental_listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                BicycleRental selected = rental_listView.getSelectionModel().getSelectedItem();
                if (selected != null) showRentalDialog(selected);
            }
        });
    }

    private void loadRentals() {
        try {
            rental_listView.getItems().setAll(rentalService.read());
        } catch (Exception e) {
            showAlert("Error", "Failed to load rentals: " + e.getMessage());
        }
    }

    private void showRentalDialog(BicycleRental existingRental) {
        Dialog<BicycleRental> dialog = new Dialog<>();
        dialog.setTitle(existingRental == null ? "New Rental" : "Edit Rental");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Declare all UI components
        ComboBox<User> userCombo = new ComboBox<>();
        ComboBox<Bicycle> bikeCombo = new ComboBox<>();
        ComboBox<Station> startStationCombo = new ComboBox<>();
        ComboBox<Station> endStationCombo = new ComboBox<>();
        TextField distanceField = new TextField();
        TextField batteryField = new TextField();
        TextField costField = new TextField();

        // Configure User ComboBox
        userCombo.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? "" : user.getName());
            }
        });
        userCombo.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? "" : user.getName());
            }
        });

        // Configure Bicycle ComboBox
        bikeCombo.setCellFactory(lv -> new ListCell<Bicycle>() {
            @Override
            protected void updateItem(Bicycle bike, boolean empty) {
                super.updateItem(bike, empty);
                setText(empty || bike == null ? "" : "Bike #" + bike.getId() + " - " + bike.getStatus());
            }
        });
        bikeCombo.setButtonCell(new ListCell<Bicycle>() {
            @Override
            protected void updateItem(Bicycle bike, boolean empty) {
                super.updateItem(bike, empty);
                setText(empty || bike == null ? "" : "Bike #" + bike.getId());
            }
        });

        // Configure Station ComboBoxes
        for (ComboBox<Station> stationCombo : new ComboBox[]{startStationCombo, endStationCombo}) {
            stationCombo.setCellFactory(lv -> new ListCell<Station>() {
                @Override
                protected void updateItem(Station station, boolean empty) {
                    super.updateItem(station, empty);
                    setText(empty || station == null ? "" : station.getName());
                }
            });
            stationCombo.setButtonCell(new ListCell<Station>() {
                @Override
                protected void updateItem(Station station, boolean empty) {
                    super.updateItem(station, empty);
                    setText(empty || station == null ? "" : station.getName());
                }
            });
        }

        // Load data into combo boxes
        try {
            userCombo.getItems().addAll(userService.read());
            bikeCombo.getItems().addAll(bicycleService.read());
            startStationCombo.getItems().addAll(stationService.read());
            endStationCombo.getItems().addAll(stationService.read());
        } catch (Exception e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }

        // Populate fields if editing
        if (existingRental != null) {
            userCombo.setValue(existingRental.getUser());
            bikeCombo.setValue(existingRental.getBicycle());
            startStationCombo.setValue(existingRental.getStart_station());
            endStationCombo.setValue(existingRental.getEnd_station());
            distanceField.setText(String.valueOf(existingRental.getDistance_km()));
            batteryField.setText(String.valueOf(existingRental.getBattery_used()));
            costField.setText(String.valueOf(existingRental.getCost()));
        }

        // Add components to grid
        grid.add(new Label("User:"), 0, 0);
        grid.add(userCombo, 1, 0);
        grid.add(new Label("Bicycle:"), 0, 1);
        grid.add(bikeCombo, 1, 1);
        grid.add(new Label("Start Station:"), 0, 2);
        grid.add(startStationCombo, 1, 2);
        grid.add(new Label("End Station:"), 0, 3);
        grid.add(endStationCombo, 1, 3);
        grid.add(new Label("Distance (km):"), 0, 4);
        grid.add(distanceField, 1, 4);
        grid.add(new Label("Battery Used (%):"), 0, 5);
        grid.add(batteryField, 1, 5);
        grid.add(new Label("Cost:"), 0, 6);
        grid.add(costField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                if (validateInput(
                        userCombo.getValue(),
                        bikeCombo.getValue(),
                        startStationCombo.getValue(),
                        distanceField.getText(),
                        batteryField.getText(),
                        costField.getText()
                )) {
                    BicycleRental rental = existingRental != null ? existingRental : new BicycleRental();
                    rental.setUser(userCombo.getValue());
                    rental.setBicycle(bikeCombo.getValue());
                    rental.setStart_station(startStationCombo.getValue());
                    rental.setEnd_station(endStationCombo.getValue());
                    rental.setDistance_km(Float.parseFloat(distanceField.getText()));
                    rental.setBattery_used(Float.parseFloat(batteryField.getText()));
                    rental.setCost(Float.parseFloat(costField.getText()));

                    if (existingRental == null) {
                        rental.setStart_time(new Timestamp(System.currentTimeMillis()));
                    }

                    return rental;
                }
            }
            return null;
        });

        Optional<BicycleRental> result = dialog.showAndWait();
        result.ifPresent(rental -> {
            try {
                if (existingRental == null) {
                    rentalService.create(rental);
                } else {
                    rentalService.update(rental);
                }
                loadRentals();
            } catch (Exception e) {
                showAlert("Error", "Failed to save rental: " + e.getMessage());
            }
        });
    }

    private void deleteSelectedRentals() {
        List<BicycleRental> selected = rental_listView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) {
            showAlert("No Selection", "Please select rentals to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Rentals");
        alert.setHeaderText("Are you sure you want to delete " + selected.size() + " rentals?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selected.forEach(rental -> {
                try {
                    rentalService.delete(rental.getId());
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete rental #" + rental.getId() + ": " + e.getMessage());
                }
            });
            loadRentals();
        }
    }

    private boolean validateInput(User user, Bicycle bike, Station startStation,
                                  String distance, String battery, String cost) {
        if (user == null || bike == null || startStation == null) {
            showAlert("Invalid Input", "Please select user, bicycle, and start station");
            return false;
        }

        try {
            float distanceVal = Float.parseFloat(distance);
            float batteryVal = Float.parseFloat(battery);
            float costVal = Float.parseFloat(cost);

            if (distanceVal < 0 || batteryVal < 0 || costVal < 0) {
                showAlert("Invalid Input", "Values cannot be negative");
                return false;
            }

            if (batteryVal > 100) {
                showAlert("Invalid Input", "Battery used cannot exceed 100%");
                return false;
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numeric values");
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