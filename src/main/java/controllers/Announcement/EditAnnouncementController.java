
package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AnnouncementService;
import org.controlsfx.control.Notifications;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EditAnnouncementController {

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentField;

    @FXML
    private ComboBox<Announcement.Zone> zoneComboBox;

    @FXML
    private CheckBox statusCheckBox;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private AnnouncementService announcementService;
    private Announcement announcementToEdit;

    public EditAnnouncementController() {
        this.announcementService = new AnnouncementService();
    }

    @FXML
    public void initialize() {
        if (zoneComboBox != null) {
            zoneComboBox.getItems().setAll(Announcement.Zone.values());
        }
    }

    public void setAnnouncementToEdit(Announcement announcement) {
        this.announcementToEdit = announcement;
        // Remplir les champs avec les données de l'annonce à modifier
        titleField.setText(announcement.getTitle());
        contentField.setText(announcement.getContent());
        zoneComboBox.setValue(announcement.getZone());
        statusCheckBox.setSelected(announcement.getStatus());
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
            String title = titleField.getText();
            String content = contentField.getText();
            Announcement.Zone zone = zoneComboBox.getValue();
            boolean status = statusCheckBox.isSelected();

            // Mettre à jour l'objet Announcement
            announcementToEdit.setTitle(title);
            announcementToEdit.setContent(content);
            announcementToEdit.setZone(zone);
            announcementToEdit.setStatus(status);
            announcementToEdit.setDate(Timestamp.valueOf(LocalDateTime.now()));

            Driver transporter = new Driver();
            transporter.setIdDriver(7);  // ID du transporteur
            announcementToEdit.setTransporter(transporter);
            // Mettre à jour l'annonce via le service
            announcementService.update(announcementToEdit);

            // Afficher une notification de type "toast"
            Notifications.create()
                    .title("Success")
                    .text("The announcement has been updated successfully.")
                    .showInformation();

            // Fermer la fenêtre
            ((Stage) submitButton.getScene().getWindow()).close();

        } catch (Exception e) {
            // Afficher une notification d'erreur
            Notifications.create()
                    .title("Error")
                    .text("An error occurred while updating the announcement: " + e.getMessage())
                    .showError();
        }
    }
}