package services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class ClimatiqService {
    private final String apiKey;
    private final String estimateUrl = "https://api.climatiq.io/data/v1/estimate";

    public ClimatiqService(String apiKey) {
        this.apiKey = apiKey;
    }

    public JSONObject calculateEnergyEmissions(
            double energyValue,
            String energyUnit,
            String activityId
    ) throws Exception {
        JSONObject requestBody = new JSONObject()
                .put("emission_factor", new JSONObject()
                        .put("activity_id", activityId)
                        .put("data_version", "^6"))
                .put("parameters", new JSONObject()
                        .put("energy", energyValue)
                        .put("energy_unit", energyUnit));

        HttpURLConnection conn = (HttpURLConnection) new URL(estimateUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + this.apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (var os = conn.getOutputStream()) {
            os.write(requestBody.toString().getBytes("utf-8"));
        }

        return handleResponse(conn);
    }

    private JSONObject handleResponse(HttpURLConnection conn) throws Exception {
        int status = conn.getResponseCode();
        InputStream stream = status < 400 ? conn.getInputStream() : conn.getErrorStream();

        try (Scanner scanner = new Scanner(stream).useDelimiter("\\A")) {
            String response = scanner.hasNext() ? scanner.next() : "";
            JSONObject result = new JSONObject(response);

            if (status >= 400) {
                throw new RuntimeException("API Error " + status + ": " + result);
            }
            return result;
        }
    }

    // Helper method for common energy types
    public JSONObject calculateElectricityEmissions(double kWh) throws Exception {
        return calculateEnergyEmissions(
                kWh,
                "kWh",
                "electricity-supply_grid-source_residual_mix"
        );
    }

    public JSONObject calculateGasolineEmissions(double liters) throws Exception {
        return calculateEnergyEmissions(
                liters,
                "l",
                "passenger_vehicle-vehicle_type_car-fuel_na-engine_size_na"
        );
    }

    public JSONObject calculateCarEmissions(double powerKW, double hours, String fuelType) throws Exception {
        double energy = powerKW * hours;
        String activityId = switch (fuelType.toLowerCase()) {
            case "gasoline" -> "passenger_vehicle-vehicle_type_car-fuel_na-engine_size_na";
            case "diesel" -> "passenger_vehicle-vehicle_type_car-fuel_diesel-engine_size_na";
            case "electric" -> "electricity-supply_grid-source_residual_mix";
            default -> throw new IllegalArgumentException("Unsupported fuel type");
        };
        return calculateEnergyEmissions(energy, "kWh", activityId);
    }
}