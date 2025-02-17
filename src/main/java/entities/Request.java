package entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Request {
    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED, CANCELED
    }

    private int idRequest;
    private User client;
    private Location departureLocation;
    private Location arrivalLocation;
    private RequestStatus status;
    private LocalDateTime requestDate;


    public Request() {
    }


    public Request(User client, Location departureLocation, Location arrivalLocation, RequestStatus status, LocalDateTime requestDate) {
        this.client = client;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.status = status;
        this.requestDate = requestDate != null ? requestDate : LocalDateTime.now();
    }

    // Constructeur avec idRequest (utile pour les requêtes existantes)
    public Request(int idRequest, User client, Location departureLocation, Location arrivalLocation, RequestStatus status, LocalDateTime requestDate) {
        this.idRequest = idRequest;
        this.client = client;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.status = status;
        this.requestDate = requestDate != null ? requestDate : LocalDateTime.now();
    }


    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Location getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(Location departureLocation) {
        this.departureLocation = departureLocation;
    }

    public Location getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(Location arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    // Méthodes utilitaires : equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return idRequest == request.idRequest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRequest);
    }

    @Override
    public String toString() {
        return "Request{" +
                "idRequest=" + idRequest +
                ", client=" + client +
                ", departureLocation=" + departureLocation +
                ", arrivalLocation=" + arrivalLocation +
                ", status=" + status +
                ", requestDate=" + requestDate +
                '}';
    }
}
