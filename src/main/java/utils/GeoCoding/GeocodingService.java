package utils.GeoCoding;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeocodingService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public static double[] getCoordinatesFromAddress(String address) {
        try {
            // Construct the API URL
            String urlString = NOMINATIM_URL + address.replace(" ", "+");
            URL url = new URL(urlString);

            // Open the connection and send the GET request
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "JavaFX App");

            // Read the response
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            JsonObject response = JsonParser.parseReader(reader).getAsJsonArray().get(0).getAsJsonObject();

            // Extract latitude and longitude from the response
            double lat = response.get("lat").getAsDouble();
            double lon = response.get("lon").getAsDouble();

            // Return the coordinates
            return new double[]{lat, lon};

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

        //USAGE
        /*String address = "ariana, tunisia";
        double[] coordinates = getCoordinatesFromAddress(address);*/

}

