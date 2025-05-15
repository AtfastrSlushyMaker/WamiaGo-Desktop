package controllers.Reservation;

import entities.Reservation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ReservationService;
import org.controlsfx.control.Notifications;

import java.sql.Timestamp;

public class EditReservationController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Reservation.Status> statusComboBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField startLocationField;

    @FXML
    private TextField endLocationField;

    @FXML
    private TextField announcementField;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private ReservationService reservationService;
    private Reservation reservationToEdit;

    public EditReservationController() {
        this.reservationService = new ReservationService();
    }

    @FXML
    public void initialize() {
        // Remplir la ComboBox des statuts
        if (statusComboBox != null) {
            statusComboBox.getItems().setAll(Reservation.Status.values());
        }
    }

    public void setReservationToEdit(Reservation reservation) {
        this.reservationToEdit = reservation;
        // Remplir les champs avec les données de la réservation à modifier
        datePicker.setValue(reservation.getDate().toLocalDateTime().toLocalDate());
        statusComboBox.setValue(reservation.getStatus());
        descriptionField.setText(reservation.getDescription());
        startLocationField.setText(reservation.getStartLocation().getAddress());
        endLocationField.setText(reservation.getEndLocation().getAddress());
        announcementField.setText(reservation.getAnnouncement().getTitle());
    }

    @FXML
    public void handleCancelButtonAction() {
        // Fermer la fenêtre actuelle
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    public void handleSubmitButtonAction() {
        try {
            // Récupérer les valeurs des champs
            Timestamp date = Timestamp.valueOf(datePicker.getValue().atStartOfDay());
            Reservation.Status status = statusComboBox.getValue();
            String description = descriptionField.getText();

            // Mettre à jour l'objet Reservation
            reservationToEdit.setDate(date);
            reservationToEdit.setStatus(status);
            reservationToEdit.setDescription(description);

            // Mettre à jour la réservation via le service
            reservationService.update(reservationToEdit);

            // Afficher une notification de type "toast"
            Notifications.create()
                    .title("Succès")
                    .text("La réservation a été mise à jour avec succès.")
                    .showInformation();

            // Fermer la fenêtre
            ((Stage) submitButton.getScene().getWindow()).close();

        } catch (Exception e) {
            // Afficher une notification d'erreur
            Notifications.create()
                    .title("Erreur")
                    .text("Une erreur s'est produite lors de la mise à jour de la réservation : " + e.getMessage())
                    .showError();
        }
    }
}