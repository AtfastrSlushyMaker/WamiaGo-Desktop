package org.wamiago.wamiago.entities;

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
}