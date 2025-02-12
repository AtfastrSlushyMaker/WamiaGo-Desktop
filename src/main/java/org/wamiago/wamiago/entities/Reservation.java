package org.wamiago.wamiago.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {
    private int idReservation;
    private LocalDateTime date;
    private Status status;
    private String description;
    private int idStartLocation;
    private int idEndLocation;
    private int idAnnouncement;

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED, ON_GOING
    }

    public Reservation() {
        this(0, LocalDateTime.now(), Status.CONFIRMED, "", 0, 0, 0);
    }

    public Reservation(int idReservation, LocalDateTime date, Status status, String description,
                       int idStartLocation, int idEndLocation, int idAnnouncement) {
        this.idReservation = idReservation;
        this.date = date;
        this.status = status;
        this.description = description;
        this.idStartLocation = idStartLocation;
        this.idEndLocation = idEndLocation;
        this.idAnnouncement = idAnnouncement;
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

    public int getIdStartLocation() {
        return idStartLocation;
    }

    public void setIdStartLocation(int idStartLocation) {
        this.idStartLocation = idStartLocation;
    }

    public int getIdEndLocation() {
        return idEndLocation;
    }

    public void setIdEndLocation(int idEndLocation) {
        this.idEndLocation = idEndLocation;
    }

    public int getIdAnnouncement() {
        return idAnnouncement;
    }

    public void setIdAnnouncement(int idAnnouncement) {
        this.idAnnouncement = idAnnouncement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return idReservation == that.idReservation && idStartLocation == that.idStartLocation &&
                idEndLocation == that.idEndLocation && idAnnouncement == that.idAnnouncement &&
                Objects.equals(date, that.date) && status == that.status &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReservation, date, status, description, idStartLocation, idEndLocation, idAnnouncement);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", date=" + date +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", idStartLocation=" + idStartLocation +
                ", idEndLocation=" + idEndLocation +
                ", idAnnouncement=" + idAnnouncement +
                '}';
    }
}