package services;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioRecorder extends Application {

    private TextArea transcriptionArea;
    private boolean isRecording = false;
    private WhisperTranscriptionService transcriptionService = new WhisperTranscriptionService();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Transcription Audio en Texte");

        Button selectFileButton = new Button("Sélectionner un fichier audio");
        transcriptionArea = new TextArea();
        transcriptionArea.setEditable(false);

        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner un fichier audio");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                transcribeAudio(selectedFile);
            }
        });

        VBox vbox = new VBox(selectFileButton, transcriptionArea);
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void transcribeAudio(File audioFile) {
        new Thread(() -> {
            try {
                String transcription = transcriptionService.transcribeAudio(audioFile);
                Platform.runLater(() -> transcriptionArea.setText(transcription));
            } catch (IOException e) {
                Platform.runLater(() -> transcriptionArea.setText("Erreur lors de la transcription : " + e.getMessage()));
            }
        }).start();
    }

    public static void recordAudio(File audioFile, int duration) throws LineUnavailableException, IOException {
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("La ligne audio n'est pas supportée.");
        }

        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        AudioInputStream ais = new AudioInputStream(line);
        System.out.println("Enregistrement en cours...");

        // Enregistrer pendant la durée spécifiée
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);

        // Arrêter l'enregistrement après la durée spécifiée
        try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        line.stop();
        line.close();
        System.out.println("Enregistrement terminé.");
    }


    public static void main(String[] args) {
        launch(args);
    }
}