package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AnnouncementService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.controlsfx.control.Notifications;
import services.DriverService;
import utils.BadWordFilter;
import utils.SessionManager;

public class AddAnnouncementController {

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

    private AnnouncementService announcementService;

    private Label messageLabel;

    public AddAnnouncementController() {
        this.announcementService = new AnnouncementService();
    }

    private Driver currentDriver;

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




    @FXML
    public void handleCancelButtonAction() {
        // Fermer la fenêtre actuelle
        //((Stage) cancelButton.getScene().getWindow()).close();
        titleField.clear();
        contentField.clear();
        zoneComboBox.getSelectionModel().clearSelection();
        statusCheckBox.setSelected(false);
    }


    @FXML
    public void handleSubmitButtonAction() {
        try {
            // Récupérer les valeurs des champs
            String title = titleField.getText();
            String content = contentField.getText();
            Announcement.Zone zone = zoneComboBox.getValue();
            boolean status = statusCheckBox.isSelected();

            Set<String> badWords = BadWordFilter.loadBadWords("C:\\Users\\BAZINFO\\Desktop\\3A\\S2\\PIDEV\\WamiaGo-Desktop\\src\\main\\resources\\bad_words.csv");
            // Filter bad words from the title and content
            title = BadWordFilter.filterBadWords(title, badWords);
            content = BadWordFilter.filterBadWords(content, badWords);

            // Vérifier que tous les champs sont remplis
            if (title.isEmpty() || content.isEmpty() || zone == null) {
                // Afficher un message d'erreur si les champs ne sont pas remplis
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Please fill in all fields.");

                // Appliquer un style CSS personnalisé pour le message d'erreur
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/Reservation/Front/reservation.css").toExternalForm());
                dialogPane.getStyleClass().add("error-alert");

                alert.showAndWait();
                return; // Ne pas créer l'annonce si les champs ne sont pas remplis
            }

            // Créer l'annonce
            Announcement announcement = new Announcement();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setZone(zone);
            announcement.setStatus(status);
            announcement.setDate(Timestamp.valueOf(LocalDateTime.now()));

            DriverService driverService = new DriverService();
            User loogedinuser = SessionManager.getInstance().getUser();
            currentDriver = driverService.getById(loogedinuser.getId());
            announcement.setTransporter(currentDriver);

            announcementService.create(announcement);

            // Afficher une notification de succès
            Notifications.create()
                    .title("Success")
                    .text("The announcement has been added successfully.")
                    .showInformation();

            // Effacer les champs après l'ajout
            titleField.clear();
            contentField.clear();
            zoneComboBox.getSelectionModel().clearSelection();
            statusCheckBox.setSelected(false);

            // Redirection vers annoucements.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements.fxml"));
            Parent announcementView = loader.load();
            Scene announcementScene = new Scene(announcementView);

            // Obtenir la fenêtre actuelle et la mettre à jour avec la nouvelle scène
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(announcementScene);
            stage.show();

        } catch (IOException e) {
            Notifications.create()
                    .title("Error")
                    .text("Failed to load the announcement view: " + e.getMessage())
                    .showError();
            e.printStackTrace();
        } catch (Exception e) {
            Notifications.create()
                    .title("Error")
                    .text("An error occurred while adding the announcement: " + e.getMessage())
                    .showError();
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