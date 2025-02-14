package org.wamiago.wamiago.entities;

import java.util.Date;

public class Trip {
    private int idTrip;
    private String departureCity;
    private String arrivalCity;
    private Date departureDate;
    private int availableSeats;
    private double pricePerPassenger;
    private Driver driver;
    private Vehicle vehicle;

    public Trip() {
    }

    public Trip(int idTrip, String departureCity, String arrivalCity, Date departureDate, int availableSeats, double pricePerPassenger, Driver driver, Vehicle vehicle) {
        this.idTrip = idTrip;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureDate = departureDate;
        this.availableSeats = availableSeats;
        this.pricePerPassenger = pricePerPassenger;
        this.driver = driver;
        this.vehicle = vehicle;
    }

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

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}