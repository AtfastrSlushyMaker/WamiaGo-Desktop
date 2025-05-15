package services;

import okhttp3.*;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class WhisperTranscriptionService {

    private static final String API_KEY = "sk-proj-FzpD3A6aAstkxWjNIk4lT3BlbkFJ9mx3r7F6x1D0FYerR7ZI"; // Remplacez par votre clé API
    private static final String API_URL = "https://api.openai.com/v1/audio/transcriptions";

    /**
     * Transcrit un fichier audio en texte en utilisant l'API Whisper d'OpenAI.
     *
     * @param audioFile Le fichier audio à transcrire.
     * @return Le texte transcrit.
     */
    public String transcribeAudio(File audioFile) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Créer le corps de la requête multipart/form-data
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(audioFile, MediaType.parse("audio/wav")))
                .addFormDataPart("model", "whisper-1") // Modèle Whisper pour la transcription
                .build();

        // Créer la requête HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        // Exécuter la requête
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur lors de la requête : " + response.code());
            }

            // Extraire la réponse JSON
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getString("text").trim();
        }
    }
}