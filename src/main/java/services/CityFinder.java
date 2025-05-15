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
    public static double[] getCoordinates() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            String response = scanner.useDelimiter("\\A").next();
            JSONObject json = new JSONObject(response);
            String[] loc = json.getString("loc").split(",");
            return new double[] {
                    Double.parseDouble(loc[0]),
                    Double.parseDouble(loc[1])
            };
        }
    }


}