package utils.GeoCoding;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GeocodingService {

    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/";

    public static double[] getCoordinatesFromAddress(String address) {
        try {
            // 1. Encode the address
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

            // 2. Build URL with proper encoding
            URI uri = new URI(
                    NOMINATIM_BASE_URL + "search" +
                            "?format=json" +
                            "&q=" + encodedAddress +
                            "&limit=1"
            );
            URL url = uri.toURL();

            // 3. Configure HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "YourApp/1.0 (contact@example.com)");

            // 4. Check HTTP status
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Nominatim Error: HTTP " + responseCode);
                return null;
            }

            // 5. Parse JSON response
            JsonArray responseArray = JsonParser.parseReader(
                    new InputStreamReader(connection.getInputStream())
            ).getAsJsonArray();

            if (responseArray.isEmpty()) {
                System.out.println("No results for: " + address);
                return null;
            }

            // 6. Extract coordinates
            JsonObject firstResult = responseArray.get(0).getAsJsonObject();
            if (!firstResult.has("lat") || !firstResult.has("lon")) {
                System.err.println("Invalid response format");
                return null;
            }

            // 7. Delay after the request to prevent too many requests in a short time
            Thread.sleep(1000);

            return new double[]{
                    Double.parseDouble(firstResult.get("lat").getAsString()),
                    Double.parseDouble(firstResult.get("lon").getAsString())
            };

        } catch (Exception e) {
            System.err.println("Geocoding Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String getAddressFromCoordinates(double lat, double lon) {
        try {
            // 1. Validate coordinates
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                System.err.println("Invalid coordinates: lat=" + lat + ", lon=" + lon);
                return null;
            }

            // 2. Build URI with proper encoding - using toString() to avoid locale issues
            URI uri = new URI(
                    NOMINATIM_BASE_URL + "reverse" +
                            "?format=json" +
                            "&lat=" + Double.toString(lat) +
                            "&lon=" + Double.toString(lon) +
                            "&addressdetails=1"
            );

            URL url = uri.toURL();
            System.out.println("Requesting URL: " + url);

            // 3. Configure HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "YourApp/1.0 (contact@example.com)");

            // 4. Check HTTP status
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Nominatim Error: HTTP " + responseCode);
                return null;
            }

            // 5. Parse JSON response
            JsonObject responseObject = JsonParser.parseReader(
                    new InputStreamReader(connection.getInputStream())
            ).getAsJsonObject();

            if (!responseObject.has("address")) {
                System.out.println("No address found for the coordinates.");
                return null;
            }

            // 6. Extract the full address
            JsonObject address = responseObject.getAsJsonObject("address");
            StringBuilder addressString = new StringBuilder();

            // Loop through and build the address string
            if (address.has("road")) addressString.append(address.get("road").getAsString()).append(", ");
            if (address.has("city")) addressString.append(address.get("city").getAsString()).append(", ");
            if (address.has("state")) addressString.append(address.get("state").getAsString()).append(", ");
            if (address.has("country")) addressString.append(address.get("country").getAsString());

            // 7. Delay after the request
            Thread.sleep(1000);

            // Return the formatted address
            return addressString.toString();

        } catch (Exception e) {
            System.err.println("Reverse Geocoding Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // USAGE EXAMPLE
    public static void main(String[] args) {
        // Test forward geocoding
        String address = "Ariana, Tunisia";
        double[] coordinates = getCoordinatesFromAddress(address);

        if (coordinates != null) {
            System.out.println("Latitude: " + coordinates[0] + ", Longitude: " + coordinates[1]);

            // Test reverse geocoding with these coordinates
            String foundAddress = getAddressFromCoordinates(coordinates[0], coordinates[1]);
            System.out.println("Found address: " + foundAddress);
        } else {
            System.out.println("Could not find coordinates for the address.");
        }
    }
}