package entities;

import javafx.scene.image.Image;

public class WeatherInfo {
    private String description;
    private Image icon;
    private double windSpeed; // Added Wind Speed

    public WeatherInfo(String description, Image icon, double windSpeed) {
        this.description = description;
        this.icon = icon;
        this.windSpeed = windSpeed;
    }

    public String getDescription() { return description; }
    public Image getIcon() { return icon; }
    public double getWindSpeed() { return windSpeed; }
}


