package entities;

import java.sql.Timestamp;

public class Ride {

    private int idRide;
    private int idTaxi;
    private int idClient;
    private int idRequest;
    private double distance;
    private int duration;
    private double price;
    private Status status;
    private Timestamp rideDate;

    public enum Status {
        Ongoing, Completed, Canceled
    }

    public Ride() {
    }

    public Ride(int idTaxi, int idClient, int idRequest, double distance, int duration, double price, Status status, Timestamp rideDate) {
        this.idTaxi = idTaxi;
        this.idClient = idClient;
        this.idRequest = idRequest;
        this.distance = distance;
        this.duration = duration;
        this.price = price;
        this.status = status;
        this.rideDate = rideDate;
    }

    public Ride(int idRide, int idTaxi, int idClient, int idRequest, double distance, int duration, double price, Status status, Timestamp rideDate) {
        this.idRide = idRide;
        this.idTaxi = idTaxi;
        this.idClient = idClient;
        this.idRequest = idRequest;
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

    public int getIdTaxi() {
        return idTaxi;
    }

    public void setIdTaxi(int idTaxi) {
        this.idTaxi = idTaxi;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
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
}
