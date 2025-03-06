package services;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class CityFinder {
    private static final String API_URL = "https://ipinfo.io/json";

    public static String getCurrentCity() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            String response = scanner.useDelimiter("\\A").next();
            JSONObject json = new JSONObject(response);
            return json.getString("city");
        }
    }

    public static void main(String[] args) {
        try {
            String city = CityFinder.getCurrentCity();
            System.out.println("╔════════════════════════════╗");
            System.out.println("║  Detected City: " + String.format("%-9s", city) + "║");
            System.out.println("╚════════════════════════════╝");
        } catch (Exception e) {
            System.err.println("Error detecting city: " + e.getMessage());
        }
    }
}