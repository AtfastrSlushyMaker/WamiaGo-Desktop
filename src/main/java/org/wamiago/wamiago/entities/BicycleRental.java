package org.wamiago.wamiago.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class BicycleRental {
    private int id;
    private User user;
    private Bicycle bicycle;
    private Station start_station;
    private Station end_station;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private float distance_km;
    private float battery_used;
    private float cost;

    public BicycleRental(int id, User user, Bicycle bicycle, Station start_station, Station end_station
            , LocalDateTime start_time, LocalDateTime end_time, float distance_km, float battery_used, float cost) {
        this.id = id;
        this.user = user;
        this.bicycle = bicycle;
        this.start_station = start_station;
        this.end_station = end_station;
        this.start_time = start_time;
        this.end_time = end_time;
        this.distance_km = distance_km;
        this.battery_used = battery_used;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Bicycle getBicycle() {
        return bicycle;
    }

    public void setBicycle(Bicycle bicycle) {
        this.bicycle = bicycle;
    }

    public Station getStart_station() {
        return start_station;
    }

    public void setStart_station(Station start_station) {
        this.start_station = start_station;
    }

    public Station getEnd_station() {
        return end_station;
    }

    public void setEnd_station(Station end_station) {
        this.end_station = end_station;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public float getDistance_km() {
        return distance_km;
    }

    public void setDistance_km(float distance_km) {
        this.distance_km = distance_km;
    }

    public float getBattery_used() {
        return battery_used;
    }

    public void setBattery_used(float battery_used) {
        this.battery_used = battery_used;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BicycleRental that)) return false;
        return getId() == that.getId() && Float.compare(getDistance_km(), that.getDistance_km()) == 0 && Float.compare(getBattery_used(), that.getBattery_used()) == 0 && Float.compare(getCost(), that.getCost()) == 0 && Objects.equals(getUser(), that.getUser()) && Objects.equals(getBicycle(), that.getBicycle()) && Objects.equals(getStart_station(), that.getStart_station()) && Objects.equals(getEnd_station(), that.getEnd_station()) && Objects.equals(getStart_time(), that.getStart_time()) && Objects.equals(getEnd_time(), that.getEnd_time());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getBicycle(), getStart_station(), getEnd_station(), getStart_time(), getEnd_time(), getDistance_km(), getBattery_used(), getCost());
    }

    @Override
    public String toString() {
        return "BicycleRental{" +
                "id=" + id +
                ", user=" + user +
                ", bicycle=" + bicycle +
                ", start_station=" + start_station +
                ", end_station=" + end_station +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", distance_km=" + distance_km +
                ", battery_used=" + battery_used +
                ", cost=" + cost +
                '}';
    }
}
