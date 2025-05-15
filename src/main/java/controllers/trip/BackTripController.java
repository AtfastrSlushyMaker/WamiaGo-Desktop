package controllers.trip;

import entities.Booking;
import entities.Driver;
import entities.Trip;
import entities.User;
import entities.Vehicle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.BookingService;
import services.TripService;
import services.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class BackTripController {
    @FXML
    private Text Username;

    @FXML
    private Button accountSetting;

    @FXML
    private Button car;
    @FXML
    private DatePicker date;
    @FXML
    private TextField price;

    @FXML
    private TextField seats;
    @FXML
    private Button carpooling;

    @FXML
    private Button closeBtn;

    @FXML
    private FlowPane viewtrips;

    @FXML
    private FlowPane viewbooking;

    @FXML
    private ComboBox<String> arrival;

    @FXML
    private ComboBox<String> departure;

    private TripService tripService;
    private BookingService bookingService;
    private UserService userService;

    private static final String[] CITIES = {
        "Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès", "Gafsa", "Jendouba", "Kairouan", "Kasserine", "Kebili",
        "Kef", "Mahdia", "Manouba", "Medenine", "Monastir", "Nabeul", "Sfax", "Sidi Bouzid", "Siliana", "Sousse",
        "Tataouine", "Tozeur", "Tunis", "Zaghouan"
    };

    @FXML
    public void initialize() {
        departure.setItems(FXCollections.observableArrayList(CITIES));
        arrival.setItems(FXCollections.observableArrayList(CITIES));
        tripService = new TripService();
        bookingService = new BookingService();
        userService = new UserService();
        loadTripsIntoFlowPane();
        loadBookingsIntoFlowPane();
        car.setOnAction(e -> handleCreateTrip());
    }

    private void loadTripsIntoFlowPane() {
        try {
            List<Trip> trips = tripService.read();
            for (Trip trip : trips) {
                VBox tripCard = createTripCard(trip);
                viewtrips.getChildren().add(tripCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBookingsIntoFlowPane() {
        try {
            List<Booking> bookings = bookingService.read();
            for (Booking booking : bookings) {
                VBox bookingCard = createBookingCard(booking);
                viewbooking.getChildren().add(bookingCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createTripCard(Trip trip) {
        VBox tripCard = new VBox(10);
        tripCard.setPadding(new Insets(10));
        tripCard.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5px;");
        tripCard.setAlignment(javafx.geometry.Pos.CENTER);

        Label routeLabel = new Label(trip.getDepartureCity() + " to " + trip.getArrivalCity());
        routeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label dateLabel = new Label("Departure Date: " + trip.getDepartureDate());
        dateLabel.setStyle("-fx-font-size: 14px;");

        Label seatsLabel = new Label("Available Seats: " + trip.getAvailableSeats());
        seatsLabel.setStyle("-fx-font-size: 14px;");

        Label priceLabel = new Label("Price per Passenger: " + trip.getPricePerPassenger());
        priceLabel.setStyle("-fx-font-size: 14px;");

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> showUpdateTripWindow(trip, tripCard));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDeleteTrip(trip, tripCard));

        HBox buttonBox = new HBox(10, updateButton, deleteButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        tripCard.getChildren().addAll(routeLabel, dateLabel, seatsLabel, priceLabel, buttonBox);
        return tripCard;
    }

    private VBox createBookingCard(Booking booking) {
        VBox bookingCard = new VBox(10);
        bookingCard.setPadding(new Insets(10));
        bookingCard.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5px;");
        bookingCard.setAlignment(javafx.geometry.Pos.CENTER);

        try {
            User passenger = userService.getById(booking.getPassenger().getId());
            Label passengerLabel = new Label("Passenger: " + passenger.getName());
            passengerLabel.setStyle("-fx-font-size: 14px;");

            Label seatsLabel = new Label("Reserved Seats: " + booking.getReservedSeats());
            seatsLabel.setStyle("-fx-font-size: 14px;");

            Label statusLabel = new Label("Status: " + booking.getStatus());
            statusLabel.setStyle("-fx-font-size: 14px;");

            if (booking.getStatus() != Booking.Status.Canceled) {
                Button statusButton = new Button();
                if (booking.getStatus() == Booking.Status.Confirmed) {
                    statusButton.setText("Cancel");
                    statusButton.setOnAction(e -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Cancel Booking");
                        alert.setHeaderText(null);
                        alert.setContentText("Do you really want to cancel?");
                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                booking.setStatus(Booking.Status.Canceled);
                                try {
                                    bookingService.update(booking);
                                    statusLabel.setText("Status: " + booking.getStatus());
                                    statusButton.setVisible(false);
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    });
                } else if (booking.getStatus() == Booking.Status.Pending) {
                    statusButton.setText("Confirm");
                    statusButton.setOnAction(e -> {
                        booking.setStatus(Booking.Status.Confirmed);
                        try {
                            bookingService.update(booking);
                            statusLabel.setText("Status: " + booking.getStatus());
                            statusButton.setText("Cancel");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                bookingCard.getChildren().add(statusButton);
            }

            bookingCard.getChildren().addAll(passengerLabel, seatsLabel, statusLabel);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookingCard;
    }

    private void showUpdateTripWindow(Trip trip, VBox tripCard) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Update Trip");

        VBox updateBox = new VBox(10);
        updateBox.setPadding(new Insets(20));
        updateBox.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        ComboBox<String> departureComboBox = new ComboBox<>(FXCollections.observableArrayList(CITIES));
        departureComboBox.setValue(trip.getDepartureCity());

        ComboBox<String> arrivalComboBox = new ComboBox<>(FXCollections.observableArrayList(CITIES));
        arrivalComboBox.setValue(trip.getArrivalCity());

        DatePicker datePicker = new DatePicker(convertToLocalDate(trip.getDepartureDate()));
        Label seatsLabel = new Label("Available Seats: " + trip.getAvailableSeats());
        seatsLabel.setStyle("-fx-text-fill: white;");

        Button minusSeatsButton = new Button();
        ImageView minusSeatsIcon = new ImageView(new Image(getClass().getResource("/images/icons/minus1.png").toExternalForm()));
        minusSeatsIcon.setFitHeight(20);
        minusSeatsIcon.setFitWidth(20);
        minusSeatsButton.setGraphic(minusSeatsIcon);
        minusSeatsButton.setOnAction(e -> {
            if (trip.getAvailableSeats() > 1) {
                trip.setAvailableSeats(trip.getAvailableSeats() - 1);
                seatsLabel.setText("Available Seats: " + trip.getAvailableSeats());
            }
        });

        Button addSeatsButton = new Button();
        ImageView addSeatsIcon = new ImageView(new Image(getClass().getResource("/images/icons/add1.png").toExternalForm()));
        addSeatsIcon.setFitHeight(20);
        addSeatsIcon.setFitWidth(20);
        addSeatsButton.setGraphic(addSeatsIcon);
        addSeatsButton.setOnAction(e -> {
            trip.setAvailableSeats(trip.getAvailableSeats() + 1);
            seatsLabel.setText("Available Seats: " + trip.getAvailableSeats());
        });

        HBox seatsBox = new HBox(10, minusSeatsButton, seatsLabel, addSeatsButton);
        seatsBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label priceLabel = new Label("Price per Passenger: " + trip.getPricePerPassenger());
        priceLabel.setStyle("-fx-text-fill: white;");

        Button minusPriceButton = new Button();
        ImageView minusPriceIcon = new ImageView(new Image(getClass().getResource("/images/icons/minus1.png").toExternalForm()));
        minusPriceIcon.setFitHeight(20);
        minusPriceIcon.setFitWidth(20);
        minusPriceButton.setGraphic(minusPriceIcon);
        minusPriceButton.setOnAction(e -> {
            if (trip.getPricePerPassenger() > 1) {
                trip.setPricePerPassenger(trip.getPricePerPassenger() - 1);
                priceLabel.setText("Price per Passenger: " + trip.getPricePerPassenger());
            }
        });

        Button addPriceButton = new Button();
        ImageView addPriceIcon = new ImageView(new Image(getClass().getResource("/images/icons/add1.png").toExternalForm()));
        addPriceIcon.setFitHeight(20);
        addPriceIcon.setFitWidth(20);
        addPriceButton.setGraphic(addPriceIcon);
        addPriceButton.setOnAction(e -> {
            trip.setPricePerPassenger(trip.getPricePerPassenger() + 1);
            priceLabel.setText("Price per Passenger: " + trip.getPricePerPassenger());
        });

        HBox priceBox = new HBox(10, minusPriceButton, priceLabel, addPriceButton);
        priceBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            trip.setDepartureCity(departureComboBox.getValue());
            trip.setArrivalCity(arrivalComboBox.getValue());
            trip.setDepartureDate(convertToDate(datePicker.getValue()));
            updateTrip(trip, tripCard);
            modalStage.close();
        });

        updateBox.getChildren().addAll(departureComboBox, arrivalComboBox, datePicker, seatsBox, priceBox, saveButton);

        Scene modalScene = new Scene(updateBox, 300, 400);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void updateTrip(Trip trip, VBox tripCard) {
        try {
            tripService.update(trip);
            viewtrips.getChildren().remove(tripCard);
            viewtrips.getChildren().add(createTripCard(trip));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteTrip(Trip trip, VBox tripCard) {
        try {
            tripService.delete(trip.getIdTrip());
            viewtrips.getChildren().remove(tripCard);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleCreateTrip() {
        String departureCity = departure.getValue();
        String arrivalCity = arrival.getValue();
        LocalDate departureDate = date.getValue();
        String priceText = price.getText();
        String seatsText = seats.getText();

        if (departureCity == null || arrivalCity == null || departureDate == null || priceText.isEmpty() || seatsText.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled.");
            return;
        }

        try {
            double priceValue = Double.parseDouble(priceText);
            int seatsValue = Integer.parseInt(seatsText);

            Trip newTrip = new Trip();
            Driver driver = new Driver();
            driver.setIdDriver(1); // Dummy value for driver_id
            newTrip.setDriver(driver);
            Vehicle vehicle = new Vehicle();
            vehicle.setIdVehicle(1); // Dummy value for vehicle_id
            newTrip.setVehicle(vehicle);
            newTrip.setDepartureCity(departureCity);
            newTrip.setArrivalCity(arrivalCity);
            newTrip.setDepartureDate(convertToDate(departureDate));
            newTrip.setPricePerPassenger(priceValue);
            newTrip.setAvailableSeats(seatsValue);

            tripService.create(newTrip);
            loadTripsIntoFlowPane();
            showAlert("Success", "Trip created with success.");
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Seats and Price must be valid numbers.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {
        if (dateToConvert instanceof java.sql.Date) {
            return ((java.sql.Date) dateToConvert).toLocalDate();
        } else {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}