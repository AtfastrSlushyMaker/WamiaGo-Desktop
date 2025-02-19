package controllers.trip;

import entities.Booking;
import entities.Trip;
import entities.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import services.BookingService;
import services.TripService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TripController {
    @FXML
    private TableView<Trip> triptable;

    @FXML
    private TableColumn<Trip, String> departure;

    @FXML
    private TableColumn<Trip, String> arrival;

    @FXML
    private TableColumn<Trip, String> departure_date;

    @FXML
    private TableColumn<Trip, Integer> seats;

    @FXML
    private TableColumn<Trip, Double> price;

    @FXML
    private TableColumn<Trip, Void> booking;

    @FXML
    private Button home_button;

    @FXML
    private Button rides_button;

    private TripService tripService;
    private BookingService bookingService;
    private Timeline timeline;

    @FXML
    public void initialize() {
        tripService = new TripService();
        bookingService = new BookingService();

        initializeTableColumns();
        configureBookingColumn();
        loadTrips();
        setupNavigation();


        // Set up a timeline to refresh the table every 5 seconds
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> loadTrips()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


    }
    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
    }
    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTableColumns() {
        departure.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        arrival.setCellValueFactory(new PropertyValueFactory<>("arrivalCity"));
        departure_date.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        seats.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        price.setCellValueFactory(new PropertyValueFactory<>("pricePerPassenger"));
    }

    private void configureBookingColumn() {
        booking.setCellFactory(param -> new TableCell<>() {
            private final Button bookingButton = new Button();
            private final ImageView bookingIcon = new ImageView();

            {
                try {
                    // Load icon with proper error handling
                    Image iconImage = new Image(getClass().getResourceAsStream("/images/icons/booking.png"));
                    bookingIcon.setImage(iconImage);
                    bookingIcon.setFitHeight(40);
                    bookingIcon.setFitWidth(40);

                    bookingButton.setGraphic(bookingIcon);
                    bookingButton.getStyleClass().add("icon-button");
                    bookingButton.setOnAction(event -> {
                        Trip trip = getTableView().getItems().get(getIndex());
                        handleBookingCreation(trip);
                    });

                    // Tooltip for better UX
                    Tooltip.install(bookingButton, new Tooltip("Book this trip"));
                } catch (NullPointerException e) {
                    System.err.println("Booking icon image not found!");
                    bookingButton.setText("Book");
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : bookingButton);
            }
        });
    }

    private void loadTrips() {
        try {
            ObservableList<Trip> trips = FXCollections.observableArrayList(tripService.read());
            triptable.setItems(trips);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Could not load trips: " + e.getMessage());
        }
    }

    private void handleBookingCreation(Trip trip) {
        try {
            Booking booking = new Booking();
            booking.setTrip(trip);
            User passenger = new User();
            passenger.setId(1); // Setting the passenger ID to 1
            booking.setPassenger(passenger);
            booking.setReservedSeats(1);
            booking.setStatus(Booking.Status.Pending);

            bookingService.create(booking);

            // Update available seats
            trip.setAvailableSeats(trip.getAvailableSeats() - booking.getReservedSeats());
            tripService.update(trip);

            showSuccessAlert("Booking Created", "Booking created successfully for trip ID: " + trip.getIdTrip());
        } catch (SQLException e) {
            showErrorAlert("Booking Error", "Failed to create booking for trip ID: " + trip.getIdTrip());
            e.printStackTrace();
        }
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}