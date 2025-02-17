package entities;

import java.sql.Timestamp;

public class Ride {
    private int idRide;
    private Request request;  // Contient déjà le client
    private Driver driver;  // Ajout du conducteur
    private double distance;
    private int duration;
    private double price;
    private Status status;
    private Timestamp rideDate;

    public enum Status {
        ONGOING, COMPLETED, CANCELED
    }

    public Ride() {
    }

    public Ride(Request request, Driver driver, double distance, int duration, double price, Status status, Timestamp rideDate) {
        this.request = request;
        this.driver = driver;
        this.distance = distance;
        this.duration = duration;
        this.price = price;
        this.status = status;
        this.rideDate = rideDate;
    }

    public Ride(int idRide, Request request, Driver driver, double distance, int duration, double price, Status status, Timestamp rideDate) {
        this.idRide = idRide;
        this.request = request;
        this.driver = driver;
        this.distance = distance;
        this.duration = duration;
        this.price = price;
        this.status = status;
        this.rideDate = rideDate;
    }

    public int getIdRide() {
        return idRide;
    }

    public void setIdRide(int idRide) {
        this.idRide = idRide;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Driver getDriver() {
        return driver;  // Getter pour driver
    }

    public void setDriver(Driver driver) {
        this.driver = driver;  // Setter pour driver
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getRideDate() {
        return rideDate;
    }

    public void setRideDate(Timestamp rideDate) {
        this.rideDate = rideDate;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "idRide=" + idRide +
                ", request=" + request +
                ", driver=" + driver +  // Affichage du driver
                ", distance=" + distance +
                ", duration=" + duration +
                ", price=" + price +
                ", status=" + status +
                ", rideDate=" + rideDate +
                '}';
    }
}
