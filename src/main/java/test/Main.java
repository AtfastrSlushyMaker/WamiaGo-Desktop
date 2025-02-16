package test;

import entities.Location;
import services.TrafficService;

public class Main {
    public static void main(String[] args) {
        // Create location instances for origin and destination
        Location origin = new Location(1, "Origin Address", 40.7128f, -74.0060f); // Example: New York City
        Location destination = new Location(2, "Destination Address", 34.0522f, -118.2437f); // Example: Los Angeles

        // Instantiate the TrafficService
        TrafficService trafficService = new TrafficService();

        // Fetch traffic data using the service
        TrafficService.RouteResponse response = trafficService.fetchTrafficData(origin, destination);

        // Calculate duration based on the response
        int duration = trafficService.calculateDuration(response);

        // Output the result
        System.out.println("Duration (in seconds): " + duration);
    }
}
