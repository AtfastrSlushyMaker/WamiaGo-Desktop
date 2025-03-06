package services;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class TrafficTimeEstimator {
    private final String apiKey;
    private final String geocodeUrl = "https://geocode.search.hereapi.com/v1/geocode";
    private final String routingUrl = "https://router.hereapi.com/v8/routes";

    // Tunisia extreme coordinates (lat, lng)
    private static final double[][] TUNISIA_BBOX = {
            {32.2295, 7.5248},   // Southwest (Kebili)
            {37.3452, 11.5983}   // Northeast (Bizerte)
    };

    public TrafficTimeEstimator(String apiKey) {
        this.apiKey = apiKey;
    }
    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Check if the connection is successful
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        // Read the response
        StringBuilder inline = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        return inline.toString();
    }

    public double[] getCoordinates(String city) throws Exception {
        String encodedCity = city.replace(" ", "%20");
        String url = geocodeUrl + "?q=" + encodedCity + "&apiKey=" + apiKey;
        String response = sendGetRequest(url);

        JSONObject json = new JSONObject(response);
        if(!json.has("items")) {
            throw new RuntimeException("No geocoding results for: " + city);
        }

        JSONArray items = json.getJSONArray("items");
        if(items.isEmpty()) throw new RuntimeException("City not found: " + city);

        JSONObject position = items.getJSONObject(0).getJSONObject("position");
        return new double[]{
                position.getDouble("lat"),
                position.getDouble("lng")
        };
    }

    public String calculateTravelTime(String originCity, String destinationCity) throws Exception {
        double[] origin = getCoordinates(originCity);
        double[] dest = getCoordinates(destinationCity);

        String url = routingUrl + "?transportMode=car"
                + "&origin=" + origin[0] + "," + origin[1]
                + "&destination=" + dest[0] + "," + dest[1]
                + "&return=summary"
                + "&apiKey=" + apiKey;

        String response = sendGetRequest(url);
        return parseTravelTime(response);
    }

    private String parseTravelTime(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);

        if(!json.has("routes")) {
            throw new RuntimeException("No routes found in response");
        }

        JSONArray routes = json.getJSONArray("routes");
        if(routes.isEmpty()) {
            throw new RuntimeException("Empty routes array");
        }

        JSONArray sections = routes.getJSONObject(0).getJSONArray("sections");
        if(sections.isEmpty()) {
            throw new RuntimeException("No sections in route");
        }

        JSONObject summary = sections.getJSONObject(0).getJSONObject("summary");

        // Handle both numeric (seconds) and ISO duration formats
        if(summary.has("duration")) {
            Object duration = summary.get("duration");
            if(duration instanceof Integer) {
                return formatDurationSeconds((Integer) duration);
            }
            return duration.toString();
        }

        throw new RuntimeException("Duration not found in response");
    }

    private String formatDurationSeconds(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    // Rest of the class remains the same (sendGetRequest, getNationalBoundingBox, main)


}