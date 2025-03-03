package services;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;


public class HuggingFaceService {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api-inference.huggingface.co/models/gpt2"; // Modèle GPT-2

    public String generateText(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Créer un objet JSON pour le corps de la requête
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("inputs", prompt);

        // Convertir l'objet JSON en chaîne
        String jsonString = jsonBody.toString();

        // Créer le corps de la requête
        RequestBody body = RequestBody.create(
                jsonString,
                MediaType.parse("application/json")
        );

        // Créer la requête HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Exécuter la requête
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur lors de la requête : " + response.code());
            }

            // Extraire la réponse JSON
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getJSONArray("generated_text")
                    .getString(0)
                    .trim();
        }
    }
}
