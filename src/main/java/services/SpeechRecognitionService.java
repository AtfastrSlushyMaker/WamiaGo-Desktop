package services;

import org.vosk.Model;
import org.vosk.Recognizer;
import javafx.concurrent.Task;
import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Set;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utils.BadWordFilter;



public class SpeechRecognitionService {
    private final Recognizer recognizer;
    private boolean isRecording = false;
    private TargetDataLine line;
    private Set<String> badWords;

    public SpeechRecognitionService(String modelPath, Set<String> badWords) throws IOException {
        Model model = new Model(modelPath);
        recognizer = new Recognizer(model, 16000);
        this.badWords = badWords;
    }

    public Task<String> startRecording() {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                isRecording = true;
                System.out.println("Recording started...");

                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                try {
                    line = (TargetDataLine) AudioSystem.getLine(info);
                    line.open(format);
                    line.start();

                    byte[] buffer = new byte[4096];
                    while (isRecording) {
                        int count = line.read(buffer, 0, buffer.length);
                        if (count > 0) {
                            recognizer.acceptWaveForm(buffer, count);
                            String result = recognizer.getResult();
                            if (result != null && !result.isEmpty()) {
                                // Extraire le texte du JSON
                                JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
                                String recognizedText = jsonObject.get("text").getAsString();

                                // Filtrer les mauvais mots
                                recognizedText = BadWordFilter.filterBadWords(recognizedText, badWords);

                                // Mettre à jour la valeur avec le texte reconnu
                                updateValue(recognizedText);
                            }
                        }
                    }
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } finally {
                    if (line != null) {
                        line.stop();
                        line.close();
                    }
                }

                String finalResult = recognizer.getFinalResult();
                // Extraire le texte du JSON final
                JsonObject jsonObject = JsonParser.parseString(finalResult).getAsJsonObject();
                String finalText = jsonObject.get("text").getAsString();

                System.out.println("Recording stopped. Final result: " + finalText);
                return finalText;
            }
        };
    }
    public void stopRecording() {
        isRecording = false;
    }
}