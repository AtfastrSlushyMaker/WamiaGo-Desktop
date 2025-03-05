package services;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class OpenAIService {
    private static final String API_URL = "https://api.openai.com/v1/completions";

    public String generateText(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Récupérer la clé API depuis les variables d'environnement
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IOException("La clé API OpenAI n'est pas définie dans les variables d'environnement.");
        }

        // Corps de la requête JSON
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "gpt-3.5-turbo-instruct");
        jsonBody.put("prompt", prompt);
        jsonBody.put("max_tokens", 200);
        jsonBody.put("temperature", 0.7);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json")
        );

        // Créer la requête HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        // Exécuter la requête
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur lors de la requête : " + response.code());
            }

            // Extraire la réponse JSON
            JSONObject jsonResponse = new JSONObject(response.body().string());
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("text")
                    .trim();
        }
    }
}