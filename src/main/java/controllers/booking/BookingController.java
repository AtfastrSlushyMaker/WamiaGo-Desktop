package controllers.booking;

import entities.Booking;
import entities.Trip;
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
import services.BookingService;
import services.TripService;

import java.io.IOException;
import java.sql.SQLException;

public class BookingController {
    @FXML
    private TableView<Booking> bookigtable;

    @FXML
    private TableColumn<Booking, Integer> seats;

    @FXML
    private TableColumn<Booking, String> status;

    @FXML
    private TableColumn<Booking, Void> minus;

    @FXML
    private TableColumn<Booking, Void> add;

    @FXML
    private TableColumn<Booking, Void> delete;

    @FXML
    private TableColumn<Booking, Void> call;

    @FXML
    private Button home_button;

    @FXML
    private Button rides_button;

    private BookingService bookingService;
    private TripService tripService;
    private ObservableList<Booking> bookingsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        bookingService = new BookingService();
        tripService = new TripService();

        initializeTableColumns();
        loadBookings();
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

    private void initializeTableColumns() {
        seats.setCellValueFactory(new PropertyValueFactory<>("reservedSeats"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        configureButtonColumn(minus, "Minus", "/images/icons/minus.png", this::handleMinusButton);
        configureButtonColumn(add, "Add", "/images/icons/add.png", this::handleAddButton);
        configureButtonColumn(delete, "Abort", "/images/icons/delete.png", booking -> {
            handleDeleteBooking(booking);
            return null;
        });
        configureButtonColumn(call, "Call", "/images/icons/call.png", booking -> {
            // Implement the call action here
            return null;
        });
    }

    private void configureButtonColumn(TableColumn<Booking, Void> column, String tooltipText, String iconPath, Callback<Booking, Void> action) {
        column.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button();
            private final ImageView icon = new ImageView();

            {
                try {
                    Image iconImage = new Image(getClass().getResourceAsStream(iconPath));
                    icon.setImage(iconImage);
                    icon.setFitHeight(40);
                    icon.setFitWidth(40);

                    button.setGraphic(icon);
                    button.getStyleClass().add("icon-button");
                    button.setOnAction(event -> {
                        Booking booking = getTableView().getItems().get(getIndex());
                        if (action != null) {
                            action.call(booking);
                        }
                    });

                    Tooltip.install(button, new Tooltip(tooltipText));
                } catch (NullPointerException e) {
                    System.err.println(tooltipText + " icon image not found!");
                    button.setText(tooltipText);
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    private void loadBookings() {
        try {
            bookingsData.setAll(bookingService.read());
            bookigtable.setItems(bookingsData);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Could not load bookings: " + e.getMessage());
        }
    }

    private Void handleMinusButton(Booking booking) {
        if (booking.getReservedSeats() > 1) {
            booking.setReservedSeats(booking.getReservedSeats() - 1);
            updateBooking(booking);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText(null);
            alert.setContentText("You're going to abort. Do you want to proceed?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    handleDeleteBooking(booking);
                }
            });
        }
        return null;
    }

    private Void handleAddButton(Booking booking) {
        try {
            Trip trip = tripService.getById(booking.getTrip().getIdTrip());
            if (booking.getReservedSeats() < trip.getAvailableSeats()) {
                booking.setReservedSeats(booking.getReservedSeats() + 1);
                updateBooking(booking);
            } else {
                showErrorAlert("Limit Reached", "You have reached the maximum reserved seats for this trip.");
            }
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Could not load trip details: " + e.getMessage());
        }
        return null;
    }

    private void updateBooking(Booking booking) {
        try {
            bookingService.update(booking);
            bookigtable.refresh();
        } catch (SQLException e) {
            showErrorAlert("Update Error", "Failed to update booking: " + e.getMessage());
        }
    }

    private void handleDeleteBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Do you really want to abort?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    bookingService.delete(booking.getIdBooking());
                    bookingsData.remove(booking);
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