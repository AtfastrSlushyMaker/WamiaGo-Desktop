package entities;

import java.sql.Timestamp;
import java.util.Objects;

public class Relocation {
    private int idRelocation;
    private Reservation reservation;
    private Timestamp date;
    private boolean status;
    private float cost;

    public Relocation() {
        this(0, new Reservation(), new Timestamp(System.currentTimeMillis()), false, 0.0f);
    }

    public Relocation(int idRelocation, Reservation reservation, Timestamp date, boolean status, float cost) {
        this.idRelocation = idRelocation;
        this.reservation = reservation;
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

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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
        return idRelocation == that.idRelocation && status == that.status &&
                Float.compare(that.cost, cost) == 0 && Objects.equals(reservation, that.reservation) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRelocation, reservation, date, status, cost);
    }

    @Override
    public String toString() {
        return "Relocation{" +
                "idRelocation=" + idRelocation +
                ", reservation=" + reservation +
                ", date=" + date +
                ", status=" + status +
                ", cost=" + cost +
                '}';
    }
}
