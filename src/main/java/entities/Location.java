package entities;

import java.util.Objects;

public class Location {
    private int id;
    private String address;
    private float latitude;
    private float longitude;

    public Location(int id, String address, float latitude, float longitude) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {
        this.id = 0;
        this.address = "";
        this.latitude = 0;
        this.longitude = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;
        return getId() == location.getId() && Float.compare(getLatitude(), location.getLatitude()) == 0 && Float.compare(getLongitude(), location.getLongitude()) == 0 && Objects.equals(getAddress(), location.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAddress(), getLatitude(), getLongitude());
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    public static double calculateDistance(Location start, Location end) {

        // Step 2: Use the Haversine formula to calculate the distance
        final int R = 6371; // Radius of the Earth in kilometers

        double lat1 = start.getLatitude();
        double lon1 = start.getLongitude();
        double lat2 = end.getLatitude();
        double lon2 = end.getLongitude();

        // Calculate the difference in latitudes and longitudes
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
