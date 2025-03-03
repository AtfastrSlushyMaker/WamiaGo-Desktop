package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import entities.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private Button submitButton, cancelButton, emojiButton, recordButton;

    private static final Logger logger = Logger.getLogger(AddAnnouncementController.class.getName());

    @FXML
    private Button generateButton;

    private boolean isRecording = false;
    private OpenAIService openAIService; // Utiliser OpenAI au lieu de SpeechRecognitionService
    private WhisperTranscriptionService transcriptionService;
    private AnnouncementService announcementService;
    private SpeechRecognitionService speechRecognitionService;
    private Driver currentDriver;


    public AddAnnouncementController() {
        this.announcementService = new AnnouncementService();
        this.transcriptionService = new WhisperTranscriptionService();
    }

    @FXML
    public void initialize() {
        if (zoneComboBox != null) {
            zoneComboBox.getItems().setAll(Announcement.Zone.values());
        }

        // Ajouter un emoji au TextArea
        emojiButton.setOnAction(event -> contentField.appendText("😊"));


        // Initialiser le service de reconnaissance vocale
        try {
            String modelPath = "C:\\Users\\BAZINFO\\Desktop\\3A\\S2\\PIDEV\\WamiaGo-Desktop\\src\\main\\resources\\models\\vosk-model-small-en-us-0.15";
            Set<String> badWords = BadWordFilter.loadBadWords("C:\\Users\\BAZINFO\\Desktop\\3A\\S2\\PIDEV\\WamiaGo-Desktop\\src\\main\\resources\\bad_words.csv");
            speechRecognitionService = new SpeechRecognitionService(modelPath, badWords);
        } catch (IOException e) {
            showAlert("Error", "Failed to load speech recognition model: " + e.getMessage(), Alert.AlertType.ERROR);
            recordButton.setDisable(true);
        }

        // Gérer l'enregistrement vocal
        recordButton.setOnAction(event -> {
            if (isRecording) stopRecording();
            else startRecording();
        });



        // Gestion du bouton "Generate"
//        generateButton.setOnAction(event -> {
//            String prompt = "Génère un texte pour une annonce de déménagement avec un ton professionnel.";
//            try {
//                OpenAIService openAIService = new OpenAIService();
//                String generatedText = openAIService.generateText(prompt);
//                contentField.setText(generatedText);
//            } catch (IOException e) {
//                showAlert("Error", "Failed to generate text: " + e.getMessage(), Alert.AlertType.ERROR);
//            }
//        });


    }

//    private void startRecording() {
//        if (!isRecording) {
//            isRecording = true;
//            recordButton.setText("Stop Recording");
//
//            // Enregistrer l'audio dans un fichier temporaire
//            File audioFile = new File("temp_audio.wav");
//            try {
//                AudioRecorder.recordAudio(audioFile, 10); // Enregistre pendant 10 secondes
//            } catch (Exception e) {
//                showAlert("Error", "Failed to record audio: " + e.getMessage(), Alert.AlertType.ERROR);
//                isRecording = false;
//                recordButton.setText("Start Recording");
//                return;
//            }
//
//            // Transcrivez l'audio avec OpenAI
//            Task<String> task = new Task<>() {
//                @Override
//                protected String call() throws Exception {
//                    return transcriptionService.transcribeAudio(audioFile);
//                }
//            };
//
//            task.setOnSucceeded(event -> {
//                String transcribedText = task.getValue();
//                contentField.appendText(transcribedText + " ");
//                isRecording = false;
//                recordButton.setText("Start Recording");
//            });
//
//            task.setOnFailed(event -> {
//                showAlert("Error", "Failed to transcribe audio: " + task.getException().getMessage(), Alert.AlertType.ERROR);
//                isRecording = false;
//                recordButton.setText("Start Recording");
//            });
//
//            new Thread(task).start();
//        }
//
//
//    }
//
//    private void stopRecording() {
//        isRecording = false;
//        recordButton.setText("Start Recording");
//    }

//    private void startRecording() {
//        if (!isRecording) {
//            isRecording = true;
//            recordButton.setText("Stop Recording");
//
//            Task<Void> task = new Task<>() {
//                @Override
//                protected Void call() throws Exception {
//                    logger.info("Début de l'enregistrement audio...");
//                    File audioFile = new File("temp_audio.wav");
//                    try {
//                        AudioRecorder.recordAudio(audioFile, 10); // Enregistrer pendant 10 secondes
//                        logger.info("Enregistrement audio terminé. Fichier créé : " + audioFile.getAbsolutePath());
//
//                        // Vérifier la taille du fichier
//                        long fileSize = audioFile.length();
//                        logger.info("Taille du fichier audio : " + fileSize + " bytes");
//
//                        if (fileSize == 0) {
//                            throw new IOException("Le fichier audio est vide.");
//                        }
//
//                        logger.info("Début de la transcription audio...");
//                        String transcribedText = transcriptionService.transcribeAudio(audioFile);
//                        logger.info("Transcription audio terminée.");
//
//                        Platform.runLater(() -> {
//                            contentField.appendText(transcribedText + " ");
//                            isRecording = false;
//                            recordButton.setText("Start Recording");
//                        });
//
//                    } catch (Exception e) {
//                        logger.severe("Erreur lors de l'enregistrement ou de la transcription audio : " + e.getMessage());
//                        throw e;
//                    }
//                    return null;
//                }
//            };
//
//            task.setOnFailed(event -> {
//                logger.severe("Erreur lors de la transcription audio : " + task.getException().getMessage());
//                Platform.runLater(() -> {
//                    showAlert("Error", "Failed to transcribe audio: " + task.getException().getMessage(), Alert.AlertType.ERROR);
//                    isRecording = false;
//                    recordButton.setText("Start Recording");
//                });
//            });
//
//            new Thread(task).start();
//        }
//    }
//
//    private void stopRecording() {
//        isRecording = false;
//        recordButton.setText("Start Recording");
//    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            recordButton.setText("Stop Recording");

            Task<String> task = speechRecognitionService.startRecording();
            task.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    contentField.appendText(newValue + " ");
                }
            });

            new Thread(task).start();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recordButton.setText("Start Recording");
            speechRecognitionService.stopRecording();
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
            String content = contentField.getText(); // Récupérer le texte transcrit
            System.out.println("Transcribed content: " + content); // Log pour vérifier le texte transcrit
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
            announcement.setContent(content); // Assigner le texte transcrit
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