package controllers.booking;

import entities.Booking;
import entities.Trip;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.BookingService;
import services.TripService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BookingController {
    @FXML
    private FlowPane BookingFlowPane;

    @FXML
    private Button home_button;

    @FXML
    private Button rides_button;

    private BookingService bookingService;
    private TripService tripService;

    @FXML
    public void initialize() {
        bookingService = new BookingService();
        tripService = new TripService();
        loadBookingsIntoFlowPane();
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
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBookingsIntoFlowPane() {
        try {
            User currentUser = SessionManager.getInstance().getUser(); // Get the current user from the session
            List<Booking> bookings = bookingService.read();
            List<Booking> userBookings = bookings.stream()
                    .filter(booking -> booking.getPassenger().getId() == currentUser.getId()) // Filter bookings by current user's ID
                    .collect(Collectors.toList());

            for (Booking booking : userBookings) {
                Trip trip = tripService.getById(booking.getTrip().getIdTrip());
                VBox bookingCard = createBookingCard(trip, booking);

                // Apply inline styles for the booking card
                bookingCard.setStyle(
                        "-fx-background-color: #1E90FF;" +
                                "-fx-border-color: #4682B4;" +
                                "-fx-border-radius: 10px;" +
                                "-fx-background-radius: 10px;" +
                                "-fx-padding: 20px;" +
                                "-fx-spacing: 15px;" +
                                "-fx-alignment: center;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0, 0, 5);"
                );

                BookingFlowPane.getChildren().add(bookingCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private VBox createBookingCard(Trip trip, Booking booking) {
        VBox bookingCard = new VBox(10);
        bookingCard.setPadding(new Insets(10));
        bookingCard.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5px;");
        bookingCard.setAlignment(javafx.geometry.Pos.CENTER);

        Label routeLabel = new Label(trip.getDepartureCity() + " to " + trip.getArrivalCity());
        routeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label dateLabel = new Label("Departure Date: " + trip.getDepartureDate());
        dateLabel.setStyle("-fx-font-size: 14px;");

        Button moreDetailsButton = new Button("More Details");
        moreDetailsButton.setOnAction(e -> showBookingDetails(booking));

        Button abortButton = new Button("Abort");
        abortButton.setOnAction(e -> handleDeleteBooking(booking, bookingCard));

        HBox buttonBox = new HBox(10, moreDetailsButton, abortButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        bookingCard.getChildren().addAll(routeLabel, dateLabel, buttonBox);
        return bookingCard;
    }

    private void showBookingDetails(Booking booking) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Booking Details");

        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(20));
        detailsBox.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label statusLabel = new Label("Status: " + booking.getStatus());
        statusLabel.setStyle("-fx-text-fill: white;");

        Label seatsLabel = new Label("Reserved Seats: " + booking.getReservedSeats());
        seatsLabel.setStyle("-fx-text-fill: white;");

        Button minusButton = new Button();
        ImageView minusIcon = new ImageView(new Image(getClass().getResource("/images/icons/minus1.png").toExternalForm()));
        minusIcon.setFitHeight(20);
        minusIcon.setFitWidth(20);
        minusButton.setGraphic(minusIcon);
        minusButton.setOnAction(e -> handleMinusButton(booking, seatsLabel));

        Button addButton = new Button();
        ImageView addIcon = new ImageView(new Image(getClass().getResource("/images/icons/add1.png").toExternalForm()));
        addIcon.setFitHeight(20);
        addIcon.setFitWidth(20);
        addButton.setGraphic(addIcon);
        addButton.setOnAction(e -> handleAddButton(booking, seatsLabel));

        HBox seatsBox = new HBox(10, minusButton, seatsLabel, addButton);
        seatsBox.setAlignment(javafx.geometry.Pos.CENTER);

        detailsBox.getChildren().addAll(statusLabel, seatsBox);

        Scene modalScene = new Scene(detailsBox, 300, 200);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void handleMinusButton(Booking booking, Label seatsLabel) {
        if (booking.getReservedSeats() > 1) {
            booking.setReservedSeats(booking.getReservedSeats() - 1);
            updateBooking(booking);
            seatsLabel.setText("Reserved Seats: " + booking.getReservedSeats());
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText(null);
            alert.setContentText("You're going to abort. Do you want to proceed?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    handleDeleteBooking(booking, null);
                }
            });
        }
    }

    private void handleAddButton(Booking booking, Label seatsLabel) {
        try {
            Trip trip = tripService.getById(booking.getTrip().getIdTrip());
            if (booking.getReservedSeats() < trip.getAvailableSeats()) {
                booking.setReservedSeats(booking.getReservedSeats() + 1);
                updateBooking(booking);
                seatsLabel.setText("Reserved Seats: " + booking.getReservedSeats());
            } else {
                showErrorAlert("Limit Reached", "Cannot reserve more seats than available.");
            }
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Could not load trip details: " + e.getMessage());
        }
    }

    private void updateBooking(Booking booking) {
        try {
            bookingService.update(booking);
        } catch (SQLException e) {
            showErrorAlert("Update Error", "Failed to update booking: " + e.getMessage());
        }
    }

    private void handleDeleteBooking(Booking booking, VBox bookingCard) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Do you really want to abort?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    bookingService.delete(booking.getIdBooking());
                    if (bookingCard != null) {
                        BookingFlowPane.getChildren().remove(bookingCard);
                    }
                    showSuccessAlert("Booking Deleted", "Booking aborted successfully.");
                } catch (SQLException e) {
                    showErrorAlert("Deletion Error", "Failed to abort booking: " + e.getMessage());
                }
            }
        });
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