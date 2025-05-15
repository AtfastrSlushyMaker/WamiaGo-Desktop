package services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import entities.InitiatePaymentResponse;
import entities.PaymentDetailsResponse;
import entities.InitiatePaymentRequest;

public class PaymentService {
    private static final String BASE_URL = "https://api.preprod.konnect.network/api/v2/";
    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiKey;

    public PaymentService(String apiKey) {
        this.apiKey = apiKey;
    }

    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) throws IOException, InterruptedException {
         ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
         String jsonBody = mapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "payments/init-payment"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), InitiatePaymentResponse.class);
        } else {
            throw new RuntimeException("Payment initiation failed: " + response.body());
        }
    }

    public PaymentDetailsResponse getPaymentStatus(String paymentRef) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "payments/" + paymentRef))
                .header("x-api-key", apiKey)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new ObjectMapper().readValue(response.body(), PaymentDetailsResponse.class);
        } else {
            throw new RuntimeException("Status check failed: " + response.body());
        }
    }
}
