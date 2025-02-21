package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AnnouncementService;
import services.DriverService;
import org.controlsfx.control.Notifications;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EditAnnouncementController {

    public Button btn_workbench1;
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

    private final AnnouncementService announcementService;
    private Announcement announcementToEdit;

    public EditAnnouncementController() {
        this.announcementService = new AnnouncementService();
    }

    @FXML
    public void initialize() {
        if (zoneComboBox != null) {
            zoneComboBox.getItems().setAll(Announcement.Zone.values());
        }

        btn_workbench1.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
                Parent homeRoot = loader.load();
                Scene homeScene = new Scene(homeRoot);
                Stage stage = (Stage) btn_workbench1.getScene().getWindow();
                stage.setScene(homeScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Initialise l'édition avec les données de l'annonce sélectionnée.
     * @param announcement L'annonce à modifier
     */
    public void setAnnouncementToEdit(Announcement announcement) {
        if (announcement == null) {
            Notifications.create()
                    .title("Erreur")
                    .text("Impossible de modifier cette annonce. Données invalides.")
                    .showError();
            return;
        }

        this.announcementToEdit = announcement;

        // Remplir les champs avec les données actuelles
        titleField.setText(announcement.getTitle());
        contentField.setText(announcement.getContent());
        zoneComboBox.setValue(announcement.getZone());
        statusCheckBox.setSelected(announcement.getStatus());
    }

    /**
     * Ferme la fenêtre sans enregistrer les modifications.
     */
    @FXML
    public void handleCancelButtonAction() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    /**
     * Enregistre les modifications apportées à l'annonce.
     */
    @FXML
    public void handleSubmitButtonAction() {
        try {
            if (announcementToEdit == null) {
                Notifications.create()
                        .title("Erreur")
                        .text("Aucune annonce sélectionnée pour la modification.")
                        .showError();
                return;
            }

            // Récupérer les valeurs des champs
            String title = titleField.getText().trim();
            String content = contentField.getText().trim();
            Announcement.Zone zone = zoneComboBox.getValue();
            boolean status = statusCheckBox.isSelected();

            // Vérifier si les champs obligatoires sont remplis
            if (title.isEmpty() || content.isEmpty() || zone == null) {
                Notifications.create()
                        .title("Erreur")
                        .text("Veuillez remplir tous les champs obligatoires.")
                        .showError();
                return;
            }

            // Mettre à jour les propriétés de l'annonce
            announcementToEdit.setTitle(title);
            announcementToEdit.setContent(content);
            announcementToEdit.setZone(zone);
            announcementToEdit.setStatus(status);
            announcementToEdit.setDate(Timestamp.valueOf(LocalDateTime.now()));

            // Récupérer dynamiquement le transporteur
            DriverService driverService = new DriverService();
            Driver currentDriver = driverService.getDriverByUserId(SessionManager.getInstance().getUser());

            if (currentDriver == null) {
                Notifications.create()
                        .title("Erreur")
                        .text("Impossible de récupérer les informations du conducteur.")
                        .showError();
                return;
            }

            announcementToEdit.setTransporter(currentDriver);

            // Mettre à jour l'annonce via le service
            announcementService.update(announcementToEdit);

            // Afficher une notification de succès
            Notifications.create()
                    .title("Succès")
                    .text("L'annonce a été mise à jour avec succès.")
                    .showInformation();

            // Fermer la fenêtre
            ((Stage) submitButton.getScene().getWindow()).close();

        } catch (SQLException e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Une erreur SQL s'est produite : " + e.getMessage())
                    .showError();
            e.printStackTrace();
        } catch (Exception e) {
            Notifications.create()
                    .title("Erreur")
                    .text("Une erreur inattendue est survenue : " + e.getMessage())
                    .showError();
            e.printStackTrace();
        }
    }
}
