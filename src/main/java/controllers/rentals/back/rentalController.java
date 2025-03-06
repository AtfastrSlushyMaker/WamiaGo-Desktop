package controllers.rentals.back;

import entities.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import services.BicycleRentalService;
import services.BicycleService;
import services.StationService;
import services.UserService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class rentalController {
    @FXML
    private FlowPane rentals_container;
    @FXML
    private Button add_button;
    @FXML
    private Button delete_button;
    @FXML
    private Button refresh_button;
    @FXML
    private TextField search_field;
    @FXML
    private Button search_button;
    @FXML
    private ComboBox<String> filter_status;

    private final BicycleRentalService rentalService = new BicycleRentalService();
    private final UserService userService = new UserService();
    private final BicycleService bicycleService = new BicycleService();
    private final StationService stationService = new StationService();

    private List<BicycleRental> allRentals = new ArrayList<>();


    private List<BicycleRental> selectedRentals = new ArrayList<>();
    @FXML
    public void initialize() {
        setupFilters();
        setupButtons();
        setupSearch();

        // Add this for debugging
        System.out.println("Starting to load rentals...");
        try {
            List<BicycleRental> rentals = rentalService.read();
            System.out.println("Loaded " + rentals.size() + " rentals");
            allRentals = rentals;
            displayRentals(rentals);
        } catch (SQLException e) {
            System.err.println("Error loading rentals: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load rentals: " + e.getMessage());
        }
    }

    private void setupFilters() {
        filter_status.getItems().addAll("All", "Active", "Completed");
        filter_status.setValue("All");
        filter_status.setOnAction(e -> filterRentals());
    }

    private void setupButtons() {
        add_button.setOnAction(event -> showRentalDialog(null));
        delete_button.setOnAction(event -> deleteSelectedRentals());
        refresh_button.setOnAction(event -> loadRentals());
    }

    private void setupSearch() {
        search_field.setOnKeyReleased(event -> {
                filterRentals();
        });
    }

    private void loadRentals() {
        try {
            allRentals = rentalService.read();

            // Debug - check IDs after loading
            System.out.println("=== Loaded Rentals ===");
            for (BicycleRental rental : allRentals) {
                System.out.println("Loaded Rental ID: " + rental.getId() +
                        ", User: " + (rental.getUser() != null ? rental.getUser().getName() : "null"));
            }

            filterRentals();
        } catch (Exception e) {
            showAlert("Error", "Failed to load rentals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterRentals() {
        if (allRentals == null) return;

        String searchTerm = search_field.getText().toLowerCase();
        String statusFilter = filter_status.getValue();

        List<BicycleRental> filtered = allRentals.stream()
                .filter(rental -> {
                    boolean statusMatch = "All".equals(statusFilter) ||
                            ("Active".equals(statusFilter) && rental.getEnd_time() == null) ||
                            ("Completed".equals(statusFilter) && rental.getEnd_time() != null);

                    // Search only by user name for clarity
                    boolean searchMatch = searchTerm.isEmpty() ||
                            (rental.getUser() != null &&
                                    rental.getUser().getName() != null &&
                                    rental.getUser().getName().toLowerCase().contains(searchTerm));

                    return statusMatch && searchMatch;
                })
                .collect(Collectors.toList());

        displayRentals(filtered);
    }

    private void displayRentals(List<BicycleRental> rentals) {
        rentals_container.getChildren().clear();
        selectedRentals.clear(); // Clear selection when displaying new rentals

        for (BicycleRental rental : rentals) {
            VBox card = createRentalCard(rental);
            rentals_container.getChildren().add(card);
        }
    }

    private VBox createRentalCard(BicycleRental rental) {
        // Debug output to check ID
        System.out.println("Creating card for rental #" + rental.getId());

        VBox card = new VBox(10);
        card.getStyleClass().add("rental-card");
        card.setUserData(rental); // Store the complete rental object with correct ID

        // Card Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        // Add checkbox for selection
        CheckBox selectBox = new CheckBox();
        selectBox.setOnAction(e -> {
            // Prevent event propagation to the card
            e.consume();

            // Get the rental directly from the card's userData
            BicycleRental storedRental = (BicycleRental) card.getUserData();
            System.out.println("Checkbox selection for rental #" + storedRental.getId());

            updateCardSelection(card, selectBox.isSelected(), storedRental);
        });

        ImageView bikeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/station/icons/bike_rental.png")));
        bikeIcon.setFitHeight(24);
        bikeIcon.setFitWidth(24);

        Label idLabel = new Label("Rental #" + rental.getId());
        idLabel.getStyleClass().add("rental-id");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(rental.getEnd_time() == null ? "Active" : "Completed");
        statusLabel.getStyleClass().addAll("rental-status",
                rental.getEnd_time() == null ? "status-active" : "status-completed");

        header.getChildren().addAll(selectBox, bikeIcon, idLabel, spacer, statusLabel);

        // Card Body
        VBox body = new VBox(8);
        body.getStyleClass().add("rental-card-body");

        // User info
        HBox userInfo = createInfoRow("User:", rental.getUser().getName());

        // Bike info
        HBox bikeInfo = createInfoRow("Bicycle:", "Bike #" + rental.getBicycle().getId());

        // Duration
        String duration = rental.getDuration_minutes() + " min";
        HBox durationInfo = createInfoRow("Duration:", duration);

        // Distance
        String distance = rental.getDistance_km() + " km";
        HBox distanceInfo = createInfoRow("Distance:", distance);

        // Cost
        String cost = "$" + rental.getCost();
        HBox costInfo = createInfoRow("Cost:", cost);

        // Stations
        String startStation = rental.getStart_station() != null ?
                rental.getStart_station().getName() : "N/A";
        String endStation = rental.getEnd_station() != null ?
                rental.getEnd_station().getName() : "N/A";

        HBox startInfo = createInfoRow("From:", startStation);
        HBox endInfo = createInfoRow("To:", endStation);

        body.getChildren().addAll(userInfo, bikeInfo, durationInfo,
                distanceInfo, costInfo, startInfo, endInfo);

        card.getChildren().addAll(header, body);

        // Add click events - preserving double-click
        card.setOnMouseClicked(e -> {
            // Get the rental directly from the card's userData
            BicycleRental storedRental = (BicycleRental) card.getUserData();

            if (e.getClickCount() == 2) {
                System.out.println("Double-click on rental #" + storedRental.getId());
                showRentalDialog(storedRental);
            } else if (e.getClickCount() == 1) {
                System.out.println("Single-click on rental #" + storedRental.getId());
                // Toggle checkbox on single click of the card
                selectBox.setSelected(!selectBox.isSelected());
                updateCardSelection(card, selectBox.isSelected(), storedRental);
            }
        });

        return card;
    }

    // New method to handle card selection state
    private void updateCardSelection(VBox card, boolean selected, BicycleRental rental) {
        if (selected) {
            if (!card.getStyleClass().contains("selected-card")) {
                card.getStyleClass().add("selected-card");
            }
            if (!selectedRentals.contains(rental)) {
                // Make sure we're adding the actual rental object with the correct ID
                selectedRentals.add(rental);
                System.out.println("Added rental #" + rental.getId() + " to selection"); // Debug line
            }
        } else {
            card.getStyleClass().remove("selected-card");
            selectedRentals.remove(rental);
            System.out.println("Removed rental #" + rental.getId() + " from selection"); // Debug line
        }
    }

    private HBox createInfoRow(String label, String value) {
        HBox row = new HBox(5);

        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("rental-info-label");

        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("rental-info-value");

        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    // Now fix the dialog display issue
    private void showRentalDialog(BicycleRental existingRental) {
        Dialog<BicycleRental> dialog = new Dialog<>();
        dialog.setTitle(existingRental == null ? "New Rental" : "Edit Rental #" + existingRental.getId());

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
        CheckBox completeRentalCheckBox = new CheckBox("Complete Rental");

        // Configure User ComboBox - key fix is here
        userCombo.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText("");
                } else {
                    setText(user.getName()); // Only show the name, nothing else
                }
            }
        });

        // Use the same approach for the button cell
        userCombo.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText("");
                } else {
                    setText(user.getName()); // Only show the name, nothing else
                }
            }
        });

        // Configure Bicycle ComboBox with improved description
        bikeCombo.setCellFactory(lv -> new ListCell<Bicycle>() {
            @Override
            protected void updateItem(Bicycle bike, boolean empty) {
                super.updateItem(bike, empty);
                setText(empty || bike == null ? "" : "Bike #" + bike.getId() + " - " + "At " + bike.getStation().getName() + " (" + bike.getStatus() + ")");
            }
        });
        bikeCombo.setButtonCell(new ListCell<Bicycle>() {
            @Override
            protected void updateItem(Bicycle bike, boolean empty) {
                super.updateItem(bike, empty);
                setText(empty || bike == null ? "" : "Bike #" + bike.getId());
            }
        });

        // Configure Station ComboBoxes with more details
        for (ComboBox<Station> stationCombo : new ComboBox[]{startStationCombo, endStationCombo}) {
            stationCombo.setCellFactory(lv -> new ListCell<Station>() {
                @Override
                protected void updateItem(Station station, boolean empty) {
                    super.updateItem(station, empty);
                    setText(empty || station == null ? "" : station.getName() + " (" + station.getLocation().getAddress() + ")");
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

        // Auto-calculate fields
        if (existingRental == null) {
            // For new rentals, set default values
            distanceField.setText("0.0");
            batteryField.setText("0.0");
            costField.setText("0.0");
        }

        // Add listeners to auto-calculate cost based on distance
        distanceField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                float distance = Float.parseFloat(newVal);
                // Basic cost calculation: $1 base + $0.50 per km
                float calculatedCost = 1.0f + (distance * 0.5f);
                costField.setText(String.format("%.2f", calculatedCost));
            } catch (NumberFormatException e) {
                // Do nothing if invalid input
            }
        });

        // Load data into combo boxes
        try {
            userCombo.getItems().addAll(userService.read());
            bikeCombo.getItems().addAll(bicycleService.read());
            startStationCombo.getItems().addAll(stationService.read());
            endStationCombo.getItems().addAll(stationService.read());
        } catch (Exception e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }

        // Special handling for the Complete Rental checkbox
        if (existingRental != null) {
            // Set checkbox state based on rental status
            completeRentalCheckBox.setSelected(existingRental.getEnd_time() != null);

            // If it's already completed, disable fields
            if (existingRental.getEnd_time() != null) {
                endStationCombo.setDisable(false);
                completeRentalCheckBox.setDisable(true);
            }
        } else {
            // For new rentals, end station and complete checkbox should be disabled initially
            endStationCombo.setDisable(true);
            completeRentalCheckBox.setDisable(true);
        }

        // Populate fields if editing
        if (existingRental != null) {
            userCombo.setCellFactory(lv -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText("");
                    } else {
                        setText(user.getName());
                    }
                }
            });

            userCombo.setButtonCell(new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText("");
                    } else {
                        setText(user.getName());
                    }
                }
            });
            bikeCombo.setValue(existingRental.getBicycle());
            startStationCombo.setValue(existingRental.getStart_station());
            endStationCombo.setValue(existingRental.getEnd_station());
            distanceField.setText(String.valueOf(existingRental.getDistance_km()));
            batteryField.setText(String.valueOf(existingRental.getBattery_used()));
            costField.setText(String.valueOf(existingRental.getCost()));

            // For ongoing rentals, enable complete checkbox
            if (existingRental.getEnd_time() == null) {
                completeRentalCheckBox.setDisable(false);

                // When complete checkbox is clicked, enable end station selection
                completeRentalCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    endStationCombo.setDisable(!newVal);
                });
            }
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

        if (existingRental != null && existingRental.getEnd_time() == null) {
            grid.add(completeRentalCheckBox, 1, 7);
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                if (validateInput(
                        userCombo.getValue(),
                        bikeCombo.getValue(),
                        startStationCombo.getValue(),
                        existingRental != null && completeRentalCheckBox.isSelected() ? endStationCombo.getValue() : null,
                        distanceField.getText(),
                        batteryField.getText(),
                        costField.getText()
                )) {
                    BicycleRental rental = existingRental != null ? existingRental : new BicycleRental();
                    rental.setId(existingRental.getId());
                    rental.setUser(userCombo.getValue());
                    rental.setBicycle(bikeCombo.getValue());
                    rental.setStart_station(startStationCombo.getValue());

                    // Set end station and end time if completing rental
                    if (existingRental != null && completeRentalCheckBox.isSelected()) {
                        rental.setEnd_station(endStationCombo.getValue());
                        rental.setEnd_time(new Timestamp(System.currentTimeMillis()));

                        // Calculate duration in minutes if completing
                        LocalDateTime startTime = rental.getStart_time().toLocalDateTime();
                        LocalDateTime endTime = rental.getEnd_time().toLocalDateTime();
                    } else {
                        rental.setEnd_station(endStationCombo.getValue());
                    }

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

                // If completing a rental, update bicycle status
                if (existingRental != null && completeRentalCheckBox.isSelected()) {
                    Bicycle bike = rental.getBicycle();
                    bike.setStatus(Bicycle.STATUS.available);
                    bike.setStation(rental.getEnd_station());
                    bicycleService.update(bike);
                }

                loadRentals();
            } catch (Exception e) {
                showAlert("Error", "Failed to save rental: " + e.getMessage());
            }
        });
    }

    private void deleteSelectedRentals() {
        if (selectedRentals.isEmpty()) {
            showAlert("No Selection", "Please select rentals to delete");
            return;
        }

        // Debug line to check IDs before deletion
        for (BicycleRental rental : selectedRentals) {
            System.out.println("Selected for deletion: Rental #" + rental.getId());
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Rentals");
        alert.setHeaderText("Are you sure you want to delete " + selectedRentals.size() + " rentals?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            for (BicycleRental rental : selectedRentals) {
                try {
                    int rentalId = rental.getId();
                    if (rentalId <= 0) {
                        showAlert("Error", "Invalid rental ID: " + rentalId);
                        continue;
                    }

                    // Delete the rental
                    System.out.println("Deleting rental #" + rentalId); // Debug line
                    rentalService.delete(rentalId);

                    // Handle bicycle status if needed
                    if (rental.getEnd_time() == null) {
                        Bicycle bike = rental.getBicycle();
                        bike.setStatus(Bicycle.STATUS.available);
                        bike.setStation(rental.getStart_station());
                        bicycleService.update(bike);
                    }
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete rental #" + rental.getId() + ": " + e.getMessage());
                    e.printStackTrace(); // Print stack trace for debugging
                }
            }

            // Clear the selection and reload
            selectedRentals.clear();
            loadRentals();
        }
    }

    private boolean validateInput(User user, Bicycle bike, Station startStation,
                                  Station endStation, String distance, String battery, String cost) {
        if (user == null || bike == null || startStation == null) {
            showAlert("Invalid Input", "Please select user, bicycle, and start station");
            return false;
        }

        // If an end station is provided (completing a rental), validate it
        if (endStation == null ) {
            showAlert("Invalid Input", "Please select an end station to complete the rental");
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