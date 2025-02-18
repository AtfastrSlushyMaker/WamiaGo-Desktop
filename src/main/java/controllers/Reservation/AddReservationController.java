package controllers.Reservation;

import entities.Reservation;
import entities.Location;
import entities.Announcement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ReservationService;
import services.LocationService;
import services.AnnouncementService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

public class AddReservationController {
    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Reservation.Status> statusComboBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private ComboBox<Location> startLocationComboBox;

    @FXML
    private ComboBox<Location> endLocationComboBox;

    @FXML
    private ComboBox<Announcement> announcementComboBox;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private ReservationService reservationService;
    private LocationService locationService;
    private AnnouncementService announcementService;

    public AddReservationController() {
        this.reservationService = new ReservationService();
        this.locationService = new LocationService();
        this.announcementService = new AnnouncementService();
    }

    @FXML
    public void initialize() {
        try {

            System.out.println("Initializing AddReservationController...");
            System.out.println("announcementComboBox: " + announcementComboBox);
            if (announcementComboBox != null) {
                announcementComboBox.getItems().addAll(/* Items to populate */);
            } else {
                System.err.println("announcementComboBox is null");
            }

            // Remplir les ComboBox pour les locations
            startLocationComboBox.getItems().setAll(locationService.read());
            endLocationComboBox.getItems().setAll(locationService.read());

            // Remplir la ComboBox pour les annonces
            announcementComboBox.getItems().setAll(announcementService.read());

        } catch (SQLException e) {
            // Afficher une notification d'erreur
            showAlert("Database Error", "An error occurred while loading data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(); // Pour déboguer
        }

        // Définir le statut par défaut à "ON_GO"
        statusComboBox.getItems().setAll(Reservation.Status.values());
        statusComboBox.setValue(Reservation.Status.ON_GOING);
    }

    @FXML
    public void handleCancelButtonAction() {
        // Fermer la fenêtre actuelle
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    public void setSelectedAnnouncement(Announcement announcement) {
        this.announcementComboBox.setValue(announcement);
    }

//    @FXML
//    public void handleSubmitButtonAction() {
//        try {
//            // Récupérer les valeurs des champs
//            LocalDate localDate = datePicker.getValue();
//            Timestamp date = Timestamp.valueOf(localDate.atStartOfDay());
//            Reservation.Status status = statusComboBox.getValue();
//            String description = descriptionField.getText();
//            Location startLocation = startLocationComboBox.getValue();
//            Location endLocation = endLocationComboBox.getValue();
//            Announcement announcement = announcementComboBox.getValue();
//
//            // Créer un objet Reservation
//            Reservation reservation = new Reservation();
//            reservation.setDate(date);
//            reservation.setStatus(status);
//            reservation.setDescription(description);
//            reservation.setStartLocation(startLocation);
//            reservation.setEndLocation(endLocation);
//            reservation.setAnnouncement(announcement);
//
//            // Ajouter la réservation via le service
//            reservationService.create(reservation);
//
//            // Afficher une notification de succès
//            showAlert("Success", "Reservation added successfully!", Alert.AlertType.INFORMATION);
//
//            // Fermer la fenêtre
//            ((Stage) submitButton.getScene().getWindow()).close();
//
//        } catch (Exception e) {
//            // Afficher une notification d'erreur
//            showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
//        }
//    }

    @FXML
    public void handleSubmitButtonAction() {
        try {
            // Récupérer les valeurs des champs
            LocalDate localDate = datePicker.getValue();
            Timestamp date = Timestamp.valueOf(localDate.atStartOfDay());
            Reservation.Status status = statusComboBox.getValue();
            String description = descriptionField.getText();
            Location startLocation = startLocationComboBox.getValue();
            Location endLocation = endLocationComboBox.getValue();
            Announcement announcement = announcementComboBox.getValue();

            // Créer un objet Reservation
            Reservation reservation = new Reservation();
            reservation.setDate(date);
            reservation.setStatus(status);
            reservation.setDescription(description);
            reservation.setStartLocation(startLocation);
            reservation.setEndLocation(endLocation);

            Announcement a  = new Announcement();
            announcement.setIdAnnouncement(65);
            reservation.setAnnouncement(a);

            // Ajouter la réservation via le service
            reservationService.create(reservation);

            // Afficher une notification de succès
            showAlert("Success", "Reservation added successfully!", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            ((Stage) submitButton.getScene().getWindow()).close();

        } catch (Exception e) {
            // Afficher une notification d'erreur
            showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
