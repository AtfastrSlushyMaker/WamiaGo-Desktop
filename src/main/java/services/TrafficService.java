package services;

import com.google.gson.Gson;
import entities.Location;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TrafficService {

    private static final String API_URL = "https://api.tomtom.com/routing/1/calculateRoute/"; // Replace with the actual API URL
    private static final String API_KEY = "LxGVbLZbDtMTHqGTeGpATDvaO9Hho1Pi"; // Replace with your actual API key

    // This method makes an API call to get traffic data and returns the RouteResponse
    public RouteResponse fetchTrafficData(Location origin, Location destination) {
        OkHttpClient client = new OkHttpClient();

        // Use the latitude and longitude for the API request
        String originCoordinates = origin.getLatitude() + "," + origin.getLongitude();
        String destinationCoordinates = destination.getLatitude() + "," + destination.getLongitude();

        String url = String.format("%s?origin=%s&destination=%s&key=%s", API_URL, originCoordinates, destinationCoordinates, API_KEY);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                // Deserialize the JSON response to a RouteResponse object
                return gson.fromJson(responseBody, RouteResponse.class);
            } else {
                System.out.println("Error: " + response.code());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to calculate the duration in seconds from the API response
    public int calculateDuration(RouteResponse routeResponse) {
        if (routeResponse != null && routeResponse.getRoutes() != null && routeResponse.getRoutes().length > 0) {
            Route firstRoute = routeResponse.getRoutes()[0];  // Get the first route (or you can modify this logic)
            if (firstRoute != null && firstRoute.getSummary() != null) {
                return firstRoute.getSummary().getDurationInSeconds(); // Duration in seconds
            }
        }
        return 0;  // Default to 0 if no valid data
    }

    // Define the necessary nested classes
    public static class RouteResponse {
        private Route[] routes;

        public Route[] getRoutes() {
            return routes;
        }

        public void setRoutes(Route[] routes) {
            this.routes = routes;
        }
    }

    public static class Route {
        private Summary summary;

        public Summary getSummary() {
            return summary;
        }

        public void setSummary(Summary summary) {
            this.summary = summary;
        }
    }

    public static class Summary {
        private int durationInSeconds;

        public int getDurationInSeconds() {
            return durationInSeconds;
        }

        public void setDurationInSeconds(int durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
        }
    }
}
