package controllers;

import entities.Announcement;
import entities.Driver;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.AnnouncementService;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AddAnnouncementController {

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

    private AnnouncementService announcementService;

    public AddAnnouncementController() {
        this.announcementService = new AnnouncementService();
    }

    @FXML
    public void initialize() {
        // Remplir la ComboBox avec les zones disponibles
        zoneComboBox.getItems().setAll(Announcement.Zone.values());
    }

    @FXML
    public void handleSubmitButtonAction() {
        try {
            // Récupérer les valeurs des champs
            String title = titleField.getText();
            String content = contentField.getText();
            Announcement.Zone zone = zoneComboBox.getValue();
            boolean status = statusCheckBox.isSelected();

            // Créer un objet Announcement
            Announcement announcement = new Announcement();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setZone(zone);
            announcement.setStatus(status);
            announcement.setDate(Timestamp.valueOf(LocalDateTime.now()));

            // Attribuer l'ID du transporteur (ici, on utilise l'ID 7 pour tester)
            Driver transporter = new Driver();
            transporter.setIdDriver(7);  // ID du transporteur
            announcement.setTransporter(transporter);

            // Ajouter l'annonce via le service
            announcementService.create(announcement);

            // Afficher un message de succès
            showAlert("Succès", "L'annonce a été ajoutée avec succès.", Alert.AlertType.INFORMATION);

            // Réinitialiser les champs
            titleField.clear();
            contentField.clear();
            zoneComboBox.getSelectionModel().clearSelection();
            statusCheckBox.setSelected(false);

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'annonce : " + e.getMessage(), Alert.AlertType.ERROR);
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