package org.wamiago.wamiago.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {
    private int idReservation;
    private LocalDateTime date;
    private Status status;
    private String description;
    private Location startLocation;
    private Location endLocation;
    private Announcement announcement;

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED, ON_GOING
    }

    public Reservation() {
        this(0, LocalDateTime.now(), Status.CONFIRMED, "", new Location(), new Location(), new Announcement());
    }

    public Reservation(int idReservation, LocalDateTime date, Status status, String description,
                       Location startLocation, Location endLocation, Announcement announcement) {
        this.idReservation = idReservation;
        this.date = date;
        this.status = status;
        this.description = description;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.announcement = announcement;
    }

    // Getters and Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return idReservation == that.idReservation &&
                Objects.equals(date, that.date) && status == that.status &&
                Objects.equals(description, that.description) &&
                Objects.equals(startLocation, that.startLocation) &&
                Objects.equals(endLocation, that.endLocation) &&
                Objects.equals(announcement, that.announcement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReservation, date, status, description, startLocation, endLocation, announcement);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", date=" + date +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startLocation=" + startLocation +
                ", endLocation=" + endLocation +
                ", announcement=" + announcement +
                '}';
    }
}