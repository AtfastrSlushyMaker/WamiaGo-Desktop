package entities;

import java.util.Objects;

public class Station {
    private int id;
    private String name;
    private Location location;
    private int total_docks;
    private int available_docks;
    private int available_bikes;
    private int charging_bikes;


    public enum STATUS {active, inactive, maintenance, disabled}
    private  STATUS status;


    public Station(int id, String name, Location location, int total_docks, int available_docks,
                   int available_bikes, int charging_bikes, STATUS status) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.total_docks = total_docks;
        this.available_docks = available_docks;
        this.available_bikes = available_bikes;
        this.charging_bikes = charging_bikes;
        this.status = status;
    }

    public Station() {
        this.id = 0;
        this.name = "";
        this.location = new Location();
        this.total_docks = 0;
        this.available_docks = 0;
        this.available_bikes = 0;
        this.charging_bikes = 0;
        this.status = STATUS.disabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getAvailable_docks() {
        return available_docks;
    }

    public void setAvailable_docks(int available_docks) {
        this.available_docks = available_docks;
    }

    public int getAvailable_bikes() {
        return available_bikes;
    }

    public void setAvailable_bikes(int available_bikes) {
        this.available_bikes = available_bikes;
    }

    public int getCharging_bikes() {
        return charging_bikes;
    }

    public void setCharging_bikes(int charging_bikes) {
        this.charging_bikes = charging_bikes;
    }

    public int getTotal_docks() {
        return total_docks;
    }

    public void setTotal_docks(int total_docks) {
        this.total_docks = total_docks;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station station)) return false;
        return  getTotal_docks() == station.getTotal_docks() && getAvailable_docks() == station.getAvailable_docks() && getAvailable_bikes() == station.getAvailable_bikes() && getCharging_bikes() == station.getCharging_bikes() && Objects.equals(getName(), station.getName()) && Objects.equals(getLocation(), station.getLocation()) && getStatus() == station.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getLocation(), getTotal_docks(), getAvailable_docks(), getAvailable_bikes(), getCharging_bikes(), getStatus());
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", total_docks=" + total_docks +
                ", available_docks=" + available_docks +
                ", available_bikes=" + available_bikes +
                ", charging_bikes=" + charging_bikes +
                ", status=" + status +
                '}';
    }
}
