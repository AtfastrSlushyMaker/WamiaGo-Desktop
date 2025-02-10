package org.wamiago.wamiago.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Relocation {
    private int idRelocation;
    private int idReservation;
    private LocalDateTime date;
    private int status;
    private float cost;

    public Relocation() {
    }

    public Relocation(int idRelocation, int idReservation, LocalDateTime date, int status, float cost) {
        this.idRelocation = idRelocation;
        this.idReservation = idReservation;
        this.date = date;
        this.status = status;
        this.cost = cost;
    }

    public Relocation(int idReservation, LocalDateTime date, int status, float cost) {
        this.idReservation = idReservation;
        this.date = date;
        this.status = status;
        this.cost = cost;
    }

    // Getters and Setters
    public int getIdRelocation() {
        return idRelocation;
    }

    public void setIdRelocation(int idRelocation) {
        this.idRelocation = idRelocation;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
        if (!(o instanceof Relocation)) return false;
        Relocation that = (Relocation) o;
        return getIdRelocation() == that.getIdRelocation() && getIdReservation() == that.getIdReservation() && getStatus() == that.getStatus() && Float.compare(that.getCost(), getCost()) == 0 && Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdRelocation(), getIdReservation(), getDate(), getStatus(), getCost());
    }

    @Override
    public String toString() {
        return "Relocation{" +
                "idRelocation=" + idRelocation +
                ", idReservation=" + idReservation +
                ", date=" + date +
                ", status=" + status +
                ", cost=" + cost +
                '}';
    }
}