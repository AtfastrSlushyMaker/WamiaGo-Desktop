package services;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import javafx.concurrent.Task;

import static java.lang.System.getenv;

public class AzureSpeechService {

    private SpeechConfig speechConfig;
    private AudioConfig audioConfig;
    private SpeechRecognizer recognizer;
    private boolean isRecording = false;
    private StringBuilder recognizedText = new StringBuilder(); // Pour accumuler le texte transcrit

    public AzureSpeechService() {
        // Récupérer la clé et la région depuis les variables d'environnement
        String subscriptionKey = getenv("AZURE_SPEECH_KEY");
        String region = getenv("AZURE_SPEECH_REGION");

        if (subscriptionKey == null || region == null) {
            throw new IllegalStateException("Les variables d'environnement AZURE_SPEECH_KEY et AZURE_SPEECH_REGION doivent être définies.");
        }

        speechConfig = SpeechConfig.fromSubscription(subscriptionKey, region);
        audioConfig = AudioConfig.fromDefaultMicrophoneInput();
        recognizer = new SpeechRecognizer(speechConfig, audioConfig);
    }

    public Task<String> startRecording() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                isRecording = true;
                StringBuilder recognizedText = new StringBuilder();

                recognizer.recognizing.addEventListener((s, e) -> {
                    if (e.getResult().getText() != null && !e.getResult().getText().isEmpty()) {
                        // Ajouter le nouveau texte transcrit sans répéter l'ancien contenu
                        recognizedText.setLength(0); // Réinitialiser le StringBuilder
                        recognizedText.append(e.getResult().getText());
                        updateValue(recognizedText.toString());
                    }
                });

                recognizer.startContinuousRecognitionAsync().get();
                while (isRecording) {
                    Thread.sleep(100);
                }

                recognizer.stopContinuousRecognitionAsync().get();
                return recognizedText.toString();
            }
        };
    }

    public void stopRecording() {
        isRecording = false;
    }
}