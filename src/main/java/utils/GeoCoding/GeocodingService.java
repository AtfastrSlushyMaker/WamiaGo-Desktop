package utils.GeoCoding;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public static double[] getCoordinatesFromAddress(String address) {
        try {
            // 1. Encode the address
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String urlString = NOMINATIM_URL + encodedAddress + "&limit=1&format=json";
            URL url = new URL(urlString);

            // 2. Configure HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "YourApp/1.0 (contact@example.com)"); // Required!

            // 3. Check HTTP status
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Nominatim Error: HTTP " + responseCode);
                return null;
            }

            // 4. Parse JSON response
            JsonArray responseArray = JsonParser.parseReader(
                    new InputStreamReader(connection.getInputStream())
            ).getAsJsonArray();

            if (responseArray.isEmpty()) {
                System.out.println("No results for: " + address);
                return null;
            }

            // 5. Extract coordinates
            JsonObject firstResult = responseArray.get(0).getAsJsonObject();
            if (!firstResult.has("lat") || !firstResult.has("lon")) {
                System.err.println("Invalid response format");
                return null;
            }

            // 6. Delay after the request to prevent too many requests in a short time
            Thread.sleep(1000); // Delay for 1 second (adjust as necessary)

            return new double[]{
                    firstResult.get("lat").getAsDouble(),
                    firstResult.get("lon").getAsDouble()
            };

        } catch (Exception e) {
            System.err.println("Geocoding Error: " + e.getMessage());
            return null;
        }
    }

    // USAGE EXAMPLE
    public static void main(String[] args) {
        String address = "Ariana, Tunisia";
        double[] coordinates = getCoordinatesFromAddress(address);

        if (coordinates != null) {
            System.out.println("Latitude: " + coordinates[0] + ", Longitude: " + coordinates[1]);
        } else {
            System.out.println("Could not find coordinates for the address.");
        }
    }
}
