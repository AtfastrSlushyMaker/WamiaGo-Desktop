package org.wamiago.wamiago.entities;

public class Booking {


    public enum Status {
        Pending,
        Confirmed,
        Canceled
    }
    private int idBooking;
    private int idTrip;
    private int idPassenger;
    private int reservedSeats;
    private Status status;

    public Booking() {
    }

    public Booking(int idBooking, int idTrip, int idPassenger, int reservedSeats, Status status) {
        this.idBooking = idBooking;
        this.idTrip = idTrip;
        this.idPassenger = idPassenger;
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

    public int getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(int idTrip) {
        this.idTrip = idTrip;
    }

    public int getIdPassenger() {
        return idPassenger;
    }

    public void setIdPassenger(int idPassenger) {
        this.idPassenger = idPassenger;
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