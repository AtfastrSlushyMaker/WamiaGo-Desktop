package utils.Weather;

import entities.Location;
import entities.WeatherInfo;
import javafx.scene.image.Image;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {
    private static final String API_KEY = System.getenv("WEATHER_API_KEY");

    private static JSONObject fetchWeatherData(Location location) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("API key for OpenWeather is missing.");
        }

        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat="
                    + location.getLatitude() + "&lon=" + location.getLongitude()
                    + "&units=metric&appid=" + API_KEY;

            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed to fetch weather data: " + conn.getResponseMessage());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWeatherDescription(Location location) {
        JSONObject weatherData = fetchWeatherData(location);
        if (weatherData == null || !weatherData.has("weather") || !weatherData.has("main")) {
            return "Weather data unavailable";
        }

        try {
            String description = weatherData.getJSONArray("weather").getJSONObject(0).optString("description", "No description");
            double temp = weatherData.getJSONObject("main").optDouble("temp", Double.NaN);
            return String.format("%.1f°C, %s", temp, description);
        } catch (Exception e) {
            e.printStackTrace();
            return "Weather data unavailable";
        }
    }

    public static WeatherInfo getWeatherInfo(Location location) {
        JSONObject weatherData = fetchWeatherData(location);
        if (weatherData == null || !weatherData.has("weather") || !weatherData.has("main")) {
            return new WeatherInfo("Weather data unavailable", null, 0);
        }

        try {
            String description = weatherData.getJSONArray("weather").getJSONObject(0).optString("description", "No description");
            double temp = weatherData.getJSONObject("main").optDouble("temp", Double.NaN);
            String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).optString("icon", "");
            double windSpeed = weatherData.has("wind") ? weatherData.getJSONObject("wind").optDouble("speed", 0) : 0;

            Image icon = null;
            if (!iconCode.isEmpty()) {
                icon = new Image("https://openweathermap.org/img/wn/" + iconCode + "@2x.png");
            }

            return new WeatherInfo(String.format("%.1f°C, %s", temp, description), icon, windSpeed);
        } catch (Exception e) {
            e.printStackTrace();
            return new WeatherInfo("Weather data unavailable", null, 0);
        }
    }
}
