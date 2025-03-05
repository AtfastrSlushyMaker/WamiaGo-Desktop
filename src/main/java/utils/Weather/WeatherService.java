package utils.Weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import entities.Location;
import entities.WeatherInfo;
import javafx.scene.image.Image;
import org.json.JSONObject;

public class WeatherService {
    private static final String API_KEY = System.getenv("WEATHER_API_KEY");

    public static JSONObject getWeatherData(Location location) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather"+"?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&units=metric&appid=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

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
        JSONObject weatherData = getWeatherData(location);
        if (weatherData != null) {
            String description = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
            double temp = weatherData.getJSONObject("main").getDouble("temp");
            return String.format("%.1f°C, %s", temp, description);
        }
        return "Weather data unavailable";
    }
    public static WeatherInfo getWeatherInfo(Location location) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&units=metric&appid=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject weatherData = new JSONObject(response.toString());
            String description = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");
            double temp = weatherData.getJSONObject("main").getDouble("temp");
            String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");
            double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");

            Image icon = new Image("https://openweathermap.org/img/wn/" + iconCode + "@2x.png");
            return new WeatherInfo(String.format("%.1f°C, %s", temp, description), icon, windSpeed);
        } catch (Exception e) {
            e.printStackTrace();
            return new WeatherInfo("Weather data unavailable", null, 0);
        }
    }
}
