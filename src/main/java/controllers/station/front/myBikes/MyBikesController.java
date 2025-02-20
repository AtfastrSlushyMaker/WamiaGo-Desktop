package controllers.station.front.myBikes;

import entities.Bicycle;
import entities.BicycleRental;
import entities.Location;
import entities.Station;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.BicycleRentalService;
import services.BicycleService;
import services.StationService;
import utils.SessionManager;

import javafx.scene.image.ImageView;
import java.awt.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class MyBikesController {
    @FXML
    private Label available_stations_label;

    @FXML
    private ListView<BicycleRental> bikes_list;

    @FXML
    private Button bookings_button;

    @FXML
    private Button history_button;

    @FXML
    private Button home_button;

    @FXML
    private Button logout_button;

    @FXML
    private Pane pane_1121;

    @FXML
    private Button rent_button;

    @FXML
    private Button rides_button;

    @FXML
    private HBox root;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane side_ankerpane;

    private final BicycleRentalService rentalService = new BicycleRentalService();

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        rent_button.setOnAction(event -> loadScene("/station/front/station.fxml"));
        history_button.setOnAction(event -> loadScene("/history/history.fxml"));
        bookings_button.setOnAction(event -> loadScene("/bookings/bookings.fxml"));
        logout_button.setOnAction(event -> loadScene("/user/login.fxml"));
    }


    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Load the CSS file

        setupNavigation();
        refreshBikesList(); // Refresh the ListView on initialization
        bikes_list.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(BicycleRental rental, boolean empty) {
                super.updateItem(rental, empty);
                if (empty || rental == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Debug: Print the rental details
                    System.out.println("Rental: " + rental);
                    if (rental.getStart_station() == null) {
                        System.err.println("Start station is null for rental: " + rental);
                        setText("Invalid rental data");
                        setGraphic(null);
                    } else {
                        setGraphic(createBikeItem(rental));
                    }
                }
            }
        });

        // Populate the list initially
        fillBikesList();
    }

    public void refreshBikesList() {
        fillBikesList(); // Call the method to populate the ListView
    }

    private void reloadCurrentScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/station/front/myBikes/myBikes.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Reload Failed", "An error occurred while reloading the page. Please try again.");
        }
    }

    public void fillBikesList() {
        try {
            // Fetch active rentals for the current user
            List<BicycleRental> activeRentals = rentalService.getActiveRentalsForUser(SessionManager.getInstance().getUser());

            // Clear and set the new items
            bikes_list.getItems().clear();
            bikes_list.getItems().addAll(activeRentals);

            // Ensure the ListView is using a custom cell factory
            bikes_list.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(BicycleRental rental, boolean empty) {
                    super.updateItem(rental, empty);
                    if (empty || rental == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Set a proper graphic instead of relying on toString()
                        setText(null);
                        setGraphic(createBikeItem(rental));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error", "Failed to load rented bikes.");
        }
    }


    private HBox createBikeItem(BicycleRental rental) {
        HBox itemBox = new HBox(10); // Horizontal spacing of 10
        itemBox.setAlignment(Pos.CENTER_LEFT); // Align items to the left
        itemBox.setPadding(new Insets(10)); // Padding around the item
        Image image = new Image(getClass().getResourceAsStream("/images/station/icons/Ebike-side.png")); // Ensure the file exists in resources/icons/
        ImageView icon = new ImageView(image);
        icon.setFitWidth(50);
        icon.setFitHeight(50);

        Label bikeLabel = new Label("Bike taken from " + rental.getStart_station().getName());
        bikeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"); // Custom font style

        Button returnButton = new Button("Return");
        returnButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-border-radius: 3px;"
        +"-fx-pref-height:50; -fx-pref-width: 100; "); // Custom button style

        returnButton.setOnAction(event -> returnBike(rental));

        itemBox.getChildren().addAll(icon,bikeLabel, returnButton);
        return itemBox;
    }



    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void returnBike(BicycleRental rental) {
        try {
            // Fetch available stations
            StationService stationService = new StationService();
            List<Station> availableStations = stationService.read();

            if (availableStations.isEmpty()) {
                showErrorDialog("No Available Stations", "There are no available stations to return the bike.");
                return;
            }

            // Show modal dialog
            Station selectedStation = showStationSelectionDialog(availableStations);

            if (selectedStation == null) {
                return; // User canceled
            }

            // Debug: Print the selected station
            System.out.println("Selected Station: " + selectedStation);

            // Update bike status and rental
            Bicycle bicycle = rental.getBicycle();
            bicycle.setStatus(Bicycle.STATUS.available);


            rental.setEnd_station(selectedStation);
            rental.setDistance_km((float)Location.calculateDistance(rental.getStart_station().getLocation(), selectedStation.getLocation()));
            rental.setBattery_used((float) Math.random() * 100); // Random battery usage
            rental.setCost((float) (rental.getDistance_km() * 0.1)); // Cost per km

            bicycle.setStation(selectedStation);
            rental.setEnd_time(new Timestamp(System.currentTimeMillis()));

            // Debug: Print the updated rental details
            System.out.println("Updated Rental: " + rental);

            // Save changes
            new BicycleService().update(bicycle);
            rentalService.update(rental);

            // Refresh UI
            refreshBikesList();

            // Show success message
            showSuccessDialog("Bike Returned", "Your Bike has been returned to " + selectedStation.getName() + ".");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Return Failed", "An error occurred while returning the bike.");
        }
    }
    private Station showStationSelectionDialog(List<Station> stations) {
        Dialog<Station> dialog = new Dialog<>();
        dialog.setTitle("Select Station");
        dialog.setHeaderText("Select a station to return the bike to:");

        // Set up buttons
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        // Create a GridPane to display station icons and names
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ToggleGroup group = new ToggleGroup(); // To ensure only one station is selected

        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);

            // Create an ImageView for the station icon
            Image image = new Image(getClass().getResourceAsStream("/images/station/icons/bike_station.png")); // Ensure the file exists in resources/icons/
            ImageView icon = new ImageView(image);
            icon.setFitWidth(50);
            icon.setFitHeight(50);

            // Create a radio button for station selection
            RadioButton radioButton = new RadioButton(station.getName());
            radioButton.setToggleGroup(group);

            // Set the first station as default selection
            if (i == 0) {
                radioButton.setSelected(true);
            }

            // Arrange items in the grid
            VBox stationBox = new VBox(5, icon, radioButton);
            stationBox.setAlignment(Pos.CENTER);
            grid.add(stationBox, i % 3, i / 3); // Arrange in a grid format (3 per row)
        }

        dialog.getDialogPane().setContent(grid);

        // Handle dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                for (Toggle toggle : group.getToggles()) {
                    RadioButton selected = (RadioButton) toggle;
                    if (selected.isSelected()) {
                        return stations.stream()
                                .filter(s -> s.getName().equals(selected.getText()))
                                .findFirst()
                                .orElse(null);
                    }
                }
            }
            return null;
        });

        Optional<Station> result = dialog.showAndWait();
        return result.orElse(null);
    }



}
