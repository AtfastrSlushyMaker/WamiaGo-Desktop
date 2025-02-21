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
import org.controlsfx.control.Notifications;
import services.DriverService;
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

            Notifications.create()
                    .title("Success")
                    .text("The announcement has been added successfully.")
                    .showInformation();



            titleField.clear();
            contentField.clear();
            zoneComboBox.getSelectionModel().clearSelection();
            statusCheckBox.setSelected(false);

        } catch (Exception e) {
            // Afficher une notification d'erreur
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