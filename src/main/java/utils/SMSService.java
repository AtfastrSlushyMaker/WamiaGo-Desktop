package utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//import static services.TrafficService.API_KEY;

public class SMSService {

    private static final String BASE_URL = "https://d96zq8.api.infobip.com";
    private static final String API_KEY = "2315255840c937d3cce5818c9629ca1a-b75600ce-1fa3-47af-8eba-d0ae8facd022";
    // public static final String TWILIO_NUMBER = "votre_numero_twilio";

    public static void sendSMS(String to, String messageBody) {
        HttpClient client = HttpClient.newHttpClient();

        String jsonBody = String.format("{\"messages\":[{\"destinations\":[{\"to\":\"%s\"}],\"from\":\"YourSenderID\",\"text\":\"%s\"}]}", to, messageBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sms/2/text/advanced"))
                .header("Authorization", "App " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendSMS("recipient_phone_number", "Hello, this is a test message from Infobip!");
    }
}