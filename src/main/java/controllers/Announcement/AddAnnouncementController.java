package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import entities.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.*;
import utils.BadWordFilter;
import utils.SessionManager;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.logging.Logger;

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
    private Button submitButton, cancelButton, emojiButton, recordButton, backButton;

    private static final Logger logger = Logger.getLogger(AddAnnouncementController.class.getName());

    @FXML
    private Button generateButton;

    private boolean isRecording = false;
    private OpenAIService openAIService;
    private WhisperTranscriptionService transcriptionService;
    private AnnouncementService announcementService;
    private SpeechRecognitionService speechRecognitionService;
    private AzureSpeechService azureSpeechService;
    private Driver currentDriver;

    public AddAnnouncementController() {
        this.announcementService = new AnnouncementService();
        this.azureSpeechService = new AzureSpeechService();
    }

    @FXML
    public void initialize() {
        if (zoneComboBox != null) {
            zoneComboBox.getItems().setAll(Announcement.Zone.values());
        }

        recordButton.setOnAction(event -> {
            if (isRecording) stopRecording();
            else startRecording();
        });

        generateButton.setOnAction(event -> {
            String userContent = contentField.getText();
            String prompt = userContent.isEmpty()
                    ? "Génère un texte pour une annonce de déménagement avec un ton professionnel."
                    : "Améliore ou complète le texte suivant pour une annonce de déménagement avec un ton professionnel : " + userContent;

            try {
                OpenAIService openAIService = new OpenAIService();
                String generatedText = openAIService.generateText(prompt);
                contentField.setText(generatedText);
            } catch (IOException e) {
                showAlert("Error", "Failed to generate text: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the announcements view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            recordButton.setText("Stop Recording");

            Platform.runLater(() -> contentField.clear());

            Task<String> task = azureSpeechService.startRecording();
            task.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    Platform.runLater(() -> contentField.setText(newValue));
                }
            });

            new Thread(task).start();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recordButton.setText("Start Recording");
            azureSpeechService.stopRecording();
        }
    }

    @FXML
    public void handleCancelButtonAction() {
        titleField.clear();
        contentField.clear();
        zoneComboBox.getSelectionModel().clearSelection();
        statusCheckBox.setSelected(false);
    }

    @FXML
    public void handleSubmitButtonAction() {
        try {
            String title = titleField.getText();
            String content = contentField.getText();
            System.out.println("Transcribed content: " + content);
            Announcement.Zone zone = zoneComboBox.getValue();
            boolean status = statusCheckBox.isSelected();

            Set<String> badWords = BadWordFilter.loadBadWords("C:\\Users\\BAZINFO\\Desktop\\3A\\S2\\PIDEV\\WamiaGo-Desktop\\src\\main\\resources\\bad_words.csv");
            title = BadWordFilter.filterBadWords(title, badWords);
            content = BadWordFilter.filterBadWords(content, badWords);

            if (title.isEmpty() || content.isEmpty() || zone == null) {
                showAlert("Error", "Please fill in all fields.", Alert.AlertType.ERROR);
                return;
            }

            Announcement announcement = new Announcement();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setZone(zone);
            announcement.setStatus(status);
            announcement.setDate(Timestamp.valueOf(LocalDateTime.now()));

            DriverService driverService = new DriverService();
            User loggedInUser = SessionManager.getInstance().getUser();
            currentDriver = driverService.getById(loggedInUser.getId());
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements.fxml"));
            Parent announcementView = loader.load();
            Scene announcementScene = new Scene(announcementView);
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(announcementScene);
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the announcement view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "An error occurred while adding the announcement: " + e.getMessage(), Alert.AlertType.ERROR);
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