package controllers.Reservation;

import entities.Announcement;
import entities.Location;
import entities.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.LocationService;
import services.ReservationService;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class AddReservationController implements Initializable {
    @FXML
    private ComboBox<Location> startLocationComboBox;
    @FXML
    private ComboBox<Location> endLocationComboBox;
    @FXML
    private TextField descriptionField;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<Announcement> announcementComboBox;

    private Announcement selectedAnnouncement; // L'annonce sélectionnée
    private final ReservationService reservationService = new ReservationService();
    private final LocationService locationService = new LocationService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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



        } catch (SQLException e) {
            // Afficher une notification d'erreur
            showAlert("Database Error", "An error occurred while loading data: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(); // Pour déboguer
        }
    }




    // Méthode pour définir l'annonce sélectionnée
    public void setSelectedAnnouncement(Announcement announcement) {
        this.selectedAnnouncement = announcement;
    }

    @FXML
    private void handleSubmitButtonAction() {
        // Récupérer les valeurs des champs
        Location startLocation = startLocationComboBox.getValue();
        Location endLocation = endLocationComboBox.getValue();
        String description = descriptionField.getText();

        // Vérifier que tous les champs sont remplis
        if (startLocation == null || endLocation == null || description.isEmpty() || selectedAnnouncement == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        // Créer une nouvelle réservation
        Reservation reservation = new Reservation();
        reservation.setDate(new Timestamp(System.currentTimeMillis())); // Date actuelle
        reservation.setStatus(Reservation.Status.ON_GOING); // Statut par défaut
        reservation.setDescription(description);
        reservation.setStartLocation(startLocation);
        reservation.setEndLocation(endLocation);
        reservation.setAnnouncement(selectedAnnouncement);

        try {
            // Enregistrer la réservation dans la base de données
            reservationService.create(reservation);
            showAlert("Succès", "Réservation créée avec succès.", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre après la réservation
            ((Stage) submitButton.getScene().getWindow()).close();
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur s'est produite lors de la création de la réservation.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelButtonAction() {
        // Fermer la fenêtre sans enregistrer
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}