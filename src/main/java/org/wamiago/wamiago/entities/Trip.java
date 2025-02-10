package org.wamiago.wamiago.entities;

import java.util.Date;

public class Trip {
    private int idTrip;

    public Trip() {
    }

    private String departureCity;
    private String arrivalCity;

    public Trip(int idTrip, String departureCity, String arrivalCity, Date departureDate, int availableSeats, double pricePerPassenger, int idDriver, int idVehicle) {
        this.idTrip = idTrip;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureDate = departureDate;
        this.availableSeats = availableSeats;
        this.pricePerPassenger = pricePerPassenger;
        this.idDriver = idDriver;
        this.idVehicle = idVehicle;
    }

    private Date departureDate;
    private int availableSeats;
    private double pricePerPassenger;
    private int idDriver;
    private int idVehicle;

    // Getters and Setters
    public int getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(int idTrip) {
        this.idTrip = idTrip;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getPricePerPassenger() {
        return pricePerPassenger;
    }

    public void setPricePerPassenger(double pricePerPassenger) {
        this.pricePerPassenger = pricePerPassenger;
    }

    public int getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(int idDriver) {
        this.idDriver = idDriver;
    }

    public int getIdVehicle() {
        return idVehicle;
    }

    public void setIdVehicle(int idVehicle) {
        this.idVehicle = idVehicle;
    }
}