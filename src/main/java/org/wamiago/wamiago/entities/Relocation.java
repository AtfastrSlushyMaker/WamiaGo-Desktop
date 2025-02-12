package org.wamiago.wamiago.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Relocation {
    private int idRelocation;
    private int idReservation;
    private LocalDateTime date;
    private boolean status;
    private float cost;

    public Relocation() {
        this(0, 0, LocalDateTime.now(), false, 0.0f);
    }

    public Relocation(int idRelocation, int idReservation, LocalDateTime date, boolean status, float cost) {
        this.idRelocation = idRelocation;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
        return idRelocation == that.idRelocation && idReservation == that.idReservation &&
                status == that.status && Float.compare(that.cost, cost) == 0 && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRelocation, idReservation, date, status, cost);
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
