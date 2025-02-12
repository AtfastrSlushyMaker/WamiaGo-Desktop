package org.wamiago.wamiago.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class Bicycle {
    private int id;
    private Station station;
    public enum STATUS {
        available,in_use,charging,maintenance,reserved
    }
    private STATUS status;
    private float battery_level;
    private float range_km;
    private Timestamp last_updated;


    public Bicycle(int id, Station station, STATUS status, float battery_level, float range_km, Timestamp last_updated) {
        this.id = id;
        this.station = station;
        this.status = status;
        this.battery_level = battery_level;
        this.range_km = range_km;
        this.last_updated = last_updated;
    }

    public Bicycle() {
        this.id = 0;
        this.station = new Station();
        this.status = STATUS.available;
        this.battery_level = 0;
        this.range_km = 0;
        this.last_updated = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public float getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(float battery_level) {
        this.battery_level = battery_level;
    }

    public float getRange_km() {
        return range_km;
    }

    public void setRange_km(float range_km) {
        this.range_km = range_km;
    }

    public Timestamp getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Timestamp last_updated) {
        this.last_updated = last_updated;
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
        if (!(o instanceof Bicycle bicycle)) return false;
        return getId() == bicycle.getId() && Float.compare(getBattery_level(), bicycle.getBattery_level()) == 0 && Float.compare(getRange_km(), bicycle.getRange_km()) == 0 && Objects.equals(getStation(), bicycle.getStation()) && Objects.equals(getLast_updated(), bicycle.getLast_updated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStation(), getBattery_level(), getRange_km(), getLast_updated());
    }

    @Override
    public String toString() {
        return "Bicycle{" +
                "id=" + id +
                ", station=" + station +
                ", battery_level=" + battery_level +
                ", range_km=" + range_km +
                ", last_updated=" + last_updated +
                '}';
    }
}
