package controllers.station.front.myBikes;

import entities.Bicycle;
import entities.BicycleRental;
import entities.Location;
import entities.Station;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.BicycleRentalService;
import services.BicycleService;
import services.StationService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class MyBikesController {
    private final BicycleRentalService rentalService = new BicycleRentalService();
    @FXML
    private Label available_stations_label;
    @FXML
    private ListView<BicycleRental> bikes_list;
    @FXML
    private Button bookings_button;
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



    // Navigation Functions
    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        rent_button.setOnAction(event -> loadScene("/station/front/station.fxml"));
        //bookings_button.setOnAction(event -> loadScene("/Annoucement/Front/announcements_client.fxml"));
        logout_button.setOnAction(event -> logout());

    }

    public void logout() {
        SessionManager.getInstance().logout();
        loadScene("/user.front/LoginSignup.fxml");
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
        HBox itemBox = new HBox(20); // Horizontal spacing of 20
        itemBox.setAlignment(Pos.CENTER_LEFT); // Align items to the left
        itemBox.setPadding(new Insets(10)); // Padding around the item
        itemBox.setStyle(
                "-fx-background-color: #ffffff; " + // White background
                        "-fx-border-color: #eeeeee; " + // Light gray border
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px; " + // Rounded corners
                        "-fx-padding: 10px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 3, 0, 0, 1);" // Subtle shadow
        );

        // Create an icon for the bike
        Image image = new Image(getClass().getResourceAsStream("/images/station/icons/Ebike-side.png"));
        ImageView icon = new ImageView(image);
        icon.setFitWidth(50);
        icon.setFitHeight(50);

        // Create label for the bike info
        Label bikeLabel = new Label("Bike taken from " + rental.getStart_station().getName());
        bikeLabel.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #333333;" // Dark gray text
        );

        // Create return button with style
        Button returnButton = new Button("Return");
        returnButton.setStyle(
                "-fx-background-color: #6BBF59; " + // Green background
                        "-fx-text-fill: white; " + // White text
                        "-fx-border-radius: 5px; " + // Rounded corners
                        "-fx-padding: 8px 16px; " + // Padding
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);" // Subtle shadow
        );

        // Button action
        returnButton.setOnAction(event -> returnBike(rental));

        // Add hover effect to the return button
        returnButton.setOnMouseEntered(e -> returnButton.setStyle(
                "-fx-background-color: #4E9D3A; " + // Darker green on hover
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 7, 0, 0, 2);" // Stronger shadow
        ));

        returnButton.setOnMouseExited(e -> returnButton.setStyle(
                "-fx-background-color: #6BBF59; " + // Green background
                        "-fx-text-fill: white; " + // White text
                        "-fx-border-radius: 5px; " + // Rounded corners
                        "-fx-padding: 8px 16px; " + // Padding
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);" // Subtle shadow
        ));

        // Add the icon, label, and button into the HBox
        itemBox.getChildren().addAll(icon, bikeLabel, createFlexibleSpace(), returnButton);

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
            Station selectedStation = showStationSelectionDialog(availableStations,rental);

            if (selectedStation == null) {
                return; // User canceled
            }

            // Debug: Print the selected station
            System.out.println("Selected Station: " + selectedStation);

            // Update bike status and rental
            Bicycle bicycle = rental.getBicycle();
            bicycle.setStatus(Bicycle.STATUS.available);


            rental.setEnd_station(selectedStation);
            rental.setDistance_km((float) Location.calculateDistance(rental.getStart_station().getLocation(), selectedStation.getLocation()));
            rental.setBattery_used((float) Math.random() * 100); // Random battery usage
            // Cost per km

            bicycle.setStation(selectedStation);
            rental.setEnd_time(new Timestamp(System.currentTimeMillis()));
            rental.setCost((float)rental.calculateCost(rental));
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

    private Station showStationSelectionDialog(List<Station> stations, BicycleRental rental) {
        Dialog<Station> dialog = new Dialog<>();
        dialog.setTitle("Return Bike");
        dialog.setHeaderText("Select a station to return your bike");

        // Set up buttons
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        // Create main content VBox
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Add rental summary section
        VBox rentalSummary = createRentalSummarySection(rental);

        // Calculate estimated cost and distance
        GridPane estimatesGrid = createEstimatesGrid(rental, stations);

        // Create a ScrollPane for the stations grid
        ScrollPane stationsScroll = new ScrollPane();
        stationsScroll.setFitToWidth(true);
        stationsScroll.setPrefHeight(300);
        stationsScroll.setStyle("-fx-background-color: transparent;");

        // Create the stations selection grid
        GridPane stationsGrid = new GridPane();
        stationsGrid.setHgap(20);
        stationsGrid.setVgap(20);
        stationsGrid.setPadding(new Insets(10));

        // Add a title for the stations section
        Label stationsTitle = new Label("Available Return Stations");
        stationsTitle.setStyle(
                "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #333333; " +
                        "-fx-padding: 0 0 10 0;"
        );

        ToggleGroup group = new ToggleGroup();

        // Track the best station for highlighting
        Station nearestStation = findNearestStation(rental.getStart_station(), stations);

        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);

            // Calculate distance from start station
            double distance = Location.calculateDistance(
                    rental.getStart_station().getLocation(),
                    station.getLocation()
            );

            // Calculate estimated cost
            float estimatedCost = rental.bestCost(rental,distance); // 0.10 per km

            // Create station selection box
            VBox stationBox = createStationSelectionBox(station, distance, estimatedCost, group);

            // Highlight the nearest station or the start station
            if (station.equals(nearestStation)) {
                stationBox.setStyle(stationBox.getStyle() +
                        "-fx-border-color: #4CAF50; -fx-border-width: 2px;");

                // Add a "recommended" label
                Label recommendedLabel = new Label("RECOMMENDED");
                recommendedLabel.setStyle(
                        "-fx-font-size: 10px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-color: #4CAF50; " +
                                "-fx-padding: 2px 5px; " +
                                "-fx-background-radius: 3px;"
                );
                ((VBox)stationBox.getChildren().get(0)).getChildren().add(0, recommendedLabel);

                // Set as default selection
                ((RadioButton)stationBox.getChildren().get(1)).setSelected(true);
            }

            // Special highlighting for the start station
            if (station.equals(rental.getStart_station())) {
                Label originalLabel = new Label("ORIGINAL STATION");
                originalLabel.setStyle(
                        "-fx-font-size: 10px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-color: #2196F3; " +
                                "-fx-padding: 2px 5px; " +
                                "-fx-background-radius: 3px;"
                );
                ((VBox)stationBox.getChildren().get(0)).getChildren().add(0, originalLabel);
            }

            // Add the station box to the grid
            stationsGrid.add(stationBox, i % 3, i / 3);
        }

        stationsScroll.setContent(stationsGrid);

        // Add all components to the main content
        mainContent.getChildren().addAll(rentalSummary, estimatesGrid, stationsTitle, stationsScroll);

        // Apply styling to the dialog
        styleDialog(dialog, confirmButton);

        // Set the content of the dialog
        dialog.getDialogPane().setContent(mainContent);

        // Set minimum width for better readability
        dialog.getDialogPane().setMinWidth(650);

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

    private VBox createRentalSummarySection(BicycleRental rental) {
        VBox summaryBox = new VBox(10);
        summaryBox.setStyle(
                "-fx-background-color: #f8f9fa; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 15px;"
        );

        // Calculate rental duration
        long rentalDurationMillis = System.currentTimeMillis() - rental.getStart_time().getTime();
        long hours = rentalDurationMillis / (60 * 60 * 1000);
        long minutes = (rentalDurationMillis % (60 * 60 * 1000)) / (60 * 1000);

        Label titleLabel = new Label("Rental Summary");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(8);

        // Add rental details
        int row = 0;
        addDetailRow(detailsGrid, row++, "Bike ID:", rental.getBicycle().getId() + "");
        addDetailRow(detailsGrid, row++, "Rented From:", rental.getStart_station().getName());
        addDetailRow(detailsGrid, row++, "Rental Start:", formatTimestamp(rental.getStart_time()));
        addDetailRow(detailsGrid, row++, "Duration:", String.format("%d hours, %d minutes", hours, minutes));

        summaryBox.getChildren().addAll(titleLabel, detailsGrid);
        return summaryBox;
    }

    private void addDetailRow(GridPane grid, int row, String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");

        Label value = new Label(valueText);

        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private String formatTimestamp(Timestamp timestamp) {
        return timestamp.toLocalDateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm")
        );
    }

    private GridPane createEstimatesGrid(BicycleRental rental, List<Station> stations) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.setStyle(
                "-fx-background-color: #e9f5e9; " +
                        "-fx-border-color: #c3e6cb; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 15px;"
        );

        Label estimatesLabel = new Label("Return Estimates");
        estimatesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(estimatesLabel, 0, 0, 2, 1);

        // Find nearest station for best estimate
        Station nearestStation = findNearestStation(rental.getStart_station(), stations);
        double bestDistance = Location.calculateDistance(
                rental.getStart_station().getLocation(),
                nearestStation.getLocation()
        );
        float bestCost = rental.bestCost(rental, bestDistance);

        // Estimated cost range
        Label costLabel = new Label("Estimated Cost:");
        costLabel.setStyle("-fx-font-weight: bold;");
        Label costValue = new Label(String.format("%.2fTND (best rate)", bestCost));
        costValue.setStyle("-fx-text-fill: #28a745;");

        // Estimated distance
        Label distanceLabel = new Label("Trip Distance:");
        distanceLabel.setStyle("-fx-font-weight: bold;");
        Label distanceValue = new Label(String.format("%.2fKm (to nearest station)", bestDistance));

        // Add to grid
        grid.add(costLabel, 0, 1);
        grid.add(costValue, 1, 1);
        grid.add(distanceLabel, 0, 2);
        grid.add(distanceValue, 1, 2);

        return grid;
    }

    private Station findNearestStation(Station startStation, List<Station> stations) {
        Station nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Station station : stations) {
            double distance = Location.calculateDistance(
                    startStation.getLocation(),
                    station.getLocation()
            );

            if (distance < minDistance) {
                minDistance = distance;
                nearest = station;
            }
        }

        return nearest;
    }

    private VBox createStationSelectionBox(Station station, double distance, float cost, ToggleGroup group) {
        // Station info section
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);

        // Create an ImageView for the station icon
        Image image = new Image(getClass().getResourceAsStream("/images/station/icons/bike_station.png"));
        ImageView icon = new ImageView(image);
        icon.setFitWidth(40);
        icon.setFitHeight(40);

        // Station name
        Label nameLabel = new Label(station.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Add additional station info
        Label addressLabel = new Label(station.getLocation().getAddress());
        addressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        // Station metrics
        HBox metricsBox = new HBox(15);
        metricsBox.setAlignment(Pos.CENTER);

        Label distanceLabel = new Label(String.format("%.2fKm", distance));
        distanceLabel.setStyle("-fx-font-size: 12px;");

        Label costLabel = new Label(String.format("%.2f ", cost));
        costLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #28a745;");

        Label availabilityLabel = new Label("Docks: " + station.getAvailable_docks());
        availabilityLabel.setStyle("-fx-font-size: 12px;");

        metricsBox.getChildren().addAll(distanceLabel, costLabel, availabilityLabel);

        // Add all info components
        infoBox.getChildren().addAll(icon, nameLabel, addressLabel, metricsBox);

        // Create a radio button for station selection
        RadioButton radioButton = new RadioButton(station.getName());
        radioButton.setToggleGroup(group);
        radioButton.setStyle("-fx-font-size: 14px;");

        // Create the main container
        VBox stationBox = new VBox(10, infoBox, radioButton);
        stationBox.setAlignment(Pos.CENTER);
        stationBox.setPadding(new Insets(15));
        stationBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-padding: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 2);"
        );

        // Add hover effects
        stationBox.setOnMouseEntered(e -> stationBox.setStyle(
                stationBox.getStyle() + "-fx-background-color: #f0f0f0; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 7, 0, 0, 3);"
        ));

        stationBox.setOnMouseExited(e -> stationBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-padding: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 5, 0, 0, 2);"
        ));

        // Click on box selects the radio button
        stationBox.setOnMouseClicked(e -> radioButton.setSelected(true));

        return stationBox;
    }

    private void styleDialog(Dialog<Station> dialog, ButtonType confirmButton) {
        // Apply inline styling to the dialog pane
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);"
        );

        // Apply inline styling to the header panel
        dialog.getDialogPane().lookup(".header-panel").setStyle(
                "-fx-background-color: #6BBF59; " +
                        "-fx-border-color: #6BBF59; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 10px 10px 0 0; " +
                        "-fx-padding: 15px;"
        );

        // Apply inline styling to the header label
        dialog.getDialogPane().lookup(".header-panel .label").setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: 'Inter';"
        );

        // Apply inline styling to the content area
        dialog.getDialogPane().lookup(".content").setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-padding: 20px; " +
                        "-fx-spacing: 10px;"
        );

        // Style the buttons
        styleDialogButtons(dialog, confirmButton);
    }

    private void styleDialogButtons(Dialog<Station> dialog, ButtonType confirmButton) {
        // Apply inline styling to the buttons
        dialog.getDialogPane().lookup(".button-bar").setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-padding: 10px; " +
                        "-fx-spacing: 10px;"
        );

        // Apply inline styling to the confirm button
        Button confirmBtn = (Button) dialog.getDialogPane().lookupButton(confirmButton);
        confirmBtn.setStyle(
                "-fx-background-color: #6BBF59; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"
        );

        // Apply hover effect to the confirm button
        confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle(
                "-fx-background-color: #4E9D3A; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 7, 0, 0, 2);" +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; "
        ));

        confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle(
                "-fx-background-color: #6BBF59; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"
        ));

        // Apply inline styling to the cancel button
        Button cancelBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelBtn.setStyle(
                "-fx-background-color: #ff4444; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"
        );

        // Apply hover effect to the cancel button
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(
                "-fx-background-color: #e73737; " +
                        "-fx-cursor: hand; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"
        ));

        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(
                "-fx-background-color: #ff4444; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-font-family: 'Inter'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 1);"
        ));
    }

    private Region createFlexibleSpace() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS); // Make it grow and take up remaining space
        return region;
    }

}