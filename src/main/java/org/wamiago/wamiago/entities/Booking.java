package org.wamiago.wamiago.entities;

public class Booking {

    public enum Status {
        Pending,
        Confirmed,
        Canceled
    }

    private int idBooking;
    private Trip trip;
    private User passenger;
    private int reservedSeats;
    private Status status;

    public Booking() {
    }

    public Booking(int idBooking, Trip trip, User passenger, int reservedSeats, Status status) {
        this.idBooking = idBooking;
        this.trip = trip;
        this.passenger = passenger;
        this.reservedSeats = reservedSeats;
        this.status = status;
    }

    // Getters and Setters
    public int getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}