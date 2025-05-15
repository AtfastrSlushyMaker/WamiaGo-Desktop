package controllers.trip;

import entities.Booking;
import entities.Trip;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.BookingService;
import services.TripService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TripController {
    @FXML
    private FlowPane TripFlowPane;

    @FXML
    private Button home_button;

    @FXML
    private Button rides_button;

    private TripService tripService;
    private BookingService bookingService;

    @FXML
    public void initialize() {
        tripService = new TripService();
        bookingService = new BookingService();
        loadTripsIntoFlowPane();
        setupNavigation();
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/trips/trip.css").toExternalForm());
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTripsIntoFlowPane() {
        try {
            List<Trip> trips = tripService.read();
            Map<String, List<Trip>> groupedTrips = trips.stream()
                    .collect(Collectors.groupingBy(trip -> trip.getDepartureCity() + " to " + trip.getArrivalCity()));

            for (Map.Entry<String, List<Trip>> entry : groupedTrips.entrySet()) {
                VBox tripCard = createTripCard(entry.getKey(), entry.getValue());
                TripFlowPane.getChildren().add(tripCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createTripCard(String route, List<Trip> trips) {
        VBox tripCard = new VBox(20); // Increased spacing between elements
        tripCard.setPadding(new Insets(20)); // Increased padding
        tripCard.setStyle("-fx-background-color: #1E90FF; -fx-border-color: #4682B4; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0, 0, 5);");
        tripCard.setAlignment(javafx.geometry.Pos.CENTER);

        tripCard.setPrefSize(300, 200); // Increased size of the card

        Label routeLabel = new Label(route);
        routeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button selectButton = new Button("Select");
        selectButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: white; -fx-border-width: 2px;");
        selectButton.setOnAction(e -> openTripOptions(trips));
        selectButton.setOnMouseEntered(e -> selectButton.setStyle("-fx-background-color: #5A9BD4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: white; -fx-border-width: 2px;"));
        selectButton.setOnMouseExited(e -> selectButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: white; -fx-border-width: 2px;"));

        tripCard.getChildren().addAll(routeLabel, selectButton);
        return tripCard;
    }

    private void openTripOptions(List<Trip> trips) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Select a Trip");

        FlowPane tripOptionsPane = new FlowPane(10, 10);
        tripOptionsPane.setPadding(new Insets(20));
        tripOptionsPane.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        for (Trip trip : trips) {
            Button tripButton = createTripButton(trip);
            tripOptionsPane.getChildren().add(tripButton);
        }

        Scene modalScene = new Scene(tripOptionsPane, 400, 300);
        modalScene.getStylesheets().add(getClass().getResource("/trips/trip.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private Button createTripButton(Trip trip) {
        Button tripButton = new Button();
        ImageView carIcon = new ImageView(new Image(getClass().getResource("/images/icons/car.png").toExternalForm()));
        carIcon.setFitHeight(40);
        carIcon.setFitWidth(40);
        tripButton.setGraphic(carIcon);
        tripButton.setOnAction(e -> showTripDetails(trip));
        return tripButton;
    }

    private void showTripDetails(Trip trip) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Trip Details");

        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label priceLabel = new Label("Price per Passenger: " + trip.getPricePerPassenger());
        priceLabel.setStyle("-fx-text-fill: white;");

        Label dateLabel = new Label("Departure Date: " + trip.getDepartureDate());
        dateLabel.setStyle("-fx-text-fill: white;");

        Button reserveButton = new Button("Reserve");
        reserveButton.setOnAction(e -> handleBookingCreation(trip));

        detailsBox.getChildren().addAll(priceLabel, dateLabel, reserveButton);

        Scene modalScene = new Scene(detailsBox, 300, 200);
        modalScene.getStylesheets().add(getClass().getResource("/trips/trip.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }

private void handleBookingCreation(Trip trip) {
    try {
        Booking booking = new Booking();
        booking.setTrip(trip);
        User currentUser = SessionManager.getInstance().getUser(); // Get the current user from the session
        booking.setPassenger(currentUser); // Set the current user as the passenger
        booking.setReservedSeats(1); // Assuming 1 seat is reserved
        booking.setStatus(Booking.Status.Pending); // Assuming the initial status is Pending

        bookingService.create(booking);
        showSuccessAlert("Booking Created", "Booking created successfully for trip ID: " + trip.getIdTrip());
    } catch (SQLException e) {
        showErrorAlert("Booking Error", "Failed to create booking for trip ID: " + trip.getIdTrip());
        e.printStackTrace();
    }
}

    private void showSuccessAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}